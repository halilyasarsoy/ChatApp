package com.halil.chatapp.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.halil.chatapp.data.User
import com.halil.chatapp.data.Users
import com.halil.chatapp.other.Resource
import kotlinx.coroutines.tasks.await

@Suppress("UNREACHABLE_CODE")
class MainRepositoryDefault : MainRepositoryInterface {
    private val auth = FirebaseAuth.getInstance()
    private val users = FirebaseFirestore.getInstance().collection("users")
    fun getUID(): String? {
        val firebaseAuth = FirebaseAuth.getInstance()
        return firebaseAuth.uid
    }

    override suspend fun register(
        name: String,
        lastname: String,
        email: String,
        password: String,
        confirmPassword: String,
        profession: String,
        imgUrl: String

    ): Resource<AuthResult> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password)
                .await()
            val uid = result.user!!.uid
            val userCreate =
                User(
                    name = name,
                    lastname = lastname,
                    email = email,
                    imgUrl = imgUrl,
                    uid = uid,
                    profession = profession
                )
            users.document(uid).set(userCreate)
            Resource.Success(result)
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun login(email: String, password: String): Resource<AuthResult> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(result)
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }

    override fun logout(result: () -> Unit) {
        auth.signOut()
        result.invoke()
    }

    override suspend fun getUser(onResult: (Resource<List<Users>>) -> Unit) {
        users.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userList = task.result?.documents?.mapNotNull { it.toObject(Users::class.java) }
                        ?.filter { it.email != auth.currentUser?.email }
                    onResult.invoke(Resource.Success(userList))
                } else {
                    onResult.invoke(Resource.Error(task.exception?.message.toString()))
                }
            }
    }

    override fun updateStatus(status: String) {
        val map = HashMap<String, Any>()
        map["status"] = status
        getUID()?.let { FirebaseDatabase.getInstance().getReference("users").child(it) }
            ?.updateChildren(map)
    }
}
