import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import Login from '../../views/Login.vue'
import { authApi } from '../../services/api'

const mockReplace = vi.fn()

vi.mock('vue-router', () => ({
  useRouter: () => ({
    replace: mockReplace
  }),
  useRoute: () => ({
    query: {}
  })
}))

describe('Login组件测试', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
  })

  it('should render Login component', () => {
    const wrapper = mount(Login)
    expect(wrapper.exists()).toBe(true)
  })

  it('should login successfully', async () => {
    authApi.userLogin.mockResolvedValue({ apiKey: 'admin', clientName: '系统用户', expiresIn: 7200 })
    const wrapper = mount(Login)

    wrapper.vm.formRef = { validate: vi.fn().mockResolvedValue(true) }
    await wrapper.vm.handleLogin()

    expect(authApi.userLogin).toHaveBeenCalled()
    expect(localStorage.getItem('apiKey')).toBe('admin')
    expect(mockReplace).toHaveBeenCalledWith('/')
  })
})
