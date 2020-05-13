package com.bitsecho.rxmvvm.base

import android.view.View
import com.bitsecho.util.RxBus
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable

fun View.click(): Observable<Boolean> {
    return triggerEvent { trigger ->
        this.setOnClickListener {
            trigger()
        }
    }
}

fun View.longClick(): Observable<Boolean> {
    return triggerEvent { trigger ->
        this.setOnLongClickListener {
            trigger()
            true
        }
    }
}

fun View.contextClick(): Observable<Boolean> {
    return triggerEvent { trigger ->
        this.setOnContextClickListener {
            trigger()
            true
        }
    }
}

private fun triggerEvent(block:(trigger: ()->Unit)->Unit): Observable<Boolean> {
    val clickBus = RxBus<Boolean>()
    val trigger  = { clickBus.post(true) }
    block(trigger)
    return clickBus.obs.observeOn(AndroidSchedulers.mainThread())
}