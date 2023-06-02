package com.halil.chatapp.repository

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
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
import java.text.SimpleDateFormat
import java.util.*

@Suppress("UNREACHABLE_CODE")
class MainRepositoryDefault : MainRepositoryInterface {

    private val auth = FirebaseAuth.getInstance()
    private val users = FirebaseFirestore.getInstance().collection("users")
    private val notes = FirebaseFirestore.getInstance().collection("notes")
    private val storageReference = Firebase.storage.reference
    private val universityLiveData = MutableLiveData<String>()
    private val departmentLiveData = MutableLiveData<String>()

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

    override fun addNotesData(university: String, department: String, context: Context) {
        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email
        val userDoc = email?.let { notes.document(it) }

        val universitiesData = hashMapOf(university to arrayListOf(department))
        userDoc?.collection("user_data")?.document("notes")
            ?.set(hashMapOf("universities" to universitiesData))
            ?.addOnSuccessListener {
                val sharedPreferences =
                    context.getSharedPreferences("MyPrefs_$email", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("university", university)
                editor.putString("department", department)
                editor.apply()
            }
            ?.addOnFailureListener { exception ->
                // Hata durumu
            }
    }

    override fun uploadFile(
        user: UserStorage,
        fileUri: Uri,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val currentTimeMillis = System.currentTimeMillis()
        val formattedDateTime =
            SimpleDateFormat("dd.MM.yyyy : HH:mm", Locale.getDefault()).format(currentTimeMillis)
        val fileName = "${user.email} - $formattedDateTime"
        val fileReference = storageReference.child(fileName)

        val uploadTask = fileReference.putFile(fileUri)
        uploadTask.addOnSuccessListener { onSuccess() }
        uploadTask.addOnFailureListener { exception -> onFailure(exception) }
    }
}
