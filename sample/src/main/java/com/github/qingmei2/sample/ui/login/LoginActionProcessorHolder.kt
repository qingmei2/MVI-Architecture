package com.github.qingmei2.sample.ui.login

import arrow.core.Either
import com.github.qingmei2.mvi.ext.reactivex.flatMapErrorActionObservable
import com.github.qingmei2.sample.entity.Errors
import com.github.qingmei2.sample.entity.LoginEntity
import com.github.qingmei2.sample.entity.LoginUser
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
            actions.flatMap {
                Observable
                    .zip(repository.prefsUser().toObservable(),
                        repository.prefsAutoLogin().toObservable(),
                        BiFunction { either: Either<Errors, LoginEntity>, autoLogin: Boolean ->
                            either.fold({
                                LoginResult.AutoLoginInfoResult.NoUserData
                            }, { autoLoginInfo ->
                                LoginResult.AutoLoginInfoResult.Success(autoLoginInfo, autoLogin)
                            })
                        })
                    .onErrorReturn(LoginResult.AutoLoginInfoResult::Failure)
                    .subscribeOn(schedulers.io())
                    .observeOn(schedulers.ui())
                    .startWith(LoginResult.AutoLoginInfoResult.InFlight)
            }
        }

    private val loginClickActionTransformer =
        ObservableTransformer<LoginAction.ClickLoginAction, LoginResult.ClickLoginResult> { actions ->
            actions.flatMap { o ->
                val (username, password) = o
                when (username.isNullOrEmpty() || password.isNullOrEmpty()) {
                    true -> onLoginParamEmptyResult()
                    false -> repository
                        .login(username, password)
                        .toObservable()
                        .flatMap { either ->
                            either.fold(::onLoginFailureResult, ::onLoginSuccessResult)
                        }
                        .onErrorReturn(LoginResult.ClickLoginResult::Failure)
                        .subscribeOn(schedulers.io())
                        .observeOn(schedulers.ui())
                        .startWith(LoginResult.ClickLoginResult.InFlight)
                }
            }
        }

    private fun onLoginParamEmptyResult(): Observable<LoginResult.ClickLoginResult> =
        Observable.just(Errors.SimpleMessageError("username or password can't be null!"))
            .map(LoginResult.ClickLoginResult::Failure)

    private fun onLoginFailureResult(error: Errors): Observable<LoginResult.ClickLoginResult> =
        Observable.just(LoginResult.ClickLoginResult.Failure(error))

    private fun onLoginSuccessResult(loginUser: LoginUser): Observable<LoginResult.ClickLoginResult> =
        Observable.just(LoginResult.ClickLoginResult.Success(loginUser))

    internal val actionProcessor =
        ObservableTransformer<LoginAction, LoginResult> { actions ->
            actions.publish { shared ->
                Observable.merge(
                    shared.ofType(LoginAction.InitialUiAction::class.java).compose(initialUiActionTransformer),
                    shared.ofType(LoginAction.ClickLoginAction::class.java).compose(loginClickActionTransformer),
                    shared.filter { all ->
                        all !is LoginAction.InitialUiAction &&
                                all !is LoginAction.ClickLoginAction
                    }.flatMapErrorActionObservable()
                )
            }.retry()
        }
}