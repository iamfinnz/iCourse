package com.example.icourse.model


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Material2(
    @SerializedName("id_material")
    val idMaterial: Int? = null,
    @SerializedName("thumbnail_material")
    val thumbnailMaterial: String? = null,
    @SerializedName("title_material")
    val titleMaterial: String? = null
) : Parcelable