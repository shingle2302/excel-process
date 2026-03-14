<template>
  <div class="client-list-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>客户端管理</span>
          <el-button type="primary" @click="createClient">
            <el-icon><Plus /></el-icon>
            新建客户端
          </el-button>
        </div>
      </template>
      <el-table :data="clients" style="width: 100%">
        <el-table-column prop="id" label="ID" width="100" />
        <el-table-column prop="clientId" label="客户端标识" />
        <el-table-column prop="clientName" label="客户端名称" />
        <el-table-column prop="clientDesc" label="客户端描述" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag
              :type="scope.row.status === '启用' ? 'success' : 'danger'"
              effect="dark"
            >
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="250">
          <template #default="scope">
            <el-button size="small" type="primary" @click="viewClient(scope.row.id)">
              查看
            </el-button>
            <el-button size="small" type="warning" @click="editClient(scope.row)">
              编辑
            </el-button>
            <el-button 
              size="small" 
              :type="scope.row.status === '启用' ? 'danger' : 'success'"
              @click="toggleClientStatus(scope.row.clientId, scope.row.status === '启用' ? '禁用' : '启用')"
            >
              {{ scope.row.status === '启用' ? '禁用' : '启用' }}
            </el-button>
            <el-button size="small" type="danger" @click="deleteClient(scope.row.id)">
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
    
    <!-- 新建/编辑客户端对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑客户端' : '新建客户端'"
      width="500px"
    >
      <el-form :model="clientForm" label-width="120px">
        <el-form-item label="客户端标识">
          <el-input v-model="clientForm.clientId" placeholder="请输入客户端标识" />
        </el-form-item>
        <el-form-item label="客户端密钥">
          <el-input v-model="clientForm.clientSecret" placeholder="请输入客户端密钥" />
        </el-form-item>
        <el-form-item label="客户端名称">
          <el-input v-model="clientForm.clientName" placeholder="请输入客户端名称" />
        </el-form-item>
        <el-form-item label="客户端描述">
          <el-input
            v-model="clientForm.clientDesc"
            type="textarea"
            :rows="3"
            placeholder="请输入客户端描述"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="clientForm.status" placeholder="请选择状态">
            <el-option label="启用" value="启用" />
            <el-option label="禁用" value="禁用" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitClientForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import { clientApi } from '../services/api'

const router = useRouter()
const clients = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const clientForm = ref({
  clientId: '',
  clientSecret: '',
  clientName: '',
  clientDesc: '',
  status: '启用'
})

const loadClients = async () => {
  try {
    const result = await clientApi.list()
    clients.value = result
    total.value = result.length
  } catch (error) {
    console.error('加载客户端列表失败:', error)
  }
}

const viewClient = (clientId) => {
  // 这里可以跳转到客户端详情页面
  ElMessage.info('查看客户端详情功能待实现')
}

const editClient = (client) => {
  isEdit.value = true
  clientForm.value = {
    ...client
  }
  dialogVisible.value = true
}

const deleteClient = async (clientId) => {
  try {
    await clientApi.delete(clientId)
    ElMessage.success('删除成功')
    loadClients()
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败')
  }
}

const toggleClientStatus = async (clientId, status) => {
  try {
    await clientApi.updateStatus(clientId, status)
    ElMessage.success('状态更新成功')
    loadClients()
  } catch (error) {
    console.error('更新状态失败:', error)
    ElMessage.error('更新状态失败')
  }
}

const createClient = () => {
  isEdit.value = false
  clientForm.value = {
    clientId: '',
    clientSecret: '',
    clientName: '',
    clientDesc: '',
    status: '启用'
  }
  dialogVisible.value = true
}

const submitClientForm = async () => {
  try {
    if (isEdit.value) {
      await clientApi.update(clientForm.value.id, clientForm.value)
      ElMessage.success('编辑成功')
    } else {
      await clientApi.create(clientForm.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadClients()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

const handleSizeChange = (size) => {
  pageSize.value = size
  loadClients()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  loadClients()
}

onMounted(() => {
  loadClients()
})
</script>

<style scoped>
.client-list-container {
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