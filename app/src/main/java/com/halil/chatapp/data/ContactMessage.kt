package com.halil.chatapp.data

import com.google.firebase.firestore.PropertyName

data class ContactMessage(
    @PropertyName("messageText")
    var messageText: String,
    @PropertyName("time")
    var time: String
) {
    constructor() : this("", "")
}
