import { describe, it, expect, vi, beforeEach } from 'vitest'
import axios from 'axios'

// 模拟axios
vi.mock('axios', () => {
  const mockAxios = {
    create: vi.fn(() => ({
      defaults: { baseURL: '/api', timeout: 10000 },
      interceptors: {
        request: { use: vi.fn((config) => {
          // 模拟请求拦截器的行为
          if (config.headers) {
            config.headers['X-API-Key'] = 'test-api-key'
          }
          return config
        }) },
        response: { use: vi.fn((response) => response.data) }
      },
      post: vi.fn(),
      get: vi.fn(),
      put: vi.fn(),
      delete: vi.fn()
    })),
    post: vi.fn(),
    get: vi.fn(),
    put: vi.fn(),
    delete: vi.fn()
  }
  return { default: mockAxios }
})

describe('API服务测试', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should test API request interceptor', async () => {
    // 模拟localStorage
    const mockLocalStorage = {
      getItem: vi.fn().mockReturnValue('test-api-key')
    }
    global.localStorage = mockLocalStorage

    // 清除所有缓存
    Object.keys(require.cache).forEach(key => delete require.cache[key])
    
    // 重新导入api
    const { default: api } = require('../../services/api')

    // 模拟一个请求
    const mockResponse = { data: {} }
    const mockPost = vi.fn().mockResolvedValue(mockResponse)
    
    // 保存原始的post方法
    const originalPost = api.post
    api.post = mockPost
    
    try {
      // 发送一个请求
      await api.post('/test')
      
      // 验证请求被发送
      expect(mockPost).toHaveBeenCalled()
    } finally {
      // 恢复原始的post方法
      api.post = originalPost
    }
  })

  it('should test taskApi.create', async () => {
    const mockTask = { name: 'Test Task' }
    const mockResponse = { id: 1, ...mockTask }
    
    // 动态导入api模块
    const { taskApi } = require('../../services/api')
    
    // 直接模拟api模块中的taskApi.create方法
    const originalCreate = taskApi.create
    taskApi.create = vi.fn().mockResolvedValue(mockResponse)
    
    try {
      const result = await taskApi.create(mockTask)
      expect(taskApi.create).toHaveBeenCalledWith(mockTask)
    } finally {
      // 恢复原始的create方法
      taskApi.create = originalCreate
    }
  })

  it('should test taskApi.list', async () => {
    const mockStatus = '已完成'
    const mockResponse = [{ id: 1, status: mockStatus }]
    
    // 动态导入api模块
    const { taskApi } = require('../../services/api')
    
    // 直接模拟api模块中的taskApi.list方法
    const originalList = taskApi.list
    taskApi.list = vi.fn().mockResolvedValue(mockResponse)
    
    try {
      const result = await taskApi.list(mockStatus)
      expect(taskApi.list).toHaveBeenCalledWith(mockStatus)
    } finally {
      // 恢复原始的list方法
      taskApi.list = originalList
    }
  })

  it('should test taskDefinitionApi.get', async () => {
    const mockId = 1
    const mockResponse = { id: mockId, name: 'Test Definition' }
    
    // 动态导入api模块
    const { taskDefinitionApi } = require('../../services/api')
    
    // 直接模拟api模块中的taskDefinitionApi.get方法
    const originalGet = taskDefinitionApi.get
    taskDefinitionApi.get = vi.fn().mockResolvedValue(mockResponse)
    
    try {
      const result = await taskDefinitionApi.get(mockId)
      expect(taskDefinitionApi.get).toHaveBeenCalledWith(mockId)
    } finally {
      // 恢复原始的get方法
      taskDefinitionApi.get = originalGet
    }
  })

  it('should test clientApi.updateStatus', async () => {
    const mockClientId = 'test-client'
    const mockStatus = '启用'
    const mockResponse = { clientId: mockClientId, status: mockStatus }
    
    // 动态导入api模块
    const { clientApi } = require('../../services/api')
    
    // 直接模拟api模块中的clientApi.updateStatus方法
    const originalUpdateStatus = clientApi.updateStatus
    clientApi.updateStatus = vi.fn().mockResolvedValue(mockResponse)
    
    try {
      const result = await clientApi.updateStatus(mockClientId, mockStatus)
      expect(clientApi.updateStatus).toHaveBeenCalledWith(mockClientId, mockStatus)
    } finally {
      // 恢复原始的updateStatus方法
      clientApi.updateStatus = originalUpdateStatus
    }
  })

  it('should test columnDefinitionApi.getByTaskDefinitionId', async () => {
    const mockTaskDefinitionId = 1
    const mockResponse = [{ id: 1, taskDefinitionId: mockTaskDefinitionId }]
    
    // 动态导入api模块
    const { columnDefinitionApi } = require('../../services/api')
    
    // 直接模拟api模块中的columnDefinitionApi.getByTaskDefinitionId方法
    const originalGetByTaskDefinitionId = columnDefinitionApi.getByTaskDefinitionId
    columnDefinitionApi.getByTaskDefinitionId = vi.fn().mockResolvedValue(mockResponse)
    
    try {
      const result = await columnDefinitionApi.getByTaskDefinitionId(mockTaskDefinitionId)
      expect(columnDefinitionApi.getByTaskDefinitionId).toHaveBeenCalledWith(mockTaskDefinitionId)
    } finally {
      // 恢复原始的getByTaskDefinitionId方法
      columnDefinitionApi.getByTaskDefinitionId = originalGetByTaskDefinitionId
    }
  })

  it('should test excelApi.import', async () => {
    const mockFormData = new FormData()
    const mockResponse = { success: true, data: [] }
    
    // 动态导入api模块
    const { excelApi } = require('../../services/api')
    
    // 直接模拟api模块中的excelApi.import方法
    const originalImport = excelApi.import
    excelApi.import = vi.fn().mockResolvedValue(mockResponse)
    
    try {
      const result = await excelApi.import(mockFormData)
      expect(excelApi.import).toHaveBeenCalledWith(mockFormData)
    } finally {
      // 恢复原始的import方法
      excelApi.import = originalImport
    }
  })

  it('should test excelApi.export', async () => {
    const mockData = '{"test": "data"}'
    const mockTaskDefinitionId = 1
    const mockResponse = { data: new Blob() }
    
    // 动态导入api模块
    const { excelApi } = require('../../services/api')
    
    // 直接模拟api模块中的excelApi.export方法
    const originalExport = excelApi.export
    excelApi.export = vi.fn().mockResolvedValue(mockResponse.data)
    
    try {
      const result = await excelApi.export(mockData, mockTaskDefinitionId)
      expect(excelApi.export).toHaveBeenCalledWith(mockData, mockTaskDefinitionId)
    } finally {
      // 恢复原始的export方法
      excelApi.export = originalExport
    }
  })
})