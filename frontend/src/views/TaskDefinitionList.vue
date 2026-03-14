<template>
  <div class="task-definition-list-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>任务定义管理</span>
          <el-button type="primary" @click="createTaskDefinition">
            <el-icon><Plus /></el-icon>
            新建任务定义
          </el-button>
        </div>
      </template>
      <el-table :data="taskDefinitions" style="width: 100%">
        <el-table-column prop="id" label="任务定义ID" width="120" />
        <el-table-column prop="name" label="任务名称" />
        <el-table-column prop="description" label="任务描述" />
        <el-table-column prop="type" label="任务类型" width="100" />
        <el-table-column prop="clientId" label="客户端ID" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" type="primary" @click="viewTaskDefinition(scope.row.id)">
              查看
            </el-button>
            <el-button size="small" type="warning" @click="editTaskDefinition(scope.row)">
              编辑
            </el-button>
            <el-button size="small" type="danger" @click="deleteTaskDefinition(scope.row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 新建/编辑任务定义对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑任务定义' : '新建任务定义'"
      width="600px"
    >
      <el-form :model="taskDefinitionForm" label-width="120px">
        <el-form-item label="任务名称">
          <el-input v-model="taskDefinitionForm.name" placeholder="请输入任务名称" />
        </el-form-item>
        <el-form-item label="任务描述">
          <el-input
            v-model="taskDefinitionForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入任务描述"
          />
        </el-form-item>
        <el-form-item label="任务类型">
          <el-select v-model="taskDefinitionForm.type" placeholder="请选择任务类型">
            <el-option label="导入" value="导入" />
            <el-option label="导出" value="导出" />
          </el-select>
        </el-form-item>
        <el-form-item label="客户端ID">
          <el-input v-model="taskDefinitionForm.clientId" placeholder="请输入客户端ID" />
        </el-form-item>
        <el-form-item label="导出参数">
          <el-input
            v-model="taskDefinitionForm.params"
            type="textarea"
            :rows="3"
            placeholder="请输入JSON格式的导出参数"
          />
        </el-form-item>
        <el-form-item label="回调URL">
          <el-input v-model="taskDefinitionForm.callbackUrl" placeholder="请输入回调URL" />
        </el-form-item>
        <el-form-item label="回调方式">
          <el-select v-model="taskDefinitionForm.callbackMethod" placeholder="请选择回调方式">
            <el-option label="GET" value="GET" />
            <el-option label="POST" value="POST" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitTaskDefinitionForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import { taskDefinitionApi } from '../services/api'

const router = useRouter()
const taskDefinitions = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const taskDefinitionForm = ref({
  name: '',
  description: '',
  type: '导入',
  clientId: '',
  params: '{}',
  callbackUrl: '',
  callbackMethod: 'POST'
})

const loadTaskDefinitions = async () => {
  try {
    const result = await taskDefinitionApi.list()
    taskDefinitions.value = result
    total.value = result.length
  } catch (error) {
    console.error('加载任务定义列表失败:', error)
  }
}

const viewTaskDefinition = (taskDefinitionId) => {
  router.push(`/task-definition/${taskDefinitionId}`)
}

const editTaskDefinition = (taskDefinition) => {
  isEdit.value = true
  taskDefinitionForm.value = {
    ...taskDefinition
  }
  dialogVisible.value = true
}

const deleteTaskDefinition = async (taskDefinitionId) => {
  try {
    await taskDefinitionApi.delete(taskDefinitionId)
    ElMessage.success('删除成功')
    loadTaskDefinitions()
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败')
  }
}

const createTaskDefinition = () => {
  isEdit.value = false
  taskDefinitionForm.value = {
    name: '',
    description: '',
    type: '导入',
    clientId: '',
    params: '{}',
    callbackUrl: '',
    callbackMethod: 'POST'
  }
  dialogVisible.value = true
}

const submitTaskDefinitionForm = async () => {
  try {
    if (isEdit.value) {
      await taskDefinitionApi.update(taskDefinitionForm.value.id, taskDefinitionForm.value)
      ElMessage.success('编辑成功')
    } else {
      await taskDefinitionApi.create(taskDefinitionForm.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadTaskDefinitions()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

const handleSizeChange = (size) => {
  pageSize.value = size
  loadTaskDefinitions()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  loadTaskDefinitions()
}

onMounted(() => {
  loadTaskDefinitions()
})
</script>

<style scoped>
.task-definition-list-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>