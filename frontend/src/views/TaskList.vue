<template>
  <div class="task-list-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <div>
            <span class="title">任务管理</span>
            <p class="subtitle">支持按状态/类型/关键词筛选，提升任务排查效率。</p>
          </div>
          <div class="header-actions">
            <el-button @click="loadTasks">刷新</el-button>
            <el-button type="primary" @click="createTask">
              <el-icon><Plus /></el-icon>
              新建任务
            </el-button>
          </div>
        </div>
      </template>

      <div class="summary-grid">
        <div v-for="item in summaryCards" :key="item.label" class="summary-item">
          <span>{{ item.label }}</span>
          <strong>{{ item.value }}</strong>
        </div>
      </div>

      <div class="task-filter">
        <el-form :inline="true" :model="filterForm">
          <el-form-item label="关键词">
            <el-input v-model="filterForm.keyword" placeholder="任务名称/描述" clearable />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="filterForm.status" placeholder="全部" clearable>
              <el-option label="待处理" value="待处理" />
              <el-option label="处理中" value="处理中" />
              <el-option label="已完成" value="已完成" />
              <el-option label="失败" value="失败" />
              <el-option label="暂停" value="暂停" />
              <el-option label="取消" value="取消" />
            </el-select>
          </el-form-item>
          <el-form-item label="类型">
            <el-select v-model="filterForm.type" placeholder="全部" clearable>
              <el-option label="导入" value="导入" />
              <el-option label="导出" value="导出" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="applyFilter">搜索</el-button>
            <el-button @click="resetFilter">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="pagedTasks" style="width: 100%" empty-text="暂无符合条件的任务">
        <el-table-column prop="id" label="任务ID" width="90" />
        <el-table-column prop="name" label="任务名称" min-width="180" />
        <el-table-column prop="type" label="任务类型" width="100" />
        <el-table-column prop="status" label="状态" width="110">
          <template #default="scope">
            <el-tag :type="getStatusType(scope && scope.row ? scope.row.status : '')" effect="dark">
              {{ scope && scope.row ? scope.row.status : '' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="progress" label="进度" width="150">
          <template #default="scope">
            <el-progress :percentage="scope && scope.row ? (scope.row.progress || 0) : 0" :stroke-width="10" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
        <el-table-column prop="updateTime" label="更新时间" min-width="170" />
        <el-table-column label="操作" width="230" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" @click="scope && scope.row && viewTask(scope.row.id)">查看</el-button>
            <el-button
              size="small"
              type="warning"
              @click="scope && scope.row && updateTaskStatus(scope.row.id, '暂停')"
              v-if="scope && scope.row && scope.row.status === '处理中'"
            >暂停</el-button>
            <el-button
              size="small"
              type="danger"
              @click="scope && scope.row && updateTaskStatus(scope.row.id, '取消')"
              v-if="scope && scope.row && (scope.row.status === '待处理' || scope.row.status === '暂停')"
            >取消</el-button>
            <el-button
              size="small"
              type="success"
              @click="scope && scope.row && updateTaskStatus(scope.row.id, '待处理')"
              v-if="scope && scope.row && scope.row.status === '失败'"
            >重试</el-button>
            <el-button
              size="small"
              type="danger"
              @click="scope && scope.row && deleteTask(scope.row.id)"
              v-if="scope && scope.row && scope.row.status === '已完成'"
            >删除</el-button>
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

    <el-dialog v-model="dialogVisible" title="新建任务" width="500px">
      <el-form :model="taskForm" label-width="120px">
        <el-form-item label="任务名称"><el-input v-model="taskForm.name" placeholder="请输入任务名称" /></el-form-item>
        <el-form-item label="任务描述">
          <el-input v-model="taskForm.description" type="textarea" :rows="3" placeholder="请输入任务描述" />
        </el-form-item>
        <el-form-item label="任务定义">
          <el-select v-model="taskForm.taskDefinitionId" placeholder="请选择任务定义">
            <el-option v-for="taskDef in taskDefinitions" :key="taskDef.id" :label="taskDef.name" :value="taskDef.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="任务类型">
          <el-select v-model="taskForm.type" placeholder="请选择任务类型">
            <el-option label="导入" value="导入" />
            <el-option label="导出" value="导出" />
          </el-select>
        </el-form-item>
        <el-form-item label="获取数据方法">
          <el-radio-group v-model="taskForm.dataFetchType">
            <el-radio label="sql">SQL查询</el-radio>
            <el-radio label="http">HTTP请求</el-radio>
          </el-radio-group>
        </el-form-item>
        <template v-if="taskForm.dataFetchType === 'sql'">
          <el-form-item label="数据源">
            <el-select v-model="taskForm.dataSourceId" placeholder="请选择数据源">
              <el-option v-for="item in dataSources" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="SQL语句">
            <el-input v-model="taskForm.querySql" type="textarea" :rows="3" placeholder="请输入查询SQL" />
          </el-form-item>
        </template>
        <template v-else>
          <el-form-item label="HTTP方法">
            <el-select v-model="taskForm.httpMethod" placeholder="请选择HTTP方法">
              <el-option label="GET" value="GET" />
              <el-option label="POST" value="POST" />
            </el-select>
          </el-form-item>
          <el-form-item label="查询接口URL">
            <el-input v-model="taskForm.httpUrl" placeholder="请输入查询接口URL" />
          </el-form-item>
          <el-form-item label="查询参数(JSON)">
            <el-input v-model="taskForm.requestParams" type="textarea" :rows="3" />
          </el-form-item>
          <el-form-item label="需要认证">
            <el-switch v-model="taskForm.httpNeedAuth" />
          </el-form-item>
          <template v-if="taskForm.httpNeedAuth">
            <el-form-item label="认证接口URL">
              <el-input v-model="taskForm.authUrl" placeholder="请输入认证接口URL" />
            </el-form-item>
            <el-form-item label="认证参数(JSON)">
              <el-input v-model="taskForm.authParams" type="textarea" :rows="3" />
            </el-form-item>
          </template>
        </template>
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
import { taskApi, taskDefinitionApi, dataSourceApi } from '../services/api'

const router = useRouter()
const tasks = ref([])
const taskDefinitions = ref([])
const dataSources = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const filterForm = ref({ keyword: '', status: '', type: '' })
const dialogVisible = ref(false)
const buildTaskForm = () => ({
  name: '',
  description: '',
  taskDefinitionId: null,
  type: '导入',
  dataFetchType: 'sql',
  dataSourceId: null,
  querySql: '',
  httpMethod: 'POST',
  httpUrl: '',
  requestParams: '{}',
  httpNeedAuth: false,
  authUrl: '',
  authParams: '{}'
})

const taskForm = ref(buildTaskForm())

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

const filteredTasks = computed(() => {
  const keyword = filterForm.value.keyword?.trim().toLowerCase()
  return tasks.value.filter((task) => {
    const statusMatch = !filterForm.value.status || task.status === filterForm.value.status
    const typeMatch = !filterForm.value.type || task.type === filterForm.value.type
    const keywordMatch = !keyword || [task.name, task.description]
      .filter(Boolean)
      .join(' ')
      .toLowerCase()
      .includes(keyword)
    return statusMatch && typeMatch && keywordMatch
  })
})

const pagedTasks = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredTasks.value.slice(start, end)
})

