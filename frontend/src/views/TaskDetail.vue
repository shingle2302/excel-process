<template>
  <div class="task-detail-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>任务详情</span>
          <el-button type="primary" @click="goBack">
            <el-icon><ArrowLeft /></el-icon>
            返回列表
          </el-button>
        </div>
      </template>
      <div v-if="task" class="task-info">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="任务ID">{{ task.id }}</el-descriptions-item>
          <el-descriptions-item label="任务名称">{{ task.name }}</el-descriptions-item>
          <el-descriptions-item label="任务类型">{{ task.type }}</el-descriptions-item>
          <el-descriptions-item label="任务状态">
            <el-tag :type="getStatusType(task.status)" effect="dark">
              {{ task.status }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="任务定义ID">{{ task.taskDefinitionId }}</el-descriptions-item>
          <el-descriptions-item label="处理进度">
            <el-progress :percentage="task.progress" :stroke-width="15" />
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ task.createTime }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ task.updateTime }}</el-descriptions-item>
          <el-descriptions-item label="开始时间">{{ task.startTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="结束时间">{{ task.endTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="文件路径">{{ task.filePath || '-' }}</el-descriptions-item>
          <el-descriptions-item label="错误信息">{{ task.errorMessage || '-' }}</el-descriptions-item>
          <el-descriptions-item label="任务描述" :span="2">{{ task.description || '-' }}</el-descriptions-item>
          <el-descriptions-item label="任务参数" :span="2">
            <pre v-if="task.params">{{ JSON.stringify(JSON.parse(task.params), null, 2) }}</pre>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="处理结果" :span="2">
            <pre v-if="task.result">{{ JSON.stringify(JSON.parse(task.result), null, 2) }}</pre>
            <span v-else>-</span>
          </el-descriptions-item>
        </el-descriptions>
        
        <el-card class="mt-4" v-if="task.status === '处理中'">
          <template #header>
            <div class="card-header">
              <span>处理进度</span>
            </div>
          </template>
          <div class="progress-container">
            <el-progress :percentage="task.progress" :stroke-width="20" />
            <div class="progress-text">
              {{ task.progress }}%
            </div>
          </div>
        </el-card>
      </div>
      <div v-else class="loading-container">
        <el-skeleton :rows="10" animated />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { taskApi } from '../services/api'

const route = useRoute()
const router = useRouter()
const task = ref(null)
const taskId = ref(route.params.id)

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

const loadTask = async () => {
  try {
    const result = await taskApi.get(taskId.value)
    task.value = result
  } catch (error) {
    console.error('加载任务详情失败:', error)
    ElMessage.error('加载任务详情失败')
  }
}

const goBack = () => {
  router.push('/tasks')
}

onMounted(() => {
  loadTask()
  // 定时刷新任务状态
  const interval = setInterval(() => {
    if (task.value && task.value.status === '处理中') {
      loadTask()
    }
  }, 5000)
  
  // 组件卸载时清除定时器
  return () => clearInterval(interval)
})
</script>

<style scoped>
.task-detail-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.task-info {
  margin-top: 20px;
}

.mt-4 {
  margin-top: 20px;
}

.loading-container {
  margin-top: 20px;
}

.progress-container {
  margin: 20px 0;
}

.progress-text {
  text-align: center;
  margin-top: 10px;
  font-size: 16px;
  font-weight: bold;
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