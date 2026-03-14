<template>
  <div class="column-definition-list-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>数据列定义管理</span>
        </div>
      </template>
      <el-table :data="columnDefinitions" style="width: 100%">
        <el-table-column prop="id" label="列定义ID" width="100" />
        <el-table-column prop="taskDefinitionId" label="任务定义ID" width="120" />
        <el-table-column prop="fieldName" label="字段名" />
        <el-table-column prop="columnName" label="列名" />
        <el-table-column prop="columnType" label="列类型" width="100" />
        <el-table-column prop="columnFormat" label="列格式" width="120" />
        <el-table-column prop="description" label="列描述" />
        <el-table-column prop="defaultValue" label="默认值" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button size="small" type="primary" @click="viewColumnDefinition(scope.row.id)">
              查看
            </el-button>
            <el-button size="small" type="danger" @click="deleteColumnDefinition(scope.row.id)">
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { columnDefinitionApi } from '../services/api'

const columnDefinitions = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadColumnDefinitions = async () => {
  try {
    const result = await columnDefinitionApi.list()
    columnDefinitions.value = result
    total.value = result.length
  } catch (error) {
    console.error('加载数据列定义列表失败:', error)
  }
}

const viewColumnDefinition = (columnDefinitionId) => {
  // 这里可以跳转到数据列定义详情页面
  ElMessage.info('查看数据列定义详情功能待实现')
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

const handleSizeChange = (size) => {
  pageSize.value = size
  loadColumnDefinitions()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  loadColumnDefinitions()
}

onMounted(() => {
  loadColumnDefinitions()
})
</script>

<style scoped>
.column-definition-list-container {
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