package com.chs.clipmaster.core.facedetector

import android.media.Image
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import javax.inject.Inject

class FaceDetectionManager @Inject constructor(
    private val onFaceDetected: (List<Face>) -> Unit
): BaseFaceDetection {
    private val faceDetector: FaceDetector

    init {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST) // 실시간 감지를 위함
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE) // 얼굴 주요 요소는 감지하지 않게
            .build()

        faceDetector = FaceDetection.getClient(options)
    }

    override fun detectFace(image: Image) { // 이미지를 통한 얼굴 감지
        val inputImage = InputImage.fromMediaImage(image, 0)

        faceDetector.process(inputImage)
            .addOnSuccessListener { faces ->
                onFaceDetected(faces)
            }
    }
}