// 测试环境配置
import { vi } from 'vitest'

// 模拟Element Plus的ElMessage
vi.mock('element-plus', () => ({
  ElMessage: {
    success: vi.fn(),
    error: vi.fn(),
    info: vi.fn(),
    warning: vi.fn()
  }
}))

// 模拟路由
vi.mock('vue-router', () => ({
  useRouter: () => ({
    push: vi.fn(),
    replace: vi.fn(),
    back: vi.fn()
  }),
  useRoute: () => ({
    params: {
      id: '1'
    },
    path: '/test'
  })
}))

// 模拟API服务
vi.mock('../services/api.js', () => ({
  default: {
    defaults: {
      baseURL: '/api',
      timeout: 10000
    },
    interceptors: {
      request: { use: vi.fn() },
      response: { use: vi.fn() }
    }
  },
  taskApi: {
    create: vi.fn().mockResolvedValue({ id: 1, name: '测试任务' }),
    list: vi.fn().mockResolvedValue([]),
    get: vi.fn().mockResolvedValue({ id: 1, name: '测试任务' }),
    updateStatus: vi.fn().mockResolvedValue(),
    updateProgress: vi.fn().mockResolvedValue(),
    delete: vi.fn().mockResolvedValue()
  },
  taskDefinitionApi: {
    create: vi.fn().mockResolvedValue({ id: 1, name: '测试任务定义' }),
    list: vi.fn().mockResolvedValue([]),
    get: vi.fn().mockResolvedValue({ id: 1, name: '测试任务定义' }),
    update: vi.fn().mockResolvedValue(),
    delete: vi.fn().mockResolvedValue(),
    getByName: vi.fn().mockResolvedValue({ id: 1, name: '测试任务定义' }),
    getByClientId: vi.fn().mockResolvedValue([])
  },
  clientApi: {
    create: vi.fn().mockResolvedValue({ id: 1, clientId: 'test-client' }),
    list: vi.fn().mockResolvedValue([]),
    get: vi.fn().mockResolvedValue({ id: 1, clientId: 'test-client' }),
    update: vi.fn().mockResolvedValue(),
    delete: vi.fn().mockResolvedValue(),
    getByClientId: vi.fn().mockResolvedValue({ id: 1, clientId: 'test-client' }),
    updateStatus: vi.fn().mockResolvedValue()
  },
  columnDefinitionApi: {
    create: vi.fn().mockResolvedValue({ id: 1, fieldName: 'test' }),
    list: vi.fn().mockResolvedValue([]),
    get: vi.fn().mockResolvedValue({ id: 1, fieldName: 'test' }),
    update: vi.fn().mockResolvedValue(),
    delete: vi.fn().mockResolvedValue(),
    getByTaskDefinitionId: vi.fn().mockResolvedValue([]),
    deleteByTaskDefinitionId: vi.fn().mockResolvedValue()
  },
  excelApi: {
    import: vi.fn().mockResolvedValue({ success: true, data: [], count: 0 }),
    export: vi.fn().mockResolvedValue(new Blob())
  }
}))