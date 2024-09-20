package com.chs.clipmaster.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.chs.clipmaster.core.navigation.AppComposeNavigator
import com.chs.clipmaster.navigation.clipMasterNavigation

@Composable
fun ClipMasterNavHost(
    navHostController: NavHostController,
    composeNavigator: AppComposeNavigator
) {
    NavHost(
        navController = navHostController,
        startDestination = "test"
    ) {
        clipMasterNavigation(composeNavigator = composeNavigator, false)
    }
}