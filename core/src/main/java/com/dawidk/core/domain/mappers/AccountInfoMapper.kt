package com.dawidk.core.domain.mappers

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.dawidk.core.datastore.UserCredentials
import com.dawidk.core.domain.model.AccountInfo

fun GoogleSignInAccount.mapToAccountInfo() = AccountInfo(
    id = id ?: "",
    displayName = displayName ?: "",
    email = email ?: ""
)

fun UserCredentials.mapToAccountInfo() = AccountInfo(
    id = userId ?: "",
    displayName = userDisplayName ?: "",
    email = userEmail ?: ""
)