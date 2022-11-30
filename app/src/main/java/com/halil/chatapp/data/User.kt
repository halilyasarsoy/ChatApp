package com.halil.chatapp.data

data class User(
    var name : String,
    var lastname : String,
    var email : String,
    var imgUrl : String
        ) {
    constructor() : this("","","","")
}