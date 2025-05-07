<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { Big } from 'big.js'
import { NInput } from 'naive-ui'

const { disabled = false } = defineProps<{ disabled?: boolean }>()

const model = defineModel<number | null>('value', {
  type: [Number, null],
  required: true
})

const originalString = ref('')
const isEditing = ref(false)

const sum = computed(() => {
  try {
    return originalString.value
      .replace(/,/g, '.')
      .split(/\s+/)
      .filter(n => n !== '')
      .map(n => new Big(n))
      .reduce((total, num) => total.add(num), new Big(0))
  } catch {
    // TODO: show error on UI
    return new Big(0)
  }
})

const inputValue = computed(() => {
  if (disabled) return model.value?.toString() ?? ''
  if (isEditing.value) return originalString.value
  return sum.value.toString()
})

function onInput(newValue: string) {
  originalString.value = newValue
}

function onFocus() {
  isEditing.value = true
}

function onBlur() {
  isEditing.value = false
  updateModel()
}

watch(() => disabled, disabled => {
  if (!disabled) {
    updateModel()
  }
})

function updateModel() {
  model.value = sum.value.toNumber()
}

</script>

<template>
  <n-input
    v-select-on-click
    :value="isEditing ? originalString : inputValue.toString()"
    :disabled="disabled"
    @input="onInput"
    @focus="onFocus"
    @blur="onBlur"
  />
</template>

<style scoped>

</style>
