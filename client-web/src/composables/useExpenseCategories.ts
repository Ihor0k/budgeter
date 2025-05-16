import api from '@/services/api.ts'
import type { ExpenseCategory } from '@/types/types.ts'
import { createCachedLoader } from '@/composables/createCachedLoader.ts'

export const useExpenseCategories = createCachedLoader<ExpenseCategory[]>(api.getExpenseCategories, []).useLoader
