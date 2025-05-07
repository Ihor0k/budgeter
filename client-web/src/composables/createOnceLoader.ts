import { type DeepReadonly, readonly, ref, type Ref } from 'vue'

export function createOnceLoader<T, D = T>(loader: () => Promise<T>, defaultValue: D) {
  const data = ref<T | D>(defaultValue)
  const loaded = ref(false)
  const loading = ref(false)

  function useLoader(): DeepReadonly<Ref<T | D>> {
    if (!loaded.value && !loading.value) {
      loading.value = true
      loader()
        .then(result => data.value = result)
        .catch(err => console.error('Load error:', err.message))
        .finally(() => {
          loaded.value = true
          loading.value = false
        })
    }

    return readonly(data) as DeepReadonly<Ref<T | D>>
  }

  return useLoader
}
