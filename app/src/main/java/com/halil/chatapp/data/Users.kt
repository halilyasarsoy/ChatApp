package com.halil.chatapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Users(
    var uid: String,
    var name: String,
    var lastname: String,
    var email: String,
    var imgUrl: String?=null,
    var userList: List<Users>,
    var profession : String,

    val online: String = "offline"

    ) : Parcelable {
    init {
        this.name= name.capitalize()
        this.lastname=lastname.capitalize()
        this.profession=profession.capitalize()
    }
    constructor() : this("", "", "", "", "", listOf(),"")
}



