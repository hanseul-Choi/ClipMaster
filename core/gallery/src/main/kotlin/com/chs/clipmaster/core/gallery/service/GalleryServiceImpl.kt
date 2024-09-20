package com.chs.clipmaster.core.gallery.service

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class GalleryServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context
): GalleryService {
    override suspend fun getRecentImageUri(): Uri? {
        return withContext(Dispatchers.IO) {
            val projection = arrayOf(MediaStore.Images.Media._ID)
            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                if (cursor.count == 0) {
                    // 디버깅: 쿼리 결과가 없는 경우
                    Log.e("GalleryRepository", "No images found in MediaStore.")
                } else {
                    Log.d("GalleryRepository", "Found ${cursor.count} images in MediaStore.")
                }

                if (cursor.moveToFirst()) {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                    Log.d("GalleryRepository", "Found image with ID: $id")
                    return@withContext Uri.withAppendedPath(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id.toString()
                    )
                }
            }
            null
        }
    }
}