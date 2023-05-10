package com.halil.chatapp.data

data class User(
    var name : String,
    var lastname : String,
    var email : String,
    var imgUrl :String?=null,
    var uid:String,
    var profession : String,
    ) {
    init {
        this.name= name.capitalize()
        this.lastname=lastname.capitalize()
        this.profession=profession.capitalize()

    }
    constructor() : this("","","","","","")
}