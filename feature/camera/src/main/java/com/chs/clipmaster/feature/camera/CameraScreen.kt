package com.chs.clipmaster.feature.camera

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel = hiltViewModel(),
) {
    val uiState by viewModel.cameraUiState.collectAsState()

    val bottomBarHeight = 180.dp
    var imageUri: Uri? = null

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
                .padding(bottom = bottomBarHeight)
        )
        CameraBottomBar(
            modifier = Modifier
                .height(bottomBarHeight)
                .align(Alignment.BottomCenter),
            recentImageUri = imageUri
        )
    }
}