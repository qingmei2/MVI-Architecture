package com.github.qingmei2.mvi.logger

import android.app.Application
import timber.log.Timber

fun Application.initLogger(isDebug: Boolean = true) {
    if (isDebug)
        Timber.plant(Timber.DebugTree())
    else
        Timber.plant(CrashReportingTree())
}

fun log(text: String) = logd(text)

fun logd(text: String) = Timber.d(text)

fun logi(text: String) = Timber.i(text)

fun logw(text: String) = Timber.w(text)

fun loge(text: String) = Timber.e(text)