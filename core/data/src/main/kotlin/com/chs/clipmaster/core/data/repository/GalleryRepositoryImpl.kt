package com.chs.clipmaster.core.data.repository

import android.net.Uri
import com.chs.clipmaster.core.gallery.service.GalleryService
import javax.inject.Inject

class GalleryRepositoryImpl @Inject constructor(
    private val galleryService: GalleryService
) : GalleryRepository {
    override suspend fun getRecentImageUri(): Uri? {
        return galleryService.getRecentImageUri()
    }

    override fun moveToGallery(recentUri: Uri?) {
        galleryService.moveToGallery(recentUri)
    }
}