<template>
  <div class="login-page">
    <el-card class="login-card" shadow="hover">
      <template #header>
        <div class="login-header">
          <h2>系统登录</h2>
          <p>请输入管理员账号信息</p>
        </div>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="账号" prop="username">
          <el-input v-model="form.username" placeholder="请输入账号，如：admin" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" @keyup.enter="handleLogin" />
        </el-form-item>
        <el-button type="primary" :loading="loading" class="login-btn" @click="handleLogin">登录</el-button>
      </el-form>
      <div class="login-tip">默认管理员：admin / ChangeMe@123（请在生产环境通过环境变量覆盖）</div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '../services/api'
import { saveAuth } from '../utils/auth.js'

const router = useRouter()
const route = useRoute()
const formRef = ref()
const loading = ref(false)

const form = ref({
  username: 'admin',
  password: 'ChangeMe@123'
})

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const result = await authApi.userLogin(form.value)
    saveAuth({
      apiKey: result.apiKey,
      clientName: result.clientName,
      expiresAt: Date.now() + (result.expiresIn || 3600) * 1000
    })
    ElMessage.success('登录成功')
    const redirect = route.query.redirect || '/'
    router.replace(redirect)
  } catch (error) {
    ElMessage.error('登录失败，请检查账号密码')
  } finally {
    loading.value = false
  }
}

defineExpose({
  handleLogin,
  formRef
})
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #eff6ff, #dbeafe);
}

.login-card {
  width: 420px;
  border-radius: 12px;
}

.login-header h2 {
  margin: 0;
}

.login-header p {
  margin: 6px 0 0;
  color: #909399;
}

.login-btn {
  width: 100%;
}

.login-tip {
  margin-top: 12px;
  color: #909399;
  font-size: 12px;
}
</style>
