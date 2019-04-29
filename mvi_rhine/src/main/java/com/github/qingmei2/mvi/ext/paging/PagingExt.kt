package com.github.qingmei2.mvi.ext.paging

import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Scheduler

object Paging {

    fun <T> buildPageKeyedDataSource(
        intPageKeyedDataSource: IntPageKeyedDataSource<T>,
        enablePlaceholders: Boolean = false,
        pageSize: Int = DEFAULT_PAGE_SIZE,
        initialLoadSizeHint: Int = DEFAULT_INITIAL_LOAD_SIZE_HINT,
        prefetchDistance: Int = DEFAULT_PREFETCH_DISTANCE
    ): Flowable<PagedList<T>> =
        buildRxJavaPagedList(
            dataSourceFactory = intPageKeyedDataSource(dataSource = intPageKeyedDataSource),
            enablePlaceholders = enablePlaceholders,
            pageSize = pageSize,
            initialLoadSizeHint = initialLoadSizeHint,
            prefetchDistance = prefetchDistance
        )

    private fun <T> intPageKeyedDataSource(
        dataSource: IntPageKeyedDataSource<T>
    ): DataSource.Factory<Int, T> = object : DataSource.Factory<Int, T>() {

        override fun create(): DataSource<Int, T> = dataSource
    }

    private fun <T> buildRxJavaPagedList(
        dataSourceFactory: DataSource.Factory<Int, T>,
        enablePlaceholders: Boolean = false,
        pageSize: Int = DEFAULT_PAGE_SIZE,
        initialLoadSizeHint: Int = DEFAULT_INITIAL_LOAD_SIZE_HINT,
        prefetchDistance: Int = DEFAULT_PREFETCH_DISTANCE
    ): Flowable<PagedList<T>> =
        RxPagedListBuilder<Int, T>(
            dataSourceFactory,
            PagedList.Config
                .Builder()
                .setInitialLoadSizeHint(initialLoadSizeHint)
                .setPageSize(pageSize)
                .setPrefetchDistance(prefetchDistance)
                .setEnablePlaceholders(enablePlaceholders)
                .build()
        ).buildFlowable(BackpressureStrategy.LATEST)

    private const val DEFAULT_PAGE_SIZE = 15
    private const val DEFAULT_PREFETCH_DISTANCE = DEFAULT_PAGE_SIZE
    private const val DEFAULT_INITIAL_LOAD_SIZE_HINT = 30
}

fun <Key, Value> DataSource.Factory<Key, Value>.toRxPagedList(
    config: PagedList.Config? = null,
    boundaryCallback: PagedList.BoundaryCallback<Value>? = null,
    fetchSchedulers: Scheduler
): Flowable<PagedList<Value>> {
    return RxPagedListBuilder<Key, Value>(
        this, config ?: PagedList.Config.Builder()
            .setInitialLoadSizeHint(PAGING_DEFAULT_INITIAL_LOAD_SIZE_HINT)
            .setPageSize(PAGING_DEFAULT_PAGE_SIZE)
            .setPrefetchDistance(PAGING_DEFAULT_PREFETCH_DISTANCE)
            .setEnablePlaceholders(false)
            .build()
    )
        .setBoundaryCallback(boundaryCallback)
        .setFetchScheduler(fetchSchedulers)
        .buildFlowable(BackpressureStrategy.LATEST)
}

const val PAGING_DEFAULT_PAGE_SIZE = 15
const val PAGING_DEFAULT_PREFETCH_DISTANCE = 15
const val PAGING_DEFAULT_INITIAL_LOAD_SIZE_HINT = 30