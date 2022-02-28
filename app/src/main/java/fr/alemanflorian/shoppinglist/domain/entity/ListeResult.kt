package fr.alemanflorian.shoppinglist.domain.entity

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ListResult(
    val status:Int,
    val liste: Liste?,
) : Parcelable