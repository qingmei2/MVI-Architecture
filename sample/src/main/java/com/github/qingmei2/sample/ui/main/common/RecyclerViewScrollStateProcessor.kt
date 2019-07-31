package com.github.qingmei2.sample.ui.main.common

import androidx.recyclerview.widget.RecyclerView
import com.github.qingmei2.sample.entity.Errors
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.zipWith
import java.util.concurrent.TimeUnit

/**
 * The [ObservableTransformer] that make [RecyclerView] Scroll state transform to
 * Visible state.
 */
val scrollStateProcessor: ObservableTransformer<Int, Boolean>
    get() = ObservableTransformer { scrollState ->
        scrollState
            .debounce(500, TimeUnit.MILLISECONDS)
            .map { it == RecyclerView.SCROLL_STATE_IDLE }
            .compose { upstream ->
                upstream
                    .zipWith(upstream.startWith(true)) { last, current ->
                        when (last == current) {
                            true -> Result.failure<Boolean>(Errors.EmptyInputError)
                            false -> Result.success(current)
                        }
                    }
            }
            .flatMap { changed ->
                changed.fold({
                    Observable.just(it)
                }, {
                    Observable.empty<Boolean>()
                })
            }
    }