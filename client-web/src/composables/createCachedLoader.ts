import { type DeepReadonly, readonly, ref, type Ref } from 'vue'

export function createCachedLoader<T, D = T>(loader: () => Promise<T>, defaultValue: D) {
  const data = ref<T | D>(defaultValue)
  const loaded = ref(false)
  const loading = ref(false)

  async function load(): Promise<void> {
    if (loading.value) return
    loading.value = true
    loader()
      .then(result => data.value = result)
      .catch(err => console.error('Load error:', err.message))
      .finally(() => {
        loaded.value = true
        loading.value = false
      })
  }

  function useLoader(): DeepReadonly<Ref<T | D>> {
    if (!loaded.value) {
      void load()
    }

    return readonly(data) as DeepReadonly<Ref<T | D>>
  }

  return {
    useLoader,
    refresh: load,
    loading: readonly(loading)
  }
}
