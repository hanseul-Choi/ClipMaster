package com.chs.clipmaster.feature.camera

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chs.clipmaster.core.data.repository.GalleryRepository
import com.chs.clipmaster.core.facedetector.FaceDetectionManager
import com.chs.clipmaster.core.facedetector.OverlayManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val galleryRepository: GalleryRepository,
    val faceDetectionManager: FaceDetectionManager,
    val overlayManager: OverlayManager,
): ViewModel() {

    private val _cameraUiState: MutableStateFlow<CameraUiState> = MutableStateFlow(CameraUiState.Idle)
    val cameraUiState: StateFlow<CameraUiState> = _cameraUiState

    init {
        getRecentUri()
    }

    internal fun getRecentUri() {
        viewModelScope.launch {
            try {
                val recentUri = galleryRepository.getRecentImageUri()

                Log.d("test", "uri : $recentUri")
                if (recentUri != null) {
                    _cameraUiState.value = CameraUiState.RecentImageSuccess(recentUri)
                } else {
                    _cameraUiState.value = CameraUiState.Error("No recent image found.")
                }
            } catch (e: Exception) {
                // 예외가 발생한 경우
                _cameraUiState.value = CameraUiState.Error("Failed to load recent image: ${e.message}")
            }
        }
    }

    internal fun moveToGallery(recentUri: Uri?) {
        galleryRepository.moveToGallery(recentUri)
    }
}

sealed interface CameraUiState {

    data object Idle : CameraUiState

    data class RecentImageSuccess(val uri: Uri) : CameraUiState

    data class Error(val message: String) : CameraUiState
}
