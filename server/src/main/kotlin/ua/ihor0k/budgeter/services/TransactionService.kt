package ua.ihor0k.budgeter.services

import io.ktor.server.plugins.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import ua.ihor0k.budgeter.db.*
import ua.ihor0k.budgeter.db.ExpenseTransactionDetail
import ua.ihor0k.budgeter.db.SecurityTransactionDetail
import ua.ihor0k.budgeter.dto.*
import java.math.BigDecimal
import java.time.YearMonth

class TransactionService {
    fun getExpenseTransactions(yearMonth: YearMonth): List<ExpenseTransactionResponse> = transaction {
        ExpenseTransactions
            .innerJoin(Transactions)
            .select(ExpenseTransactions.columns)
            .where { filterByYearMonthOp(yearMonth) }
            .let(ExpenseTransaction.Companion::wrapRows)
            .with(
                ExpenseTransaction::transaction,
                Transaction::account,
                ExpenseTransaction::details,
                ExpenseTransactionDetail::category
            )
            .map { it.toDto() }
    }

    fun createExpenseTransaction(expenseTransactionRequest: ExpenseTransactionRequest): ExpenseTransactionResponse =
        transaction {
            validateExpenseTransactionDetails(expenseTransactionRequest)
            val account = findAccount(expenseTransactionRequest.accountId)
            val transaction = Transaction.new {
                this.init(account, expenseTransactionRequest)
            }
            val expenseTransaction = ExpenseTransaction.new {
                this.init(transaction)
            }
            expenseTransactionRequest.details.forEachIndexed { index, expenseTransactionDetail ->
                val category = findExpenseCategory(expenseTransactionDetail)
                ExpenseTransactionDetail.new {
                    this.init(expenseTransaction, index, category, expenseTransactionDetail)
                }
            }
            expenseTransaction.toDto()
        }

    fun updateExpenseTransaction(id: Int, expenseTransactionRequest: ExpenseTransactionRequest): Unit = transaction {
        validateExpenseTransactionDetails(expenseTransactionRequest)
        ExpenseTransaction.findByIdAndUpdate(id) { expenseTransaction ->
            val account = findAccount(expenseTransactionRequest.accountId)
            expenseTransaction.transaction.init(account, expenseTransactionRequest)
            expenseTransaction.init(expenseTransaction.transaction)
            var processed = 0
            expenseTransaction.details.forEachIndexed { index, expenseTransactionDetail ->
                if (index < expenseTransactionRequest.details.size) {
                    processed += 1
                    val detail = expenseTransactionRequest.details[index]
                    val category = findExpenseCategory(detail)
                    expenseTransactionDetail.init(expenseTransaction, index, category, detail)
                } else {
                    expenseTransactionDetail.delete()
                }
            }
            expenseTransactionRequest.details.drop(processed).forEachIndexed { index, detail ->
                val category = findExpenseCategory(detail)
                ExpenseTransactionDetail.new {
                    this.init(expenseTransaction, processed + index, category, detail)
                }
            }
        } ?: throw NotFoundException("ExpenseTransaction not found")
    }

    fun deleteExpenseTransaction(id: Int): Unit = transaction {
        val deleted = ExpenseTransactions.deleteWhere { ExpenseTransactions.id eq id }
        if (deleted == 0) throw NotFoundException("ExpenseTransaction not found")
    }

    fun getIncomeTransactions(yearMonth: YearMonth): List<IncomeTransactionResponse> = transaction {
        IncomeTransactions
            .innerJoin(Transactions)
            .select(IncomeTransactions.columns)
            .where { filterByYearMonthOp(yearMonth) }
            .let(IncomeTransaction.Companion::wrapRows)
            .with(
                IncomeTransaction::transaction,
                Transaction::account,
                IncomeTransaction::category
            )
            .map { it.toDto() }
    }

    fun createIncomeTransaction(incomeTransactionRequest: IncomeTransactionRequest): IncomeTransactionResponse =
        transaction {
            val account = findAccount(incomeTransactionRequest.accountId)
            val category = findIncomeCategory(incomeTransactionRequest)
            val transaction = Transaction.new {
                this.init(account, incomeTransactionRequest)
            }
            val incomeTransaction = IncomeTransaction.new {
                this.init(transaction, category, incomeTransactionRequest)
            }
            incomeTransaction.toDto()
        }

