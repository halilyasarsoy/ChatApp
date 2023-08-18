package com.halil.chatapp.data

import com.google.firebase.firestore.PropertyName

data class Contact(
    @PropertyName("mail")
    var mail: String,
    @PropertyName("uid")
    var uid: String,
    @PropertyName("messages")
    var messages: List<Message> = listOf()
) {
    constructor() : this("", "", listOf())
}