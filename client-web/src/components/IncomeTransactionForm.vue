<script setup lang="ts">
import { computed, reactive } from 'vue'
import type { IncomeCategory, IncomeTransaction, User } from '@/types/types.ts'
import { NButton, NCard, NDatePicker, NFlex, NForm, NFormItem, NH2, NInput, NSelect } from 'naive-ui'
import FormulaInput from '@/components/FormulaInput.vue'

const props = defineProps<{
  users: User[],
  categories: IncomeCategory[],
  incomeTransaction?: IncomeTransaction
}>()

const emit = defineEmits(['save'])
const state = reactive({
  accountId: props.incomeTransaction?.accountId ?? null,
  date: parseLocalDate(props.incomeTransaction?.date) ?? new Date().setHours(0, 0, 0, 0),
  categoryId: props.incomeTransaction?.categoryId ?? null,
  amount: parseNumber(props.incomeTransaction?.amount) ?? 0,
  description: props.incomeTransaction?.description ?? null
})

function parseLocalDate(dateString?: string): number | null {
  if (dateString === undefined) return null
  const date = new Date(dateString)
  return new Date(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate(), 0, 0, 0).getTime()
}

function parseNumber(value?: string): number | null {
  if (value === undefined) return null
  return Number(value)
}

function toLocalDate(timestamp: number): string {
  const date = new Date(timestamp)
  return new Date(date.getTime() - (date.getTimezoneOffset() * 60000)).toISOString().substring(0, 10)
}

const accountOptions = computed(() => props.users.flatMap(user => user.accounts.map(account => ({
  label: user.name + ' - ' + account.name,
  value: account.id
}))))

const categoryOptions = computed(() => props.categories.map(category => ({
  label: category.name,
  value: category.id
})))

function requiresDescription(categoryId: number | null): boolean {
  if (categoryId === null) return false
  return props.categories.find((category) => category.id === categoryId)?.requiresDescription ?? false
}


function save() {
  if (state.accountId == null) {
    alert('Account is missing')
    return
  }
  if (state.categoryId == null) {
    alert('Category is missing')
    return
  }
  if (requiresDescription(state.categoryId) && !state.description) {
    alert('Description is missing')
    return
  }

  const incomeTransaction: IncomeTransaction = {
    accountId: state.accountId,
    date: toLocalDate(state.date),
    categoryId: state.categoryId,
    amount: state.amount.toString(),
    description: state.description
  }

  emit('save', incomeTransaction)
}

</script>

<template>
  <n-card>
    <n-h2>{{ incomeTransaction ? 'Update' : 'Add' }} Income Transaction</n-h2>

    <n-form :model="state">
      <n-flex vertical>
        <n-flex>
          <n-form-item label="Date" for="date" path="date">
            <n-date-picker format="dd-MMM" v-model:value="state.date" type="date" />
          </n-form-item>

          <n-form-item label="Account" path="accountId">
            <n-select class="account"
                      v-model:value="state.accountId"
                      placeholder="Select an account"
                      :options="accountOptions"
                      filterable
            />
          </n-form-item>

          <n-form-item label="Category" for="category" path="category">
            <n-select v-model:value="state.categoryId"
                      :options="categoryOptions"
                      filterable
                      placeholder="Select a category">
            </n-select>
          </n-form-item>

          <n-form-item label="Amount" for="amount" path="amount">
            <FormulaInput v-model:value="state.amount" />
          </n-form-item>

          <n-form-item label="Description" for="description" path="description">
            <n-input class="description" v-model:value="state.description"
                     :required="requiresDescription(state.categoryId)"
                     type="textarea"
                     :autosize="{ minRows: 1 }" />
          </n-form-item>
        </n-flex>

        <n-button
          class="save-button"
          type="success"
          @click="save"
        >
          {{ incomeTransaction ? 'Update' : 'Save' }}
        </n-button>
      </n-flex>
    </n-form>
  </n-card>
</template>

<style scoped>

.n-input-number,
.n-input,
.n-select,
.n-date-picker {
  width: 15em;
}

.description {
  width: 100%;
  min-width: 15em;
}

tbody {
  vertical-align: top;
}

td {
  padding: 4px;
}

.save-button {
  align-self: flex-start;
}

</style>
