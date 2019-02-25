package com.github.qingmei2.mvi.ext.paging

import android.annotation.SuppressLint
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PageKeyedDataSource
import io.reactivex.processors.PublishProcessor

@SuppressLint("CheckResult")
class IntPageKeyedDataSource<T>(
    private val loadInitial: IntPageKeyedDataInitialProvider<T>,
    private val loadAfter: IntPageKeyedDataEachTimeProvider<T>
) : PageKeyedDataSource<Int, T>(), DefaultLifecycleObserver {

    private val cancelProcessor: PublishProcessor<Unit> = PublishProcessor.create<Unit>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, T>) {
        loadInitial(params)
            .takeUntil(cancelProcessor)
            .subscribe { data ->
                val (list, pageIndex, hasAdjacentPageKey) = data
                when (hasAdjacentPageKey) {
                    true -> callback.onResult(list, pageIndex, pageIndex + 1)
                    false -> callback.onResult(list, pageIndex, null)
                }
            }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {
        loadAfter(params)
            .takeUntil(cancelProcessor)
            .subscribe { data ->
                val (list, pageIndex, hasAdjacentPageKey) = data
                when (hasAdjacentPageKey) {
                    true -> callback.onResult(list, pageIndex + 1)
                    false -> callback.onResult(list, null)
                }
            }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {
        // do nothing
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        cancelProcessor.onNext(Unit)
        cancelProcessor.onComplete()
    }
}