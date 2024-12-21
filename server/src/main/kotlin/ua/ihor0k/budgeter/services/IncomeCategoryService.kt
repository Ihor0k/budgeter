package ua.ihor0k.budgeter.services

import io.ktor.server.plugins.*
import org.jetbrains.exposed.sql.transactions.transaction
import ua.ihor0k.budgeter.db.IncomeCategory
import ua.ihor0k.budgeter.dto.IncomeCategoryRequest
import ua.ihor0k.budgeter.dto.IncomeCategoryResponse

class IncomeCategoryService {
    fun createIncomeCategory(incomeCategoryRequest: IncomeCategoryRequest): IncomeCategoryResponse = transaction {
        val incomeCategory = IncomeCategory.new {
            this.name = incomeCategoryRequest.name
            this.requiresDescription = incomeCategoryRequest.requiresDescription
        }
        IncomeCategoryResponse(
            incomeCategory.id.value,
            incomeCategory.name,
            incomeCategory.requiresDescription
        )
    }

    fun updateIncomeCategory(id: Int, incomeCategoryRequest: IncomeCategoryRequest): Unit = transaction {
        IncomeCategory.findByIdAndUpdate(id) { incomeCategory ->
            incomeCategory.name = incomeCategoryRequest.name
            incomeCategory.requiresDescription = incomeCategoryRequest.requiresDescription
        } ?: throw NotFoundException("IncomeCategory not found")
    }

    fun getIncomeCategories(): List<IncomeCategoryResponse> = transaction {
        IncomeCategory.all()
            .map { incomeCategory ->
                IncomeCategoryResponse(
                    incomeCategory.id.value,
                    incomeCategory.name,
                    incomeCategory.requiresDescription
                )
            }
    }
}