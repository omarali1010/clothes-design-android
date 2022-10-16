package com.example.designme.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class designsItem(
    @SerializedName("about")
    val about: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("color")
    val color: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("likes")
    val likes: Int,
    @SerializedName("size")
    val size: String,
    @SerializedName("title")
    val title: String
) : Parcelable