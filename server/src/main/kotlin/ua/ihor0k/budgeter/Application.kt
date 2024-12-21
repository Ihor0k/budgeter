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
import org.jetbrains.exposed.sql.Database
import java.time.YearMonth

fun Application.module() {
//    Database.connect("jdbc:h2:mem:test", user = "root")
    Database.connect(
        url = environment.config.property("postgres.url").getString(),
        user = environment.config.property("postgres.user").getString(),
        password = environment.config.property("postgres.password").getString(),
    )
//    transaction {
//        SchemaUtils.drop(UserTable, AccountTable)
//        SchemaUtils.create(Users, Cities)
//        val kyiv = City.new {
//            name = "Kyiv"
//        }
//        val netishyn = City.new {
//            name = "Netishyn"
//        }
//        User.new {
//            name = "ihor"
//            age = 26
//            city = kyiv
//        }
//        User.new {
//            name = "Kolya"
//            age = 26
//            city = kyiv
//        }
//        User.new {
//            name = "nastya"
//            age = 31
//            city = netishyn
//        }
//        SchemaUtils.create(UserTable, AccountTable)
//        val ihor = UserEntity.new {
//            name = "Ihor"
//        }
//        val nastya = UserEntity.new {
//            name = "Nastya"
//        }
//        AccountEntity.new {
//            name = "account1"
//            user = ihor
//        }
//        AccountEntity.new {
//            name = "account2"
//            user = ihor
//        }
//        AccountEntity.new {
//            name = "account3"
//            user = nastya
//        }
//        AccountEntity.new {
//            name = "account4"
//            user = nastya
//        }
//        commit()
//        transaction {
//            println("!!! Created !!!")
//
//            println("!!! GET CITIES !!!")
//            val cities = City.all().with(City::users).toList()
//            val citiesMap = cities.associate { city -> city.name to city.users.map { user -> user.name } }
//            println(Json.encodeToString(citiesMap))
//
//            println("!!! GET USERS !!!")
//            val users = UserEntity.all().with(UserEntity::accounts).toList()
//            val usersMap = users.associate { user -> user.name to user.accounts.map { account -> account.name } }
//            println(Json.encodeToString(usersMap))
//        }
//        SchemaUtils.drop(Cities, Users)
//    }
    install(CORS) {
        allowHost("localhost:5173")
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
    }
    install(ContentNegotiation) {
        json()
    }
    install(DataConversion) {   // TODO: remove it when switch to kotlinx-datetime YearMonth
        convert<YearMonth> {
            decode { values -> YearMonth.parse(values.single()) }
        }
    }
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            println(cause.cause)
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
