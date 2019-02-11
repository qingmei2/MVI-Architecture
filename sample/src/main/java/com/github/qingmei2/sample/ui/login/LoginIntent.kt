package com.github.qingmei2.sample.ui.login

import com.github.qingmei2.mvi.base.intent.IIntent

sealed class LoginIntent : IIntent {

    object StartLoginIntent : LoginIntent()

    data class EditUsernameIntent(
        val username: String
    ) : LoginIntent()

    data class EditPasswordIntent(
        val username: String
    ) : LoginIntent()

    object LoginSuccessIntent : LoginIntent()
}