package com.halil.chatapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun updateDepartment(newDepartment: String) {
        _department.value = newDepartment
    }
}
