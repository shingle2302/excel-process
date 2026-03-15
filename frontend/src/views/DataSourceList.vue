<template>
  <div class="data-source-list-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>数据源管理</span>
          <el-button type="primary" @click="createDataSource">
            <el-icon><Plus /></el-icon>
            新建数据源
          </el-button>
        </div>
      </template>
      <el-table :data="dataSources" style="width: 100%">
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="name" label="数据源名称" min-width="160" />
        <el-table-column prop="type" label="类型" width="120" />
        <el-table-column prop="description" label="描述" min-width="220" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button size="small" type="warning" @click="editDataSource(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteDataSource(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑数据源' : '新建数据源'" width="640px">
      <el-form :model="dataSourceForm" label-width="120px">
        <el-form-item label="数据源名称">
          <el-input v-model="dataSourceForm.name" placeholder="请输入数据源名称" />
        </el-form-item>
        <el-form-item label="数据源类型">
          <el-select v-model="dataSourceForm.type" placeholder="请选择数据源类型">
            <el-option label="mysql" value="mysql" />
            <el-option label="postgresql" value="postgresql" />
            <el-option label="oracle" value="oracle" />
            <el-option label="sqlserver" value="sqlserver" />
          </el-select>
        </el-form-item>
        <el-form-item label="连接配置(JSON)">
          <el-input
            v-model="dataSourceForm.connectionConfig"
            type="textarea"
            :rows="4"
            placeholder='例如: {"host":"127.0.0.1","port":3306,"database":"db","username":"root"}'
          />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="dataSourceForm.description" type="textarea" :rows="3" placeholder="请输入数据源描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button @click="testConnection">测试连通性</el-button>
          <el-button type="primary" @click="submitDataSourceForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { dataSourceApi } from '../services/api'

const dataSources = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const dataSourceForm = ref({
  name: '',
  type: 'mysql',
  connectionConfig: '{}',
  description: ''
})

const loadDataSources = async () => {
  try {
    dataSources.value = await dataSourceApi.list()
  } catch (error) {
    console.error('加载数据源失败:', error)
  }
}

const createDataSource = () => {
  isEdit.value = false
  dataSourceForm.value = {
    name: '',
    type: 'mysql',
    connectionConfig: '{}',
    description: ''
  }
  dialogVisible.value = true
}

const editDataSource = (dataSource) => {
  isEdit.value = true
  dataSourceForm.value = { ...dataSource }
  dialogVisible.value = true
}


const testConnection = async () => {
  try {
    await dataSourceApi.testConnection(dataSourceForm.value)
    ElMessage.success('连通性测试通过')
    return true
  } catch (error) {
    console.error('连通性测试失败:', error)
    ElMessage.error(error?.response?.data?.message || '连通性测试失败')
    return false
  }
}

const submitDataSourceForm = async () => {
  const passed = await testConnection()
  if (!passed) return

  try {
    if (isEdit.value) {
      await dataSourceApi.update(dataSourceForm.value.id, dataSourceForm.value)
      ElMessage.success('编辑成功')
    } else {
      await dataSourceApi.create(dataSourceForm.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    await loadDataSources()
  } catch (error) {
    console.error('保存数据源失败:', error)
    ElMessage.error('保存失败')
  }
}

const deleteDataSource = async (id) => {
  try {
    await dataSourceApi.delete(id)
    ElMessage.success('删除成功')
    await loadDataSources()
  } catch (error) {
    console.error('删除数据源失败:', error)
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  loadDataSources()
})
</script>

<style scoped>
.data-source-list-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
