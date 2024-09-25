package com.chs.clipmaster.core.facedetector

import android.graphics.RectF
import android.media.Image
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import javax.inject.Inject

class FaceDetectionManager @Inject constructor(
): BaseFaceDetectionManager {
    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE) // 실시간 감지 시 PERFORMANCE_MODE_FAST 사용
        .setMinFaceSize(0.15f)
        .build()

    private val detector = FaceDetection.getClient(options)

    override fun detectFace(
        image: Image,
        onFacesDetected: (List<RectF>) -> Unit,
        onComplete: () -> Unit
    ) {
        val inputImage = InputImage.fromMediaImage(image, 0)

        detector.process(inputImage)
            .addOnSuccessListener { faces ->
                onFacesDetected(faces.map { face ->
                    RectF(face.boundingBox)
                })
            }
            .addOnFailureListener { e ->
                Log.e("FaceDetectionManager", "Face detection failed", e)
            }
            .addOnCompleteListener {
                onComplete()
            }
    }
}