package com.chs.clipmaster.core.navigation

import androidx.navigation.NavOptions

sealed class NavigationCommand {
    data object NavigateUp : NavigationCommand() // 뒤로가기
}

sealed class ComposeNavigationCommand : NavigationCommand() {
    // 특정 루트로 이동
    data class NavigateToRoute(val route: String, val options: NavOptions? = null) : ComposeNavigationCommand()

    // Navigate 결과
    data class NavigateUpWithResult<T>(
        val key: String,
        val result: T,
        val route: String? = null
    ) : ComposeNavigationCommand()

    // 특정 루트로 뒤로가기
    data class PopupToRoute(val route: String, val inclusive: Boolean) : ComposeNavigationCommand() //
}