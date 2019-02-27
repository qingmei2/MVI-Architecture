package com.github.qingmei2.sample.ui.main

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.github.qingmei2.mvi.base.viewmodel.AutoDisposeViewModel

class MainViewModel : AutoDisposeViewModel() {

    companion object {
        fun instance(fragment: Fragment): MainViewModel =
            ViewModelProviders
                .of(fragment, MainViewModelFactory)[MainViewModel::class.java]
    }
}

@Suppress("UNCHECKED_CAST")
object MainViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        MainViewModel() as T
}