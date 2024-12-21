import { createRouter, createWebHistory } from 'vue-router'
import TheWelcome from '@/components/TheWelcome.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Welcome',
      component: TheWelcome
    },
    {
      path: '/expense',
      name: 'Expense',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('@/views/ExpenseTransactionsView.vue')
    },
    {
      path: '/test',
      name: 'Test',
      component: () => import('@/components/Test.vue')
    }
  ]
})

export default router
