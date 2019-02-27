package com.github.qingmei2.sample.ui.main.repos

import com.github.qingmei2.mvi.base.intent.IIntent

sealed class ReposIntent : IIntent {

    object InitialIntent : ReposIntent()

    data class SortTypeChangeIntent(
        val sort: String
    ) : ReposIntent()
}