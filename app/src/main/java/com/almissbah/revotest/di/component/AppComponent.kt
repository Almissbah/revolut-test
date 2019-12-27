package com.almissbah.revotest.di.component

import android.app.Application
import com.almissbah.revotest.RevoApp
import com.almissbah.revotest.di.module.ActivityBuilderModule
import com.almissbah.revotest.di.module.ApiModule
import com.almissbah.revotest.di.module.FragmentModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [ApiModule::class, AndroidSupportInjectionModule::class, ActivityBuilderModule::class, FragmentModule::class])
interface AppComponent : AndroidInjector<DaggerApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(revoApp: RevoApp?)
}