package com.bitsecho.rxmvvm.base

import com.bitsecho.util.RxBus
import io.reactivex.rxjava3.core.Observable

class RxModel<T> {
    private val modelBus = RxBus<T>()
    var last: T? = null
        private set
    val obs: Observable<T> = modelBus.obs.map {
        last = it
        it
    }
    fun post(t: T) = modelBus.post(t)
}

