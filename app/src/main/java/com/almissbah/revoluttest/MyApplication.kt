package com.almissbah.revoluttest

import com.almissbah.revoluttest.di.component.AppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class MyApplication : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val component: AppComponent = DaggerAppComponent.builder().application(this).build()
        component.inject(this)
        return component
    }
}