package com.github.qingmei2.sample.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.qingmei2.mvi.base.viewmodel.BaseViewModel
import com.github.qingmei2.mvi.ext.reactivex.notOfType
import com.github.qingmei2.mvi.util.SingletonHolderSingleArg
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

class LoginViewModel(
    private val processorHolder: LoginActionProcessorHolder
) : BaseViewModel<LoginIntent, LoginViewState>() {

    private val intentsSubject: PublishSubject<LoginIntent> = PublishSubject.create()
    private val statesObservable: Observable<LoginViewState> = compose()

    override fun processIntents(intents: Observable<LoginIntent>) {
        intents.autoDisposable(this).subscribe(intentsSubject)
    }

    override fun states(): Observable<LoginViewState> = statesObservable

    private val intentFilter: ObservableTransformer<LoginIntent, LoginIntent>
        get() = ObservableTransformer { intents ->
            intents.publish { shared ->
                Observable.merge(
                    shared.ofType(LoginIntent.InitialIntent::class.java).take(1),
                    shared.notOfType(LoginIntent.InitialIntent::class.java)
                )
            }
        }

    /**
     * Compose all components to create the stream logic
     */
    private fun compose(): Observable<LoginViewState> {
        return intentsSubject
            .compose(intentFilter)
            .map(this::actionFromIntent)
            .compose(processorHolder.actionProcessor)
            .scan(LoginViewState.idle(), reducer)
            .distinctUntilChanged()
            .replay(1)
            .autoConnect(0)
    }

    private fun actionFromIntent(intent: LoginIntent): LoginAction {
        return when (intent) {
            is LoginIntent.InitialIntent -> LoginAction.InitialUiAction
            is LoginIntent.LoginClicksIntent -> LoginAction.ClickLoginAction(intent.username, intent.password)
        }
    }

    companion object {

        private val reducer = BiFunction { previousState: LoginViewState, result: LoginResult ->
            when (result) {
                is LoginResult.ClickLoginResult -> when (result) {
                    is LoginResult.ClickLoginResult.Success -> {
                        previousState.copy(
                            isLoading = false,
                            uiEvents = LoginViewState.LoginUiEvents.JumpMain(result.user)
                        )
                    }
                    is LoginResult.ClickLoginResult.Failure -> previousState.copy(
                        isLoading = false,
                        errors = result.error
                    )
                    is LoginResult.ClickLoginResult.InFlight -> previousState.copy(isLoading = true)
                }
                is LoginResult.AutoLoginInfoResult -> when (result) {
                    is LoginResult.AutoLoginInfoResult.Success -> {
                        previousState.copy(
                            isLoading = true,
                            uiEvents = LoginViewState.LoginUiEvents.TryAutoLogin(
                                loginEntity = result.user,
                                autoLogin = result.autoLogin
                            )
                        )
                    }
                    is LoginResult.AutoLoginInfoResult.NoUserData -> {
                        previousState.copy(
                            isLoading = false
                        )
                    }
                    is LoginResult.AutoLoginInfoResult.Failure -> previousState.copy(
                        isLoading = false,
                        errors = result.error
                    )
                    is LoginResult.AutoLoginInfoResult.InFlight -> previousState.copy(isLoading = true)
                }
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class LoginViewModelFactory private constructor(
    private val processorHolder: LoginActionProcessorHolder
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        LoginViewModel(processorHolder) as T

    companion object :
        SingletonHolderSingleArg<LoginViewModelFactory, LoginActionProcessorHolder>(::LoginViewModelFactory)
}