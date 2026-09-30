package com.devvault.app.di

import com.devvault.app.data.repository.DevVaultRepository
import com.devvault.app.data.repository.DevVaultRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindDevVaultRepository(
        impl: DevVaultRepositoryImpl
    ): DevVaultRepository
}