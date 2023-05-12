package com.halil.chatapp.repository

import android.net.Uri
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.halil.chatapp.data.User
import com.halil.chatapp.data.UserStorage
import com.halil.chatapp.data.Users
import com.halil.chatapp.other.Resource
import kotlinx.coroutines.tasks.await

@Suppress("UNREACHABLE_CODE")
class MainRepositoryDefault : MainRepositoryInterface {

    private val auth = FirebaseAuth.getInstance()
    private val users = FirebaseFirestore.getInstance().collection("users")
    private val notes = FirebaseFirestore.getInstance().collection("notes")
    private val storageReference = Firebase.storage.reference


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

    override fun addNotesData(university: String, department: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email
        val universityDoc = notes.document(university)
        universityDoc.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val documentSnapshot = task.result
                val emailDepartments =
                    documentSnapshot?.get("users") as? HashMap<String, ArrayList<String>>

                if (emailDepartments != null && email != null) {
                    val departments = emailDepartments[email]
                    if (departments != null) {
                        departments.add(department)
                    } else {
                        emailDepartments[email] = arrayListOf(department)
                    }

                    universityDoc.update("users", emailDepartments)
                        .addOnSuccessListener {
                        }
                        .addOnFailureListener { e ->
                        }
                } else if (documentSnapshot == null || !documentSnapshot.exists()) {
                    val data = hashMapOf(
                        "users" to hashMapOf(email to arrayListOf(department))
                    )
                    universityDoc.set(data)
                        .addOnSuccessListener {
                        }
                        .addOnFailureListener { e ->
                        }
                }
            }
        }
    }

    override fun uploadFile(
        user: UserStorage,
        fileUri: Uri,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val fileName = "${user.email}_${System.currentTimeMillis()}"
        val fileReference = storageReference.child(fileName)

        val uploadTask = fileReference.putFile(fileUri)
        uploadTask.addOnSuccessListener { onSuccess() }
        uploadTask.addOnFailureListener { exception -> onFailure(exception) }
    }


}
