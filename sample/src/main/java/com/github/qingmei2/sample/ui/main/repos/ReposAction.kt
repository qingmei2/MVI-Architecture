package com.github.qingmei2.sample.ui.main.repos

import com.github.qingmei2.mvi.base.action.IAction

sealed class ReposAction : IAction {

    object InitialAction : ReposAction()
}