package com.halil.chatapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.halil.chatapp.data.GetListUniversityNotes
import com.halil.chatapp.data.Users
import com.halil.chatapp.other.Resource
import com.halil.chatapp.repository.MainRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(private val repository: MainRepositoryInterface) :
    ViewModel() {

    private val _addStatus = MutableLiveData<Resource<Any>>()
    val addStatus: LiveData<Resource<Any>> = _addStatus

    private val _userList = MutableLiveData<Resource<List<Users>>>()
    val userList = _userList

    private val _universitiyNameList = MutableLiveData<Resource<List<GetListUniversityNotes>>>()
    val universityNameList = _universitiyNameList

    private val _departmentList = MutableLiveData<Resource<List<GetListUniversityNotes>>>()
    val departmentList = _departmentList

    private val _notesList = MutableLiveData<Resource<List<String>>>()
    val notesList = _notesList

    private val _universityData = MutableLiveData<List<String>>()
    val universityData: LiveData<List<String>> = _universityData


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

    fun getUniversityName() {
        _universitiyNameList.postValue(Resource.Loading())
        viewModelScope.launch {
            repository.getUniversityNameList {
                _universitiyNameList.postValue(it)
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

    fun addNoteToFirestore(university: String, department: String, context: Context) {
        viewModelScope.launch {
            repository.addNotesData(university, department, context)
            _universityData.value = listOf(university)
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

}
