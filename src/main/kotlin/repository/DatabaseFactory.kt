package com.example.repository

import com.example.data.table.NoteTable
import com.example.data.table.UserTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        Database.connect(hikari())

        transaction {
            SchemaUtils.create(UserTable)
            SchemaUtils.create(NoteTable)
        }
    }

    private fun hikari() : HikariDataSource {
        val config = HikariConfig().apply {
            driverClassName = System.getenv("JDBC_DRIVER") ?: "org.postgresql.Driver"
            jdbcUrl = System.getenv("DATABASE_URL") ?: "jdbc:postgresql://localhost:8081/notes_db"
            username = System.getenv("DB_USER") ?: "postgres"
            password = System.getenv("DB_PASSWORD") ?: "Vasu@4002"
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(block: () -> T):T =
        withContext(Dispatchers.IO){
            transaction { block() }
        }
}