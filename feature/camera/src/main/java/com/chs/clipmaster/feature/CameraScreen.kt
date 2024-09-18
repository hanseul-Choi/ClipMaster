package com.chs.clipmaster.feature

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.chs.clipmaster.feature.camera.CameraPreview

@Composable
fun CameraScreen(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        CameraPreview()
    }
}