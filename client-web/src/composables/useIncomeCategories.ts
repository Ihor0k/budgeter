import api from '@/services/api.ts'
import type { IncomeCategory } from '@/types/types.ts'
import { createCachedLoader } from '@/composables/createCachedLoader.ts'

export const useIncomeCategories = createCachedLoader<IncomeCategory[]>(api.getIncomeCategories, []).useLoader
