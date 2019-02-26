package com.github.qingmei2.sample.ui.main.home

import androidx.paging.PagedList
import com.github.qingmei2.mvi.base.viewstate.IViewState
import com.github.qingmei2.sample.entity.ReceivedEvent

data class HomeViewState(
    val error: Throwable?,
    val isRefreshing: Boolean,
    val progressVisible: Boolean,
    val uiEvent: HomeUIEvent?
) : IViewState {

    sealed class HomeUIEvent {

        data class InitialSuccess(val pageList: PagedList<ReceivedEvent>) : HomeUIEvent()
    }

    companion object {

        fun idle(): HomeViewState {
            return HomeViewState(
                error = null,
                isRefreshing = false,
                progressVisible = false,
                uiEvent = null
            )
        }
    }
}