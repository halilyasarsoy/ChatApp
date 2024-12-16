package com.halil.chatapp.repository

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.halil.chatapp.data.Contact
import com.halil.chatapp.data.ContactMessage
import com.halil.chatapp.data.GetListUniversityNotes
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
    private val firebaseDatabase: FirebaseDatabase
        get() = FirebaseDatabase.getInstance()


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
                Users(
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

    override suspend fun getUserFromFirestoreCollection(): Resource<Users> {
        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        return try {
            val user = users.document(userId).get().await().toObject(Users::class.java)
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
    override fun getUserStatuses(callback: (Map<String, String>) -> Unit) {
        val userStatusRef = FirebaseDatabase.getInstance().getReference("User-Status")
        userStatusRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val statusMap = mutableMapOf<String, String>()
                for (child in snapshot.children) {
                    val uid = child.key
                    val status = child.child("status").getValue(String::class.java) ?: "offline"
                    if (uid != null) {
                        statusMap[uid] = status
                    }
                }
                callback(statusMap)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Repository", "Error fetching user statuses: ${error.message}")
                callback(emptyMap()) // Hata durumunda boş bir map dönüyoruz.
            }
        })
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

    override fun sendFriendRequest(
        currentUserId: String,
        targetUserId: String,
        result: (Boolean) -> Unit
    ) {
        val friendRequestRef = FirebaseDatabase.getInstance().getReference("Friend-Requests")
        friendRequestRef.child(currentUserId).child("sent").child(targetUserId).setValue(true)
            .addOnSuccessListener {
                friendRequestRef.child(targetUserId).child("received").child(currentUserId)
                    .setValue(true)
                    .addOnSuccessListener {
                        result(true)
                    }
                    .addOnFailureListener {
                        result(false)
                    }
            }
            .addOnFailureListener {
                result(false)
            }
    }

    override fun getFriendRequests(currentUserId: String, result: (List<String>) -> Unit) {
        val friendRequestRef = FirebaseDatabase.getInstance().getReference("Friend-Requests")
            .child(currentUserId).child("received")

        friendRequestRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val requests = mutableListOf<String>()
                snapshot.children.forEach {
                    requests.add(it.key.toString())
                }
                result(requests) // Yeni UID listesini döndür
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FriendRequests", "Failed to listen for friend requests: ${error.message}")
                result(emptyList()) // Hata durumunda boş liste döndür
            }
        })
    }


    override fun getSentFriendRequests(currentUserId: String, result: (List<String>) -> Unit) {
        val sentRequestsRef = FirebaseDatabase.getInstance().getReference("Friend-Requests")
            .child(currentUserId).child("sent")

        sentRequestsRef.get().addOnSuccessListener { snapshot ->
            val sentList = mutableListOf<String>()
            snapshot.children.forEach { sentList.add(it.key.toString()) }
            result(sentList)
        }.addOnFailureListener {
            result(emptyList())
        }
    }

    override fun approveFriendRequest(
        currentUserId: String,
        targetUser: Users,
        callback: (Boolean) -> Unit
    ) {
        val approvedFriendsRef = firebaseDatabase.getReference("Approved-Friends")
            .child(currentUserId).child(targetUser.uid!!)

        approvedFriendsRef.setValue(targetUser)
            .addOnSuccessListener {
                val reverseApprovedRef = firebaseDatabase.getReference("Approved-Friends")
                    .child(targetUser.uid!!).child(currentUserId)

                reverseApprovedRef.setValue(true)
                    .addOnSuccessListener {
                        val friendRequestsRef = firebaseDatabase.getReference("Friend-Requests")

                        friendRequestsRef.child(currentUserId).child("sent")
                            .child(targetUser.uid!!).setValue(true)

                        friendRequestsRef.child(targetUser.uid!!).child("sent")
                            .child(currentUserId).setValue(true)

                        removeFriendRequest(currentUserId, targetUser.uid!!) {
                            callback(true)
                        }
                    }
                    .addOnFailureListener {
                        callback(false)
                    }
                Log.d("approveFriendRequest", "User added successfully to Approved-Friends")

            }
            .addOnFailureListener {
                callback(false)
            }
    }


    override fun rejectFriendRequest(
        currentUserId: String,
        targetUserId: String,
        callback: (Boolean) -> Unit
    ) {
        val friendRequestsRef =
            firebaseDatabase.getReference("Friend-Requests").child(currentUserId).child("received")
                .child(targetUserId)
        friendRequestsRef.removeValue()
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    override fun fetchApprovedFriends(currentUserId: String, callback: (List<Users>) -> Unit) {
        val approvedFriendsRef = firebaseDatabase.getReference("Approved-Friends").child(currentUserId)

        approvedFriendsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val uids = snapshot.children.mapNotNull { it.key }
                val userList = mutableListOf<Users>()

                if (uids.isEmpty()) {
                    callback(emptyList())
                    return
                }

                val firestore = FirebaseFirestore.getInstance().collection("users")
                uids.forEach { uid ->
                    firestore.document(uid).get()
                        .addOnSuccessListener { userSnapshot ->
                            val user = userSnapshot.toObject(Users::class.java)
                            user?.let { userList.add(it) }

                            if (userList.size == uids.size) {
                                callback(userList)
                            }
                        }
                        .addOnFailureListener {
                            Log.e("fetchApprovedFriends", "Failed to fetch user: $uid")
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("fetchApprovedFriends", "Error: ${error.message}")
                callback(emptyList())
            }
        })
    }

    override fun removeFriendRequest(
        currentUserId: String,
        targetUserId: String,
        callback: (Boolean) -> Unit
    ) {
        val friendRequestsRef =
            firebaseDatabase.getReference("Friend-Requests").child(currentUserId).child("received")
                .child(targetUserId)
        friendRequestsRef.removeValue()
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }

    }
}