    fun updateIncomeTransaction(id: Int, incomeTransactionRequest: IncomeTransactionRequest): Unit = transaction {
        IncomeTransaction.findByIdAndUpdate(id) { incomeTransaction ->
            val account = findAccount(incomeTransactionRequest.accountId)
            val category = findIncomeCategory(incomeTransactionRequest)
            incomeTransaction.transaction.init(account, incomeTransactionRequest)
            incomeTransaction.init(incomeTransaction.transaction, category, incomeTransactionRequest)
        } ?: throw NotFoundException("IncomeTransaction not found")
    }

    fun deleteIncomeTransaction(id: Int): Unit = transaction {
        val deleted = IncomeTransactions.deleteWhere { IncomeTransactions.id eq id }
        if (deleted == 0) throw NotFoundException("IncomeTransaction not found")
    }

    fun getSecurityTransactions(yearMonth: YearMonth): List<SecurityTransactionResponse> = transaction {
        SecurityTransactions
            .innerJoin(Transactions)
            .select(SecurityTransactions.columns)
            .where { filterByYearMonthOp(yearMonth) }
            .let(SecurityTransaction.Companion::wrapRows)
            .with(
                SecurityTransaction::transaction,
                Transaction::account,
                SecurityTransaction::security,
                SecurityTransaction::details
            )
            .map { it.toDto() }
    }

    fun createSecurityTransaction(securityTransactionRequest: SecurityTransactionRequest): SecurityTransactionResponse =
        transaction {
            validateSecurityTransactionDetails(securityTransactionRequest)
            val account = findAccount(securityTransactionRequest.accountId)
            val security = findSecurity(securityTransactionRequest.securityId)
            val transaction = Transaction.new {
                this.init(account, securityTransactionRequest)
            }
            val securityTransaction = SecurityTransaction.new {
                this.init(transaction, security, securityTransactionRequest)
            }
            securityTransactionRequest.details.forEachIndexed { index, securityTransactionDetail ->
                SecurityTransactionDetail.new {
                    this.init(securityTransaction, index, securityTransactionDetail)
                }
            }
            securityTransaction.toDto()
        }

    fun updateSecurityTransaction(id: Int, securityTransactionRequest: SecurityTransactionRequest): Unit = transaction {
        validateSecurityTransactionDetails(securityTransactionRequest)
        SecurityTransaction.findByIdAndUpdate(id) { securityTransaction ->
            val account = findAccount(securityTransactionRequest.accountId)
            val security = findSecurity(securityTransactionRequest.securityId)
            securityTransaction.transaction.init(account, securityTransactionRequest)
            securityTransaction.init(securityTransaction.transaction, security, securityTransactionRequest)
            var processed = 0
            securityTransaction.details.forEachIndexed { index, securityTransactionDetail ->
                if (index < securityTransactionRequest.details.size) {
                    processed += 1
                    val detail = securityTransactionRequest.details[index]
                    securityTransactionDetail.init(securityTransaction, index, detail)
                } else {
                    securityTransactionDetail.delete()
                }
            }
            securityTransactionRequest.details.drop(processed).forEachIndexed { index, detail ->
                SecurityTransactionDetail.new {
                    this.init(securityTransaction, processed + index, detail)
                }
            }
        } ?: throw NotFoundException("SecurityTransaction not found")
    }

    fun deleteSecurityTransaction(id: Int): Unit = transaction {
        val deleted = SecurityTransactions.deleteWhere { SecurityTransactions.id eq id }
        if (deleted == 0) throw NotFoundException("SecurityTransaction not found")
    }

    fun getTransferTransactions(yearMonth: YearMonth): List<TransferTransactionResponse> = transaction {
        TransferTransactions
            .join(
                Transactions,
                joinType = JoinType.INNER,
                onColumn = TransferTransactions.inTransactionId,
                otherColumn = Transactions.id
            )
            .select(TransferTransactions.columns)
            .where { filterByYearMonthOp(yearMonth) }
            .let(TransferTransaction.Companion::wrapRows)
            .with(
                TransferTransaction::inTransaction,
                TransferTransaction::outTransaction,
                Transaction::account
            )
            .map { it.toDto() }
    }

