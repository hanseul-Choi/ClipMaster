package com.chs.clipmaster.core.navigation

import androidx.navigation.NavOptionsBuilder
import androidx.navigation.navOptions
import javax.inject.Inject

class ClipmasterComposeNavigator @Inject constructor() : AppComposeNavigator() {
    override fun navigate(route: String, optionsBuilder: (NavOptionsBuilder.() -> Unit)?) {
        val options = optionsBuilder?.let { navOptions(it) }
        navigationCommands.tryEmit(ComposeNavigationCommand.NavigateToRoute(route, options))
    }

    override fun navigateAndClearBackStack(route: String) { // navigate 후 backStack 초기화
        navigationCommands.tryEmit(
            ComposeNavigationCommand.NavigateToRoute(
                route,
                navOptions {
                    popUpTo(0)
                }
            )
        )
    }

    override fun popUpTo(route: String, inclusive: Boolean) {
        navigationCommands.tryEmit(ComposeNavigationCommand.PopupToRoute(route, inclusive))
    }

    override fun <T> navigateBackWithResult(key: String, result: T, route: String?) {
        navigationCommands.tryEmit(
            ComposeNavigationCommand.NavigateUpWithResult(
                key = key,
                result = result,
                route = route
            )
        )
    }
}