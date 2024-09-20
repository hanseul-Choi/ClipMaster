package com.chs.clipmaster.feature.camera

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CameraScreen(
    modifier: Modifier = Modifier
) {
    val bottomBarHeight = 180.dp

    Box(modifier = modifier.fillMaxSize()) {
        CameraPreview(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = bottomBarHeight)
        )
        CameraBottomBar(
            modifier = Modifier
                .height(bottomBarHeight)
                .align(Alignment.BottomCenter)
        )
    }
}