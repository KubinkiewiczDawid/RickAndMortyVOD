package com.dawidk.settings

import android.app.Activity
import android.content.Context
import com.dawidk.common.registration.FirebaseAuthApi
import com.dawidk.common.registration.GoogleClientApi
import com.dawidk.core.domain.model.AccountInfo
import com.dawidk.settings.R.string
import com.dawidk.settings.model.Setting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SettingsListProvider(
    context: Context,
    activity: Activity,
    googleClientApi: GoogleClientApi,
    firebaseAuthClient: FirebaseAuthApi
) {

    private val resources = context.resources
    private val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    private val settingsList = mutableListOf(
        Setting.Title("About"),
        Setting.Info("${pInfo.versionName} (${pInfo.versionCode})"),
        Setting.Button("Sign out") {
            if (firebaseAuthClient.isUserLoggedIn()) {
                firebaseAuthClient.signOut()
            } else {
                googleClientApi.signOut(activity)
            }
        }
    )
    val settingsFlow: Flow<List<Setting>> = flow {
        emit(settingsList)
    }

    fun addUserInfo(accountInfo: AccountInfo) {
        settingsList.apply {
            add(
                0,
                Setting.Title(resources.getString(string.user_info))
            )
            add(
                1,
                Setting.Info(accountInfo.email)
            )
        }
    }
}