package ua.ihor0k.budgeter.services

import io.ktor.server.plugins.*
import org.jetbrains.exposed.sql.transactions.transaction
import ua.ihor0k.budgeter.db.ExpenseCategory
import ua.ihor0k.budgeter.dto.ExpenseCategoryRequest
import ua.ihor0k.budgeter.dto.ExpenseCategoryResponse

class ExpenseCategoryService {
    fun createExpenseCategory(expenseCategoryRequest: ExpenseCategoryRequest): ExpenseCategoryResponse = transaction {
        val expenseCategory = ExpenseCategory.new {
            this.name = expenseCategoryRequest.name
            this.requiresDescription = expenseCategoryRequest.requiresDescription
        }
        ExpenseCategoryResponse(
            expenseCategory.id.value,
            expenseCategory.name,
            expenseCategory.requiresDescription
        )
    }

    fun updateExpenseCategory(id: Int, expenseCategoryRequest: ExpenseCategoryRequest): Unit = transaction {
        ExpenseCategory.findByIdAndUpdate(id) { expenseCategory ->
            expenseCategory.name = expenseCategoryRequest.name
            expenseCategory.requiresDescription = expenseCategoryRequest.requiresDescription
        } ?: throw NotFoundException("ExpenseCategory not found")
    }

    fun getExpenseCategories(): List<ExpenseCategoryResponse> = transaction {
        ExpenseCategory.all()
            .map { expenseCategory ->
                ExpenseCategoryResponse(
                    expenseCategory.id.value,
                    expenseCategory.name,
                    expenseCategory.requiresDescription
                )
            }
    }
}