package com.github.qingmei2.mvi.ext.reactivex

import android.view.View
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

fun View.throttleFirstClicks(): Observable<Unit> =
    clicks().throttleFirst(500, TimeUnit.MILLISECONDS)