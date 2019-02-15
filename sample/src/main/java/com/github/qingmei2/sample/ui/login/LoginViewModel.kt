package com.github.qingmei2.sample.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.qingmei2.mvi.base.viewmodel.BaseViewModel
import com.github.qingmei2.mvi.ext.reactivex.notOfType
import com.github.qingmei2.mvi.util.SingletonHolderSingleArg
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.subjects.PublishSubject
import java.util.function.BiFunction

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
            // Cache each state and pass it to the reducer to create a new state from
            // the previous cached one and the latest Result emitted from the action processor.
            // The Scan operator is used here for the caching.
            .scan(LoginViewState.idle(), reducer)
            // When a reducer just emits previousState, there's no reason to call render. In fact,
            // redrawing the UI in cases like this can cause jank (e.g. messing up snackbar animations
            // by showing the same snackbar twice in rapid succession).
            .distinctUntilChanged()
            // Emit the last one event of the stream on subscription
            // Useful when a View rebinds to the ViewModel after rotation.
            .replay(1)
            // Create the stream on creation without waiting for anyone to subscribe
            // This allows the stream to stay alive even when the UI disconnects and
            // match the stream's lifecycle to the ViewModel's one.
            .autoConnect(0)
    }

    private fun actionFromIntent(intent: LoginIntent): LoginAction {
        return when (intent) {
            is LoginIntent.InitialIntent -> LoginAction.InitialUiAction
            is LoginIntent.LoginClicksIntent -> LoginAction.ClickLoginAction(intent.username, intent.password)
            is LoginIntent.EditPasswordIntent -> LoginAction.EditPasswordAction(intent.password)
            is LoginIntent.EditUsernameIntent -> LoginAction.EditUsernameAction(intent.username)
        }
    }

    companion object {

        private val reducer = BiFunction { previousState: LoginViewState, result: LoginResult ->
            when (result) {
                is LoginResult.ClickLoginResult -> when (result) {
                    is LoginResult.ClickLoginResult.Success -> {

                    }
                    is LoginResult.ClickLoginResult.Failure -> {

                    }
                    is LoginResult.ClickLoginResult.InFlight -> {

                    }
                }
                is LoginResult.AutoLoginInfoResult -> when (result) {
                    is LoginResult.AutoLoginInfoResult.Success -> {

                    }
                    is LoginResult.AutoLoginInfoResult.Failure -> {

                    }
                    is LoginResult.AutoLoginInfoResult.InFlight -> {

                    }
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