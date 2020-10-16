package com.example.snplc.di

import com.example.snplc.repositories.AuthRepository
import com.example.snplc.repositories.DefaultAuthRepository
import com.example.snplc.repositories.DefaultMainRepository
import com.example.snplc.repositories.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object MainModule {

    @ActivityScoped
    @Provides
    fun provideMainRepository() = DefaultMainRepository() as MainRepository
}