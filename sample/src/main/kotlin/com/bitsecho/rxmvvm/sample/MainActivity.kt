package com.bitsecho.rxmvvm.sample

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import com.bitsecho.janko.appcompat.lparams
import com.bitsecho.janko.base.*
import com.bitsecho.rxmvvm.base.RxActivity
import com.bitsecho.rxmvvm.base.RxModel
import com.bitsecho.rxmvvm.base.RxViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : RxActivity() {
    //To appoint a ViewModel
    override fun appointVM(): RxViewModel = MainVM()

    //To appoint a UI with Janko
    override fun appointUI(): UI = MainUI()

    //Bind View event to ViewModel event
    override fun bindViewEvent() {
        find<Button>(R.id.button).onClick { //View event
            vm<MainVM>().refreshTime() //ViewModel event
        }
    }

    //Bind Model to View
    override fun bindModelData() {
        vm<MainVM>().text.obs.compose(bindLifeCycle()) // Subscribe Model Observable
            .subscribe {
                find<AppCompatTextView>(R.id.text).ui { // View Change
                    text = it
                }
            }
    }

    //Do some others after two way binding
    override fun onPostBinding(savedInstanceState: Bundle?) {
        window.statusBarColor = getColor(R.color.colorPrimaryDark)
        find<Toolbar>(R.id.toolbar).ui {
            setSupportActionBar(this)
        }
        vm<MainVM>().refreshTime()
    }

}

class MainUI: UI {
    override fun createView(ctx: Context): View {
        return with(ctx) {
            LinearLayoutCompat(ctx).ui {
                orientation = LinearLayoutCompat.VERTICAL

                Toolbar(ctx).ui {
                    id = R.id.toolbar
                    title = "RxMVVM"
                    setTitleTextColor(getColor(R.color.white))
                    setBackgroundColor(getColor(R.color.colorPrimary))
                }.lparams {
                    width = matchParent
                    height = wrapContent
                }.into(this)

                LinearLayoutCompat(ctx).ui {
                    orientation = LinearLayoutCompat.VERTICAL
                    gravity = Gravity.CENTER

                    AppCompatTextView(ctx). ui {
                        id = R.id.text
                        setTextColor(getColor(R.color.black))
                        textSize = 20f
                    }.lparams {
                        width = wrapContent
                        height = wrapContent
                        bottomMargin = dip(40)
                    }.into(this)

                    Button(ctx).ui {
                        id = R.id.button
                        text = "Refresh"
                        setTextColor(getColor(R.color.white))
                        setBackgroundColor(getColor(R.color.colorPrimary))
                    }.lparams {
                        width = dip(200)
                        height = wrapContent
                    }.into(this)
                }.lparams {
                    width = wrapContent
                    height = matchParent
                    gravity = Gravity.CENTER
                }.into(this)

            }.lparams {
                width = matchParent
                height = matchParent
            }
        }
    }
}

class MainVM: RxViewModel() {
    // A sample Model
    val text = RxModel<String>()

    // A sample ViewModel event. Can be added Service Layer call or Network call.
    fun refreshTime() {
        val newText = SimpleDateFormat.getDateTimeInstance().format(Date(System.currentTimeMillis()))
        text.post(newText)
    }
}

