package com.example.icourse.model


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class PartsPage(
    @SerializedName("content")
    val content: String? = null,
    @SerializedName("type")
    val type: String? = null
) : Parcelable