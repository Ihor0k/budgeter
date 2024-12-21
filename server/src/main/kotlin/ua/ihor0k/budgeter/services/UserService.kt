package ua.ihor0k.budgeter.services

import io.ktor.server.plugins.*
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.transactions.transaction
import ua.ihor0k.budgeter.dto.AccountRequest
import ua.ihor0k.budgeter.dto.AccountResponse
import ua.ihor0k.budgeter.dto.UserRequest
import ua.ihor0k.budgeter.dto.UserResponse
import ua.ihor0k.budgeter.db.Account
import ua.ihor0k.budgeter.db.User

class UserService {
    fun createUser(userRequest: UserRequest): UserResponse = transaction {
        val user = User.new {
            this.name = userRequest.name
        }
        UserResponse(
            user.id.value,
            user.name,
            accounts = emptyList(),
        )
    }

    fun updateUser(id: Int, userRequest: UserRequest): Unit = transaction {
        User.findByIdAndUpdate(id) { user ->
            user.name = userRequest.name
        } ?: throw NotFoundException("User not found")
    }

    fun getUsers(): List<UserResponse> = transaction {
        User.all().with(User::accounts)
            .map { user ->
                UserResponse(
                    user.id.value,
                    user.name,
                    user.accounts.map { account ->
                        AccountResponse(
                            account.id.value,
                            account.name
                        )
                    }
                )
            }
            .toList()
    }

    fun addUserAccount(userId: Int, accountRequest: AccountRequest): AccountResponse = transaction {
        val user = User.findById(userId) ?: throw NotFoundException("User not found")
        val account = Account.new {
            this.user = user
            this.name = accountRequest.name
        }
        AccountResponse(account.id.value, account.name)
    }

    fun updateUserAccount(userId: Int, id: Int, accountRequest: AccountRequest): Unit = transaction {
        val user = User.findById(userId) ?: throw NotFoundException("User not found")
        Account.findByIdAndUpdate(id) { account ->
            if (account.user != user) {
                throw BadRequestException("Account does not belong to this user")
            }
            account.name = accountRequest.name
        } ?: throw NotFoundException("Account not found")
    }

}