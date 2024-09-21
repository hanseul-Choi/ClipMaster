package com.chs.clipmaster.core.facedetector

import android.media.Image

interface BaseFaceDetection {
    fun detectFace(image: Image)
}