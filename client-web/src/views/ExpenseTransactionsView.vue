<script setup lang="ts">
import ExpenseTransactionForm from '@/components/ExpenseTransactionForm.vue'
import IncomeTransactionForm from '@/components/IncomeTransactionForm.vue'
import TransactionsTable from '@/components/TransactionsTable.vue'
import type { ExpenseTransaction, IncomeTransaction } from '@/types/types.ts'
import api from '@/services/api.ts'
import { refreshAccountBalances } from '@/composables/useAccountBalances.ts'
import AccountBalancesPanel from '@/components/AccountBalancesPanel.vue'

function createExpenseTransaction(expenseTransactionRequest: ExpenseTransaction) {
  api.createExpenseTransaction(expenseTransactionRequest)
    .then(response => {
      console.log('OK', response) // TODO
      refreshAccountBalances()
    })
    .catch(err => console.error('Error adding expense transaction.', err.response.data ?? err.message))
}

function createIncomeTransaction(incomeTransactionRequest: IncomeTransaction) {
  api.createIncomeTransaction(incomeTransactionRequest)
    .then(response => {
      console.log('OK', response) // TODO
      refreshAccountBalances()
    })
    .catch(err => console.error('Error adding income transaction.', err.response.data ?? err.message))
}
</script>

<template>
  <ExpenseTransactionForm @save="createExpenseTransaction" />
  <IncomeTransactionForm @save="createIncomeTransaction" />
  <TransactionsTable />
  <div class="account-balance-panel-wrapper">
    <AccountBalancesPanel />
  </div>
</template>

<style scoped>
.account-balance-panel-wrapper {
  position: fixed;
  top: 1rem;
  right: 1rem;
  width: 20em;
  z-index: 1;
}

</style>
