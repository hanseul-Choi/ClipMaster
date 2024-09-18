package com.chs.clipmaster.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.chs.clipmaster.core.navigation.AppComposeNavigator
import com.chs.clipmaster.navigation.ClipMasterNavHost
import com.chs.clipmaster.ui.theme.ClipMasterTheme

@Composable
fun ClipMasterMain(
    composeNavigator: AppComposeNavigator
) {
    val navHostController = rememberNavController()

    LaunchedEffect(Unit) {
        composeNavigator.handleNavigationCommands(navHostController)
    }

    ClipMasterTheme {
        ClipMasterNavHost(
            navHostController = navHostController,
            composeNavigator = composeNavigator
        )
    }
}