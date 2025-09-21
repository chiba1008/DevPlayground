import { createRouter, createWebHistory } from 'vue-router'
import { useAuth } from '@/composables/useAuth'
import HomeView from '@/views/HomeView.vue'
import UserManagerView from '@/views/UserManagerView.vue'
import LoginView from '@/views/LoginView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: { requiresAuth: true }
    },
    {
      path: '/users',
      name: 'users',
      component: UserManagerView,
      meta: { requiresAuth: true }
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: { requiresAuth: false }
    },
  ],
})

router.beforeEach(async (to, _from, next) => {
  const { isAuthenticated, checkAuthStatus } = useAuth()
  
  // Check auth status if not already checked
  if (!isAuthenticated.value) {
    await checkAuthStatus()
  }
  
  // If route requires auth and user is not authenticated
  if (to.meta.requiresAuth && !isAuthenticated.value) {
    next({ name: 'login' })
  }
  // If user is authenticated and trying to access login page
  else if (to.name === 'login' && isAuthenticated.value) {
    next({ name: 'home' })
  }
  else {
    next()
  }
})

export default router
