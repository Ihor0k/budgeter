<script setup lang="ts">
import ExpenseTransactionForm from '@/components/ExpenseTransactionForm.vue'
import { onMounted, ref } from 'vue'
import type { ExpenseCategory, User } from '@/types/types.ts'
import api from '@/services/api.ts'

const users = ref<User[]>([])
const expenseCategories = ref<ExpenseCategory[]>([])

onMounted(() => {
  api.get<User[]>('/users')
    .then(response => users.value = response.data)
    .catch(err => console.error('Error loading users.', err.message))
  api.get<ExpenseCategory[]>('/expenseCategories')
    .then(response => expenseCategories.value = response.data)
    .catch(err => console.error('Error loading expense categories.', err.message))
})
const expenseTransaction = { 'date': '2024-12-12' }
</script>

<template>
  <ExpenseTransactionForm :users="users" :categories="expenseCategories" :expense-transaction="expenseTransaction" />
</template>

<style scoped>

</style>
