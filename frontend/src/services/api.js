import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 从localStorage获取API Key
    const apiKey = localStorage.getItem('apiKey') || 'test-client'
    if (apiKey) {
      config.headers['X-API-Key'] = apiKey
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    console.error('API请求错误:', error)
    return Promise.reject(error)
  }
)

// 任务相关API
export const taskApi = {
  create: (task) => api.post('/tasks', task),
  list: (status) => api.get('/tasks', { params: { status } }),
  get: (id) => api.get(`/tasks/${id}`),
  updateStatus: (id, status) => api.put(`/tasks/${id}/status`, null, { params: { status } }),
  updateProgress: (id, progress) => api.put(`/tasks/${id}/progress`, null, { params: { progress } }),
  delete: (id) => api.delete(`/tasks/${id}`)
}

// 任务定义相关API
export const taskDefinitionApi = {
  create: (taskDefinition) => api.post('/task-definitions', taskDefinition),
  list: () => api.get('/task-definitions'),
  get: (id) => api.get(`/task-definitions/${id}`),
  update: (id, taskDefinition) => api.put(`/task-definitions/${id}`, taskDefinition),
  delete: (id) => api.delete(`/task-definitions/${id}`),
  getByName: (name) => api.get(`/task-definitions/name/${name}`),
  getByClientId: (clientId) => api.get(`/task-definitions/client/${clientId}`)
}

// 客户端相关API
export const clientApi = {
  create: (client) => api.post('/clients', client),
  list: () => api.get('/clients'),
  get: (id) => api.get(`/clients/${id}`),
  update: (id, client) => api.put(`/clients/${id}`, client),
  delete: (id) => api.delete(`/clients/${id}`),
  getByClientId: (clientId) => api.get(`/clients/client-id/${clientId}`),
  updateStatus: (clientId, status) => api.put(`/clients/client-id/${clientId}/status`, null, { params: { status } })
}

// 数据列定义相关API
export const columnDefinitionApi = {
  create: (columnDefinition) => api.post('/column-definitions', columnDefinition),
  list: () => api.get('/column-definitions'),
  get: (id) => api.get(`/column-definitions/${id}`),
  update: (id, columnDefinition) => api.put(`/column-definitions/${id}`, columnDefinition),
  delete: (id) => api.delete(`/column-definitions/${id}`),
  getByTaskDefinitionId: (taskDefinitionId) => api.get(`/column-definitions/task-definition/${taskDefinitionId}`),
  deleteByTaskDefinitionId: (taskDefinitionId) => api.delete(`/column-definitions/task-definition/${taskDefinitionId}`)
}

// Excel处理相关API
export const excelApi = {
  import: (formData) => api.post('/excel/import', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  }),
  export: (data, taskDefinitionId) => api.post('/excel/export', null, {
    params: { data, taskDefinitionId },
    responseType: 'blob'
  })
}

export default api