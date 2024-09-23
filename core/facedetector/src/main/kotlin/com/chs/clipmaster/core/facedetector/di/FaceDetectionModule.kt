package com.chs.clipmaster.core.facedetector.di

import com.chs.clipmaster.core.facedetector.BaseFaceDetectionManager
import com.chs.clipmaster.core.facedetector.BaseOverlayManager
import com.chs.clipmaster.core.facedetector.FaceDetectionManager
import com.chs.clipmaster.core.facedetector.OverlayManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface FaceDetectionModule {

    @Binds
    fun bindFaceDetectionManager(
        faceDetectionManager: FaceDetectionManager
    ): BaseFaceDetectionManager

    @Binds
    fun bindOverlayManager(
        overlayManager: OverlayManager
    ): BaseOverlayManager
}