package com.chs.clipmaster.core.gallery.service

import android.net.Uri

interface GalleryService {
    suspend fun getRecentImageUri(): Uri?

    fun moveToGallery()
}