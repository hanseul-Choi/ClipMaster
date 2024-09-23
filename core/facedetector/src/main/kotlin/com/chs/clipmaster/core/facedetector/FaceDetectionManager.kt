package com.chs.clipmaster.core.facedetector

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
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .build()

    private val detector = FaceDetection.getClient(options)

    override fun detectFace(
        image: Image,
        onFacesDetected: (List<Face>) -> Unit,
        onComplete: () -> Unit
    ) {
        val inputImage = InputImage.fromMediaImage(image, 0)

        detector.process(inputImage)
            .addOnSuccessListener { faces ->
                onFacesDetected(faces) // 얼굴 감지 결과를 콜백으로 전달
            }
            .addOnFailureListener { e ->
                Log.e("FaceDetectionManager", "Face detection failed", e)
            }
            .addOnCompleteListener {
                onComplete()
            }
    }
}