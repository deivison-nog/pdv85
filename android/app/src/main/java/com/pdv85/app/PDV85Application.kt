package com.pdv85.app

import android.app.Application
import com.pdv85.app.data.remote.NetworkClient

class PDV85Application : Application() {
    override fun onCreate() {
        super.onCreate()
        NetworkClient.init(applicationContext)
    }
}
