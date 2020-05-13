package com.bitsecho.rxmvvm.base

import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

fun Disposable.disposeOnDestroy(viewModel: RxViewModel) = viewModel.compositeDisposable.add(this)

open class RxViewModel{
    val compositeDisposable = CompositeDisposable()
    private var isRunning = false

    fun onPause() {
        isRunning = false
    }

    fun onResume() {
        isRunning = true
    }

    fun onDestroy() {
        compositeDisposable.dispose()
    }

    protected fun <T>bindLifeCycle(): ObservableTransformer<T, T> {
        return ObservableTransformer {
            it.filter { isRunning }
        }
    }
}