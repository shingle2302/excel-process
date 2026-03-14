import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import ExcelImport from '../../views/ExcelImport.vue'
import { taskDefinitionApi, excelApi } from '../../services/api'
import { ElMessage } from 'element-plus'

// 模拟图标
vi.mock('@element-plus/icons-vue', () => ({
  Upload: { template: '<div>Upload</div>' }
}))

describe('ExcelImport组件测试', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render ExcelImport component', () => {
    const wrapper = mount(ExcelImport)
    expect(wrapper.exists()).toBe(true)
  })

  it('should load task definitions on mount', async () => {
    // 模拟API响应
    taskDefinitionApi.list.mockResolvedValue([{ id: 1, name: 'Definition 1' }])

    const wrapper = mount(ExcelImport)
    await wrapper.vm.$nextTick()

    expect(taskDefinitionApi.list).toHaveBeenCalled()
  })

  it('should handle file change', async () => {
    const wrapper = mount(ExcelImport)
    const mockFile = new File(['test'], 'test.xlsx', { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })

    wrapper.vm.handleFileChange({ raw: mockFile })
    expect(wrapper.vm.form.file).toBe(mockFile)
  })

  it('should validate form before import', async () => {
    const wrapper = mount(ExcelImport)

    // 测试缺少任务定义
    await wrapper.vm.handleImport()
    expect(ElMessage.error).toHaveBeenCalledWith('请选择任务定义')

    // 测试缺少文件
    wrapper.vm.form.taskDefinitionId = 1
    await wrapper.vm.handleImport()
    expect(ElMessage.error).toHaveBeenCalledWith('请选择Excel文件')
  })

  it('should handle import success', async () => {
    // 模拟API响应
    excelApi.import.mockResolvedValue({ success: true, data: [{ id: 1, name: 'Test' }], count: 1 })

    const wrapper = mount(ExcelImport)
    wrapper.vm.form.taskDefinitionId = 1
    wrapper.vm.form.file = new File(['test'], 'test.xlsx')

    await wrapper.vm.handleImport()
    expect(excelApi.import).toHaveBeenCalled()
    expect(ElMessage.success).toHaveBeenCalledWith('导入成功')
    expect(wrapper.vm.importResult).toEqual({
      success: true,
      data: [{ id: 1, name: 'Test' }],
      count: 1,
      columns: [{ fieldName: 'id', columnName: 'id' }, { fieldName: 'name', columnName: 'name' }]
    })
  })

  it('should handle import failure', async () => {
    // 模拟API错误
    excelApi.import.mockRejectedValue(new Error('Import failed'))

    const wrapper = mount(ExcelImport)
    wrapper.vm.form.taskDefinitionId = 1
    wrapper.vm.form.file = new File(['test'], 'test.xlsx')

    await wrapper.vm.handleImport()
    expect(excelApi.import).toHaveBeenCalled()
    expect(ElMessage.error).toHaveBeenCalledWith('导入失败')
    expect(wrapper.vm.importResult).toEqual({
      success: false,
      error: 'Import failed'
    })
  })

  it('should reset form', async () => {
    const wrapper = mount(ExcelImport)
    wrapper.vm.form.taskDefinitionId = 1
    wrapper.vm.form.file = new File(['test'], 'test.xlsx')
    wrapper.vm.importResult = { success: true, data: [] }

    wrapper.vm.resetForm()
    expect(wrapper.vm.form.taskDefinitionId).toBe(null)
    expect(wrapper.vm.form.file).toBe(null)
    expect(wrapper.vm.importResult).toBe(null)
  })
})