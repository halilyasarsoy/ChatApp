package com.halil.chatapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
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

    private val _stats = MutableLiveData<Resource<AuthResult>>()
    val stats: LiveData<Resource<AuthResult>> = _stats



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

}
