package com.halil.chatapp.repository

import android.content.Context
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.halil.chatapp.data.Contact
import com.halil.chatapp.data.ContactMessage
import com.halil.chatapp.data.GetListUniversityNotes
import com.halil.chatapp.data.User
import com.halil.chatapp.data.Users
import com.halil.chatapp.other.Resource
import kotlinx.coroutines.tasks.await

//@Suppress("UNREACHABLE_CODE")
class MainRepositoryDefault : MainRepositoryInterface {
    private val message_field = "messages"
    private val auth = FirebaseAuth.getInstance()
    private val users = FirebaseFirestore.getInstance().collection("users")
    private val notes = FirebaseFirestore.getInstance().collection("notes")
    private val universityInfo = FirebaseFirestore.getInstance().collection("university")
    private val contacts = FirebaseFirestore.getInstance().collection("Contacts")
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
        department: String,
        onResult: (Resource<List<String>>) -> Unit
    ) {
        notes.document(universityName).get()
            .addOnCompleteListener { universityTask ->
                if (universityTask.isSuccessful) {
                    val rawData = universityTask.result?.data?.get(department)
                    val noteContentList = if (rawData is List<*>) {
                        rawData.filterIsInstance<String>()
                    } else {
                        emptyList()
                    }
                    onResult.invoke(Resource.Success(noteContentList))
                } else {
                    onResult.invoke(Resource.Error(universityTask.exception?.message.toString()))
                }
            }
    }


    override suspend fun sendMessage(messageText: String, time: String) {
        val contact = Contact()
        val message = ContactMessage()
        contact.mail = auth.currentUser?.email.toString()
        contact.uid = auth.currentUser?.uid.toString()
        message.messageText = messageText
        message.time = time
        val userMail = auth.currentUser?.email.toString()
        val docRef = contacts.document(userMail)
        docRef.get().addOnSuccessListener { document ->
            if (document.data != null) {
                docRef.update(message_field, FieldValue.arrayUnion(message))
            } else {
                contacts.document(userMail).set(contact)
                docRef.update(message_field, FieldValue.arrayUnion(message))
            }
        }
    }

    override suspend fun getUserFromFirestoreCollection(): Resource<User> {
        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        return try {
            val user = users.document(userId).get().await().toObject(User::class.java)
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }

    override fun updateStatusWithDisconnect(uid: String, status: String) {
        val userStatusRef = FirebaseDatabase.getInstance().getReference("User-Status").child(uid)
        userStatusRef.onDisconnect().updateChildren(mapOf("status" to "offline"))
        userStatusRef.updateChildren(mapOf("status" to status))
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

    override fun addNotesData(department: String, context: Context) {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid
        if (uid != null) {
            val universityDoc = universityInfo.document(uid)
            val data = mapOf("department" to department)
            universityDoc.set(data)
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
                    if (department != null) listOf(department) else emptyList()
                callback.invoke(departmentList)
            }.addOnFailureListener {
                callback.invoke(emptyList())
            }
        }
    }

    override suspend fun searchUniversity(query: String): List<GetListUniversityNotes> {
        return try {
            val querySnapshot = notes
                .get()
                .await()

            val universityList = mutableListOf<GetListUniversityNotes>()
            for (document in querySnapshot.documents) {

                val universityNote = GetListUniversityNotes(document.id)
                universityList.add(universityNote)
            }
            val filteredList =
                universityList.filter { it.university.contains(query, ignoreCase = true) }
            filteredList
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getDepartmentNameDb(onResult: (String) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid
        if (uid != null) {
            universityInfo.document(uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val department = documentSnapshot.getString("department")
                        onResult(department ?: "Bilgi Yok")
                    } else {
                        onResult("Kullanıcı Bulunamadı")
                    }
                }
                .addOnFailureListener { e ->
                    onResult("Hata: $e")
                }
        }
    }
}