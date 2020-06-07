package com.bitsecho.rxmvvm.base

import android.content.Intent
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
    private var isRunning = false
    private val activityCodeMap = HashMap<String, RxBus<Intent>>()
    private val optionMenuIdMap = HashMap<Int, RxBus<MenuItem>>()
    private val lifeCycleList = mutableListOf<LifeCycle>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isRunning = true
        rxViewModel = appointVM()
        appointUI().setContentView(this)
        initLifeCycles()
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

    private fun initLifeCycles() {
        val isRunningLifeCycle = object: LifeCycle {
            override fun onResume() { isRunning = true }
            override fun onPause() { isRunning = false }
            override fun onDestroy() { isRunning = false }
        }

        val rxViewModelLifeCycle = object: LifeCycle {
            override fun onResume() = rxViewModel.onResume()
            override fun onPause() = rxViewModel.onPause()
            override fun onDestroy() = rxViewModel.onDestroy()
        }

        registerLifeCycle(isRunningLifeCycle)
        registerLifeCycle(rxViewModelLifeCycle)
    }

    override fun onResume() {
        super.onResume()
        lifeCycleList.forEach { it.onResume() }
    }

    override fun onPause() {
        lifeCycleList.forEach { it.onPause() }
        super.onPause()
    }

    override fun onDestroy() {
        lifeCycleList.forEach { it.onDestroy() }
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val menuItemBus = optionMenuIdMap[item.itemId]
        menuItemBus?.post(item)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val codeKey = ""+requestCode+"_"+resultCode
        activityCodeMap[codeKey]?.post(data?: Intent())
    }

    fun subscribeActivityResult(requestCode: Int, resultCode: Int): Observable<Intent> {
        val activityCodeKey = ""+requestCode+"_"+resultCode
        val bus = RxBus<Intent>()
        activityCodeMap[activityCodeKey] = bus
        return bus.obs
    }

    fun subscribeOptionItemSelected(itemId: Int): Observable<MenuItem> {
        val bus = RxBus<MenuItem>()
        optionMenuIdMap[itemId] = bus
        return bus.obs
    }

    fun registerLifeCycle(lifeCycle: LifeCycle) {
        lifeCycleList.add(lifeCycle)
    }
}

interface LifeCycle {
    fun onResume()
    fun onPause()
    fun onDestroy()
}







