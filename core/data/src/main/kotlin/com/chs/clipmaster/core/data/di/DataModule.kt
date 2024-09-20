package com.chs.clipmaster.core.data.di

import com.chs.clipmaster.core.data.repository.GalleryRepository
import com.chs.clipmaster.core.data.repository.GalleryRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {
    @Binds
    fun bindGalleryRepository(
        galleryRepository: GalleryRepositoryImpl
    ): GalleryRepository
}