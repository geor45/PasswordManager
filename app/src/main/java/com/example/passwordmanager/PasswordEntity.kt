package com.example.passwordmanager

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passwords_table") // <--- ΑΛΛΑΓΗ ΕΔΩ
data class PasswordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val password: String,
    val category: String,
    val username: String = "",
    val websiteUrl: String = ""
)