package com.halil.chatapp.repository

import android.content.Context
import com.google.firebase.auth.AuthResult
import com.halil.chatapp.data.GetListUniversityNotes
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
    fun updateStatusWithDisconnect(uid: String, status: String)
    suspend fun getUser(onResult: (Resource<List<Users>>) -> Unit)

    fun updateStatus(userId: String, status: String)

    fun getUserStatuses(callback: (Map<String, String>) -> Unit)

    fun addNotesData(department: String, context: Context)

    suspend fun getUserInfo(context: Context, callback: (List<String>) -> Unit)

    suspend fun getUniversityNameList(onResult: (Resource<List<GetListUniversityNotes>>) -> Unit)

    suspend fun getDepartmentList(
        universityName: String,
        onResult: (Resource<List<GetListUniversityNotes>>) -> Unit
    )

    suspend fun getNotesList(
        universityName: String,
        department: String,
        onResult: (Resource<List<String>>) -> Unit
    )

    suspend fun sendMessage(messageText: String, time: String)

    suspend fun getUserFromFirestoreCollection(): Resource<Users>
    suspend fun searchUniversity(query: String): List<GetListUniversityNotes>

    suspend fun getDepartmentNameDb(onResult: (String) -> Unit)


    //send friend

    fun sendFriendRequest(currentUserId: String, targetUserId: String, result: (Boolean) -> Unit)
    fun getFriendRequests(currentUserId: String, result: (List<String>) -> Unit)
    fun getSentFriendRequests(currentUserId: String, result: (List<String>) -> Unit)

    fun approveFriendRequest(currentUserId: String, targetUser: Users, callback: (Boolean) -> Unit)
    fun rejectFriendRequest(currentUserId: String, targetUserId: String, callback: (Boolean) -> Unit)
    fun fetchApprovedFriends(currentUserId: String, callback: (List<Users>) -> Unit)
    fun removeFriendRequest(currentUserId: String, targetUserId: String, callback: (Boolean) -> Unit)



}