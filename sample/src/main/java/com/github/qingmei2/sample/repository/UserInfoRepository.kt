package com.github.qingmei2.sample.repository

import android.content.SharedPreferences
import com.github.qingmei2.mvi.util.SingletonHolderSingleArg
import com.github.qingmei2.mvi.util.prefs.boolean
import com.github.qingmei2.mvi.util.prefs.string

class UserInfoRepository(prefs: SharedPreferences) {

    var accessToken: String by prefs.string("user_access_token", "")

    var username by prefs.string("username", "")

    var password by prefs.string("password", "")

    var isAutoLogin: Boolean by prefs.boolean("auto_login", true)

    companion object :
            SingletonHolderSingleArg<UserInfoRepository, SharedPreferences>(::UserInfoRepository)
}