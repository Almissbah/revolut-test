package com.almissbah.revoluttest.di.module

import com.almissbah.revoluttest.ui.main.CurrenciesFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {
    @ContributesAndroidInjector
    abstract fun contributeCurrenciesFragment(): CurrenciesFragment

}