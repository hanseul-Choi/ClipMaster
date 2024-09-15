package com.chs.clipmaster.core.navigation.di

import com.chs.clipmaster.core.navigation.AppComposeNavigator
import com.chs.clipmaster.core.navigation.ClipmasterComposeNavigator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class NavigationModule {

    @Binds
    @Singleton
    abstract fun provideComposeNavigator(
        clipmasterComposeNavigator: ClipmasterComposeNavigator
    ): AppComposeNavigator
}