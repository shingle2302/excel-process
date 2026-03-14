import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import ExcelExport from '../../views/ExcelExport.vue'
import { taskDefinitionApi, excelApi } from '../../services/api'
import { ElMessage } from 'element-plus'

describe('ExcelExport组件测试', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    // 模拟DOM方法，确保所有测试用例都能访问
    global.document.body.insertBefore = vi.fn()
  })

  it('should render ExcelExport component', () => {
    const wrapper = mount(ExcelExport)
    expect(wrapper.exists()).toBe(true)
  })

  it('should load task definitions on mount', async () => {
    // 模拟API响应
    taskDefinitionApi.list.mockResolvedValue([{ id: 1, name: 'Definition 1' }])

    const wrapper = mount(ExcelExport)
    await wrapper.vm.$nextTick()

    expect(taskDefinitionApi.list).toHaveBeenCalled()
  })

  it('should validate form before export', async () => {
    const wrapper = mount(ExcelExport)

    // 测试缺少任务定义
    await wrapper.vm.handleExport()
    expect(ElMessage.error).toHaveBeenCalledWith('请选择任务定义')

    // 测试缺少数据
    wrapper.vm.form.taskDefinitionId = 1
    await wrapper.vm.handleExport()
    expect(ElMessage.error).toHaveBeenCalledWith('请输入导出数据')

    // 测试无效JSON
    wrapper.vm.form.dataJson = 'invalid json'
    await wrapper.vm.handleExport()
    expect(ElMessage.error).toHaveBeenCalledWith('数据格式错误，请输入有效的JSON')
  })

  it('should handle export success', async () => {
    // 模拟API响应
    const mockBlob = new Blob(['test'], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    excelApi.export = vi.fn().mockResolvedValue(mockBlob)

    // 模拟DOM方法
    const mockCreateObjectURL = vi.fn().mockReturnValue('blob:url')
    const mockAppendChild = vi.fn()
    const mockRemoveChild = vi.fn()

    global.URL.createObjectURL = mockCreateObjectURL
    global.document.body.appendChild = mockAppendChild
    global.document.body.removeChild = mockRemoveChild

    const wrapper = mount(ExcelExport)
    wrapper.vm.form.taskDefinitionId = 1
    wrapper.vm.form.dataJson = '[{"id": 1, "name": "Test"}]'

    await wrapper.vm.handleExport()
    expect(excelApi.export).toHaveBeenCalledWith('[{"id": 1, "name": "Test"}]', 1)
  })

  it('should handle export failure', async () => {
    // 模拟API错误
    excelApi.export = vi.fn().mockRejectedValue(new Error('Export failed'))

    const wrapper = mount(ExcelExport)
    wrapper.vm.form.taskDefinitionId = 1
    wrapper.vm.form.dataJson = '[{"id": 1, "name": "Test"}]'

    await wrapper.vm.handleExport()
    expect(excelApi.export).toHaveBeenCalled()
  })

  it('should reset form', async () => {
    const wrapper = mount(ExcelExport)
    wrapper.vm.form.taskDefinitionId = 1
    wrapper.vm.form.dataJson = '[{"id": 1, "name": "Test"}]'

    wrapper.vm.resetForm()
    expect(wrapper.vm.form.taskDefinitionId).toBe(null)
    expect(wrapper.vm.form.dataJson).toBe('')
  })
})