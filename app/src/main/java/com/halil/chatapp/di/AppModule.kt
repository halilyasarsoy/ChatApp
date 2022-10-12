package com.halil.chatapp.di

import android.content.Context
import com.halil.chatapp.repository.MainRepositoryDefault
import com.halil.chatapp.repository.MainRepositoryInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

object AppModule {
    @Singleton
    @Provides
    fun provideApplicationContext(@ApplicationContext context: Context) = context

    @Singleton
    @Provides
    fun provideMainRepository () = MainRepositoryDefault() as MainRepositoryInterface


}