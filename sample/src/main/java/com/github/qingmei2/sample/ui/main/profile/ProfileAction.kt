package com.github.qingmei2.sample.ui.main.profile

import com.github.qingmei2.mvi.base.action.IAction

sealed class ProfileAction : IAction {

    object InitialAction : ProfileAction()
}