import axios from 'axios'
import { clearAuth } from '../utils/auth.js'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

api.interceptors.request.use(
  config => {
    const apiKey = localStorage.getItem('apiKey')
    if (apiKey) {
      config.headers['X-API-Key'] = apiKey
    }
    return config
  },
  error => Promise.reject(error)
)

api.interceptors.response.use(
  response => response.data,
  error => {
    const status = error?.response?.status
    if (status === 401 && typeof window !== 'undefined' && !window.location.pathname.startsWith('/login')) {
      clearAuth()
      window.location.href = '/login'
    }
    console.error('API请求错误:', error)
    return Promise.reject(error)
  }
)

export const authApi = {
  userLogin: (payload) => api.post('/auth/user-login', payload),
  login: (payload) => api.post('/auth/login', payload)
}

export const taskApi = {
  create: (task) => api.post('/tasks', task),
  externalCreate: (payload) => api.post('/tasks/external', payload),
  list: (status) => api.get('/tasks', { params: { status } }),
  get: (id) => api.get(`/tasks/${id}`),
  updateStatus: (id, status) => api.put(`/tasks/${id}/status`, null, { params: { status } }),
  updateProgress: (id, progress) => api.put(`/tasks/${id}/progress`, null, { params: { progress } }),
  delete: (id) => api.delete(`/tasks/${id}`)
}

export const taskDefinitionApi = {
  create: (taskDefinition) => api.post('/task-definitions', taskDefinition),
  list: () => api.get('/task-definitions'),
  get: (id) => api.get(`/task-definitions/${id}`),
  update: (id, taskDefinition) => api.put(`/task-definitions/${id}`, taskDefinition),
  delete: (id) => api.delete(`/task-definitions/${id}`),
  getByName: (name) => api.get(`/task-definitions/name/${name}`),
  getByClientId: (clientId) => api.get(`/task-definitions/client/${clientId}`)
}

export const clientApi = {
  create: (client) => api.post('/clients', client),
  list: () => api.get('/clients'),
  get: (id) => api.get(`/clients/${id}`),
  update: (id, client) => api.put(`/clients/${id}`, client),
  delete: (id) => api.delete(`/clients/${id}`),
  getByClientId: (clientId) => api.get(`/clients/client-id/${clientId}`),
  updateStatus: (clientId, status) => api.put(`/clients/client-id/${clientId}/status`, null, { params: { status } })
}

export const dataSourceApi = {
  create: (dataSource) => api.post('/data-sources', dataSource),
  testConnection: (dataSource) => api.post('/data-sources/test-connection', dataSource),
  list: () => api.get('/data-sources'),
  get: (id) => api.get(`/data-sources/${id}`),
  update: (id, dataSource) => api.put(`/data-sources/${id}`, dataSource),
  delete: (id) => api.delete(`/data-sources/${id}`)
}

export const columnDefinitionApi = {
  create: (columnDefinition) => api.post('/column-definitions', columnDefinition),
  list: () => api.get('/column-definitions'),
  get: (id) => api.get(`/column-definitions/${id}`),
  update: (id, columnDefinition) => api.put(`/column-definitions/${id}`, columnDefinition),
  delete: (id) => api.delete(`/column-definitions/${id}`),
  getByTaskDefinitionId: (taskDefinitionId) => api.get(`/column-definitions/task-definition/${taskDefinitionId}`),
  deleteByTaskDefinitionId: (taskDefinitionId) => api.delete(`/column-definitions/task-definition/${taskDefinitionId}`)
}

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