const summaryCards = computed(() => {
  const totalCount = tasks.value.length
  return [
    { label: '总任务', value: totalCount },
    { label: '进行中', value: tasks.value.filter(item => item.status === '处理中').length },
    { label: '失败任务', value: tasks.value.filter(item => item.status === '失败').length },
    { label: '完成率', value: totalCount ? `${Math.round((tasks.value.filter(item => item.status === '已完成').length / totalCount) * 100)}%` : '0%' }
  ]
})

const syncTotal = () => {
  total.value = filteredTasks.value.length
}

const loadTasks = async () => {
  try {
    const result = await taskApi.list()
    tasks.value = result
    currentPage.value = 1
    syncTotal()
  } catch (error) {
    console.error('加载任务列表失败:', error)
  }
}

const applyFilter = () => {
  currentPage.value = 1
  syncTotal()
}

const loadTaskDefinitions = async () => {
  try {
    taskDefinitions.value = await taskDefinitionApi.list()
  } catch (error) {
    console.error('加载任务定义失败:', error)
  }
}

const loadDataSources = async () => {
  try {
    dataSources.value = await dataSourceApi.list()
  } catch (error) {
    console.error('加载数据源失败:', error)
  }
}

const viewTask = (taskId) => {
  router.push(`/task/${taskId}`)
}

const updateTaskStatus = async (taskId, status) => {
  try {
    await taskApi.updateStatus(taskId, status)
    ElMessage.success('状态更新成功')
    await loadTasks()
  } catch (error) {
    console.error('更新状态失败:', error)
    ElMessage.error('更新状态失败')
  }
}

const deleteTask = async (taskId) => {
  try {
    await taskApi.delete(taskId)
    ElMessage.success('删除成功')
    await loadTasks()
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败')
  }
}

const createTask = () => {
  taskForm.value = buildTaskForm()
  dialogVisible.value = true
}

const submitTaskForm = async () => {
  try {
    await taskApi.create(taskForm.value)
    ElMessage.success('任务创建成功')
    dialogVisible.value = false
    await loadTasks()
  } catch (error) {
    console.error('创建任务失败:', error)
    ElMessage.error('创建任务失败')
  }
}

const resetFilter = () => {
  filterForm.value = { keyword: '', status: '', type: '' }
  applyFilter()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  syncTotal()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
}

onMounted(async () => {
  await Promise.all([loadTasks(), loadTaskDefinitions(), loadDataSources()])
})

defineExpose({
  getStatusType,
  viewTask,
  updateTaskStatus,
  deleteTask,
  submitTaskForm,
  resetFilter,
  handleSizeChange,
  handleCurrentChange,
  filterForm,
  taskForm,
  pageSize,
  currentPage,
  applyFilter
})
</script>

<style scoped>
.task-list-container {
  padding: 20px;
}

.title {
  font-size: 18px;
  font-weight: 600;
}

.subtitle {
  margin: 6px 0 0;
  color: #909399;
  font-size: 13px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 14px;
}

.summary-item {
  background: #f7f9fc;
  border: 1px solid #ebeef5;
  border-radius: 10px;
  padding: 12px;
  display: flex;
  justify-content: space-between;
  align-items: baseline;
}

.summary-item span {
  color: #909399;
  font-size: 13px;
}

.summary-item strong {
  font-size: 20px;
  color: #303133;
}

.task-filter {
  margin: 14px 0 4px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 992px) {
  .summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
