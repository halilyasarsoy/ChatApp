package com.halil.chatapp.repository

import android.content.Context
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.halil.chatapp.data.GetListUniversityNotes
import com.halil.chatapp.data.User
import com.halil.chatapp.data.Users
import com.halil.chatapp.other.Resource
import kotlinx.coroutines.tasks.await

@Suppress("UNREACHABLE_CODE")
class MainRepositoryDefault : MainRepositoryInterface {

    private val auth = FirebaseAuth.getInstance()
    private val users = FirebaseFirestore.getInstance().collection("users")
    private val notes = FirebaseFirestore.getInstance().collection("notes")
    private val universityInfo = FirebaseFirestore.getInstance().collection("university")

    private fun getUID(): String? {
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


    override suspend fun getUser(onResult: (Resource<List<Users>>) -> Unit) {
        users.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userList =
                        task.result?.documents?.mapNotNull { it.toObject(Users::class.java) }
                            ?.filter { it.email != auth.currentUser?.email }
                    onResult.invoke(Resource.Success(userList))
                } else {
                    onResult.invoke(Resource.Error(task.exception?.message.toString()))
                }
            }
    }

    override suspend fun getUniversityNameList(onResult: (Resource<List<GetListUniversityNotes>>) -> Unit) {
        notes.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val universityList =
                        task.result?.documents?.map { GetListUniversityNotes(it.id) }
                    onResult.invoke(Resource.Success(universityList))
                } else {
                    onResult.invoke(Resource.Error(task.exception?.message.toString()))
                }
            }
    }

    override suspend fun getDepartmentList(
        universityName: String,
        onResult: (Resource<List<GetListUniversityNotes>>) -> Unit
    ) {
        notes.document(universityName).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val departmentMap = task.result?.data?.keys?.toList() ?: emptyList()
                    val departmentList = departmentMap.map { department ->
                        GetListUniversityNotes(universityName, department)
                    }
                    onResult.invoke(Resource.Success(departmentList))
                } else {
                    onResult.invoke(Resource.Error(task.exception?.message.toString()))
                }
            }
    }

    override suspend fun getNotesList(
        universityName: String,
        departmentName: String,
        onResult: (Resource<List<String>>) -> Unit
    ) {
        notes.document(universityName).get()
            .addOnCompleteListener { universityTask ->
                if (universityTask.isSuccessful) {
                    val departmentMap = universityTask.result?.data?.get(departmentName) as? List<String>
                    val noteContentList = departmentMap ?: emptyList()
                    onResult.invoke(Resource.Success(noteContentList))
                } else {
                    onResult.invoke(Resource.Error(universityTask.exception?.message.toString()))
                }
            }
    }





    override fun logout(result: () -> Unit) {
        getUID()?.let {
            FirebaseDatabase.getInstance().getReference("User-Status").child(it)
                .updateChildren(mapOf("status" to "offline"))
        }
        auth.signOut()
        result.invoke()
    }

    override fun updateStatus(userId: String, status: String) {
        val map = HashMap<String, Any>()
        map["status"] = status
        FirebaseDatabase.getInstance().getReference("User-Status").child(userId)
            .updateChildren(map)
    }

    override fun addNotesData(university: String, department: String, context: Context) {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid
        if (uid != null) {
            val universityDoc = universityInfo.document(uid)
            val data = hashMapOf("university" to university, "department" to department)
            universityDoc.set(data)
                .addOnSuccessListener {
                }
                .addOnFailureListener { exception ->
                }
        }
    }

    override suspend fun getUserInfo(context: Context, callback: (List<String>) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid
        if (uid != null) {
            val userDoc = universityInfo.document(uid)
            userDoc.get().addOnSuccessListener { document ->
                val department = document?.get("department") as? String
                val departmentList =
                    if (department != null) listOf(department) else emptyList<String>()
                callback.invoke(departmentList)
            }.addOnFailureListener { exception ->
                // Hata durumuyla ilgili işlemler
                callback.invoke(emptyList())
            }
        }
    }

}
