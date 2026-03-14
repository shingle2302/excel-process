import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import TaskDetail from '../../views/TaskDetail.vue'
import { taskApi } from '../../services/api'
import { ElMessage } from 'element-plus'

// 模拟图标
vi.mock('@element-plus/icons-vue', () => ({
  ArrowLeft: { template: '<div>ArrowLeft</div>' }
}))

// 模拟路由
const mockPush = vi.fn()
vi.mock('vue-router', () => ({
  useRouter: () => ({
    push: mockPush
  }),
  useRoute: () => ({
    params: { id: '1' },
    path: '/test'
  })
}))

describe('TaskDetail组件测试', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render TaskDetail component', () => {
    const wrapper = mount(TaskDetail)
    expect(wrapper.exists()).toBe(true)
  })

  it('should load task detail on mount', async () => {
    // 模拟API响应
    const mockTask = {
      id: 1,
      name: 'Test Task',
      type: '导入',
      status: '已完成',
      progress: 100,
      createTime: '2024-01-01 00:00:00',
      updateTime: '2024-01-01 00:00:00',
      description: 'Test Description',
      params: '{}',
      result: '{}'
    }
    taskApi.get.mockResolvedValue(mockTask)

    const wrapper = mount(TaskDetail)
    await wrapper.vm.$nextTick()

    expect(taskApi.get).toHaveBeenCalledWith('1')
  })

  it('should handle API error when loading task', async () => {
    // 模拟API错误
    taskApi.get.mockRejectedValue(new Error('API Error'))

    const wrapper = mount(TaskDetail)
    // 等待loadTask函数执行完成
    await new Promise(resolve => setTimeout(resolve, 100))

    expect(ElMessage.error).toHaveBeenCalled()
  })

  it('should return correct status type', () => {
    const wrapper = mount(TaskDetail)
    expect(wrapper.vm.getStatusType('待处理')).toBe('info')
    expect(wrapper.vm.getStatusType('处理中')).toBe('warning')
    expect(wrapper.vm.getStatusType('已完成')).toBe('success')
    expect(wrapper.vm.getStatusType('失败')).toBe('danger')
    expect(wrapper.vm.getStatusType('暂停')).toBe('warning')
    expect(wrapper.vm.getStatusType('取消')).toBe('info')
    expect(wrapper.vm.getStatusType('unknown')).toBe('info')
  })

  it('should navigate back when goBack is called', async () => {
    // 清除之前的调用
    mockPush.mockClear()

    const wrapper = mount(TaskDetail)
    await wrapper.vm.goBack()

    expect(mockPush).toHaveBeenCalledWith('/tasks')
  })

  it('should refresh task status periodically when task is in progress', async () => {
    // 模拟API响应
    const mockTask = {
      id: 1,
      name: 'Test Task',
      status: '处理中',
      progress: 50
    }
    taskApi.get.mockResolvedValue(mockTask)

    // 模拟setInterval
    const mockSetInterval = vi.fn()
    global.setInterval = mockSetInterval

    const wrapper = mount(TaskDetail)
    await wrapper.vm.$nextTick()

    expect(mockSetInterval).toHaveBeenCalled()
  })
})