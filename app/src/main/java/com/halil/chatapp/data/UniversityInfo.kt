package com.halil.chatapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UniversityInfo(
    val university: String,
    val department: String,
) : Parcelable
