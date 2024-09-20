package com.chs.clipmaster.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.chs.clipmaster.core.designsystem.ClipMasterBottomBar
import com.chs.clipmaster.core.designsystem.FilterSelectedBar
import com.chs.clipmaster.core.navigation.AppComposeNavigator
import com.chs.clipmaster.feature.CameraScreen

fun NavGraphBuilder.clipMasterNavigation(
   composeNavigator: AppComposeNavigator,
   isFilterMode: Boolean
) {
    composable(route = "test") {
        Scaffold(
            bottomBar = {
                if (isFilterMode) {
                    FilterSelectedBar()
                } else {
                    ClipMasterBottomBar(
                        onGalleryClick = {},
                        onFilterClick = {},
                        onCaptureClick = {},
                        recentImageUri = null,
                    )
                }
            }
        ) { padding ->
            CameraScreen(modifier = Modifier.padding(padding))
        }
    }
}