import api from '@/services/api.ts'
import type { ExpenseCategory } from '@/types/types.ts'
import { createOnceLoader } from '@/composables/createOnceLoader.ts'

export const useExpenseCategories = createOnceLoader<ExpenseCategory[]>(api.getExpenseCategories, [])