    fun createTransferTransaction(transferTransactionRequest: TransferTransactionRequest): TransferTransactionResponse =
        transaction {
            validateTransferTransactionRequest(transferTransactionRequest)
            val fromAccount = findAccount(transferTransactionRequest.fromAccountId)
            val toAccount = findAccount(transferTransactionRequest.toAccountId)
            val outTransaction = Transaction.new {
                this.initOut(fromAccount, transferTransactionRequest)
            }
            val inTransaction = Transaction.new {
                this.initIn(toAccount, transferTransactionRequest)
            }
            val transferTransaction = TransferTransaction.new {
                this.init(outTransaction, inTransaction)
            }
            transferTransaction.toDto()
        }

    fun updateTransferTransaction(id: Int, transferTransactionRequest: TransferTransactionRequest): Unit = transaction {
        validateTransferTransactionRequest(transferTransactionRequest)
        TransferTransaction.findByIdAndUpdate(id) { transferTransaction ->
            val fromAccount = findAccount(transferTransactionRequest.fromAccountId)
            val toAccount = findAccount(transferTransactionRequest.toAccountId)
            transferTransaction.outTransaction.initOut(fromAccount, transferTransactionRequest)
            transferTransaction.inTransaction.initIn(toAccount, transferTransactionRequest)
            transferTransaction.init(transferTransaction.outTransaction, transferTransaction.inTransaction)
        } ?: throw NotFoundException("TransferTransaction not found")
    }

    fun deleteTransferTransaction(id: Int): Unit = transaction {
        val deleted = TransferTransactions.deleteWhere { TransferTransactions.id eq id }
        if (deleted == 0) throw NotFoundException("TransferTransaction not found")
    }

    private fun findExpenseCategory(expenseTransactionDetail: ua.ihor0k.budgeter.dto.ExpenseTransactionDetail): ExpenseCategory {
        val category = findExpenseCategory(expenseTransactionDetail.categoryId)
        if (category.requiresDescription && expenseTransactionDetail.description.isNullOrEmpty()) {
            throw BadRequestException("Description cannot be empty")
        }
        return category
    }

    private fun findIncomeCategory(incomeTransactionRequest: IncomeTransactionRequest): IncomeCategory {
        val category = findIncomeCategory(incomeTransactionRequest.categoryId)
        if (category.requiresDescription && incomeTransactionRequest.description.isNullOrEmpty()) {
            throw BadRequestException("Description cannot be empty")
        }
        return category
    }

    private fun filterByYearMonthOp(yearMonth: YearMonth): Op<Boolean> =
        (Transactions.date greaterEq yearMonth.startOfMonth()) and (Transactions.date lessEq yearMonth.endOfMonth())

    private fun findAccount(id: Int): Account =
        Account.findById(id) ?: throw NotFoundException("Account not found")

    private fun findExpenseCategory(id: Int): ExpenseCategory =
        ExpenseCategory.findById(id) ?: throw NotFoundException("ExpenseCategory not found")

    private fun findIncomeCategory(id: Int): IncomeCategory =
        IncomeCategory.findById(id) ?: throw NotFoundException("IncomeCategory not found")

    private fun findSecurity(id: Int): Security =
        Security.findById(id) ?: throw NotFoundException("Security not found")

    private fun validateExpenseTransactionDetails(expenseTransactionRequest: ExpenseTransactionRequest) {
        if (expenseTransactionRequest.details.isEmpty()) throw BadRequestException("Details cannot be empty")
        val expectedSum = expenseTransactionRequest.amount.toBigDecimalScale2()
        val actualSum = expenseTransactionRequest.details.sumOf { it.amount.toBigDecimalScale2() }
        if (expectedSum != actualSum) {
            throw BadRequestException("Details sum is not equal to transaction amount")
        }
    }

    private fun validateSecurityTransactionDetails(securityTransactionRequest: SecurityTransactionRequest) {
        if (securityTransactionRequest.details.isEmpty()) throw BadRequestException("Details cannot be empty")
        val expectedSum = securityTransactionRequest.amount.toBigDecimalScale2()
        val fee = securityTransactionRequest.fee?.toBigDecimalScale2() ?: BigDecimal.ZERO
        val detailsSum = securityTransactionRequest.details.sumOf { it.totalPrice.toBigDecimalScale8() }
        val actualSum = detailsSum.add(fee)
        if (expectedSum != actualSum) {
            throw BadRequestException("Details sum is not equal to transaction amount")
        }
        securityTransactionRequest.details.forEach {
            if (!it.quantity.toBigDecimalScale8().multiply(it.unitPrice.toBigDecimalScale8())
                    .equals(it.totalPrice.toBigDecimalScale8())
            ) {
                throw BadRequestException("quantity * unitPrice != totalPrice")
            }
            // TODO: allow small error (smth like 0.0001)?
            // TODO: validate exchangeRate
            // TODO: validate quantity * unitPriceOriginalCurrency == totalPriceOriginalCurrency
        }
    }

