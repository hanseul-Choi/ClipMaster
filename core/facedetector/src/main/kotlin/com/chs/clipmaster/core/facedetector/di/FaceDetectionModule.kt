package com.chs.clipmaster.core.facedetector.di

import com.chs.clipmaster.core.facedetector.BaseFaceDetection
import com.chs.clipmaster.core.facedetector.FaceDetectionManager
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
    ): BaseFaceDetection
}