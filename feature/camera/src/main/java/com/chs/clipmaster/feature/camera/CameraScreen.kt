package com.chs.clipmaster.feature.camera

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.RectF
import android.media.ExifInterface
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.chs.clipmaster.core.facedetector.R.drawable as faceR
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import java.io.File

@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    // ImageCapture 객체 생성
    val imageCapture = remember { ImageCapture.Builder().build() }

    var faceBoundingBoxes by remember { mutableStateOf<List<RectF>>(emptyList()) }

    // 머리띠 이미지를 Bitmap으로 변환하여 OverlayManager에 전달
    val headbandBitmap = remember {
        BitmapFactory.decodeResource(context.resources, faceR.hairband1)
    }
    viewModel.overlayManager.setHeadbandBitmap(headbandBitmap)

    val uiState by viewModel.cameraUiState.collectAsState()

    val bottomBarHeight = 180.dp // BottomBar 크기 설정
    var imageUri: Uri? = null
    var previewView by remember { mutableStateOf<PreviewView?>(null) } // PreviewView를 상태로 관리

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
                .padding(bottom = bottomBarHeight), // BottomBar 여백 추가
            imageCapture = imageCapture, // ImageCapture 전달
            faceDetectionManager = viewModel.faceDetectionManager,
            onFacesDetected = { faces ->
                faceBoundingBoxes = faces
            },
            onPreviewViewCreated = { view -> previewView = view } // PreviewView 참조 저장
        )

        // 얼굴에 맞춰 오버레이 그리기
        Canvas(modifier = Modifier.fillMaxSize()) {
            viewModel.overlayManager.drawOverlay(this, faceBoundingBoxes)
        }

        // BottomBar 크기 설정 및 UI 추가
        CameraBottomBar(
            modifier = Modifier
                .height(bottomBarHeight) // BottomBar 크기 복원
                .align(Alignment.BottomCenter),
            recentImageUri = imageUri,
            onGalleryClick = { viewModel.moveToGallery(imageUri) },
            onCaptureClick = {
                // previewView가 null이 아닐 때만 캡처 실행
                previewView?.let { view ->
                    captureCombinedImage(
                        context,
                        imageCapture,
                        headbandBitmap,
                        faceBoundingBoxes,
                        view // PreviewView 전달
                    ) { uri ->
                        viewModel.getRecentUri() // 이미지 저장 완료 후 갤러리 URI 업데이트
                    }
                } ?: Log.e("CameraScreen", "PreviewView is null, cannot capture image")
            }
        )
    }
}

private fun captureCombinedImage(
    context: Context,
    imageCapture: ImageCapture,
    headbandBitmap: Bitmap,
    faceBoundingBoxes: List<RectF>,
    previewView: PreviewView, // 프리뷰 뷰 추가
    onImageCaptured: (Uri?) -> Unit
) {
    // 사진 파일 경로 설정
    val photoFile = File(
        context.externalCacheDirs.firstOrNull(),
        "${System.currentTimeMillis()}.jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exception: ImageCaptureException) {
                Log.e("captureCombinedImage", "Photo capture failed: ${exception.message}", exception)
                onImageCaptured(null) // 오류 발생 시 null을 반환
            }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val capturedBitmap = BitmapFactory.decodeFile(photoFile.absolutePath)

                // 이미지의 회전 정보를 가져오기
                val rotatedBitmap = rotateBitmapIfNeeded(photoFile, capturedBitmap)

                // 캡처된 이미지와 오버레이 결합
                val combinedBitmap = combineCapturedImageWithOverlay(rotatedBitmap, headbandBitmap, faceBoundingBoxes, previewView)

                // 결합된 이미지를 MediaStore에 저장
                saveBitmapToGallery(context, combinedBitmap) { uri ->
                    if (uri != null) {
                        Log.d("captureCombinedImage", "Image saved successfully: $uri")
                        onImageCaptured(uri) // 성공적으로 저장된 이미지의 URI를 반환
                    } else {
                        Log.e("captureCombinedImage", "Failed to save image")
                        onImageCaptured(null)
                    }
                }
            }
        }
    )
}

