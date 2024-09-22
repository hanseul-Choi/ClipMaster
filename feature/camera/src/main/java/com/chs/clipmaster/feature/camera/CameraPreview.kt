package com.chs.clipmaster.feature.camera

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    imageCapture: ImageCapture // 외부에서 ImageCapture를 전달받음
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)

            // 코루틴을 사용해 CameraProvider를 가져옴
            lifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                try {
                    // 코루틴 기반으로 CameraProvider 가져오기
                    val cameraProvider = getInstanceSuspend(ctx)

                    // Preview 설정
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    // Camera Selector (전면 카메라 선택)
                    val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                    // 기존 바인딩 해제
                    cameraProvider.unbindAll()

                    // Lifecycle과 연결
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture // ImageCapture 추가
                    )
                } catch (e: Exception) {
                    // 에러 처리 필요
                }
            }

            previewView
        },
        modifier = modifier
    )
}

suspend fun getInstanceSuspend(context: Context): ProcessCameraProvider {
    return suspendCancellableCoroutine { cont ->
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                cont.resume(cameraProvider) // 성공 시 resume
            } catch (e: Exception) {
                cont.resumeWithException(e) // 실패 시 예외 전달
            }
        }, ContextCompat.getMainExecutor(context)) // 메인 스레드에서 실행되도록 설정

        // 코루틴이 취소되면 Future도 취소
        cont.invokeOnCancellation {
            cameraProviderFuture.cancel(true)
        }
    }
}