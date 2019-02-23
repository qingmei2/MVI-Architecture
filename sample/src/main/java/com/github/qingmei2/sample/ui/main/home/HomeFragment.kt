package com.github.qingmei2.sample.ui.main.home

import com.github.qingmei2.mvi.base.view.fragment.BaseFragment
import com.github.qingmei2.sample.R
import io.reactivex.Observable
import org.kodein.di.Kodein

class HomeFragment : BaseFragment<HomeIntent, HomeViewState>() {

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein)
        import(homeKodeinModule)
    }

    override val layoutId: Int = R.layout.fragment_home

    override fun intents(): Observable<HomeIntent> {
        return Observable.merge(
            initialIntent(),
            initialIntent()
        )
    }

    private fun initialIntent(): Observable<HomeIntent.InitialIntent> =
        Observable.just(HomeIntent.InitialIntent)

    override fun render(state: HomeViewState) {

    }
}