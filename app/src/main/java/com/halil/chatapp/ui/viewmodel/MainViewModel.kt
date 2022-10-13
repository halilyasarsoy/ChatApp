package com.halil.chatapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.halil.chatapp.repository.MainRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(private val repository: MainRepositoryInterface) :
    ViewModel() {

        fun logout (result:() -> Unit){
            repository.logout(result)
        }
    }