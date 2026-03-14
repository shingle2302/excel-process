<template>
  <div class="task-definition-detail-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>任务定义详情</span>
          <el-button type="primary" @click="goBack">
            <el-icon><ArrowLeft /></el-icon>
            返回列表
          </el-button>
        </div>
      </template>
      <div v-if="taskDefinition" class="task-definition-info">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="任务定义ID">{{ taskDefinition.id }}</el-descriptions-item>
          <el-descriptions-item label="任务名称">{{ taskDefinition.name }}</el-descriptions-item>
          <el-descriptions-item label="任务类型">{{ taskDefinition.type }}</el-descriptions-item>
          <el-descriptions-item label="客户端ID">{{ taskDefinition.clientId }}</el-descriptions-item>
          <el-descriptions-item label="回调URL">{{ taskDefinition.callbackUrl || '-' }}</el-descriptions-item>
          <el-descriptions-item label="回调方式">{{ taskDefinition.callbackMethod || '-' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ taskDefinition.createTime }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ taskDefinition.updateTime }}</el-descriptions-item>
          <el-descriptions-item label="任务描述" :span="2">{{ taskDefinition.description || '-' }}</el-descriptions-item>
          <el-descriptions-item label="导出参数" :span="2">
            <pre v-if="taskDefinition.params">{{ JSON.stringify(JSON.parse(taskDefinition.params), null, 2) }}</pre>
            <span v-else>-</span>
          </el-descriptions-item>
        </el-descriptions>
        
        <el-card class="mt-4">
          <template #header>
            <div class="card-header">
              <span>关联的数据列定义</span>
              <el-button type="primary" @click="addColumnDefinition">
                <el-icon><Plus /></el-icon>
                添加数据列定义
              </el-button>
            </div>
          </template>
          <el-table :data="columnDefinitions" style="width: 100%">
            <el-table-column prop="id" label="列定义ID" width="100" />
            <el-table-column prop="fieldName" label="字段名" />
            <el-table-column prop="columnName" label="列名" />
            <el-table-column prop="columnType" label="列类型" width="120" />
            <el-table-column prop="columnFormat" label="列格式" width="120" />
            <el-table-column prop="description" label="列描述" />
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button size="small" type="primary" @click="editColumnDefinition(scope.row)">
                  编辑
                </el-button>
                <el-button size="small" type="danger" @click="deleteColumnDefinition(scope.row.id)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
      <div v-else class="loading-container">
        <el-skeleton :rows="10" animated />
      </div>
    </el-card>
    
    <!-- 添加/编辑数据列定义对话框 -->
    <el-dialog
      v-model="columnDefinitionDialogVisible"
      :title="isEditColumnDefinition ? '编辑数据列定义' : '添加数据列定义'"
      width="500px"
    >
      <el-form :model="columnDefinitionForm" label-width="120px">
        <el-form-item label="字段名">
          <el-input v-model="columnDefinitionForm.fieldName" placeholder="请输入字段名" />
        </el-form-item>
        <el-form-item label="列名">
          <el-input v-model="columnDefinitionForm.columnName" placeholder="请输入列名" />
        </el-form-item>
        <el-form-item label="列类型">
          <el-select v-model="columnDefinitionForm.columnType" placeholder="请选择列类型">
            <el-option label="文本" value="文本" />
            <el-option label="数字" value="数字" />
            <el-option label="日期" value="日期" />
            <el-option label="布尔值" value="布尔值" />
          </el-select>
        </el-form-item>
        <el-form-item label="列格式">
          <el-input v-model="columnDefinitionForm.columnFormat" placeholder="请输入列格式" />
        </el-form-item>
        <el-form-item label="列描述">
          <el-input
            v-model="columnDefinitionForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入列描述"
          />
        </el-form-item>
        <el-form-item label="默认值">
          <el-input v-model="columnDefinitionForm.defaultValue" placeholder="请输入默认值" />
        </el-form-item>
        <el-form-item label="校验规则">
          <el-input
            v-model="columnDefinitionForm.validationRules"
            type="textarea"
            :rows="2"
            placeholder="请输入JSON格式的校验规则"
          />
        </el-form-item>
        <el-form-item label="映射规则">
          <el-input
            v-model="columnDefinitionForm.mappingRules"
            type="textarea"
            :rows="2"
            placeholder="请输入JSON格式的映射规则"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="columnDefinitionDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitColumnDefinitionForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, Plus } from '@element-plus/icons-vue'
import { taskDefinitionApi, columnDefinitionApi } from '../services/api'

const route = useRoute()
const router = useRouter()
const taskDefinition = ref(null)
const columnDefinitions = ref([])
const taskDefinitionId = ref(route.params.id)
const columnDefinitionDialogVisible = ref(false)
const isEditColumnDefinition = ref(false)
const columnDefinitionForm = ref({
  fieldName: '',
  columnName: '',
  columnType: '文本',
  columnFormat: '',
  description: '',
  defaultValue: '',
  validationRules: '{}',
  mappingRules: '{}'
})

const loadTaskDefinition = async () => {
  try {
    const result = await taskDefinitionApi.get(taskDefinitionId.value)
    taskDefinition.value = result
  } catch (error) {
    console.error('加载任务定义详情失败:', error)
    ElMessage.error('加载任务定义详情失败')
  }
}

const loadColumnDefinitions = async () => {
  try {
    const result = await columnDefinitionApi.getByTaskDefinitionId(taskDefinitionId.value)
    columnDefinitions.value = result
  } catch (error) {
    console.error('加载数据列定义失败:', error)
  }
}

const goBack = () => {
  router.push('/task-definitions')
}

const addColumnDefinition = () => {
  isEditColumnDefinition.value = false
  columnDefinitionForm.value = {
    fieldName: '',
    columnName: '',
    columnType: '文本',
    columnFormat: '',
    description: '',
    defaultValue: '',
    validationRules: '{}',
    mappingRules: '{}'
  }
  columnDefinitionDialogVisible.value = true
}

const editColumnDefinition = (columnDefinition) => {
  isEditColumnDefinition.value = true
  columnDefinitionForm.value = {
    ...columnDefinition
  }
  columnDefinitionDialogVisible.value = true
}

const deleteColumnDefinition = async (columnDefinitionId) => {
  try {
    await columnDefinitionApi.delete(columnDefinitionId)
    ElMessage.success('删除成功')
    loadColumnDefinitions()
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败')
  }
}

const submitColumnDefinitionForm = async () => {
  try {
    if (isEditColumnDefinition.value) {
      await columnDefinitionApi.update(columnDefinitionForm.value.id, columnDefinitionForm.value)
      ElMessage.success('编辑成功')
    } else {
      const formData = {
        ...columnDefinitionForm.value,
        taskDefinitionId: taskDefinitionId.value
      }
      await columnDefinitionApi.create(formData)
      ElMessage.success('创建成功')
    }
    columnDefinitionDialogVisible.value = false
    loadColumnDefinitions()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadTaskDefinition()
  loadColumnDefinitions()
})
</script>

<style scoped>
.task-definition-detail-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.task-definition-info {
  margin-top: 20px;
}

.mt-4 {
  margin-top: 20px;
}

.loading-container {
  margin-top: 20px;
}

pre {
  white-space: pre-wrap;
  word-break: break-all;
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  font-family: 'Courier New', Courier, monospace;
  font-size: 14px;
  line-height: 1.5;
}
</style>