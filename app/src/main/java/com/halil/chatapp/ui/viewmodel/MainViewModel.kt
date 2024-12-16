package com.halil.chatapp.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.halil.chatapp.data.GetListUniversityNotes
import com.halil.chatapp.data.User
import com.halil.chatapp.data.Users
import com.halil.chatapp.other.Resource
import com.halil.chatapp.repository.MainRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(private val repository: MainRepositoryInterface) :
    ViewModel() {

    private val _userList = MutableLiveData<Resource<List<Users>>>()
    val userList = _userList

    private val _departmentList = MutableLiveData<Resource<List<GetListUniversityNotes>>>()
    val departmentList = _departmentList

    private val _notesList = MutableLiveData<Resource<List<String>>>()
    val notesList = _notesList

    private val _universityData = MutableLiveData<List<String>>()
    val universityData: LiveData<List<String>> = _universityData

    private val _userData = MutableLiveData<Resource<User>>()
    val userData = _userData

    private val _department = MutableLiveData<String>()
    val department: LiveData<String>
        get() = _department

    fun updateStatus(userId: String, status: String) {
        viewModelScope.launch {
            repository.updateStatus(userId, status)
        }
    }

    fun logout(result: () -> Unit) {
        repository.logout(result)
    }

    fun updateStatusWithDisconnect(uid: String, status: String) {
        repository.updateStatusWithDisconnect(uid, status)
    }

    fun getUser() {
        _userList.postValue(Resource.Loading())
        viewModelScope.launch {
            repository.getUser {
                _userList.postValue(it)
            }
        }
    }

    private val _universityNameList = MutableLiveData<Resource<List<GetListUniversityNotes>>>()
    val universityNameList: LiveData<Resource<List<GetListUniversityNotes>>> = _universityNameList
    fun getUniversityName() {
        viewModelScope.launch {
            repository.getUniversityNameList { result ->
                _universityNameList.value = result
            }
        }
    }

    fun searchUniversity(query: String) {
        viewModelScope.launch {
            try {
                val result = repository.searchUniversity(query)
                _universityNameList.value = Resource.Success(result)
            } catch (e: Exception) {
                _universityNameList.value =
                    Resource.Error("Arama sırasında bir hata oluştu: ${e.message}")
            }
        }
    }

    fun getDepartmentList(universityName: String) {
        _departmentList.postValue(Resource.Loading())
        viewModelScope.launch {
            repository.getDepartmentList(universityName) {
                _departmentList.postValue(it)
            }
        }
    }

    fun getNotesList(universityName: String, department: String) {
        _notesList.postValue(Resource.Loading())
        viewModelScope.launch {
            repository.getNotesList(universityName, department) {
                _notesList.postValue(it)
            }
        }
    }

    fun addNoteToFirestore(department: String, context: Context) {
        viewModelScope.launch {
            repository.addNotesData(department, context)
            _universityData.postValue(listOf(department))
        }
    }

    fun fetchNotesData(context: Context) {
        viewModelScope.launch {
            repository.getUserInfo(context) { universities ->
                _universityData.value = universities
            }
        }
    }

    fun sendMessages(messageText: String, time: String) {
        viewModelScope.launch {
            repository.sendMessage(messageText, time)
        }
    }

    fun getUserData() {
        _userData.postValue(Resource.Loading())
        viewModelScope.launch {
            val user = repository.getUserFromFirestoreCollection()
            _userData.postValue(user)
        }
    }

    fun bind() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getDepartmentNameDb { departmentInfo ->
                _department.postValue(departmentInfo)
            }
        }
    }

    //send friend prota
    private val _friendRequestStatus = MutableLiveData<String?>()
    val friendRequestStatus: LiveData<String?> get() = _friendRequestStatus

    fun sendFriendRequest(currentUserId: String, targetUserId: String) {
        repository.sendFriendRequest(currentUserId, targetUserId) { success ->
            if (success) {
                _friendRequestStatus.postValue("Friend request sent to $targetUserId")
            } else {
                _friendRequestStatus.postValue("Failed to send friend request")
            }
            _friendRequestStatus.postValue(null) // Mesajı sıfırla
        }
    }

    private val _sentFriendRequests = MutableLiveData<List<String>>()
    val sentFriendRequests: LiveData<List<String>> get() = _sentFriendRequests

    fun fetchSentFriendRequests(currentUserId: String) {
        repository.getSentFriendRequests(currentUserId) { sentList ->
            _sentFriendRequests.postValue(sentList)
        }
    }

    fun resetFriendRequestStatus() {
        _friendRequestStatus.postValue(null) // LiveData'yı sıfırla
    }


    //request list
    private val _friendRequests = MutableLiveData<List<User>>()
    val friendRequests: LiveData<List<User>> get() = _friendRequests

    fun fetchFriendRequests(userId: String) {
        repository.getFriendRequests(userId) { requestUids ->
            Log.d("FriendRequests", "Real-time UIDs: $requestUids") // Gelen UID'leri logla

            if (requestUids.isEmpty()) {
                _friendRequests.postValue(emptyList()) // Eğer istek yoksa boş liste gönder
                return@getFriendRequests
            }

            val userList = mutableListOf<User>()
            val firestore = FirebaseFirestore.getInstance().collection("users")

            for (uid in requestUids) {
                firestore.document(uid).get().addOnSuccessListener { userSnapshot ->
                    val user = userSnapshot.toObject(User::class.java)
                    if (user != null) {
                        userList.add(user)
                    } else {
                        Log.e("FriendRequests", "User is null for UID: $uid")
                    }

                    if (userList.size == requestUids.size) {
                        _friendRequests.postValue(userList) // Tüm kullanıcılar alındığında LiveData'yı güncelle
                    }
                }.addOnFailureListener {
                    Log.e("FriendRequests", "Failed to fetch user for UID: $uid")
                }
            }
        }
    }



    private val _approvedUsers = MutableLiveData<List<User>>()
    val approvedUsers: LiveData<List<User>> get() = _approvedUsers

    fun approveFriendRequest(targetUser: User) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        repository.approveFriendRequest(currentUserId, targetUser) { success ->
            if (success) {
                fetchApprovedFriends()
                _friendRequestStatus.postValue("Friend request approved for ${targetUser.name}")
            } else {
                _friendRequestStatus.postValue("Failed to approve friend request")
            }
        }
    }

    fun rejectFriendRequest(targetUserId: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        repository.rejectFriendRequest(currentUserId, targetUserId) { success ->
            if (success) {
                _friendRequestStatus.postValue("Friend request rejected")
            } else {
                _friendRequestStatus.postValue("Failed to reject friend request")
            }
        }
    }

    fun fetchApprovedFriends() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        repository.fetchApprovedFriends(currentUserId) { approvedList ->
            _approvedUsers.postValue(approvedList) // LiveData'ya yansıt
        }
    }




}
