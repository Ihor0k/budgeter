import api from '@/services/api.ts'
import { createOnceLoader } from '@/composables/createOnceLoader.ts'
import type { User } from '@/types/types.ts'

export const useUsers = createOnceLoader<User[]>(api.getUsers, [])
