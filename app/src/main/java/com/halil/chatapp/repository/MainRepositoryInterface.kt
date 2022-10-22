package com.halil.chatapp.repository

import com.google.firebase.auth.AuthResult
import com.google.rpc.context.AttributeContext.Auth
import com.halil.chatapp.data.User
import com.halil.chatapp.data.Users
import com.halil.chatapp.other.Resource

interface MainRepositoryInterface {

    suspend fun register(
        name: String,
        lastname: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Resource<AuthResult>

    suspend fun login(email: String, password: String): Resource<AuthResult>

    fun logout (result : () -> Unit)

    suspend fun getUser(onResult : (Resource<List<Users>>) -> Unit)
}