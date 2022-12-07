package com.halil.chatapp.data

data class Users(
    var uid: String? = null,
    var name: String,
    var lastname: String,
    var email:String,
    var imgUrl: String,
    var userList: List<Users>
) {

    constructor() : this("","", "", "", "", listOf())

}



