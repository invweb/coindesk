package com.app.coindesk.mvvm

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import androidx.navigation.findNavController
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import com.app.coindesk.R
import com.app.coindesk.database.AppDatabase
import com.app.coindesk.entity.Coins
import com.app.coindesk.placeholder.JsonPlaceholderRoot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class InitViewModel(private val app: Application) : AndroidViewModel(app) {
    private val checkString = app.getString(R.string.shared_preference_check)
    private val preferences = "myPreferences"

    private var flag:Boolean? =  loadFlagFromSharedPreference(app.applicationContext)

    private val errorMessageLive = MutableLiveData<String>()

    private var takenOnlyFromDatabase: Boolean = false

    private fun saveDataToDbAsync(coins: Coins){
        GlobalScope.launch(Dispatchers.IO) {
            AppDatabase.getInstance(app)!!.getDao().insertCoins(coins)
        }
    }

    private fun saveLastUpdateDateToPreferences(){
        val dateMillis : Long = System.currentTimeMillis()
        saveDateToSharedPreference(app, dateMillis)
    }

    private fun loadFlagFromSharedPreference(context: Context): Boolean? {
        val sharedPreference = context.getSharedPreferences(
            context.getString(R.string.shared_preference_time), Context.MODE_PRIVATE)
        return sharedPreference?.getBoolean(checkString, false)
    }

    private fun saveTimeToSharedPreference(context: Context){
        val sharedPreference = context.getSharedPreferences(preferences, Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putLong(context.getString(R.string.shared_preference_date_millis), System.currentTimeMillis())
        editor.apply()
    }

    private fun saveDateToSharedPreference(context: Context, dateMillis : Long){
        val sharedPreference = context.getSharedPreferences(
            context.getString(R.string.shared_preference_time), Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putLong(context.getString(R.string.shared_preference_date_millis), dateMillis)
        editor.apply()
    }

    fun getCoins(): Array<Coins>  {
        return AppDatabase.getInstance(app)!!.getDao().getCoins()
    }

    fun saveCoinsToDB(
        activity: Activity
    ){
        JsonPlaceholderRoot
            .api
            .getCoins()
            .enqueue(object : Callback, retrofit2.Callback<Coins> {
                override fun onFailure(
                    call: retrofit2.Call<Coins>,
                    t: Throwable
                ) {
                    takenOnlyFromDatabase = true

                    activity.findNavController(R.id.button).navigate(R.id.action_MainFragment_to_LaunchesFragment)

                    flag = false
                }

                override fun onResponse(
                    call: retrofit2.Call<Coins>,
                    response: retrofit2.Response<Coins>
                ) {
                    saveLastUpdateDateToPreferences()

                    val body : Coins? = response.body()
                    val bodyNotNull : Coins = body!!

                    saveDataToDbAsync(bodyNotNull)

                    takenOnlyFromDatabase = false

                    saveTimeToSharedPreference(activity)

                    GlobalScope.launch(Dispatchers.IO) {
                        getCoins()
                    }
                }

                override fun onFailure(
                    call: Call,
                    e: IOException) {
                    errorMessageLive.value = e.message

                    takenOnlyFromDatabase = true

                    activity.findNavController(R.id.button).navigate(R.id.action_MainFragment_to_LaunchesFragment)
                }

                override fun onResponse(
                    call: Call,
                    response: Response
                ) {
                    //not needed, using another onResponse
                }
            })
    }
}
