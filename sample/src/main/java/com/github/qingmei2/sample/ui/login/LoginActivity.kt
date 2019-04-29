package com.github.qingmei2.sample.ui.login

import android.view.View
import com.github.qingmei2.mvi.base.view.activity.BaseActivity
import com.github.qingmei2.sample.R
import com.github.qingmei2.sample.entity.Errors
import com.github.qingmei2.sample.ui.main.MainActivity
import com.github.qingmei2.sample.utils.toast
import com.jakewharton.rxbinding3.view.clicks
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_login.*
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class LoginActivity : BaseActivity<LoginIntent, LoginViewState>() {

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein)
        import(loginKodeinModule)
    }

    private val loginClicksIntentPublisher =
        PublishSubject.create<LoginIntent.LoginClicksIntent>()

    override val layoutId: Int = R.layout.activity_login

    private val mViewModel: LoginViewModel by instance()

    override fun onStart() {
        super.onStart()

        bind()
    }

    override fun intents(): Observable<LoginIntent> = Observable.mergeArray<LoginIntent>(
        loginClicksIntentPublisher
    ).startWith(LoginIntent.InitialIntent)

    private fun bind() {
        btnLogin.clicks()
            .map {
                LoginIntent.LoginClicksIntent(
                    username = tvUsername.text.toString(),
                    password = tvPassword.text.toString()
                )
            }
            .autoDisposable(scopeProvider)
            .subscribe(loginClicksIntentPublisher)

        mViewModel.states()
            .autoDisposable(scopeProvider)
            .subscribe(this::render)

        mViewModel.processIntents(intents())
    }

    override fun render(state: LoginViewState) {
        when (state.uiEvents) {
            is LoginViewState.LoginUiEvents.JumpMain -> {
                MainActivity.launch(this)
                // finish()
                return
            }
            is LoginViewState.LoginUiEvents.TryAutoLogin -> {
                val username = state.uiEvents.username
                val password = state.uiEvents.password
                tvUsername.setText(username.toCharArray(), 0, username.length)
                tvPassword.setText(password.toCharArray(), 0, password.length)
                if (state.uiEvents.autoLogin) {
                    loginClicksIntentPublisher.onNext(LoginIntent.LoginClicksIntent(username, password))
                }
            }
        }

        progressBar.visibility = when (state.isLoading) {
            true -> View.VISIBLE
            false -> View.GONE
        }

        state.errors?.apply {
            when (this) {
                is Errors.SimpleMessageError -> {
                    toast { simpleMessage }
                }
                else -> {
                    toast { localizedMessage }
                }
            }
        }
    }
}
