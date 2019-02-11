package com.github.qingmei2.sample.ui.login

import com.github.qingmei2.mvi.base.view.activity.BaseActivity
import com.github.qingmei2.sample.R
import io.reactivex.Observable

class LoginActivity : BaseActivity<LoginIntent, LoginViewState>() {

    override val layoutId: Int = R.layout.activity_login

    override fun intents(): Observable<LoginIntent> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun render(state: LoginViewState) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
