<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { Add, LockClosed, LockOpenOutline, Remove } from '@vicons/ionicons5'
import { Big } from 'big.js'
import type { ExpenseCategory, ExpenseTransaction, ExpenseTransactionDetail, User } from '@/types/types.ts'
import { NButton, NCard, NDatePicker, NFlex, NForm, NFormItem, NH2, NIcon, NInput, NSelect, NTable } from 'naive-ui'
import FormulaInput from '@/components/FormulaInput.vue'

const props = defineProps<{
  users: User[],
  categories: ExpenseCategory[],
  expenseTransaction?: ExpenseTransaction
}>()

const emit = defineEmits(['save'])
const state = reactive({
  accountId: props.expenseTransaction?.accountId ?? null,
  date: parseLocalDate(props.expenseTransaction?.date) ?? new Date().setHours(0, 0, 0, 0),
  amount: parseNumber(props.expenseTransaction?.amount) ?? 0,
  details: props.expenseTransaction?.details?.map(detail => ({
    categoryId: detail.categoryId,
    amount: Number(detail.amount),
    description: detail.description
  })) ?? [{ categoryId: null, amount: null, description: null }] as {
    categoryId: number | null;
    amount: number | null,
    description: string | null
  }[]
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

function detailAmountInputDisabled(index: number): boolean {
  return autoCalculateIndex.value === index
}

function addDetail() {
  state.details.push({ categoryId: null, amount: null, description: null })
}

function removeDetail(index: number) {
  state.details.splice(index, 1)

  // TODO: should I just set autoCalculateIndex to null ?
  if (autoCalculateIndex.value !== null && autoCalculateIndex.value >= state.details.length) {
    autoCalculateIndex.value = state.details.length - 1
  }
}

const autoCalculateIndex = ref<number | null>(0)
const sumManuallyEnteredAmounts = computed(() => state.details.reduce((sum, d, index) => index === autoCalculateIndex.value ? sum : sum.plus(new Big(d.amount ?? 0)), new Big(0)))
const autoCalculateAmount = computed(() => new Big(state.amount ?? 0).minus(sumManuallyEnteredAmounts.value))

watch([autoCalculateIndex, autoCalculateAmount, () => state.details], ([index, amount, details]) => {
  if (index !== null && index < details.length) {
    details[index].amount = amount.toNumber()
  }
})

function checkCategory(detail: { categoryId: number | null; description: string | null }) {
  if (requiresDescription(detail.categoryId) && !detail.description) {
    alert('Missing description')
  }
}

function save() {
  const totalDetailsAmount = state.details.reduce((sum, d) => sum.add(new Big(d.amount ?? 0)), new Big(0))
  if (!totalDetailsAmount.eq(new Big(state.amount ?? 0))) {
    alert('The total amount must match the sum of the details.')
    return
  }
  if (state.accountId == null) {
    alert('Account is missing')
    return
  }
  if (state.details.find((detail) => detail.amount == null)) {
    alert('Details amount is missing')
    return
  }
  if (state.details.find((detail) => detail.categoryId == null)) {
    alert('Details category is missing')
    return
  }
  state.details.forEach(checkCategory)

  const details: ExpenseTransactionDetail[] = state.details.map(detail => ({
    categoryId: detail.categoryId!,
    amount: detail.amount!.toString(),
    description: detail.description
  }))
  const expenseTransaction: ExpenseTransaction = {
    accountId: state.accountId,
    date: toLocalDate(state.date),
    amount: state.amount.toString(),
    details: details
  }

  emit('save', expenseTransaction)
}

function onLockClick(index: number) {
  const currentAutoCalculateIndex = autoCalculateIndex.value
  if (currentAutoCalculateIndex === index) {
    autoCalculateIndex.value = null
  } else {
    autoCalculateIndex.value = index
  }
}

</script>

<template>
  <n-card>
    <n-h2>{{ expenseTransaction ? 'Update' : 'Add' }} Expense Transaction</n-h2>

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

          <n-form-item label="Total Amount" for="amount" path="amount">
            <FormulaInput
              v-model:value="state.amount"
              :disabled="false"
            />
            <!--          <n-input-number :precision="2" v-model:value="state.amount" />-->
          </n-form-item>
        </n-flex>

        <n-flex>
          <n-table>
            <thead>
            <tr>
              <th>Category</th>
              <th>Amount</th>
              <th>Description</th>
              <th></th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="(detail, index) in state.details" :key="index">
              <td>
                <n-form-item path="category" :show-label="false" :show-feedback="false">
                  <n-select v-model:value="detail.categoryId"
                            :options="categoryOptions"
                            filterable
                            placeholder="Select a category">
                  </n-select>
                </n-form-item>
              </td>
              <td>
                <n-form-item path="detailAmount" :show-label="false" :show-feedback="false">
                  <FormulaInput
                    :disabled="detailAmountInputDisabled(index)"
                    v-model:value="detail.amount"
                  />
                  <n-button
                    circle
                    quaternary
                    :type="autoCalculateIndex === index ? 'primary' : 'default'"
                    @click="onLockClick(index)"
                  >
                    <template #icon>
                      <n-icon>
                        <LockClosed v-if="autoCalculateIndex === index" />
                        <LockOpenOutline v-else />
                      </n-icon>
                    </template>
                  </n-button>
                </n-form-item>
              </td>
              <td>
                <n-form-item path="description" :show-label="false" :show-feedback="false">
                  <n-input class="description" v-model:value="detail.description"
                           :required="requiresDescription(detail.categoryId)"
                           type="textarea"
                           :autosize="{ minRows: 1 }" />
                </n-form-item>
              </td>

              <td>
                <n-button
                  circle
                  @click="removeDetail(index)"
                  :disabled="state.details.length === 1"
                >
                  <template #icon>
                    <n-icon>
                      <Remove />
                    </n-icon>
                  </template>
                </n-button>
              </td>
            </tr>
            </tbody>
          </n-table>

          <n-button circle @click="addDetail">
            <template #icon>
              <n-icon>
                <Add />
              </n-icon>
            </template>
          </n-button>
        </n-flex>
        <n-button
          class="save-button"
          type="success"
          @click="save"
        >
          {{ expenseTransaction ? 'Update' : 'Save' }}
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
