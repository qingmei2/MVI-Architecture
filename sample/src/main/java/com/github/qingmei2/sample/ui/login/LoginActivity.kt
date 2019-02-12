package com.github.qingmei2.sample.ui.login

import android.content.Intent
import android.view.View
import com.github.qingmei2.mvi.base.view.activity.BaseActivity
import com.github.qingmei2.sample.R
import com.github.qingmei2.sample.ui.main.MainActivity
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
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

    private val editUsernameIntentPublisher =
        PublishSubject.create<LoginIntent.EditUsernameIntent>()
    private val editPasswordIntentPublisher =
        PublishSubject.create<LoginIntent.EditPasswordIntent>()
    private val loginClicksIntentPublisher =
        PublishSubject.create<LoginIntent.LoginClicksIntent>()

    override val layoutId: Int = R.layout.activity_login

    private val viewModel: LoginViewModel by instance()

    override fun onStart() {
        super.onStart()

        bind()
    }

    override fun intents(): Observable<LoginIntent> = Observable.mergeArray(
        editPasswordIntentPublisher,
        editUsernameIntentPublisher,
        loginClicksIntentPublisher
    )

    override fun render(state: LoginViewState) {
        when (state.uiEvents) {
            LoginViewState.LoginUiEvents.JUMP_MAIN -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                return
            }
        }

        progressBar.visibility = when (state.isLoading) {
            true -> View.VISIBLE
            false -> View.GONE
        }
    }

    private fun bind() {
        tvUsername.textChanges()
            .map { LoginIntent.EditUsernameIntent(it.toString()) }
            .autoDisposable(scopeProvider)
            .subscribe(editUsernameIntentPublisher)
        tvPassword.textChanges()
            .map { LoginIntent.EditPasswordIntent(it.toString()) }
            .autoDisposable(scopeProvider)
            .subscribe(editPasswordIntentPublisher)
        btnLogin.clicks()
            .map {
                LoginIntent.LoginClicksIntent(
                    username = tvUsername.text.toString(),
                    password = tvPassword.text.toString()
                )
            }
            .autoDisposable(scopeProvider)
            .subscribe(loginClicksIntentPublisher)
    }
}
