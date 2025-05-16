<script setup lang="ts">
import { useUsers } from '@/composables/useUsers.ts'
import { useAccountBalances } from '@/composables/useAccountBalances.ts'
import { computed } from 'vue'
import type { Account } from '@/types/types.ts'
import { WalletOutline } from '@vicons/ionicons5'
import { NCard, NDivider, NFlex, NIcon, NList, NListItem, NText } from 'naive-ui'
import Big from '@/utils/bigWithFormat.ts'

const users = useUsers()
const accountBalances = useAccountBalances()

const getBalance = (accountId: number): Big => {
  const balance = accountBalances.value.find(b => b.accountId === accountId)
  return balance ? new Big(balance.balance) : new Big(0)
}

const getUserTotal = (user: { accounts: readonly Account[] }): Big => {
  return user.accounts.reduce((sum, acc) => sum.plus(getBalance(acc.id!)), new Big(0))
}

const grandTotal = computed(() =>
  users.value.reduce((sum, user) => sum.plus(getUserTotal(user)), new Big(0))
)
</script>

<template>
  <n-card size="small" title="Balance">
    <template v-for="user in users" :key="user.id">
      <n-list :show-divider="false">
        <template #header>
          <n-flex justify="space-between">
            <n-text depth="1">{{ user.name }}</n-text>
            <n-text depth="1">{{ getUserTotal(user) }}</n-text>
          </n-flex>
        </template>
        <n-list-item
          v-for="account in user.accounts"
          :key="account.id"
        >
          <template #prefix>
            <n-icon :component="WalletOutline" size="14" class="icon" />
          </template>
          <n-flex justify="space-between">
            <n-text depth="2">{{ account.name }}</n-text>
            <n-text depth="2">{{ getBalance(account.id!) }}</n-text>
          </n-flex>
        </n-list-item>
      </n-list>
      <n-divider />
    </template>
    <n-flex justify="space-between">
      <n-text depth="1">Total</n-text>
      <n-text depth="1">{{ grandTotal }}</n-text>
    </n-flex>
  </n-card>
</template>

<style scoped>

</style>
