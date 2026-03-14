<template>
  <div class="task-list-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>任务管理</span>
          <el-button type="primary" @click="createTask">
            <el-icon><Plus /></el-icon>
            新建任务
          </el-button>
        </div>
      </template>
      <div class="task-filter">
        <el-form :inline="true" :model="filterForm">
          <el-form-item label="状态">
            <el-select v-model="filterForm.status" placeholder="全部">
              <el-option label="全部" value="" />
              <el-option label="待处理" value="待处理" />
              <el-option label="处理中" value="处理中" />
              <el-option label="已完成" value="已完成" />
              <el-option label="失败" value="失败" />
              <el-option label="暂停" value="暂停" />
              <el-option label="取消" value="取消" />
            </el-select>
          </el-form-item>
          <el-form-item label="类型">
            <el-select v-model="filterForm.type" placeholder="全部">
              <el-option label="全部" value="" />
              <el-option label="导入" value="导入" />
              <el-option label="导出" value="导出" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadTasks">
              搜索
            </el-button>
            <el-button @click="resetFilter">
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>
      <el-table :data="tasks" style="width: 100%">
        <el-table-column prop="id" label="任务ID" width="100" />
        <el-table-column prop="name" label="任务名称" />
        <el-table-column prop="type" label="任务类型" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag
              :type="scope && scope.row ? getStatusType(scope.row.status) : 'info'"
              effect="dark"
            >
              {{ scope && scope.row ? scope.row.status : '' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="progress" label="进度" width="150">
          <template #default="scope">
            <el-progress :percentage="scope && scope.row ? scope.row.progress : 0" :stroke-width="10" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" type="primary" @click="viewTask(scope.row.id)" v-if="scope && scope.row">
              查看
            </el-button>
            <el-button
              size="small"
              type="warning"
              @click="updateTaskStatus(scope.row.id, '暂停')"
              v-if="scope && scope.row && scope.row.status === '处理中'"
            >
              暂停
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="updateTaskStatus(scope.row.id, '取消')"
              v-if="scope && scope.row && (scope.row.status === '待处理' || scope.row.status === '暂停')"
            >
              取消
            </el-button>
            <el-button 
              size="small" 
              type="success" 
              @click="updateTaskStatus(scope.row.id, '待处理')"
              v-if="scope && scope.row && scope.row.status === '失败'"
            >
              重试
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="deleteTask(scope.row.id)"
              v-if="scope && scope.row && scope.row.status === '已完成'"
            >
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
    
    <!-- 新建任务对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="新建任务"
      width="500px"
    >
      <el-form :model="taskForm" label-width="120px">
        <el-form-item label="任务名称">
          <el-input v-model="taskForm.name" placeholder="请输入任务名称" />
        </el-form-item>
        <el-form-item label="任务描述">
          <el-input
            v-model="taskForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入任务描述"
          />
        </el-form-item>
        <el-form-item label="任务定义">
          <el-select v-model="taskForm.taskDefinitionId" placeholder="请选择任务定义">
            <el-option
              v-for="taskDef in taskDefinitions"
              :key="taskDef.id"
              :label="taskDef.name"
              :value="taskDef.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="任务类型">
          <el-select v-model="taskForm.type" placeholder="请选择任务类型">
            <el-option label="导入" value="导入" />
            <el-option label="导出" value="导出" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitTaskForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { taskApi, taskDefinitionApi } from '../services/api'

const router = useRouter()
const tasks = ref([])
const taskDefinitions = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const filterForm = ref({
  status: '',
  type: ''
})
const dialogVisible = ref(false)
const taskForm = ref({
  name: '',
  description: '',
  taskDefinitionId: null,
  type: '导入'
})

const getStatusType = (status) => {
  const statusMap = {
    '待处理': 'info',
    '处理中': 'warning',
    '已完成': 'success',
    '失败': 'danger',
    '暂停': 'warning',
    '取消': 'info'
  }
  return statusMap[status] || 'info'
}

const loadTasks = async () => {
  try {
    const result = await taskApi.list(filterForm.value.status)
    tasks.value = result
    total.value = result.length
  } catch (error) {
    console.error('加载任务列表失败:', error)
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

const viewTask = (taskId) => {
  router.push(`/task/${taskId}`)
}

const updateTaskStatus = async (taskId, status) => {
  try {
    await taskApi.updateStatus(taskId, status)
    ElMessage.success('状态更新成功')
    loadTasks()
  } catch (error) {
    console.error('更新状态失败:', error)
    ElMessage.error('更新状态失败')
  }
}

const deleteTask = async (taskId) => {
  try {
    await taskApi.delete(taskId)
    ElMessage.success('删除成功')
    loadTasks()
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败')
  }
}

const createTask = () => {
  taskForm.value = {
    name: '',
    description: '',
    taskDefinitionId: null,
    type: '导入'
  }
  dialogVisible.value = true
}

const submitTaskForm = async () => {
  try {
    await taskApi.create(taskForm.value)
    ElMessage.success('任务创建成功')
    dialogVisible.value = false
    loadTasks()
  } catch (error) {
    console.error('创建任务失败:', error)
    ElMessage.error('创建任务失败')
  }
}

const resetFilter = () => {
  filterForm.value = {
    status: '',
    type: ''
  }
  loadTasks()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  loadTasks()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  loadTasks()
}

onMounted(() => {
  loadTasks()
  loadTaskDefinitions()
})
</script>

<style scoped>
.task-list-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.task-filter {
  margin: 20px 0;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>