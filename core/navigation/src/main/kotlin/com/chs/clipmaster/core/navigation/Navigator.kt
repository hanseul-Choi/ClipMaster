package com.chs.clipmaster.core.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onSubscription

abstract class Navigator {
    val navigationCommands = MutableSharedFlow<NavigationCommand>(extraBufferCapacity = Int.MAX_VALUE) // 일회성 이벤트 용도

    val navControllerFlow = MutableStateFlow<NavController?>(null) // 상태 필요

    fun navigateUp() {
        navigationCommands.tryEmit(NavigationCommand.NavigateUp)
    }
}

abstract class AppComposeNavigator : Navigator() {
    abstract fun navigate(route: String, optionsBuilder: (NavOptionsBuilder.() -> Unit)? = null)
    abstract fun <T> navigateBackWithResult(key: String, result: T, route: String?)

    abstract fun popUpTo(route: String, inclusive: Boolean)
    abstract fun navigateAndClearBackStack(route: String)

    suspend fun handleNavigationCommands(navController: NavController) {
        navigationCommands
            .onSubscription { this@AppComposeNavigator.navControllerFlow.value = navController } // 구독 시작할 때
            .onCompletion { this@AppComposeNavigator.navControllerFlow.value = null } // flow 수집이 완료 혹은 취소될 때
            .collect { navController.handleComposeNavigationCommand(it) }
    }

    private fun NavController.handleComposeNavigationCommand(navigationCommand: NavigationCommand) {
        when (navigationCommand) {
            is ComposeNavigationCommand.NavigateToRoute -> {
                navigate(navigationCommand.route, navigationCommand.options)
            }

            NavigationCommand.NavigateUp -> navigateUp()

            is ComposeNavigationCommand.PopupToRoute -> popBackStack(
                navigationCommand.route,
                navigationCommand.inclusive // pop 할 때, 자신 경로를 남길지 여부
            )

            is ComposeNavigationCommand.NavigateUpWithResult<*> -> {
                navUpWithResult(navigationCommand)
            }
        }
    }

    private fun NavController.navUpWithResult(
        navigationCommand: ComposeNavigationCommand.NavigateUpWithResult<*>
    ) {
        val backStackEntry = navigationCommand.route?.let { getBackStackEntry(it) } ?: previousBackStackEntry

        backStackEntry?.savedStateHandle?.set(
            navigationCommand.key,
            navigationCommand.result
        )

        navigationCommand.route?.let {
            popBackStack(it, false)
        } ?: run {
            navigateUp()
        }
    }
}