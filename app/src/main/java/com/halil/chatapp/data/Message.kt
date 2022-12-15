package com.halil.chatapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Message(
    var senderId: String = "",
    var receiverId: String = "",
    var message: String = ""
) : Parcelable