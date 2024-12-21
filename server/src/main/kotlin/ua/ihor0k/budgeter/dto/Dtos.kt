package ua.ihor0k.budgeter.dto

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class UserRequest(val name: String)

@Serializable
data class UserResponse(val id: Int, val name: String, val accounts: List<AccountResponse>)

@Serializable
data class AccountRequest(val name: String)

@Serializable
data class AccountResponse(val id: Int, val name: String)

@Serializable
data class IncomeCategoryRequest(val name: String, val requiresDescription: Boolean)

@Serializable
data class IncomeCategoryResponse(val id: Int, val name: String, val requiresDescription: Boolean)

@Serializable
data class ExpenseCategoryRequest(val name: String, val requiresDescription: Boolean)

@Serializable
data class ExpenseCategoryResponse(val id: Int, val name: String, val requiresDescription: Boolean)

@Serializable
data class SecurityRequest(val name: String, val originalCurrency: String, val ticker: String?, val isin: String?)

@Serializable
data class SecurityResponse(val id: Int, val name: String, val originalCurrency: String, val ticker: String?, val isin: String?)

@Serializable
data class ExpenseTransactionRequest(
    val accountId: Int,
    val date: LocalDate,
    val amount: String,
    val details: List<ExpenseTransactionDetail>
)

@Serializable
data class ExpenseTransactionDetail(
    val categoryId: Int,
    val amount: String,
    val description: String?
)

@Serializable
data class ExpenseTransactionResponse(
    val id: Int,
    val accountId: Int,
    val date: LocalDate,
    val amount: String,
    val details: List<ExpenseTransactionDetail>
)

@Serializable
data class IncomeTransactionRequest(
    val accountId: Int,
    val date: LocalDate,
    val categoryId: Int,
    val amount: String,
    val description: String?
)

@Serializable
data class IncomeTransactionResponse(
    val id: Int,
    val accountId: Int,
    val date: LocalDate,
    val categoryId: Int,
    val amount: String,
    val description: String?
)

@Serializable
data class SecurityTransactionRequest(
    val accountId: Int,
    val date: LocalDate,
    val amount: String,
    val securityId: Int,
    val fee: String?,
    val type: SecurityTransactionType,
    val details: List<SecurityTransactionDetail>
)

@Serializable
data class SecurityTransactionResponse(
    val id: Int,
    val accountId: Int,
    val date: LocalDate,
    val amount: String,
    val securityId: Int,
    val fee: String?,
    val type: SecurityTransactionType,
    val details: List<SecurityTransactionDetail>
)

@Serializable
enum class SecurityTransactionType {
    BUY,
    SELL,
    DIVIDEND
}

@Serializable
data class SecurityTransactionDetail(
    val quantity: String,
    val exchangeRate: String?,
    val unitPrice: String,
    val unitPriceOriginalCurrency: String?,
    val totalPrice: String,
    val totalPriceOriginalCurrency: String?
)

@Serializable
data class TransferTransactionRequest(
    val fromAccountId: Int,
    val toAccountId: Int,
    val date: LocalDate,
    val amount: String
)

@Serializable
data class TransferTransactionResponse(
    val id: Int,
    val fromAccountId: Int,
    val toAccountId: Int,
    val date: LocalDate,
    val amount: String
)
