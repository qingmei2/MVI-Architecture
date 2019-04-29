package com.github.qingmei2.sample.ui.main.home

import androidx.paging.PagedList
import com.github.qingmei2.sample.entity.ReceivedEvent

sealed class HomeResult {

    data class InitialResult(val pagedList: PagedList<ReceivedEvent>) : HomeResult()

    object SwipeRefreshResult : HomeResult()

    sealed class LoadingPageResult : HomeResult() {
        data class InFlight(val isFirstlyLoad: Boolean) : LoadingPageResult()
        data class Success(val isFirstlyLoad: Boolean) : LoadingPageResult()
        data class Failure(
            val isFirstlyLoad: Boolean,
            val error: Throwable
        ) : LoadingPageResult()
    }

    object ScrollToTopResult : HomeResult()

    data class FloatActionButtonVisibleResult(val visible: Boolean) : HomeResult()
}