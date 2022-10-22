package com.halil.chatapp.data

data class Users (
    var name : String,
    var lastname : String,
    var userList : List<Users>
        ){
    constructor() : this("","", listOf())
}
