package com.yzytmac.webrtcandroid

import android.app.Application
import android.content.Context

/**
 * Created by yzy on 2020-06-19 16:53
 * Email: yzytmac@163.com
 * Phone: 18971165201
 * QQ: 398564331
 * Description:
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        context=this
    }

    companion object {
        lateinit var context:Context
    }
}