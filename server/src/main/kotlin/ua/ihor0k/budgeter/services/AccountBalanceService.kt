package ua.ihor0k.budgeter.services

import org.jetbrains.exposed.sql.sum
import org.jetbrains.exposed.sql.transactions.transaction
import ua.ihor0k.budgeter.db.Transactions
import ua.ihor0k.budgeter.dto.AccountBalanceResponse
import java.math.BigDecimal

class AccountBalanceService {
    fun getAccountBalances(): List<AccountBalanceResponse> = transaction {
        Transactions
            .select(Transactions.accountId, Transactions.amount.sum())
            .groupBy(Transactions.accountId)
            .map {
                val accountId: Int = it[Transactions.accountId].value
                val balance: BigDecimal = it[Transactions.amount.sum()] ?: BigDecimal.ZERO
                AccountBalanceResponse(accountId, balance.toPlainString())
            }
    }
}
