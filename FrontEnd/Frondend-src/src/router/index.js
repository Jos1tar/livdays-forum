import { createRouter, createWebHistory } from 'vue-router';

// 导入组件
import Home from '../components/home.vue';
import Login from '../components/Login.vue';

// 定义路由规则
const routes = [
  {
    path: '/',
    name: 'login',
    component: Login,
  },
  {
    path: '/home',
    name: 'home',
    component: Home,
    meta: { requiresAuth: true },
  },
  // 未知路径回到登录页
  { path: '/:pathMatch(.*)*', redirect: '/' },
];

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 没有 token 时不允许进入需要登录的页面
router.beforeEach((to) => {
  if (to.meta.requiresAuth && !localStorage.getItem('token')) {
    return { path: '/' };
  }
});

export default router;