    private fun validateTransferTransactionRequest(transferTransactionRequest: TransferTransactionRequest) {
        if (transferTransactionRequest.fromAccountId == transferTransactionRequest.toAccountId) {
            throw BadRequestException("Transfer to same account")
        }
    }

    private fun YearMonth.startOfMonth(): LocalDate = this.atDay(1).toKotlinLocalDate()
    private fun YearMonth.endOfMonth(): LocalDate = this.atEndOfMonth().toKotlinLocalDate()

    private fun Transaction.init(account: Account, expenseTransactionRequest: ExpenseTransactionRequest) {
        this.account = account
        this.date = expenseTransactionRequest.date
        this.amount = expenseTransactionRequest.amount.toBigDecimalScale2().negate()
        this.type = TransactionType.EXPENSE
    }

    private fun ExpenseTransaction.init(transaction: Transaction) {
        this.transaction = transaction
    }

    private fun ExpenseTransactionDetail.init(
        expenseTransaction: ExpenseTransaction,
        index: Int,
        category: ExpenseCategory,
        expenseTransactionDetailRequest: ua.ihor0k.budgeter.dto.ExpenseTransactionDetail
    ) {
        this.expenseTransaction = expenseTransaction
        this.index = index
        this.category = category
        this.amount = expenseTransactionDetailRequest.amount.toBigDecimalScale2()
        this.description = expenseTransactionDetailRequest.description
    }

    private fun Transaction.init(account: Account, incomeTransactionRequest: IncomeTransactionRequest) {
        this.account = account
        this.date = incomeTransactionRequest.date
        this.amount = incomeTransactionRequest.amount.toBigDecimalScale2()
        this.type = TransactionType.INCOME
    }

    private fun IncomeTransaction.init(
        transaction: Transaction,
        category: IncomeCategory,
        incomeTransactionRequest: IncomeTransactionRequest
    ) {
        this.transaction = transaction
        this.category = category
        this.description = incomeTransactionRequest.description
    }

    private fun Transaction.init(account: Account, securityTransactionRequest: SecurityTransactionRequest) {
        val isBuy = securityTransactionRequest.type == ua.ihor0k.budgeter.dto.SecurityTransactionType.BUY
        this.account = account
        this.date = securityTransactionRequest.date
        this.amount = securityTransactionRequest.amount.toBigDecimalScale2().let { if (isBuy) it.negate() else it }
        this.type = TransactionType.SECURITY
    }

    private fun SecurityTransaction.init(
        transaction: Transaction,
        security: Security,
        securityTransactionRequest: SecurityTransactionRequest
    ) {
        this.transaction = transaction
        this.security = security
        this.fee = securityTransactionRequest.fee?.toBigDecimalScale2()
        this.type = securityTransactionRequest.type.toDao()
    }

    private fun SecurityTransactionDetail.init(
        securityTransaction: SecurityTransaction,
        index: Int,
        securityTransactionDetail: ua.ihor0k.budgeter.dto.SecurityTransactionDetail
    ) {
        this.securityTransaction = securityTransaction
        this.index = index
        this.quantity = securityTransactionDetail.quantity.toBigDecimalScale8()
        this.exchangeRate = securityTransactionDetail.exchangeRate?.toBigDecimalScale8()
        this.unitPrice = securityTransactionDetail.unitPrice.toBigDecimalScale8()
        this.unitPriceOriginalCurrency = securityTransactionDetail.unitPriceOriginalCurrency?.toBigDecimalScale8()
        this.totalPrice = securityTransactionDetail.totalPrice.toBigDecimalScale8()
        this.totalPriceOriginalCurrency = securityTransactionDetail.totalPriceOriginalCurrency?.toBigDecimalScale8()
    }

    private fun Transaction.initOut(fromAccount: Account, transferTransactionRequest: TransferTransactionRequest) {
        this.account = fromAccount
        this.date = transferTransactionRequest.date
        this.amount = transferTransactionRequest.amount.toBigDecimalScale2().negate()
        this.type = TransactionType.TRANSFER
    }

