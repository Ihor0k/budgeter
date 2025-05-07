<script setup lang="ts">
import type { GetTransactionsParams, Transaction } from '@/types/types.ts'
import { type DataTableBaseColumn, type DataTableColumns, type DataTableFilterState, NDataTable } from 'naive-ui'
import { computed, h, reactive, ref, type VNodeChild, watchEffect } from 'vue'
import DateRangePicker from '@/components/DateRangePicker.vue'
import api from '@/services/api.ts'
import { useUsers } from '@/composables/useUsers.ts'

const users = useUsers()

const transactions = ref<Transaction[]>([])

const accounts = computed(() => Object.fromEntries(
  users.value.flatMap(user => user.accounts.map(account => [account.id, `${user.name} ${account.name}`]))
))

const dateRangeFilterInitValue: [string, string] = ['2025-01-01', '2025-04-30']
const dateRangeFilter = ref<[string, string] | null>(dateRangeFilterInitValue)
const accountFilter = ref<number[]>([])

const dateColumn = reactive<DataTableBaseColumn<Transaction>>({
  title: 'Date',
  key: 'date',
  filter: true,
  filterOptionValues: dateRangeFilterInitValue,
  renderFilterMenu: ({ hide }): VNodeChild => {
    return h(
      DateRangePicker,
      {
        defaultValue: dateRangeFilter.value,
        onConfirm: (value: [string, string] | null) => {
          hide()
          dateColumn.filterOptionValues = value
          dateRangeFilter.value = value
        }
      }
    )
  }
})

const accountColumn = computed<DataTableBaseColumn<Transaction>>(() => ({
  title: 'Account',
  key: 'account',
  filter: true,
  filterOptions: users.value.flatMap(user =>
    user.accounts.map(account => (
      {
        label: user.name + ' - ' + account.name,
        value: account.id!
      }
    ))
  )
}))

const amountColumn: DataTableBaseColumn<Transaction> = {
  title: 'Amount',
  key: 'amount'
}

const columns = computed<DataTableColumns<Transaction>>(() => [
  dateColumn,
  accountColumn.value,
  amountColumn
])

function handleUpdateFilter(filters: DataTableFilterState, sourceColumn: DataTableBaseColumn) {
  if (sourceColumn.key === 'account') {
    accountFilter.value = filters[sourceColumn.key] as number[]
  }
}

watchEffect(async () => {
  const dateRange = dateRangeFilter.value
  const accountIds = accountFilter.value
  transactions.value = await loadTransactions(dateRange, accountIds)
})

function loadTransactions(dateRange: [string, string] | null = null, accountIds: number[] = []): Promise<Transaction[]> {
  const params: GetTransactionsParams = {
    from: dateRange?.[0],
    to: dateRange?.[1],
    accountIds: accountIds
  }
  return api.getTransactions(params)
}


const data = computed(() => transactions.value.map(transaction => ({
  id: transaction.id,
  account: accounts.value[transaction.accountId],
  date: transaction.date,
  amount: transaction.amount,
  type: transaction.type
})))
</script>

<template>
  <n-data-table
    :columns="columns"
    :data="data"
    @update:filters="handleUpdateFilter"
  />
</template>

<style scoped>

</style>
