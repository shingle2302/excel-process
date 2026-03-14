import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/tasks',
    name: 'tasks',
    component: () => import('../views/TaskList.vue')
  },
  {
    path: '/task/:id',
    name: 'taskDetail',
    component: () => import('../views/TaskDetail.vue')
  },
  {
    path: '/task-definitions',
    name: 'taskDefinitions',
    component: () => import('../views/TaskDefinitionList.vue')
  },
  {
    path: '/task-definition/:id',
    name: 'taskDefinitionDetail',
    component: () => import('../views/TaskDefinitionDetail.vue')
  },
  {
    path: '/clients',
    name: 'clients',
    component: () => import('../views/ClientList.vue')
  },
  {
    path: '/column-definitions',
    name: 'columnDefinitions',
    component: () => import('../views/ColumnDefinitionList.vue')
  },
  {
    path: '/excel/import',
    name: 'excelImport',
    component: () => import('../views/ExcelImport.vue')
  },
  {
    path: '/excel/export',
    name: 'excelExport',
    component: () => import('../views/ExcelExport.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router