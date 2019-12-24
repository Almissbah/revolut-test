package com.almissbah.revoluttest.ui.main

import android.os.Bundle
import com.almissbah.revoluttest.R
import com.almissbah.revoluttest.data.Repository
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var repository: Repository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, CurrenciesFragment.newInstance())
                .commitNow()
        }
    }

}
