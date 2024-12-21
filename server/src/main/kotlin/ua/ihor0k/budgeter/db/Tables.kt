package ua.ihor0k.budgeter.db

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.postgresql.util.PGobject

object Users : IntIdTable("users") {
    val name = text("name")
}

object Accounts : IntIdTable("accounts") {
    val userId = reference("user_id", Users.id)
    val name = text("name")
}

object IncomeCategories : IntIdTable("income_categories") {
    val name = text("name")
    val requiresDescription = bool("requires_description").default(false)
}

object ExpenseCategories : IntIdTable("expense_categories") {
    val name = text("name")
    val requiresDescription = bool("requires_description").default(false)
}

object Securities : IntIdTable("securities") {
    val name = text("name")
    val originalCurrency = varchar("original_currency", 3)
    val ticker = text("ticker").nullable()
    val isin = varchar("isin", 12).nullable()
}

object Transactions : IntIdTable("transactions") {
    val accountId = reference("account_id", Accounts.id)
    val date = date("date")
    val amount = decimal("amount", 10, 2)
    val type = customEnumeration(
        "type", "transaction_type",
        { value -> TransactionType.valueOf(value as String) },
        { PGEnum("transaction_type", it) }
    )
}

object ExpenseTransactions : IntIdTable("expense_transactions") {
    val transactionId = reference("transaction_id", Transactions.id)
}

object ExpenseTransactionDetails : IntIdTable("expense_transaction_details") {
    val expenseTransactionId = reference("expense_transaction_id", ExpenseTransactions.id)
    val index = integer("index")
    val categoryId = reference("category_id", ExpenseCategories.id)
    val amount = decimal("amount", 10, 2)
    val description = text("description").nullable()

    init {
        uniqueIndex(expenseTransactionId, index)
    }
}

object IncomeTransactions : IntIdTable("income_transactions") {
    val transactionId = reference("transaction_id", Transactions.id)
    val categoryId = reference("category_id", IncomeCategories.id)
    val description = text("description").nullable()
}

object TransferTransactions : IntIdTable("transfer_transactions") {
    val outTransactionId = reference("out_transaction_id", Transactions.id)
    val inTransactionId = reference("in_transaction_id", Transactions.id)
}

object SecurityTransactions : IntIdTable("security_transactions") {
    val transactionId = reference("transaction_id", Transactions.id)
    val securityId = reference("security_id", Securities.id)
    val fee = decimal("fee", 10, 2).nullable()
    val type = customEnumeration(
        "type", "security_transaction_type",
        { value -> SecurityTransactionType.valueOf(value as String) },
        { PGEnum("security_transaction_type", it) }
    )
}

object SecurityTransactionDetails : IntIdTable("security_transaction_details") {
    val securityTransactionId = reference("security_transaction_id", SecurityTransactions.id)
    val index = integer("index")
    val quantity = decimal("quantity", 15, 8)
    val exchangeRate = decimal("exchange_rate", 15, 8).nullable()
    val unitPrice = decimal("unit_price", 15, 8)
    val unitPriceOriginalCurrency = decimal("unit_price_original_currency", 15, 8).nullable()
    val totalPrice = decimal("total_price", 15, 8)
    val totalPriceOriginalCurrency = decimal("total_price_original_currency", 15, 8).nullable()

    init {
        uniqueIndex(securityTransactionId, index)
    }
}

class PGEnum<T : Enum<T>>(enumTypeName: String, enumValue: T?) : PGobject() {
    init {
        value = enumValue?.name
        type = enumTypeName
    }
}
