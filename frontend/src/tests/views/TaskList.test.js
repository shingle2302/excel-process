import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import TaskList from '../../views/TaskList.vue'
import { taskApi, taskDefinitionApi } from '../../services/api'
import { ElMessage } from 'element-plus'

// 模拟图标
vi.mock('@element-plus/icons-vue', () => ({
  Plus: { template: '<div>Plus</div>' }
}))

// 模拟vue-router
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

describe('TaskList组件测试', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render TaskList component', () => {
    const wrapper = mount(TaskList)
    expect(wrapper.exists()).toBe(true)
  })

  it('should load tasks on mount', async () => {
    // 模拟API响应
    taskApi.list.mockResolvedValue([{ id: 1, name: 'Task 1' }])

    const wrapper = mount(TaskList)
    await wrapper.vm.$nextTick()

    expect(taskApi.list).toHaveBeenCalled()
  })

  it('should load task definitions on mount', async () => {
    // 模拟API响应
    taskDefinitionApi.list.mockResolvedValue([{ id: 1, name: 'Definition 1' }])

    const wrapper = mount(TaskList)
    await wrapper.vm.$nextTick()

    expect(taskDefinitionApi.list).toHaveBeenCalled()
  })

  it('should return correct status type', () => {
    const wrapper = mount(TaskList)
    expect(wrapper.vm.getStatusType('待处理')).toBe('info')
    expect(wrapper.vm.getStatusType('处理中')).toBe('warning')
    expect(wrapper.vm.getStatusType('已完成')).toBe('success')
    expect(wrapper.vm.getStatusType('失败')).toBe('danger')
    expect(wrapper.vm.getStatusType('暂停')).toBe('warning')
    expect(wrapper.vm.getStatusType('取消')).toBe('info')
    expect(wrapper.vm.getStatusType('unknown')).toBe('info')
  })

  it('should navigate to task detail when viewTask is called', async () => {
    // 直接测试viewTask方法的逻辑
    const mockTaskId = 1
    const expectedPath = `/task/${mockTaskId}`
    
    // 清除之前的调用
    mockPush.mockClear()
    
    const wrapper = mount(TaskList)
    await wrapper.vm.viewTask(mockTaskId)
    
    expect(mockPush).toHaveBeenCalledWith(expectedPath)
  })

  it('should update task status', async () => {
    // 模拟API响应
    taskApi.updateStatus.mockResolvedValue()
    taskApi.list.mockResolvedValue([])

    const wrapper = mount(TaskList)
    await wrapper.vm.updateTaskStatus(1, '暂停')

    expect(taskApi.updateStatus).toHaveBeenCalledWith(1, '暂停')
    expect(ElMessage.success).toHaveBeenCalledWith('状态更新成功')
  })

  it('should delete task', async () => {
    // 模拟API响应
    taskApi.delete.mockResolvedValue()
    taskApi.list.mockResolvedValue([])

    const wrapper = mount(TaskList)
    await wrapper.vm.deleteTask(1)

    expect(taskApi.delete).toHaveBeenCalledWith(1)
    expect(ElMessage.success).toHaveBeenCalledWith('删除成功')
  })

  it('should create task', async () => {
    // 模拟API响应
    taskApi.create.mockResolvedValue({ id: 1, name: 'Test Task' })
    taskApi.list.mockResolvedValue([])

    const wrapper = mount(TaskList)
    wrapper.vm.taskForm = {
      name: 'Test Task',
      description: 'Test Description',
      taskDefinitionId: 1,
      type: '导入'
    }

    await wrapper.vm.submitTaskForm()
    expect(taskApi.create).toHaveBeenCalledWith(wrapper.vm.taskForm)
    expect(ElMessage.success).toHaveBeenCalledWith('任务创建成功')
  })

  it('should reset filter', async () => {
    const wrapper = mount(TaskList)
    wrapper.vm.filterForm = {
      status: '已完成',
      type: '导入'
    }

    wrapper.vm.resetFilter()
    expect(wrapper.vm.filterForm.status).toBe('')
    expect(wrapper.vm.filterForm.type).toBe('')
  })

  it('should handle pagination', async () => {
    taskApi.list.mockResolvedValue([])

    const wrapper = mount(TaskList)
    await wrapper.vm.handleSizeChange(20)
    expect(wrapper.vm.pageSize).toBe(20)

    await wrapper.vm.handleCurrentChange(2)
    expect(wrapper.vm.currentPage).toBe(2)
  })
})