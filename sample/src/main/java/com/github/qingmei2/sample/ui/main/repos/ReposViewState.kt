package com.github.qingmei2.sample.ui.main.repos

import com.github.qingmei2.mvi.base.viewstate.IViewState

data class ReposViewState(
    val error: Throwable?,
    val isRefreshing: Boolean,
    val progressVisible: Boolean,
    val uiEvent: ReposUIEvent?
) : IViewState {

    companion object {

        fun idle(): ReposViewState {
            return ReposViewState(
                error = null,
                isRefreshing = false,
                progressVisible = false,
                uiEvent = null
            )
        }
    }
}

sealed class ReposUIEvent {

}