package com.sg.findrestaurant.di.module

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sg.findrestaurant.data.RestaurantRepository
import com.sg.findrestaurant.data.remote.ApiServiceInterface
import com.sg.findrestaurant.utils.Constants
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
class ApiClientModule {

    @Provides
    @Singleton
    fun provideGson(): Gson =
        GsonBuilder().setLenient().create()


    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiServiceInterface =
        retrofit.create(ApiServiceInterface::class.java)

    @Singleton
    @Provides
    fun provideRestaurantRepository(
        apiServiceInterface: ApiServiceInterface
    ): RestaurantRepository =
        RestaurantRepository(apiServiceInterface)
}