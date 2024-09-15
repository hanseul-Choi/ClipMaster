package com.chs.clipmaster.core.navigation

import androidx.navigation.NamedNavArgument

private fun String.appendArguments(navArgument: List<NamedNavArgument>): String {
    val mandatoryArgument = navArgument.filter { it.argument.defaultValue == null }
        .takeIf { it.isNotEmpty() }
        ?.joinToString(separator = "/", prefix = "/") { "{${it.name}}" }
        .orEmpty()

    val optionalArgument = navArgument.filter { it.argument.defaultValue != null }
        .takeIf { it.isNotEmpty() }
        ?.joinToString(separator = "&", prefix = "?") { "${it.name}={${it.name}}"}
        .orEmpty()

    return "$this$mandatoryArgument$optionalArgument"
}