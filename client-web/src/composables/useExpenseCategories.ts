import { readonly, ref } from 'vue'
import api from '@/services/api.ts'
import type { ExpenseCategory } from '@/types/types.ts'

const expenseCategories = ref<ExpenseCategory[]>()
const loaded = ref(false)

function loadData() {
  if (loaded.value) return

  api.api.get<ExpenseCategory[]>('/expenseCategories')
    .then(response => expenseCategories.value = response.data)
    .catch(err => console.error('Error loading expense categories.', err.message))
    .finally(() => loaded.value = true)
}

export function useExpenseCategories() {
  loadData()
  return readonly(expenseCategories)
}
