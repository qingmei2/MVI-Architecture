package com.github.qingmei2.sample.ui.main.profile

import com.github.qingmei2.mvi.base.intent.IIntent

sealed class ProfileIntent : IIntent {

    object InitialIntent : ProfileIntent()
}