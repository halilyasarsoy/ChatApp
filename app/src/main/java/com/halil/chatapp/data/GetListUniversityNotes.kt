package com.halil.chatapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GetListUniversityNotes(
    val university: String,
) : Parcelable
