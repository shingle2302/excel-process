<template>
  <div class="home-container">
    <el-card shadow="hover" class="overview-card">
      <template #header>
        <div class="card-header">
          <div>
            <h2 class="section-title">系统概览</h2>
            <p class="section-desc">关注核心运营指标，快速发现任务执行风险。</p>
          </div>
          <el-button type="primary" plain @click="loadStats">刷新数据</el-button>
        </div>
      </template>

      <el-row :gutter="16" class="kpi-row">
        <el-col :xs="24" :sm="12" :md="6" v-for="item in kpiCards" :key="item.label">
          <el-card class="stat-card" shadow="never">
            <div class="stat-item">
              <div class="stat-label">{{ item.label }}</div>
              <div class="stat-value">{{ item.value }}</div>
              <div class="stat-trend">{{ item.desc }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="16" class="mt-4">
        <el-col :xs="24" :md="10">
          <el-card shadow="never" class="panel-card">
            <template #header>
              <span>任务健康度</span>
            </template>
            <div class="health-item">
              <span>完成率</span>
              <el-progress :percentage="completionRate" :stroke-width="12" />
            </div>
            <div class="health-item" v-for="item in statusStats" :key="item.status">
              <span>{{ item.status }}</span>
              <el-tag :type="item.tagType" effect="plain">{{ item.count }}</el-tag>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :md="14">
          <el-card shadow="never" class="panel-card">
            <template #header>
              <span>快捷入口</span>
            </template>
            <div class="quick-actions">
              <el-button type="primary" @click="router.push('/tasks')">任务看板</el-button>
              <el-button type="success" @click="router.push('/excel/import')">新建导入任务</el-button>
              <el-button type="warning" @click="router.push('/excel/export')">发起导出任务</el-button>
            </div>
            <p class="action-tip">建议每日优先处理“失败/暂停”任务，减少数据交付延迟。</p>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="mt-4" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>最近任务</span>
          <el-button text type="primary" @click="router.push('/tasks')">查看全部</el-button>
        </div>
      </template>
      <el-table :data="recentTasks" style="width: 100%">
        <el-table-column prop="id" label="任务ID" width="90" />
        <el-table-column prop="name" label="任务名称" min-width="160" />
        <el-table-column prop="type" label="任务类型" width="100" />
        <el-table-column prop="status" label="状态" width="110">
          <template #default="scope">
            <el-tag :type="getStatusType(scope && scope.row ? scope.row.status : '')">{{ scope && scope.row ? scope.row.status : '' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
        <el-table-column prop="progress" label="进度" width="130">
          <template #default="scope">
            <el-progress :percentage="scope && scope.row ? (scope.row.progress || 0) : 0" :stroke-width="10" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="110">
          <template #default="scope">
            <el-button size="small" type="primary" @click="scope && scope.row && viewTask(scope.row.id)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { taskApi, taskDefinitionApi, clientApi, columnDefinitionApi } from '../services/api'

const router = useRouter()
const taskCount = ref(0)
const taskDefinitionCount = ref(0)
const clientCount = ref(0)
const columnDefinitionCount = ref(0)
const recentTasks = ref([])
const allTasks = ref([])

const getStatusType = (status) => {
  const map = {
    '待处理': 'info',
    '处理中': 'warning',
    '已完成': 'success',
    '失败': 'danger',
    '暂停': 'warning',
    '取消': 'info'
  }
  return map[status] || 'info'
}

const completionRate = computed(() => {
  if (!allTasks.value.length) return 0
  const done = allTasks.value.filter(task => task.status === '已完成').length
  return Math.round((done / allTasks.value.length) * 100)
})

const statusStats = computed(() => {
  const statuses = ['失败', '处理中', '待处理', '暂停']
  return statuses.map(status => {
    const count = allTasks.value.filter(task => task.status === status).length
    return {
      status,
      count,
      tagType: getStatusType(status)
    }
  })
})

const kpiCards = computed(() => ([
  { label: '总任务数', value: taskCount.value, desc: '任务执行总量' },
  { label: '任务定义数', value: taskDefinitionCount.value, desc: '可复用模板配置' },
  { label: '客户端数', value: clientCount.value, desc: '已接入系统客户' },
  { label: '字段定义数', value: columnDefinitionCount.value, desc: '数据标准化基础' }
]))

const loadStats = async () => {
  try {
    const tasks = await taskApi.list()
    allTasks.value = tasks
    taskCount.value = tasks.length

    const [taskDefinitions, clients, columnDefinitions] = await Promise.all([
      taskDefinitionApi.list(),
      clientApi.list(),
      columnDefinitionApi.list()
    ])

    taskDefinitionCount.value = taskDefinitions.length
    clientCount.value = clients.length
    columnDefinitionCount.value = columnDefinitions.length
    recentTasks.value = tasks.slice(0, 6)
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const viewTask = (taskId) => {
  router.push(`/task/${taskId}`)
}

onMounted(loadStats)

defineExpose({
  loadStats,
  viewTask,
  getStatusType
})
</script>

<style scoped>
.home-container {
  padding: 20px;
}

.overview-card {
  border-radius: 12px;
}

.section-title {
  margin: 0;
  font-size: 20px;
}

.section-desc {
  margin: 6px 0 0;
  color: #909399;
  font-size: 13px;
}

.kpi-row {
  margin-top: 12px;
}

.stat-card {
  border: 1px solid #ebeef5;
  border-radius: 10px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.stat-label {
  color: #909399;
  font-size: 13px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
}

.stat-trend {
  font-size: 12px;
  color: #67c23a;
}

.panel-card {
  border-radius: 10px;
  min-height: 220px;
}

.health-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
  gap: 14px;
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.action-tip {
  margin-top: 16px;
  color: #909399;
  font-size: 13px;
}

.mt-4 {
  margin-top: 20px;
}
</style>
