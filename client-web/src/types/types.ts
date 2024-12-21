export interface User {
  id?: number;
  name: string;
  accounts: Account[];
}

export interface Account {
  id?: number;
  name: string;
}

export interface ExpenseCategory {
  id?: number;
  name: string;
  requiresDescription: boolean;
}

export interface ExpenseTransaction {
  id?: number;
  accountId: number;
  date: string;
  amount: string;
  details: ExpenseTransactionDetail[];
}

export interface ExpenseTransactionDetail {
  categoryId: number;
  amount: string;
  description: string;
}
