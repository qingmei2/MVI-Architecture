package com.github.qingmei2.sample.ui.main.repos

import com.github.qingmei2.mvi.base.action.IAction

sealed class ReposAction : IAction {

    object InitialAction : ReposAction()

    object SwipeRefreshAction : ReposAction()

    data class SortTypeChangedAction(
        val sortType: String
    ) : ReposAction()

    data class ScrollStateChangedAction(val state: Int) : ReposAction()

    object ScrollToTopAction : ReposAction()
}