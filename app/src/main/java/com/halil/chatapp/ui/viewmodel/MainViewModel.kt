package com.halil.chatapp.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.halil.chatapp.data.NotesData
import com.halil.chatapp.data.UserStorage
import com.halil.chatapp.data.Users
import com.halil.chatapp.other.Resource
import com.halil.chatapp.repository.MainRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(private val repository: MainRepositoryInterface) :
    ViewModel() {
    private val _userList = MutableLiveData<Resource<List<Users>>>()
    val userList = _userList

    private val _universityData = MutableLiveData<List<String>>()
    val universityData: LiveData<List<String>> = _universityData

    private val _universitiesList = MutableLiveData<List<NotesData>>()
    val universitiesList: MutableLiveData<List<NotesData>> = _universitiesList

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

    fun addNoteToFirestore(university: String, department: String, context: Context) {
        viewModelScope.launch {
            repository.addNotesData(university, department, context)
            _universityData.value = listOf(university)
        }
    }

    fun fetchNotesData(context: Context) {
        viewModelScope.launch {
            repository.getNotesData(context) { universities ->
                _universityData.value = universities
            }
        }
    }

    fun getInfoUniversities() {
        viewModelScope.launch {
            repository.getUniversitiesInfo { universities ->
                _universitiesList.value = universities
            }
        }
    }

    fun uploadFile(
        user: UserStorage,
        fileUri: Uri,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.uploadFile(user, fileUri, onSuccess, onFailure)
    }
}
