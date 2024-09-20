package com.chs.clipmaster

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.chs.clipmaster.core.navigation.AppComposeNavigator
import com.chs.clipmaster.navigation.ClipMasterNavHost
import chs.clipmaster.ui.theme.ClipMasterTheme

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