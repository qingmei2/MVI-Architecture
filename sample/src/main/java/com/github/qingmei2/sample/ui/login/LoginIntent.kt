package com.github.qingmei2.sample.ui.login

import com.github.qingmei2.mvi.base.intent.IIntent

sealed class LoginIntent : IIntent {

    object InitialIntent : LoginIntent()

    data class LoginClicksIntent(
        val username: String?,
        val password: String?
    ) : LoginIntent()

    data class EditUsernameIntent(
        val username: String
    ) : LoginIntent()

    data class EditPasswordIntent(
        val password: String
    ) : LoginIntent()
}