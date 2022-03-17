package com.sg.findrestaurant.di.module

import com.sg.findrestaurant.ui.activity.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class RestaurantActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity
}