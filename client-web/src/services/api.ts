import axios from 'axios'
import type { GetTransactionsParams, Transaction } from '@/types/types.ts'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL
})

export async function getTransactions(params: GetTransactionsParams): Promise<Transaction[]> {
  const { from, to, accountIds } = params

  const queryParams = new URLSearchParams()
  if (from) queryParams.append('from', from)
  if (to) queryParams.append('to', to)
  if (accountIds) accountIds.forEach(id => queryParams.append('accountIds', id.toString()))

  const queryString = queryParams.toString()
  const url = `/transactions${queryString ? `?${queryString}` : ''}`

  return api.get<Transaction[]>(url).then(response => response.data)
}

export default {
  api,  // TODO: remove 'api' and export specific functions
  getTransactions
}
