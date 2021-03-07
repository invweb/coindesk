package com.app.coindesk.application

import android.app.Application
import com.app.coindesk.database.AppDatabase

class DeskApplication: Application() {

    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()

        database = AppDatabase.getInstance(this)!!
    }
}