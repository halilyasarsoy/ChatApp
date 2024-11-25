package com.halil.chatapp.ui.activity

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ChatApp : Application(), DefaultLifecycleObserver {

    override fun onCreate() {
        super<Application>.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        FirebaseAuth.getInstance().currentUser?.uid?.let { uid ->
            FirebaseDatabase.getInstance().getReference("User-Status").child(uid)
                .updateChildren(mapOf("status" to "online"))
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        FirebaseAuth.getInstance().currentUser?.uid?.let { uid ->
            Handler(Looper.getMainLooper()).postDelayed({
                if (!ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                    FirebaseDatabase.getInstance().getReference("User-Status").child(uid)
                        .updateChildren(mapOf("status" to "offline"))
                }
            }, 3000)
        }
    }
}
