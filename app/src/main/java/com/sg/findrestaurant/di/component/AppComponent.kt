package com.sg.findrestaurant.di.component

import android.app.Application
import com.sg.findrestaurant.MyApp
import com.sg.findrestaurant.di.module.ApiClientModule
import com.sg.findrestaurant.di.module.RestaurantActivityModule
import com.sg.findrestaurant.di.module.RestaurantViewModelModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules =
    [
        AndroidInjectionModule::class,
        ApiClientModule::class,
        RestaurantViewModelModule::class,
        RestaurantActivityModule::class
    ]
)
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }

    fun inject(application: MyApp)
}
