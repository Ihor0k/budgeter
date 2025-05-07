package ua.ihor0k.budgeter

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.dataconversion.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.Database

fun Application.module() {
    Database.connect(
        url = environment.config.property("postgres.url").getString(),
        user = environment.config.property("postgres.user").getString(),
        password = environment.config.property("postgres.password").getString(),
    )
//    transaction {
//        val ihor = User.new {
//            name = "Ihor"
//        }
//        val nastya = User.new {
//            name = "Nastya"
//        }
//        Account.new {
//            name = "Nordea"
//            user = ihor
//        }
//        Account.new {
//            name = "Monobanm"
//            user = ihor
//        }
//        Account.new {
//            name = "Nordea"
//            user = nastya
//        }
//        Account.new {
//            name = "S-Panki"
//            user = nastya
//        }
//        ExpenseCategory.new {
//            name = "Food"
//            requiresDescription = false
//        }
//        ExpenseCategory.new {
//            name = "Household needs"
//            requiresDescription = false
//        }
//        ExpenseCategory.new {
//            name = "Travel"
//            requiresDescription = true
//        }
//        ExpenseCategory.new {
//            name = "Other"
//            requiresDescription = true
//        }
//        IncomeCategory.new {
//            name = "Salary"
//            requiresDescription = false
//        }
//        IncomeCategory.new {
//            name = "S-Pankki"
//            requiresDescription = false
//        }
//        IncomeCategory.new {
//            name = "Other"
//            requiresDescription = true
//        }
//    }
    install(CORS) {
        allowHost("localhost:5173")
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
    }
    install(ContentNegotiation) {
        json()
    }
    install(DataConversion) {
        convert<LocalDate> {
            decode { values -> LocalDate.parse(values.single()) }
        }
    }
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            println("Error: $cause")
            when (cause) {
                is BadRequestException -> {
                    call.response.status(HttpStatusCode.BadRequest)
                    cause.message?.let { call.respondText(it) }
                }

                is NotFoundException -> {
                    call.response.status(HttpStatusCode.NotFound)
                    cause.message?.let { call.respondText(it) }
                }

                else -> {
                    call.response.status(HttpStatusCode.InternalServerError)
                }
            }
        }
    }
    configureRouting()
}
