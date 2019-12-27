package com.almissbah.revotest.di.module

import com.almissbah.revotest.data.RevoRepository
import com.almissbah.revotest.data.remote.CurrenciesApiService
import com.almissbah.revotest.utils.BASE_URL
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
object ApiModule {

    @Provides
    @Reusable
    @JvmStatic
    internal fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        return gsonBuilder.create()
    }

    @Provides
    @Reusable
    @JvmStatic
    internal fun provideRetrofitInterface(gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
    }

    @Provides
    @Reusable
    @JvmStatic
    internal fun provideApiService(retrofit: Retrofit): CurrenciesApiService {
        return retrofit.create(CurrenciesApiService::class.java)
    }


    @Provides
    @Reusable
    @JvmStatic
    internal fun provideRepository(currenciesApiService: CurrenciesApiService): RevoRepository {
        return RevoRepository(currenciesApiService)
    }
}
