package ua.ihor0k.budgeter

// TODO: kotlinx-datetime should introduce YearMonth in the next release. https://github.com/Kotlin/kotlinx-datetime/pull/457
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.dataconversion.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.util.converters.*
import io.ktor.util.reflect.*
import ua.ihor0k.budgeter.dto.*
import ua.ihor0k.budgeter.services.*
import java.time.YearMonth
import kotlin.reflect.KProperty

fun Application.configureRouting() {
    val userService = UserService()
    val incomeCategoryService = IncomeCategoryService()
    val expenseCategoryService = ExpenseCategoryService()
    val transactionService = TransactionService()
    val securityService = SecurityService()

    routing {
        route("/users") {
            get {
                val users = userService.getUsers()
                call.respond(HttpStatusCode.OK, users)
            }
            post {
                val userRequest = call.receive<UserRequest>()
                val userResponse = userService.createUser(userRequest)
                call.respond(HttpStatusCode.Created, userResponse)
            }
            put("/{id}") {
                val id by call.pathParameterDelegate<Int>()
                val userRequest = call.receive<UserRequest>()
                userService.updateUser(id, userRequest)
                call.respond(HttpStatusCode.NoContent)
            }
            route("/{userId}/accounts") {
                post {
                    val userId by call.pathParameterDelegate<Int>()
                    val accountRequest = call.receive<AccountRequest>()
                    val accountResponse = userService.addUserAccount(userId, accountRequest)
                    call.respond(HttpStatusCode.Created, accountResponse)
                }
                put("/{id}") {
                    val userId by call.pathParameterDelegate<Int>()
                    val id by call.pathParameterDelegate<Int>()
                    val accountRequest = call.receive<AccountRequest>()
                    userService.updateUserAccount(userId, id, accountRequest)
                    call.respond(HttpStatusCode.NoContent)
                }
            }
        }
        route("/incomeCategories") {
            get {
                val incomeCategories = incomeCategoryService.getIncomeCategories()
                call.respond(HttpStatusCode.OK, incomeCategories)
            }
            post {
                val incomeCategoryRequest = call.receive<IncomeCategoryRequest>()
                val incomeCategoryResponse = incomeCategoryService.createIncomeCategory(incomeCategoryRequest)
                call.respond(HttpStatusCode.Created, incomeCategoryResponse)
            }
            put("/{id}") {
                val id by call.pathParameterDelegate<Int>()
                val incomeCategoryRequest = call.receive<IncomeCategoryRequest>()
                incomeCategoryService.updateIncomeCategory(id, incomeCategoryRequest)
                call.respond(HttpStatusCode.NoContent)
            }
        }
        route("/expenseCategories") {
            get {
                val expenseCategories = expenseCategoryService.getExpenseCategories()
                call.respond(HttpStatusCode.OK, expenseCategories)
            }
            post {
                val expenseCategoryRequest = call.receive<ExpenseCategoryRequest>()
                val expenseCategoryResponse = expenseCategoryService.createExpenseCategory(expenseCategoryRequest)
                call.respond(HttpStatusCode.Created, expenseCategoryResponse)
            }
            put("/{id}") {
                val id: Int by call.pathParameters
                val expenseCategoryRequest = call.receive<ExpenseCategoryRequest>()
                expenseCategoryService.updateExpenseCategory(id, expenseCategoryRequest)
                call.respond(HttpStatusCode.NoContent)
            }
        }
        route("/transactions") {
            route("/expenses") {
                get {
                    val yearMonth by call.queryParameterDelegate<YearMonth>()
                    val expenseTransactions = transactionService.getExpenseTransactions(yearMonth)
                    call.respond(HttpStatusCode.OK, expenseTransactions)
                }
                post {
                    val expenseTransactionRequest = call.receive<ExpenseTransactionRequest>()
                    val expenseTransactionResponse =
                        transactionService.createExpenseTransaction(expenseTransactionRequest)
                    call.respond(HttpStatusCode.OK, expenseTransactionResponse)
                }
                put("/{id}") {
                    val id by call.pathParameterDelegate<Int>()
                    val expenseTransactionRequest = call.receive<ExpenseTransactionRequest>()
                    transactionService.updateExpenseTransaction(id, expenseTransactionRequest)
                    call.respond(HttpStatusCode.NoContent)
                }
                delete("/{id}") {
                    val id by call.pathParameterDelegate<Int>()
                    transactionService.deleteExpenseTransaction(id)
                    call.respond(HttpStatusCode.NoContent)
                }
            }
            route("/incomes") {
                get {
                    val yearMonth by call.queryParameterDelegate<YearMonth>()
                    val incomeTransactions = transactionService.getIncomeTransactions(yearMonth)
                    call.respond(HttpStatusCode.OK, incomeTransactions)
                }
                post {
                    val incomeTransactionRequest = call.receive<IncomeTransactionRequest>()
                    val incomeTransactionResponse = transactionService.createIncomeTransaction(incomeTransactionRequest)
                    call.respond(HttpStatusCode.OK, incomeTransactionResponse)
                }
                put("/{id}") {
                    val id by call.pathParameterDelegate<Int>()
                    val incomeTransactionRequest = call.receive<IncomeTransactionRequest>()
                    transactionService.updateIncomeTransaction(id, incomeTransactionRequest)
                    call.respond(HttpStatusCode.NoContent)
                }
                delete("/{id}") {
                    val id by call.pathParameterDelegate<Int>()
                    transactionService.deleteIncomeTransaction(id)
                    call.respond(HttpStatusCode.NoContent)
                }
            }
            route("/securities") {
                get {
                    val yearMonth by call.queryParameterDelegate<YearMonth>()
                    val securityTransactions = transactionService.getSecurityTransactions(yearMonth)
                    call.respond(HttpStatusCode.OK, securityTransactions)
                }
                post {
                    val securityTransactionRequest = call.receive<SecurityTransactionRequest>()
                    val securityTransactionResponse =
                        transactionService.createSecurityTransaction(securityTransactionRequest)
                    call.respond(HttpStatusCode.OK, securityTransactionResponse)
                }
                put("/{id}") {
                    val id by call.pathParameterDelegate<Int>()
                    val securityTransactionRequest = call.receive<SecurityTransactionRequest>()
                    transactionService.updateSecurityTransaction(id, securityTransactionRequest)
                    call.respond(HttpStatusCode.NoContent)
                }
                delete("/{id}") {
                    val id by call.pathParameterDelegate<Int>()
                    transactionService.deleteSecurityTransaction(id)
                    call.respond(HttpStatusCode.NoContent)
                }
            }
            route("/transfers") {
                get {
                    val yearMonth by call.queryParameterDelegate<YearMonth>()
                    val transferTransactions = transactionService.getTransferTransactions(yearMonth)
                    call.respond(HttpStatusCode.OK, transferTransactions)
                }
                post {
                    val transferTransactionRequest = call.receive<TransferTransactionRequest>()
                    val transferTransactionResponse = transactionService.createTransferTransaction(transferTransactionRequest)
                    call.respond(HttpStatusCode.OK, transferTransactionResponse)
                }
                put("/{id}") {
                    val id by call.pathParameterDelegate<Int>()
                    val transferTransactionRequest = call.receive<TransferTransactionRequest>()
                    transactionService.updateTransferTransaction(id, transferTransactionRequest)
                    call.respond(HttpStatusCode.NoContent)
                }
                delete("/{id}") {
                    val id by call.pathParameterDelegate<Int>()
                    transactionService.deleteTransferTransaction(id)
                    call.respond(HttpStatusCode.NoContent)
                }
            }
        }
        route("/securities") {
            get {
                val securities = securityService.getSecurities()
                call.respond(HttpStatusCode.OK, securities)
            }
            post {
                val securityRequest = call.receive<SecurityRequest>()
                val securityResponse = securityService.createSecurity(securityRequest)
                call.respond(HttpStatusCode.OK, securityResponse)
            }
            put("/{id}") {
                val id by call.pathParameterDelegate<Int>()
                val securityRequest = call.receive<SecurityRequest>()
                securityService.updateSecurity(id, securityRequest)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}

private class ParameterDelegate<R>(
    private val parameters: Parameters,
    private val conversionService: ConversionService,
    private val typeInfo: TypeInfo
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): R {
        val name = property.name
        val values = parameters.getAll(name) ?: throw MissingRequestParameterException(name)
        return try {
            @Suppress("UNCHECKED_CAST")
            conversionService.fromValues(values, typeInfo) as R
        } catch (cause: Exception) {
            throw ParameterConversionException(name, typeInfo.type.simpleName ?: typeInfo.type.toString(), cause)
        }
    }
}

private inline fun <reified T> RoutingCall.queryParameterDelegate(): ParameterDelegate<T> =
    ParameterDelegate(this.queryParameters, this.application.conversionService, typeInfo<T>())

private inline fun <reified T> RoutingCall.pathParameterDelegate(): ParameterDelegate<T> =
    ParameterDelegate(this.pathParameters, this.application.conversionService, typeInfo<T>())

