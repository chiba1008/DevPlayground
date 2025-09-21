import { createRouter, createWebHistory } from 'vue-router'
import { useAuth } from '@/composables/useAuth'
import HomeView from '@/views/HomeView.vue'
import UserManagerView from '@/views/UserManagerView.vue'
import LoginView from '@/views/LoginView.vue'
import AdminView from '@/views/AdminView.vue'
import CreateUserView from '@/views/CreateUserView.vue'

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
      path: '/admin',
      name: 'admin',
      component: AdminView,
      meta: { requiresAuth: true, requiredRoles: ['ADMIN'] }
    },
    {
      path: '/users',
      name: 'users',
      component: UserManagerView,
      meta: { requiresAuth: true, requiredRoles: ['ADMIN'] }
    },
    {
      path: '/users/create',
      name: 'create-user',
      component: CreateUserView,
      meta: { requiresAuth: true, requiredRoles: ['ADMIN'] }
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
  const { isAuthenticated, checkAuthStatus, hasAnyRole } = useAuth()
  
  // Check auth status if not already checked
  if (!isAuthenticated.value) {
    await checkAuthStatus()
  }
  
  // If route requires auth and user is not authenticated
  if (to.meta.requiresAuth && !isAuthenticated.value) {
    next({ name: 'login' })
    return
  }
  
  // If route requires specific roles
  if (to.meta.requiredRoles && isAuthenticated.value) {
    const requiredRoles = to.meta.requiredRoles as string[]
    if (!hasAnyRole(requiredRoles)) {
      // Redirect to home if user doesn't have required roles
      next({ name: 'home' })
      return
    }
  }
  
  // If user is authenticated and trying to access login page
  if (to.name === 'login' && isAuthenticated.value) {
    next({ name: 'home' })
    return
  }
  
  next()
})

export default router
