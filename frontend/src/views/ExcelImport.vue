<template>
  <div class="excel-import-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>Excel导入</span>
        </div>
      </template>
      <div class="import-form">
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
          <el-form-item label="Excel文件">
            <el-upload
              class="upload-demo"
              drag
              action=""
              :auto-upload="false"
              :on-change="handleFileChange"
              :show-file-list="true"
              accept=".xlsx,.xls,.csv"
            >
              <el-icon class="el-icon--upload"><Upload /></el-icon>
              <div class="el-upload__text">
                拖放文件到此处，或 <em>点击上传</em>
              </div>
              <template #tip>
                <div class="el-upload__tip">
                  请上传Excel文件（.xlsx, .xls, .csv），单个文件大小不超过100MB
                </div>
              </template>
            </el-upload>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleImport" :loading="loading">
              开始导入
            </el-button>
            <el-button @click="resetForm">
              重置
            </el-button>
          </el-form-item>
        </el-form>
        
        <el-card v-if="importResult" class="mt-4">
          <template #header>
            <div class="card-header">
              <span>导入结果</span>
            </div>
          </template>
          <div v-if="importResult.success" class="success-result">
            <el-alert
              title="导入成功"
              type="success"
              show-icon
            />
            <el-table :data="importResult.data" style="width: 100%; margin-top: 20px">
              <el-table-column 
                v-for="(column, index) in importResult.columns" 
                :key="index"
                :prop="column.fieldName"
                :label="column.columnName"
              />
            </el-table>
            <div class="result-info">
              共导入 {{ importResult.count }} 条数据
            </div>
          </div>
          <div v-else class="error-result">
            <el-alert
              title="导入失败"
              type="error"
              show-icon
            />
            <div class="error-message">
              {{ importResult.error }}
            </div>
          </div>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Upload } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { taskDefinitionApi, excelApi } from '../services/api'

const form = ref({
  taskDefinitionId: null,
  file: null
})
const loading = ref(false)
const taskDefinitions = ref([])
const importResult = ref(null)

const handleFileChange = (file) => {
  form.value.file = file.raw
}

const handleImport = async () => {
  if (!form.value.taskDefinitionId) {
    ElMessage.error('请选择任务定义')
    return
  }
  if (!form.value.file) {
    ElMessage.error('请选择Excel文件')
    return
  }
  
  loading.value = true
  
  try {
    const formData = new FormData()
    formData.append('file', form.value.file)
    formData.append('taskDefinitionId', form.value.taskDefinitionId)
    
    const result = await excelApi.import(formData)
    importResult.value = result
    
    // 提取列信息
    if (result.success && result.data && result.data.length > 0) {
      const firstRow = result.data[0]
      result.columns = Object.keys(firstRow).map(key => ({
        fieldName: key,
        columnName: key
      }))
    }
    
    ElMessage.success('导入成功')
  } catch (error) {
    console.error('导入失败:', error)
    importResult.value = {
      success: false,
      error: error.message || '导入失败，请重试'
    }
    ElMessage.error('导入失败')
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.value = {
    taskDefinitionId: null,
    file: null
  }
  importResult.value = null
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
.excel-import-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.import-form {
  margin-top: 20px;
}

.mt-4 {
  margin-top: 20px;
}

.success-result {
  margin-top: 20px;
}

.error-result {
  margin-top: 20px;
}

.error-message {
  margin-top: 10px;
  color: #f56c6c;
}

.result-info {
  margin-top: 10px;
  font-size: 14px;
  color: #606266;
}
</style>