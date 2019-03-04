package com.github.qingmei2.sample.ui.main.repos

import androidx.paging.PagedList
import com.github.qingmei2.sample.entity.Repo

sealed class ReposResult {

    sealed class QueryReposResult : ReposResult() {
        data class Success(val pagedList: PagedList<Repo>) : QueryReposResult()
        data class Failure(val error: Throwable) : QueryReposResult()
        object InFlight : QueryReposResult()
    }

    object ScrollToTopResult : ReposResult()

    data class FloatActionButtonVisibleResult(val visible: Boolean) : ReposResult()
}