package com.github.qingmei2.sample.ui.main.repos

import androidx.paging.PagedList
import com.github.qingmei2.sample.entity.ReceivedEvent

sealed class ReposResult {

    sealed class InitialResult : ReposResult() {
        data class Success(val pagedList: PagedList<ReceivedEvent>) : InitialResult()
    }
}