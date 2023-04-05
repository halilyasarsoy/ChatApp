package com.halil.chatapp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var name : String,
    var lastname : String,
    var email : String,
    var imgUrl :String?=null,
    var uid:String,
    var profession : String,

): Parcelable {
    init {
        this.name= name.capitalize()
        this.lastname=lastname.capitalize()
        this.profession=profession.capitalize()

    }
    constructor() : this("","","","","","")
}