private fun combineCapturedImageWithOverlay(
    capturedBitmap: Bitmap,
    overlayBitmap: Bitmap,
    faceBoundingBoxes: List<RectF>,
    previewView: PreviewView // 프리뷰 뷰 추가하여 좌표 변환에 사용
): Bitmap {
    // 캡처된 이미지와 동일한 크기의 빈 비트맵 생성
    val combinedBitmap = Bitmap.createBitmap(capturedBitmap.width, capturedBitmap.height, capturedBitmap.config)
    val canvas = android.graphics.Canvas(combinedBitmap)

    // 캡처된 이미지를 먼저 그리기
    canvas.drawBitmap(capturedBitmap, 0f, 0f, null)

    // 프리뷰 크기와 이미지 크기 가져오기
    val previewWidth = previewView.width.toFloat()
    val previewHeight = previewView.height.toFloat()
    val imageWidth = capturedBitmap.width.toFloat()
    val imageHeight = capturedBitmap.height.toFloat()

    // 얼굴 좌표를 이미지 크기에 맞춰 변환 및 오버레이 그리기
    faceBoundingBoxes.forEach { rect ->
        // 프리뷰에서 이미지로 좌표를 변환
        val scaledRect = mapRectFromPreviewToImage(rect, previewWidth, previewHeight, imageWidth, imageHeight)

        // 머리띠가 얼굴의 중앙에 위치하도록 조정 (가로 방향)
        val headbandX = scaledRect.centerX() - (overlayBitmap.width / 2f)
        // 머리띠가 얼굴의 위쪽에 위치하도록 조정 (세로 방향)
        val headbandY = scaledRect.top - (overlayBitmap.height / 2f)

        // 머리띠를 캡처된 이미지 위에 그리기
        canvas.drawBitmap(overlayBitmap, headbandX, headbandY, null)
    }

    return combinedBitmap
}


private fun mapRectFromPreviewToImage(
    rect: RectF,
    previewWidth: Float,
    previewHeight: Float,
    imageWidth: Float,
    imageHeight: Float
): RectF {
    // 이미지의 크기가 프리뷰의 크기와 비율이 다를 수 있으므로, 중앙 정렬 기준으로 비율을 맞춥니다.
    val widthRatio = imageWidth / previewWidth
    val heightRatio = imageHeight / previewHeight

    // 이미지와 프리뷰 사이의 좌표 오프셋을 계산 (중앙 정렬을 고려)
//    val offsetX = (imageWidth - (previewWidth * widthRatio)) / 2f
//    val offsetY = (imageHeight - (previewHeight * heightRatio)) / 2f

    // 변환된 좌표 반환 (좌표에 오프셋 적용)
    return RectF(
        rect.left * widthRatio,
        rect.top * heightRatio,
        rect.right * widthRatio + 200f,
        rect.bottom * heightRatio
    )
}


// 결합된 이미지를 MediaStore에 저장하는 함수
private fun saveBitmapToGallery(context: Context, bitmap: Bitmap, onScanComplete: (Uri?) -> Unit) {
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES) // 저장할 경로 설정 (API 29 이상)
    }

    // MediaStore에 파일 저장
    val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    if (uri != null) {
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }

        // 실제 파일 경로 가져오기
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))

                // MediaScanner를 파일 경로 기반으로 실행
                MediaScannerConnection.scanFile(context, arrayOf(filePath), null) { path, scannedUri ->
                    if (scannedUri != null) {
                        Log.d("MediaScanner", "Successfully scanned $path: $scannedUri")
                        onScanComplete(scannedUri) // 스캔 완료 후 URI 반환
                    } else {
                        Log.e("MediaScanner", "Failed to scan $path")
                        onScanComplete(null)
                    }
                }
            } else {
                Log.e("saveBitmapToGallery", "Failed to retrieve file path for scanning")
                onScanComplete(null)
            }
        }
    } else {
        Log.e("saveBitmapToGallery", "Failed to insert image into MediaStore")
        onScanComplete(null) // URI가 null인 경우에도 안전하게 처리
    }
}

private fun rotateBitmapIfNeeded(photoFile: File, bitmap: Bitmap): Bitmap {
    val exif = ExifInterface(photoFile.absolutePath)
    val rotationDegrees = when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90
        ExifInterface.ORIENTATION_ROTATE_180 -> 180
        ExifInterface.ORIENTATION_ROTATE_270 -> 270
        else -> 0
    }

    return if (rotationDegrees != 0) {
        val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } else {
        bitmap
    }
}