package com.github.qingmei2.sample.ui.login

import arrow.core.Either
import com.github.qingmei2.sample.entity.Errors
import com.github.qingmei2.sample.entity.LoginEntity
import com.github.qingmei2.sample.http.scheduler.SchedulerProvider
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction

class LoginActionProcessorHolder(
    private val repository: LoginDataSourceRepository,
    private val schedulers: SchedulerProvider
) {

    private val initialUiActionTransformer =
        ObservableTransformer<LoginAction.InitialUiAction, LoginResult.AutoLoginInfoResult> { actions ->
            Observable
                .zip(repository.prefsUser().toObservable(),
                    repository.prefsAutoLogin().toObservable(),
                    BiFunction { either: Either<Errors, LoginEntity>, autoLogin: Boolean ->
                        when (autoLogin) {
                            true -> {
                                either.fold({ errors ->
                                    LoginResult.AutoLoginInfoResult.Failure(errors)
                                }, { autoLoginInfo ->
                                    LoginResult.AutoLoginInfoResult.Success(autoLoginInfo)
                                })
                            }
                            false -> LoginResult.AutoLoginInfoResult.NoAutoLogin
                        }
                    })
                .onErrorReturn(LoginResult.AutoLoginInfoResult::Failure)
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
                .startWith(LoginResult.AutoLoginInfoResult.InFlight)
        }

    internal val actionProcessor =
        ObservableTransformer<LoginAction, LoginResult> { actions ->
            actions.publish { shared ->
                Observable.merge(
                    shared.ofType(LoginAction.InitialUiAction::class.java).compose(initialUiActionTransformer),
                    shared.filter { all ->
                        all !is LoginAction.InitialUiAction
                    }.flatMap { unknown ->
                        Observable.error<LoginResult>(
                            IllegalArgumentException("Unknown Action type: $unknown")
                        )
                    }
                )
            }
        }
}