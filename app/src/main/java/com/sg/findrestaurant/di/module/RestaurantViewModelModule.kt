package com.sg.findrestaurant.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sg.findrestaurant.ui.viewmodel.RestaurantViewModel
import com.sg.findrestaurant.ui.viewmodel.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class RestaurantViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(RestaurantViewModel::class)
    protected abstract fun listingViewModel(viewModel: RestaurantViewModel): ViewModel
}