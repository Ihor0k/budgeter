<script setup lang="ts">
import ExpenseTransactionForm from '@/components/ExpenseTransactionForm.vue'
import IncomeTransactionForm from '@/components/IncomeTransactionForm.vue'
import TransactionsTable from '@/components/TransactionsTable.vue'
import type { ExpenseTransaction, IncomeTransaction } from '@/types/types.ts'
import api from '@/services/api.ts'

function createExpenseTransaction(expenseTransactionRequest: ExpenseTransaction) {
  api.createExpenseTransaction(expenseTransactionRequest)
    .then(response => console.log('OK', response)) // TODO
    .catch(err => console.error('Error adding expense transaction.', err.response.data ?? err.message))
}

function createIncomeTransaction(incomeTransactionRequest: IncomeTransaction) {
  api.createIncomeTransaction(incomeTransactionRequest)
    .then(response => console.log('OK', response)) // TODO
    .catch(err => console.error('Error adding income transaction.', err.response.data ?? err.message))
}
</script>

<template>
  <ExpenseTransactionForm @save="createExpenseTransaction" />
  <IncomeTransactionForm @save="createIncomeTransaction" />
  <TransactionsTable />
</template>

<style scoped>

</style>
