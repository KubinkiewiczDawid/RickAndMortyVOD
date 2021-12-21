package com.dawidk.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccountInfo(
    val id: String,
    val displayName: String,
    val email: String
) : Parcelable {

    companion object {

        val EMPTY = AccountInfo(
            id = "",
            displayName = "",
            email = "",
        )
    }
}
