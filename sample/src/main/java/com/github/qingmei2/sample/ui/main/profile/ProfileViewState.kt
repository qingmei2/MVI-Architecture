package com.github.qingmei2.sample.ui.main.profile

import com.github.qingmei2.mvi.base.viewstate.IViewState
import com.github.qingmei2.sample.entity.LoginUser

data class ProfileViewState(
    val error: Throwable?,
    val isRefreshing: Boolean,
    val uiEvent: ProfileUIEvent?
) : IViewState {

    companion object {

        fun idle(): ProfileViewState {
            return ProfileViewState(
                error = null,
                isRefreshing = false,
                uiEvent = null
            )
        }
    }
}

sealed class ProfileUIEvent {

    data class InitialSuccess(val user: LoginUser) : ProfileUIEvent()
}