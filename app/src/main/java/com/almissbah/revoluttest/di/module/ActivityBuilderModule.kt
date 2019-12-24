package com.almissbah.revoluttest.di.module

import com.almissbah.revoluttest.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {
    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeMainActivity(): MainActivity?

}
