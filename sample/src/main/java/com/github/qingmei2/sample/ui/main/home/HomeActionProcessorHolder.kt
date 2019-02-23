package com.github.qingmei2.sample.ui.main.home

import com.github.qingmei2.sample.http.scheduler.SchedulerProvider

class HomeActionProcessorHolder(
    private val repository: HomeRepository,
    private val schedulerProvider: SchedulerProvider
) {

}