import api from '@/services/api.ts'
import { createCachedLoader } from '@/composables/createCachedLoader.ts'
import type { User } from '@/types/types.ts'

export const useUsers = createCachedLoader<User[]>(api.getUsers, []).useLoader
