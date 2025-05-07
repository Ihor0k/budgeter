<script setup lang="ts">
import { NDatePicker } from 'naive-ui'
import type { Shortcuts } from 'naive-ui/es/date-picker/src/interface'
import { computed } from 'vue'

const emit = defineEmits(['confirm'])
const { defaultValue = null } = defineProps<{
  defaultValue?: [string, string] | null,
}>()

const value = computed<[number, number] | null>(() => {
  if (defaultValue) {
    return [new Date(defaultValue[0]).getTime(), new Date(defaultValue[1]).getTime()]
  }
  return null
})

function onConfirm(value: [number, number] | null, formattedValue: [string, string] | null) {
  emit('confirm', formattedValue)
}

function onClear() {
  emit('confirm', null)
}

function getMonthToDate(): [number, number] {
  const today = new Date()
  const start = new Date(today.getFullYear(), today.getMonth(), 1)
  return [start.getTime(), today.getTime()]
}

function getYearToDate(): [number, number] {
  const today = new Date()
  const start = new Date(today.getFullYear(), 0, 1)
  return [start.getTime(), today.getTime()]
}

function getLast1Month(): [number, number] {
  const today = new Date()
  const start = new Date(today.getFullYear(), today.getMonth() - 1, 1)
  const end = new Date(today.getFullYear(), today.getMonth(), 0) // 0th day of current month = last day of previous month
  return [start.getTime(), end.getTime()]
}

function getLast3Months(): [number, number] {
  const today = new Date()
  const start = new Date(today.getFullYear(), today.getMonth() - 3, 1)
  const end = new Date(today.getFullYear(), today.getMonth(), 0)
  return [start.getTime(), end.getTime()]
}

function getLast6Months(): [number, number] {
  const today = new Date()
  const start = new Date(today.getFullYear(), today.getMonth() - 6, 1)
  const end = new Date(today.getFullYear(), today.getMonth(), 0)
  return [start.getTime(), end.getTime()]
}

function getLast1Year(): [number, number] {
  const today = new Date()
  const start = new Date(today.getFullYear() - 1, today.getMonth(), 1)
  const end = new Date(today.getFullYear(), today.getMonth(), 0)
  return [start.getTime(), end.getTime()]
}

const shortcuts: Shortcuts = {
  'Month to date': () => getMonthToDate(),
  'Year to date': () => getYearToDate(),
  '1 month': () => getLast1Month(),
  '3 months': () => getLast3Months(),
  '6 months': () => getLast6Months(),
  '1 year': () => getLast1Year()
}

</script>

<template>
  <n-date-picker
    :default-value="value"
    type="daterange"
    panel
    clearable
    :shortcuts="shortcuts"
    @confirm="onConfirm"
    @clear="onClear"
  />
</template>

<style scoped>

</style>
