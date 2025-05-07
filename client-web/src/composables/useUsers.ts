import { type DeepReadonly, readonly, type Ref, ref } from 'vue'
import api from '@/services/api.ts'
import type { User } from '@/types/types.ts'

const users = ref<User[]>([])
const loaded = ref(false)

function loadData() {
  if (loaded.value) return

  api.api.get<User[]>('/users')
    .then(response => users.value = response.data)
    .catch(err => console.error('Error loading users.', err.message))
    .finally(() => loaded.value = true)
}

export function useUsers(): DeepReadonly<Ref<User[]>> {
  loadData()
  return readonly(users)
}