    private fun Transaction.initIn(toAccount: Account, transferTransactionRequest: TransferTransactionRequest) {
        this.account = toAccount
        this.date = transferTransactionRequest.date
        this.amount = transferTransactionRequest.amount.toBigDecimalScale2()
        this.type = TransactionType.TRANSFER
    }

    private fun TransferTransaction.init(outTransaction: Transaction, inTransaction: Transaction) {
        this.outTransaction = outTransaction
        this.inTransaction = inTransaction
    }

    private fun ExpenseTransaction.toDto(): ExpenseTransactionResponse =
        ExpenseTransactionResponse(
            this.id.value,
            this.transaction.account.id.value,
            this.transaction.date,
            this.transaction.amount.toPlainString(),
            this.details.map { it.toDto() }
        )

    private fun ExpenseTransactionDetail.toDto(): ua.ihor0k.budgeter.dto.ExpenseTransactionDetail =
        ua.ihor0k.budgeter.dto.ExpenseTransactionDetail(
            this.category.id.value,
            this.amount.toPlainString(),
            this.description
        )

    private fun IncomeTransaction.toDto(): IncomeTransactionResponse =
        IncomeTransactionResponse(
            this.id.value,
            this.transaction.account.id.value,
            this.transaction.date,
            this.category.id.value,
            this.transaction.amount.toPlainString(),
            this.description
        )

    private fun SecurityTransaction.toDto(): SecurityTransactionResponse {
        val isBuy = this.type == ua.ihor0k.budgeter.db.SecurityTransactionType.BUY
        return SecurityTransactionResponse(
            this.id.value,
            this.transaction.id.value,
            this.transaction.date,
            this.transaction.amount.let { if (isBuy) it.negate() else it }.toPlainString(),
            this.security.id.value,
            this.fee.toString(),
            this.type.toDto(),
            this.details.map { it.toDto() }
        )
    }

    private fun SecurityTransactionDetail.toDto(): ua.ihor0k.budgeter.dto.SecurityTransactionDetail =
        ua.ihor0k.budgeter.dto.SecurityTransactionDetail(
            this.quantity.toPlainString(),
            this.exchangeRate?.toPlainString(),
            this.unitPrice.toPlainString(),
            this.unitPriceOriginalCurrency?.toPlainString(),
            this.totalPrice.toPlainString(),
            this.totalPriceOriginalCurrency?.toPlainString(),
        )

    private fun ua.ihor0k.budgeter.db.SecurityTransactionType.toDto(): ua.ihor0k.budgeter.dto.SecurityTransactionType =
        when (this) {
            ua.ihor0k.budgeter.db.SecurityTransactionType.BUY -> ua.ihor0k.budgeter.dto.SecurityTransactionType.BUY
            ua.ihor0k.budgeter.db.SecurityTransactionType.SELL -> ua.ihor0k.budgeter.dto.SecurityTransactionType.SELL
            ua.ihor0k.budgeter.db.SecurityTransactionType.DIVIDEND -> ua.ihor0k.budgeter.dto.SecurityTransactionType.DIVIDEND
        }

    private fun ua.ihor0k.budgeter.dto.SecurityTransactionType.toDao(): ua.ihor0k.budgeter.db.SecurityTransactionType =
        when (this) {
            ua.ihor0k.budgeter.dto.SecurityTransactionType.BUY -> ua.ihor0k.budgeter.db.SecurityTransactionType.BUY
            ua.ihor0k.budgeter.dto.SecurityTransactionType.SELL -> ua.ihor0k.budgeter.db.SecurityTransactionType.SELL
            ua.ihor0k.budgeter.dto.SecurityTransactionType.DIVIDEND -> ua.ihor0k.budgeter.db.SecurityTransactionType.DIVIDEND
        }

    private fun TransferTransaction.toDto(): TransferTransactionResponse =
        TransferTransactionResponse(
            this.id.value,
            this.outTransaction.account.id.value,
            this.inTransaction.account.id.value,
            this.inTransaction.date,
            this.inTransaction.amount.toPlainString()
        )

    private fun String.toBigDecimalScale2(): BigDecimal = BigDecimal(this).setScale(2)
    private fun String.toBigDecimalScale8(): BigDecimal = BigDecimal(this).setScale(8)
}