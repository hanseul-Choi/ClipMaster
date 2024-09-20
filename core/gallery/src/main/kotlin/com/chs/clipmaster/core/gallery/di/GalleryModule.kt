package com.chs.clipmaster.core.gallery.di

import com.chs.clipmaster.core.gallery.service.GalleryService
import com.chs.clipmaster.core.gallery.service.GalleryServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface GalleryModule {

    @Binds
    fun bindGalleryService(
        galleryServiceImpl: GalleryServiceImpl
    ): GalleryService
}