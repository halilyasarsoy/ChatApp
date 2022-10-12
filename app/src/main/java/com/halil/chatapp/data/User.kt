package com.halil.chatapp.data



data class User (
    var uuid : String,
    var lastname : String,
    var email : String,
        ) {
    constructor() : this("","","")
}