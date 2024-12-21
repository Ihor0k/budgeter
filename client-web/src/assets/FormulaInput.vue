<script setup lang="ts">
import { computed, defineProps, ref, watch } from 'vue'
import { Big } from 'big.js'
import { NInput } from 'naive-ui'

const props = defineProps({
  disabled: {
    type: Boolean,
    default: false
  }
})

const model = defineModel<number | null>('value', {
  type: [Number, null],
  required: true
})

// State
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
    return new Big(0)
  }
})

const inputValue = computed(() => {
  if (props.disabled) return model.value?.toString() ?? ''
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

watch(() => props.disabled, disabled => {
  if (!disabled) {
    updateModel()
  }
})

function updateModel() {
  model.value = sum.value.toNumber()
}


/*

const formula = ref<string>(props.value?.toString() ?? '')
const isEditing = ref<boolean>(false)

const inputValue = computed({
  get: () => isEditing.value ? formula.value : sum.value,
  set: (newValue) => {
    if (isEditing.value) formula.value = newValue
  }
})

const sum = computed(() => {
  try {
    return formula.value
      .replace(/,/g, '.')
      .split(/\s+/)
      .filter(n => n !== '')
      .map(n => new Big(n))
      .reduce((total, num) => total.add(num), new Big(0))
      .toString()
  } catch {
    return formula.value
  }
})

watch(sum, (newValue) => emit('update:value', Number(newValue)))
watch(() => props.value, (newValue) => inputValue.value = newValue?.toString() ?? '')

const onBlur = () => {
  isEditing.value = false
}

const onFocus = () => {
  isEditing.value = true
}*/
</script>

<template>
  <n-input
    :value="isEditing ? originalString : inputValue.toString()"
    :disabled="disabled"
    @input="onInput"
    @focus="onFocus"
    @blur="onBlur"
  />
</template>

<style scoped>

</style>
