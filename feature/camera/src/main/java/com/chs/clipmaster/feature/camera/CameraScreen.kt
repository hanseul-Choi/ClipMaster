package com.chs.clipmaster.feature.camera

import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.provider.MediaStore
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import java.io.File

@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    val uiState by viewModel.cameraUiState.collectAsState()

    val onImageCaptured: (Uri) -> Unit = { uri ->
        viewModel.getRecentUri()
    }

    val bottomBarHeight = 180.dp
    var imageUri: Uri? = null

    // ImageCapture를 remember로 관리
    val imageCapture = remember { ImageCapture.Builder().build() }

    when (uiState) {
        is CameraUiState.Idle -> {
        }
        is CameraUiState.RecentImageSuccess -> {
            imageUri = (uiState as CameraUiState.RecentImageSuccess).uri
        }
        is CameraUiState.Error -> {
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        CameraPreview(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = bottomBarHeight),
            imageCapture = imageCapture,
        )
        CameraBottomBar(
            modifier = Modifier
                .height(bottomBarHeight)
                .align(Alignment.BottomCenter),
            recentImageUri = imageUri,
            onGalleryClick = { viewModel.moveToGallery() },
            onCaptureClick = { takePhoto(context, imageCapture, onImageCaptured) },
        )
    }
}


private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    onImageCaptured: (Uri) -> Unit,
) {
    // 파일 경로 설정
    val photoFile = File(
        context.externalMediaDirs.firstOrNull(),
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
                onError(exception)
            }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)
                onImageCaptured(savedUri) // 이미지가 저장된 후 처리
                addImageToGallery(context, savedUri)
            }
        }
    )
}

private fun addImageToGallery(context: Context, imageUri: Uri) {
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyApp") // 저장할 경로 설정 (API 29 이상)
    }

    // MediaStore에 이미지를 삽입
    context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)?.also { uri ->
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }
}

// 29 미만 사용
fun addImageToGalleryLegacy(context: Context, photoFile: File) {
    MediaScannerConnection.scanFile(
        context,
        arrayOf(photoFile.absolutePath),
        arrayOf("image/jpeg"),
        null
    )
}