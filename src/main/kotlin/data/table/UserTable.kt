package com.example.data.table

import org.jetbrains.exposed.sql.Table

object UserTable : Table(){
    val email = varchar("email", 150)
    val username = varchar("name", 150)
    val hashPassword = varchar("password", 50)

    override val primaryKey: PrimaryKey = PrimaryKey(email)
}