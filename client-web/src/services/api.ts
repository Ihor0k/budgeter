import axios from 'axios'
import type {
  ExpenseCategory,
  ExpenseTransaction,
  GetTransactionsParams,
  IncomeCategory,
  IncomeTransaction,
  Transaction,
  User
} from '@/types/types.ts'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL
})

async function createExpenseTransaction(request: ExpenseTransaction) {
  api.post<ExpenseTransaction>('/transactions/expenses', request).then(response => response.data)
}

async function createIncomeTransaction(request: IncomeTransaction) {
  api.post<ExpenseTransaction>('/transactions/incomes', request).then(response => response.data)
}

async function getTransactions(params: GetTransactionsParams): Promise<Transaction[]> {
  const { from, to, accountIds } = params

  const queryParams = new URLSearchParams()
  if (from) queryParams.append('from', from)
  if (to) queryParams.append('to', to)
  if (accountIds) accountIds.forEach(id => queryParams.append('accountIds', id.toString()))

  const queryString = queryParams.toString()
  const url = `/transactions${queryString ? `?${queryString}` : ''}`

  return api.get<Transaction[]>(url).then(response => response.data)
}

async function getUsers(): Promise<User[]> {
  return api.get<User[]>('/users').then(response => response.data)
}

async function getExpenseCategories(): Promise<ExpenseCategory[]> {
  return api.get<ExpenseCategory[]>('/expenseCategories').then(response => response.data)
}

async function getIncomeCategories(): Promise<IncomeCategory[]> {
  return api.get<IncomeCategory[]>('/incomeCategories').then(response => response.data)
}

export default {
  createExpenseTransaction,
  createIncomeTransaction,
  getTransactions,
  getUsers,
  getExpenseCategories,
  getIncomeCategories
}
