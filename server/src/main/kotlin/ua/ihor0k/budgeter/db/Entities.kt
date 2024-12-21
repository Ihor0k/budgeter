package ua.ihor0k.budgeter.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var name by Users.name

    val accounts by Account referrersOn Accounts.userId
}

class Account(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Account>(Accounts)

    var user by User referencedOn Accounts.userId
    var name by Accounts.name
}

class IncomeCategory(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<IncomeCategory>(IncomeCategories)

    var name by IncomeCategories.name
    var requiresDescription by IncomeCategories.requiresDescription
}

class ExpenseCategory(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ExpenseCategory>(ExpenseCategories)

    var name by ExpenseCategories.name
    var requiresDescription by ExpenseCategories.requiresDescription
}

class Security(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Security>(Securities)

    var name by Securities.name
    var originalCurrency by Securities.originalCurrency
    var ticker by Securities.ticker
    var isin by Securities.isin
}

class Transaction(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Transaction>(Transactions)

    var account by Account referencedOn Transactions.accountId
    var date by Transactions.date
    var amount by Transactions.amount
    var type by Transactions.type

    val expenseTransaction by ExpenseTransaction optionalBackReferencedOn ExpenseTransactions.transactionId
    val incomeTransaction by IncomeTransaction optionalBackReferencedOn IncomeTransactions.transactionId
    val securityTransaction by SecurityTransaction optionalBackReferencedOn SecurityTransactions.transactionId
    val outTransferTransaction by TransferTransaction optionalBackReferencedOn TransferTransactions.outTransactionId
    val inTransferTransaction by TransferTransaction optionalBackReferencedOn TransferTransactions.inTransactionId
}

class ExpenseTransaction(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ExpenseTransaction>(ExpenseTransactions)

    var transaction by Transaction referencedOn ExpenseTransactions.transactionId

    val details by ExpenseTransactionDetail referrersOn ExpenseTransactionDetails.expenseTransactionId orderBy ExpenseTransactionDetails.index
}

class ExpenseTransactionDetail(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ExpenseTransactionDetail>(ExpenseTransactionDetails)

    var expenseTransaction by ExpenseTransaction referencedOn ExpenseTransactionDetails.expenseTransactionId
    var index by ExpenseTransactionDetails.index
    var category by ExpenseCategory referencedOn ExpenseTransactionDetails.categoryId
    var amount by ExpenseTransactionDetails.amount
    var description by ExpenseTransactionDetails.description
}

class IncomeTransaction(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<IncomeTransaction>(IncomeTransactions)

    var transaction by Transaction referencedOn IncomeTransactions.transactionId
    var category by IncomeCategory referencedOn IncomeTransactions.categoryId
    var description by IncomeTransactions.description
}

class TransferTransaction(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TransferTransaction>(TransferTransactions)

    var outTransaction by Transaction referencedOn TransferTransactions.outTransactionId
    var inTransaction by Transaction referencedOn TransferTransactions.inTransactionId
}

class SecurityTransaction(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SecurityTransaction>(SecurityTransactions)

    var transaction by Transaction referencedOn SecurityTransactions.transactionId
    var security by Security referencedOn SecurityTransactions.securityId
    var fee by SecurityTransactions.fee
    var type by SecurityTransactions.type

    val details by SecurityTransactionDetail referrersOn SecurityTransactionDetails.securityTransactionId orderBy SecurityTransactionDetails.index
}

class SecurityTransactionDetail(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SecurityTransactionDetail>(SecurityTransactionDetails)

    var securityTransaction by SecurityTransaction referencedOn SecurityTransactionDetails.securityTransactionId
    var index by SecurityTransactionDetails.index
    var quantity by SecurityTransactionDetails.quantity
    var exchangeRate by SecurityTransactionDetails.exchangeRate
    var unitPrice by SecurityTransactionDetails.unitPrice
    var unitPriceOriginalCurrency by SecurityTransactionDetails.unitPriceOriginalCurrency
    var totalPrice by SecurityTransactionDetails.totalPrice
    var totalPriceOriginalCurrency by SecurityTransactionDetails.totalPriceOriginalCurrency
}