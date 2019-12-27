package com.almissbah.revotest.di.module

import com.almissbah.revotest.ui.main.fragment.CurrenciesFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {
    @ContributesAndroidInjector
    abstract fun contributeCurrenciesFragment(): CurrenciesFragment

}