package com.app.coindesk.mvvm

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class InitViewModelFactory(private val myApplication: Application) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return InitViewModel(myApplication) as T
    }
}