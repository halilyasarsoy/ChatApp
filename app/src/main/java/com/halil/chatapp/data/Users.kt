package com.halil.chatapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Users(
    var uid: String,
    var name: String,
    var lastname: String,
    var email: String,
    var imgUrl: String,
    var userList: List<Users>
) :Parcelable{

    constructor() : this("", "", "", "", "", listOf())

}



