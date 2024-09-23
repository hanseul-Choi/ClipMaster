package com.chs.clipmaster.feature.camera

import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.chs.clipmaster.core.facedetector.OverlayManager
import java.io.File
import com.chs.clipmaster.core.facedetector.R.drawable as faceR

@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    // 얼굴 좌표 관리
    var faceBoundingBoxes by remember { mutableStateOf<List<RectF>>(emptyList()) }

    // 머리띠 이미지를 Bitmap으로 변환하여 OverlayManager에 전달
    val headbandBitmap = remember {
        BitmapFactory.decodeResource(context.resources, faceR.hairband1)
    }
    viewModel.overlayManager.setHeadbandBitmap(headbandBitmap)

    val uiState by viewModel.cameraUiState.collectAsState()

    val onImageCaptured: (Uri) -> Unit = { uri ->
        viewModel.getRecentUri()
    }

    val bottomBarHeight = 180.dp
    var imageUri: Uri? = null

    // ImageCapture를 remember로 관리
    val imageCapture = remember { ImageCapture.Builder().build() }

    when (uiState) {
        is CameraUiState.Idle -> { }
        is CameraUiState.RecentImageSuccess -> {
            imageUri = (uiState as CameraUiState.RecentImageSuccess).uri
        }
        is CameraUiState.Error -> {
            Log.e("CameraScreen", "Error: ${(uiState as CameraUiState.Error).message}")
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // 카메라 프리뷰와 얼굴 좌표 업데이트
        CameraPreview(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = bottomBarHeight),
            faceDetectionManager = viewModel.faceDetectionManager,
            onFacesDetected = { faces ->
                faceBoundingBoxes = faces
            }
        )

        // 얼굴에 맞춰 오버레이 그리기
        Canvas(modifier = Modifier.fillMaxSize()) {
            viewModel.overlayManager.drawOverlay(this, faceBoundingBoxes)
        }

        CameraBottomBar(
            modifier = Modifier
                .height(bottomBarHeight)
                .align(Alignment.BottomCenter),
            recentImageUri = imageUri,
            onGalleryClick = { viewModel.moveToGallery(imageUri) },
            onCaptureClick = { takePhoto(context, imageCapture, onImageCaptured) }
        )
    }
}

private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    onImageCaptured: (Uri) -> Unit
) {
    // 파일 경로 설정
    val photoFile = File(
        context.externalCacheDirs.firstOrNull(), // 캐시 저장소에 저장
        "${System.currentTimeMillis()}.jpg"
    )

    // Output 옵션 설정
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    // 사진 캡처
    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exception: ImageCaptureException) {
                Log.e("takePhoto", "Error capturing photo", exception)
            }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)
                // 이미지가 저장된 후 MediaStore에 추가
                addImageToGallery(context, savedUri) { updatedUri ->
                    // MediaScanner가 완료된 후 저장된 이미지 URI 반환
                    onImageCaptured(updatedUri)
                }
            }
        }
    )
}

private fun addImageToGallery(context: Context, imageUri: Uri, onScanComplete: (Uri) -> Unit) {
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES) // 저장할 경로 설정 (API 29 이상)
    }

    // MediaStore에 파일 저장
    val insertedUri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

    Log.d("addImageToGallery", "insertedUri is : $insertedUri")

    if (insertedUri != null) {
        context.contentResolver.openOutputStream(insertedUri)?.use { outputStream ->
            context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        // MediaStore에서 파일의 경로 가져오기
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        context.contentResolver.query(insertedUri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))

                MediaScannerConnection.scanFile(context, arrayOf(filePath), null) { path, uri ->
                    if (uri != null) {
                        Log.d("MediaScanner", "Successfully scanned $path: $uri")
                        onScanComplete(insertedUri) // 스캔 완료 후 URI 반환
                    } else {
                        Log.e("MediaScanner", "Failed to scan $path")
                    }
                }
            } else {
                Log.e("addImageToGallery", "Failed to retrieve file path for scanning")
            }
        }
    } else {
        Log.e("addImageToGallery", "Failed to insert image into MediaStore")
    }
}