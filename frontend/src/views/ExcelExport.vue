<template>
  <div class="excel-export-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>Excel导出</span>
        </div>
      </template>
      <div class="export-form">
        <el-form :model="form" label-width="120px">
          <el-form-item label="任务定义">
            <el-select v-model="form.taskDefinitionId" placeholder="请选择任务定义">
              <el-option
                v-for="taskDef in taskDefinitions"
                :key="taskDef.id"
                :label="taskDef.name"
                :value="taskDef.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="导出数据">
            <el-input
              v-model="form.dataJson"
              type="textarea"
              :rows="10"
              placeholder="请输入JSON格式的数据"
            />
            <div class="form-tip">
              示例: [{"id": 1, "username": "测试", "email":"zhangsan@test.com","createTime": "2025-03-15 13:31:00"}]
            </div>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleExport" :loading="loading">
              开始导出
            </el-button>
            <el-button @click="resetForm">
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { taskDefinitionApi, excelApi } from '../services/api'

const form = ref({
  taskDefinitionId: null,
  dataJson: ''
})
const loading = ref(false)
const taskDefinitions = ref([])

const handleExport = async () => {
  if (!form.value.taskDefinitionId) {
    ElMessage.error('请选择任务定义')
    return
  }
  if (!form.value.dataJson) {
    ElMessage.error('请输入导出数据')
    return
  }
  
  try {
    // 验证JSON格式
    JSON.parse(form.value.dataJson)
  } catch (error) {
    ElMessage.error('数据格式错误，请输入有效的JSON')
    return
  }
  
  loading.value = true
  
  try {
    const response = await excelApi.export(form.value.dataJson, form.value.taskDefinitionId)
    
    // 创建下载链接
    const url = window.URL.createObjectURL(new Blob([response]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `export_${new Date().getTime()}.xlsx`)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败，请重试')
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.value = {
    taskDefinitionId: null,
    dataJson: ''
  }
}

const loadTaskDefinitions = async () => {
  try {
    const result = await taskDefinitionApi.list()
    taskDefinitions.value = result
  } catch (error) {
    console.error('加载任务定义失败:', error)
  }
}

onMounted(() => {
  loadTaskDefinitions()
})
</script>

<style scoped>
.excel-export-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.export-form {
  margin-top: 20px;
}

.form-tip {
  margin-top: 5px;
  font-size: 12px;
  color: #909399;
}
</style>