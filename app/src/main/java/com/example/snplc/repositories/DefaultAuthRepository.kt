package com.example.snplc.repositories

import com.example.snplc.other.Resource
import com.google.firebase.auth.AuthResult

class DefaultAuthRepository : AuthRepository {

    override suspend fun login(email: String, password: String): Resource<AuthResult> {
        TODO("Not yet implemented")
    }

    override suspend fun register(
        email: String,
        username: String,
        password: String
    ): Resource<AuthResult> {
        TODO("Not yet implemented")
    }
}