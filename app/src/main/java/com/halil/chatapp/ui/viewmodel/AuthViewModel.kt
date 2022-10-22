package com.halil.chatapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import com.halil.chatapp.other.Resource
import com.halil.chatapp.repository.MainRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: MainRepositoryInterface) :
    ViewModel() {

    private val _registerStatus = MutableLiveData<Resource<AuthResult>>()
    val registerStatus: LiveData<Resource<AuthResult>> = _registerStatus

    private val _loginStatus = MutableLiveData<Resource<AuthResult>>()
    val loginStatus: LiveData<Resource<AuthResult>> = _loginStatus

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            delay(3000)
            _isLoading.value = false
        }
    }

    fun login(email: String, password: String) {
        val error = if (email.isEmpty() || password.isEmpty()) "Please fill all blanks" else null
        error?.let {
            _loginStatus.postValue(Resource.Error(it))
            return
        }
        _registerStatus.postValue(Resource.Loading())
        viewModelScope.launch {
            var result = repository.login(email, password)
            _loginStatus.postValue(result)
        }
    }

    fun register(
        name: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        val error =
            if (name.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || password != confirmPassword) "Make sure it's right. Something is wrong.." else null
        error?.let {
            _registerStatus.postValue(Resource.Error(it))
            return
        }
        _registerStatus.postValue(Resource.Loading())
        viewModelScope.launch {
            val result = repository.register(
                name = name,
                lastname = lastName,
                email = email,
                password = password,
                confirmPassword = confirmPassword
            )
            _registerStatus.postValue(result)
        }
    }
}