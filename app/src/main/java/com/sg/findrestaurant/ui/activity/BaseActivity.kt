package com.sg.findrestaurant.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    //abstract fun getIntentData()
    abstract fun initViews()
    abstract fun initClasses()
    abstract fun initObservers()
    abstract fun loadData()


    fun showToast(message: String) {
        Toast.makeText(baseContext, message, Toast.LENGTH_SHORT).show()
    }
}