package ua.ihor0k.budgeter.services

import io.ktor.server.plugins.*
import org.jetbrains.exposed.sql.transactions.transaction
import ua.ihor0k.budgeter.db.Security
import ua.ihor0k.budgeter.dto.SecurityRequest
import ua.ihor0k.budgeter.dto.SecurityResponse

class SecurityService {
    fun createSecurity(securityRequest: SecurityRequest): SecurityResponse = transaction {
        val security = Security.new {
            this.name = securityRequest.name
            this.originalCurrency = securityRequest.originalCurrency
            this.ticker = securityRequest.ticker
            this.isin = securityRequest.isin
        }
        SecurityResponse(
            security.id.value,
            security.name,
            security.originalCurrency,
            security.ticker,
            security.isin
        )
    }

    fun updateSecurity(id: Int, securityRequest: SecurityRequest): Unit = transaction {
        Security.findByIdAndUpdate(id) { security ->
            security.name = securityRequest.name
            security.originalCurrency = securityRequest.originalCurrency
            security.ticker = securityRequest.ticker
            security.isin = securityRequest.isin
        } ?: throw NotFoundException("Security not found")
    }

    fun getSecurities(): List<SecurityResponse> = transaction {
        Security.all()
            .map { security ->
                SecurityResponse(
                    security.id.value,
                    security.name,
                    security.originalCurrency,
                    security.ticker,
                    security.isin
                )
            }
    }

}