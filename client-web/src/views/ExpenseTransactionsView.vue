<script setup lang="ts">
import ExpenseTransactionForm from '@/components/ExpenseTransactionForm.vue'
import IncomeTransactionForm from '@/components/IncomeTransactionForm.vue'
import { onMounted, ref } from 'vue'
import type { ExpenseCategory, IncomeCategory, User } from '@/types/types.ts'
import api from '@/services/api.ts'

const users = ref<User[]>([])
const expenseCategories = ref<ExpenseCategory[]>([])
const incomeCategories = ref<IncomeCategory[]>([])

onMounted(() => {
  api.get<User[]>('/users')
    .then(response => users.value = response.data)
    .catch(err => console.error('Error loading users.', err.message))
  api.get<ExpenseCategory[]>('/expenseCategories')
    .then(response => expenseCategories.value = response.data)
    .catch(err => console.error('Error loading expense categories.', err.message))
  api.get<IncomeCategory[]>('/incomeCategories')
    .then(response => incomeCategories.value = response.data)
    .catch(err => console.error('Error loading income categories.', err.message))
})

function createExpenseTransaction(expenseTransactionRequest) {
  api.post<CreateExpenseTransaction>('/transactions/expenses', expenseTransactionRequest)
    .then(response => console.log('OK', response)) // TODO
    .catch(err => console.error('Error adding expense transaction.', err.response.data ?? err.message))
}

function createIncomeTransaction(incomeTransactionRequest) {
  api.post<CreateExpenseTransaction>('/transactions/incomes', incomeTransactionRequest)
    .then(response => console.log('OK', response)) // TODO
    .catch(err => console.error('Error adding income transaction.', err.response.data ?? err.message))
}
</script>

<template>
  <ExpenseTransactionForm :users="users" :categories="expenseCategories" @save="createExpenseTransaction" />
  <IncomeTransactionForm :users="users" :categories="incomeCategories" @save="createIncomeTransaction" />
</template>

<style scoped>

</style>
