package com.github.qingmei2.sample.ui.login

import com.github.qingmei2.mvi.base.action.IAction

sealed class LoginAction : IAction {

    object InitialUiAction : LoginAction()

    data class ClickLoginAction(
        val username: String?,
        val password: String?
    ) : LoginAction()

    data class EditUsernameAction(
        val username: String
    ) : LoginAction()

    data class EditPasswordAction(
        val password: String
    ) : LoginAction()
}