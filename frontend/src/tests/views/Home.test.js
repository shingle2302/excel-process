import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import Home from '../../views/Home.vue'
import { taskApi, taskDefinitionApi, clientApi, columnDefinitionApi } from '../../services/api'

// 模拟Element Plus组件
vi.mock('element-plus', () => ({
  ElCard: { template: '<div><slot></slot></div>' },
  ElRow: { template: '<div><slot></slot></div>' },
  ElCol: { template: '<div><slot></slot></div>' },
  ElTable: { template: '<div><slot></slot></div>' },
  ElTableColumn: { template: '<div><slot></slot></div>' },
  ElProgress: { template: '<div></div>' },
  ElButton: { template: '<button><slot></slot></button>' }
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

describe('Home组件测试', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render Home component', () => {
    const wrapper = mount(Home)
    expect(wrapper.exists()).toBe(true)
  })

  it('should load stats on mount', async () => {
    // 模拟API响应
    taskApi.list = vi.fn().mockResolvedValue([{ id: 1, name: 'Task 1' }])
    taskDefinitionApi.list = vi.fn().mockResolvedValue([{ id: 1, name: 'Definition 1' }])
    clientApi.list = vi.fn().mockResolvedValue([{ id: 1, clientId: 'client1' }])
    columnDefinitionApi.list = vi.fn().mockResolvedValue([{ id: 1, fieldName: 'field1' }])

    const wrapper = mount(Home)
    // 等待loadStats方法执行
    await new Promise(resolve => setTimeout(resolve, 100))

    expect(taskApi.list).toHaveBeenCalled()
  })

  it('should handle API errors', async () => {
    // 模拟所有API错误
    taskApi.list = vi.fn().mockRejectedValue(new Error('API Error'))
    taskDefinitionApi.list = vi.fn().mockRejectedValue(new Error('API Error'))
    clientApi.list = vi.fn().mockRejectedValue(new Error('API Error'))
    columnDefinitionApi.list = vi.fn().mockRejectedValue(new Error('API Error'))

    const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {})

    const wrapper = mount(Home)
    // 等待loadStats方法执行
    await new Promise(resolve => setTimeout(resolve, 100))

    expect(taskApi.list).toHaveBeenCalled()
    expect(consoleSpy).toHaveBeenCalled()

    consoleSpy.mockRestore()
  })

  it('should navigate to task detail when viewTask is called', async () => {
    // 清除之前的调用
    mockPush.mockClear()

    const wrapper = mount(Home)
    await wrapper.vm.viewTask(1)

    expect(mockPush).toHaveBeenCalledWith('/task/1')
  })
})