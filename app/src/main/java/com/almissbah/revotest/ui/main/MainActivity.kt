package com.almissbah.revotest.ui.main

import android.os.Bundle
import com.almissbah.revotest.R
import com.almissbah.revotest.ui.main.fragment.CurrenciesFragment
import dagger.android.support.DaggerAppCompatActivity

class MainActivity : DaggerAppCompatActivity() {

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
