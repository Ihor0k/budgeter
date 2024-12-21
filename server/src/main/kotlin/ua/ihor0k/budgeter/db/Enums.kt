package ua.ihor0k.budgeter.db

enum class TransactionType {
    EXPENSE,
    INCOME,
    SECURITY,
    TRANSFER
}

enum class SecurityTransactionType {
    BUY,
    SELL,
    DIVIDEND
}