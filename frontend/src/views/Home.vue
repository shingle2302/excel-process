<template>
  <div class="home-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>系统概览</span>
        </div>
      </template>
      <div class="dashboard">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-item">
                <div class="stat-value">{{ taskCount }}</div>
                <div class="stat-label">总任务数</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-item">
                <div class="stat-value">{{ taskDefinitionCount }}</div>
                <div class="stat-label">任务定义数</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-item">
                <div class="stat-value">{{ clientCount }}</div>
                <div class="stat-label">客户端数</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-item">
                <div class="stat-value">{{ columnDefinitionCount }}</div>
                <div class="stat-label">数据列定义数</div>
              </div>
            </el-card>
          </el-col>
        </el-row>
        
        <el-card class="mt-4">
          <template #header>
            <div class="card-header">
              <span>最近任务</span>
            </div>
          </template>
          <el-table :data="recentTasks" style="width: 100%">
            <el-table-column prop="id" label="任务ID" width="100" />
            <el-table-column prop="name" label="任务名称" />
            <el-table-column prop="type" label="任务类型" width="100" />
            <el-table-column prop="status" label="状态" width="100" />
            <el-table-column prop="createTime" label="创建时间" width="180" />
            <el-table-column prop="progress" label="进度" width="100">
              <template #default="scope">
                <el-progress :percentage="scope && scope.row ? scope.row.progress : 0" :stroke-width="10" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button size="small" type="primary" @click="viewTask(scope.row.id)">
                  查看
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { taskApi, taskDefinitionApi, clientApi, columnDefinitionApi } from '../services/api'

const router = useRouter()
const taskCount = ref(0)
const taskDefinitionCount = ref(0)
const clientCount = ref(0)
const columnDefinitionCount = ref(0)
const recentTasks = ref([])

const loadStats = async () => {
  try {
    // 加载任务数量
    const tasks = await taskApi.list()
    taskCount.value = tasks.length
    
    // 加载任务定义数量
    const taskDefinitions = await taskDefinitionApi.list()
    taskDefinitionCount.value = taskDefinitions.length
    
    // 加载客户端数量
    const clients = await clientApi.list()
    clientCount.value = clients.length
    
    // 加载数据列定义数量
    const columnDefinitions = await columnDefinitionApi.list()
    columnDefinitionCount.value = columnDefinitions.length
    
    // 加载最近任务
    recentTasks.value = tasks.slice(0, 5)
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const viewTask = (taskId) => {
  router.push(`/task/${taskId}`)
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.home-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dashboard {
  margin-top: 20px;
}

.stat-card {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #1890ff;
}

.stat-label {
  font-size: 14px;
  color: #606266;
  margin-top: 8px;
}

.mt-4 {
  margin-top: 20px;
}
</style>