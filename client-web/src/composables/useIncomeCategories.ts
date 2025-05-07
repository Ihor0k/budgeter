import api from '@/services/api.ts'
import type { IncomeCategory } from '@/types/types.ts'
import { createOnceLoader } from '@/composables/createOnceLoader.ts'

export const useIncomeCategories = createOnceLoader<IncomeCategory[]>(api.getIncomeCategories, [])
