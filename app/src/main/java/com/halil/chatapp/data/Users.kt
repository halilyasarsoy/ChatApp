package com.halil.chatapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Users(
    var name: String,
    var lastname: String,
    var email: String,
    var imgUrl: String? = null,
    var uid: String,
    var profession: String,
    var approved: Boolean = true,
    var status: Boolean = false
) : Parcelable {
    init {
        this.name = name.capitalize()
        this.lastname = lastname.capitalize()
        this.profession = profession.capitalize()
    }

    constructor() : this("", "", "", "", "",  "")
}