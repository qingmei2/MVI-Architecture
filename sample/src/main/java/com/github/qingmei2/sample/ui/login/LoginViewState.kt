package com.github.qingmei2.sample.ui.login

import com.github.qingmei2.mvi.base.viewstate.IViewState

data class LoginViewState(
    val editUsername: String,
    val editPassword: String,
    val autoLogin: Boolean
) : IViewState