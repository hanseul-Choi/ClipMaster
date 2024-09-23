package com.chs.clipmaster.feature.camera

import android.graphics.RectF
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.chs.clipmaster.core.facedetector.BaseFaceDetectionManager

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    imageCapture: ImageCapture, // ImageCapture 전달
    faceDetectionManager: BaseFaceDetectionManager, // 얼굴 감지 매니저
    onFacesDetected: (List<RectF>) -> Unit, // 얼굴 좌표 전달 콜백
    onPreviewViewCreated: (PreviewView) -> Unit // PreviewView 생성 후 전달
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                // Preview 설정
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                // ImageAnalysis 설정 (얼굴 인식용)
                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->
                            val image = imageProxy.image
                            if (image != null) {
                                // 얼굴 감지 로직 호출
                                faceDetectionManager.detectFace(image, { faces ->
                                    // 얼굴 감지 후 좌표 변환
                                    val faceRects = faces.map { face ->
                                        // 얼굴 좌표를 프리뷰 좌표로 변환
                                        mapRectToPreview(RectF(face.boundingBox), imageProxy, previewView)
                                    }
                                    onFacesDetected(faceRects) // 좌표 전달
                                }) {
                                    // 이미지 리소스 해제
                                    imageProxy.close()
                                }
                            } else {
                                imageProxy.close() // image가 null인 경우에도 반드시 close 호출
                            }
                        }
                    }

                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalyzer,
                        imageCapture // ImageCapture 추가
                    )
                } catch (e: Exception) {
                    Log.e("CameraPreview", "Failed to bind camera use cases", e)
                }
            }, ContextCompat.getMainExecutor(ctx))

            // PreviewView가 생성된 후 외부로 전달
            onPreviewViewCreated(previewView)

            previewView
        },
        modifier = modifier
    )
}

fun mapRectToPreview(rect: RectF, imageProxy: ImageProxy, previewView: PreviewView): RectF {
    // 프리뷰 화면의 크기 가져오기
    val previewWidth = previewView.width.toFloat()
    val previewHeight = previewView.height.toFloat()

    // 이미지 크기
    val imageWidth = imageProxy.width.toFloat()
    val imageHeight = imageProxy.height.toFloat()

    // 좌표 변환 비율 계산
    val widthRatio = previewWidth / imageWidth
    val heightRatio = previewHeight / imageHeight

    // 얼굴 좌표를 프리뷰 화면 좌표로 변환
    return RectF(
        rect.left * widthRatio,
        rect.top * heightRatio,
        rect.right * widthRatio,
        rect.bottom * heightRatio
    )
}