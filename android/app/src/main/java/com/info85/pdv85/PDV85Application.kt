package com.info85.pdv85

import android.app.Application
import com.info85.pdv85.data.remote.NetworkClient

class PDV85Application : Application() {
    override fun onCreate() {
        super.onCreate()
        NetworkClient.init(applicationContext)
    }
}
