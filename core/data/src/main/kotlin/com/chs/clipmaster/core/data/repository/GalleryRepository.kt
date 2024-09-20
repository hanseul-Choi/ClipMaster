package com.chs.clipmaster.core.data.repository

import android.net.Uri

interface GalleryRepository {
    suspend fun getRecentImageUri(): Uri?
    fun moveToGallery()
}