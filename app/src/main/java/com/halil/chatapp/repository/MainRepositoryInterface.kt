package com.halil.chatapp.repository

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.AuthResult
import com.halil.chatapp.data.GetListUniversityNotes
import com.halil.chatapp.data.NotesData
import com.halil.chatapp.data.UserStorage
import com.halil.chatapp.data.Users
import com.halil.chatapp.other.Resource

interface MainRepositoryInterface {

    suspend fun register(
        name: String,
        lastname: String,
        email: String,
        password: String,
        confirmPassword: String,
        profession: String,

        imgUrl: String
    ): Resource<AuthResult>

    suspend fun login(email: String, password: String): Resource<AuthResult>

    fun logout(result: () -> Unit)

    suspend fun getUser(onResult: (Resource<List<Users>>) -> Unit)

    fun updateStatus(status: String, userId: String)

    fun addNotesData(university: String, department: String, context: Context)

    suspend fun getNotesData(context: Context, callback: (List<String>) -> Unit)

    suspend fun getUniversityNameList(onResult: (Resource<List<GetListUniversityNotes>>) -> Unit)
    suspend fun getDepartmentList(universityName: String, onResult: (Resource<List<GetListUniversityNotes>>) -> Unit)
}