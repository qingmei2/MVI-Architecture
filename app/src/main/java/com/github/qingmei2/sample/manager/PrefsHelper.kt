package com.github.qingmei2.sample.manager

import android.content.SharedPreferences
import com.github.qingmei2.mvi.util.prefs.boolean
import com.github.qingmei2.mvi.util.prefs.string

class PrefsHelper(prefs: SharedPreferences) {

    var autoLogin by prefs.boolean("autoLogin", true)

    var username by prefs.string("username", "")
    var password by prefs.string("password", "")
}