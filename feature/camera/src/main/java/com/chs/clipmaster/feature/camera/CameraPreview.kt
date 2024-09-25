package com.chs.clipmaster.feature.camera

import android.graphics.RectF
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
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

@OptIn(ExperimentalGetImage::class)
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    imageCapture: ImageCapture,
    faceDetectionManager: BaseFaceDetectionManager,
    onFacesDetected: (List<RectF>) -> Unit,
    onPreviewViewCreated: (PreviewView) -> Unit
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
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) // 이미지 비차단 모드
                    .build()
                    .also {
                        it.setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->
                            val image = imageProxy.image
                            if (image != null) {
                                // 얼굴 감지 로직 호출
                                faceDetectionManager.detectFace(image, { faces ->
                                    val faceRects = faces.map { rectFace ->
                                        // 얼굴 좌표를 preview 좌표에 맞게 변환
                                        mapRectToPreview(rectFace, imageProxy, previewView)
                                    }
                                    onFacesDetected(faceRects) // 좌표 전달
                                }) {
                                    imageProxy.close()
                                }
                            } else {
                                imageProxy.close()
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
                        imageCapture
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