package fr.alemanflorian.shoppinglist.domain.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class ProductFromListe(val product: Product, var nb : Int = 0, var count:Int = 0): Parcelable