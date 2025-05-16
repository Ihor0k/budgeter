import api from '@/services/api'
import { createCachedLoader } from '@/composables/createCachedLoader.ts'

export const {
  useLoader: useAccountBalances,
  refresh: refreshAccountBalances,
} = createCachedLoader(api.getAccountBalances, [])
