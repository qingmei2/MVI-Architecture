package com.github.qingmei2.sample.ui.main.repos

import androidx.paging.PagedList
import com.github.qingmei2.sample.entity.Repo

sealed class ReposResult {

    data class InitialResult(val pagedList: PagedList<Repo>) : ReposResult()

    object SwipeRefreshResult : ReposResult()

    object SortTypeChangedResult : ReposResult()

    data class FloatActionButtonVisibleResult(val visible: Boolean) : ReposResult()

    object ScrollToTopResult : ReposResult()

    sealed class ReposPageResult : ReposResult() {
        data class InFlight(val isFirstlyLoad: Boolean) : ReposPageResult()
        data class Success(val isFirstlyLoad: Boolean) : ReposPageResult()
        data class Failure(
            val isFirstlyLoad: Boolean,
            val error: Throwable
        ) : ReposPageResult()
    }
}