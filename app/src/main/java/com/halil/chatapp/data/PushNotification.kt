package com.halil.chatapp.data

import com.halil.chatapp.data.NotificationData

data class PushNotification(
    var data: NotificationData,
    var to: String
)