package com.chs.clipmaster.core.facedetector

import android.graphics.RectF
import android.media.Image
import com.google.mlkit.vision.face.Face

interface BaseFaceDetectionManager {
    fun detectFace(image: Image, onFacesDetected: (List<RectF>) -> Unit, onComplete: () -> Unit)
}
