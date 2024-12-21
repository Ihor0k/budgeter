import { readonly, ref } from 'vue'
import api from '@/services/api.ts'
import type { User } from '@/types/types.ts'

const users = ref<User[]>()
const loaded = ref(false)

function loadData() {
  if (loaded.value) return

  api.get<User[]>('/users')
    .then(response => users.value = response.data)
    .catch(err => console.error('Error loading users.', err.message))
    .finally(() => loaded.value = true)
}

export function useUsers() {
  loadData()
  return readonly(users)
}
