<template>
  <router-view v-if="isLoginPage" />
  <div v-else class="app-container">
    <el-container class="main-layout">
      <el-header class="top-header">
        <div class="brand">
          <h1>Excel处理服务管理系统</h1>
          <span>更高效的数据任务运营台</span>
        </div>
        <div class="right-actions">
          <el-tag type="success" effect="dark">稳定运行</el-tag>
          <el-button type="danger" link @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>
      <el-container>
        <el-aside width="220px" class="side-nav">
          <el-menu router :default-active="activeMenu" class="menu" @select="handleMenuSelect">
            <el-menu-item index="/"><el-icon><House /></el-icon><span>首页</span></el-menu-item>
            <el-menu-item index="/tasks"><el-icon><List /></el-icon><span>任务管理</span></el-menu-item>
            <el-menu-item index="/task-definitions"><el-icon><Document /></el-icon><span>任务定义管理</span></el-menu-item>
            <el-menu-item index="/clients"><el-icon><User /></el-icon><span>客户端管理</span></el-menu-item>
            <el-menu-item index="/column-definitions"><el-icon><Grid /></el-icon><span>数据列定义管理</span></el-menu-item>
            <el-menu-item index="/excel/import"><el-icon><Upload /></el-icon><span>Excel导入</span></el-menu-item>
            <el-menu-item index="/excel/export"><el-icon><Download /></el-icon><span>Excel导出</span></el-menu-item>
          </el-menu>
        </el-aside>
        <el-main class="content-area">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { House, List, Document, User, Grid, Upload, Download } from '@element-plus/icons-vue'
import { clearAuth } from './utils/auth.js'

const route = useRoute()
const router = useRouter()
const activeMenu = ref(route.path)
const isLoginPage = computed(() => route.path === '/login')

const handleMenuSelect = (key) => {
  activeMenu.value = key
}

const handleLogout = () => {
  clearAuth()
  router.replace('/login')
}

watch(() => route.path, (newPath) => {
  activeMenu.value = newPath
})
</script>

<style scoped>
.app-container {
  width: 100%;
  height: 100vh;
  background: #f3f6fb;
}

.main-layout {
  height: 100vh;
}

.top-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(90deg, #1d4ed8, #2563eb);
  color: #fff;
  padding: 0 20px;
  box-shadow: 0 4px 14px rgba(37, 99, 235, 0.2);
}

.right-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand h1 {
  font-size: 20px;
  margin: 0;
}

.brand span {
  opacity: 0.85;
  font-size: 12px;
}

.side-nav {
  background-color: #fff;
  border-right: 1px solid #ebeef5;
}

.menu {
  border-right: none;
  padding-top: 8px;
}

.content-area {
  overflow: auto;
}
</style>
