<template>
  <div class="app-container">
    <el-container style="height: 100vh; border: 1px solid #eee;">
      <el-header style="background-color: #1890ff; color: white; text-align: center; line-height: 60px;">
        <h1>Excel处理服务管理系统</h1>
      </el-header>
      <el-container>
        <el-aside width="200px" style="background-color: #f0f2f5;">
          <el-menu
            router
            :default-active="activeMenu"
            class="el-menu-vertical-demo"
            @select="handleMenuSelect"
          >
            <el-menu-item index="/">
              <el-icon><House /></el-icon>
              <span>首页</span>
            </el-menu-item>
            <el-menu-item index="/tasks">
              <el-icon><List /></el-icon>
              <span>任务管理</span>
            </el-menu-item>
            <el-menu-item index="/task-definitions">
              <el-icon><Document /></el-icon>
              <span>任务定义管理</span>
            </el-menu-item>
            <el-menu-item index="/clients">
              <el-icon><User /></el-icon>
              <span>客户端管理</span>
            </el-menu-item>
            <el-menu-item index="/column-definitions">
              <el-icon><Grid /></el-icon>
              <span>数据列定义管理</span>
            </el-menu-item>
            <el-menu-item index="/excel/import">
              <el-icon><Upload /></el-icon>
              <span>Excel导入</span>
            </el-menu-item>
            <el-menu-item index="/excel/export">
              <el-icon><Download /></el-icon>
              <span>Excel导出</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { House, List, Document, User, Grid, Upload, Download } from '@element-plus/icons-vue'

const route = useRoute()
const activeMenu = ref('/')

const handleMenuSelect = (key, keyPath) => {
  activeMenu.value = key
}

onMounted(() => {
  activeMenu.value = route.path
})
</script>

<style>
.app-container {
  width: 100%;
  height: 100vh;
}

.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 200px;
  min-height: 400px;
}
</style>