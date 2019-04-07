package com.github.qingmei2.mvi.base.view.adapter

import io.reactivex.Observable

interface AutoDisposeViewHolderEventsProvider {

    fun providesObservable(): Observable<AutoDisposeViewHolder.ViewHolderEvent>
}