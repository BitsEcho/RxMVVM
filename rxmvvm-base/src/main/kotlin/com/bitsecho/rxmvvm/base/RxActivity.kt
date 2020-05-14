package com.bitsecho.rxmvvm.base

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bitsecho.janko.base.UI
import com.bitsecho.janko.base.setContentView
import com.bitsecho.util.RxBus
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

fun Disposable.disposeOnDestroy(rxActivity: RxActivity) = rxActivity.compositeDisposable.add(this)

abstract class RxActivity: AppCompatActivity() {
    val compositeDisposable = CompositeDisposable()
    private lateinit var rxViewModel: RxViewModel
    protected val rxOptionMenu = RxOptionMenu()
    private var isRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isRunning = true
        rxViewModel = appointVM()
        appointUI().setContentView(this)
        bindViewEvent()
        bindModelData()
        onPostBinding(savedInstanceState)
    }

    protected abstract fun appointVM(): RxViewModel
    protected abstract fun appointUI(): UI
    protected abstract fun bindViewEvent()
    protected abstract fun bindModelData()
    protected abstract fun onPostBinding(savedInstanceState: Bundle?)

    protected fun <T: RxViewModel>vm(): T = rxViewModel as T

    protected fun <T>bindLifeCycle(): ObservableTransformer<T, T> =  ObservableTransformer { it.filter { isRunning } }

    override fun onResume() {
        super.onResume()
        isRunning = true
        rxViewModel.onResume()
    }

    override fun onPause() {
        rxViewModel.onPause()
        isRunning = false
        super.onPause()
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        rxViewModel.onDestroy()
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        rxOptionMenu.rxMenuItemBus.post(item)
        return true
    }

    inner class RxOptionMenu {
        val rxMenuItemBus = RxBus<MenuItem>()
        fun selected(): Observable<MenuItem> = rxMenuItemBus.obs
    }
}







