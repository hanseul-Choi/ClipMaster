package com.chs.clipmaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.chs.clipmaster.core.navigation.AppComposeNavigator
import com.chs.clipmaster.ui.ClipMasterMain
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    internal lateinit var appComposeNavigator: AppComposeNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClipMasterMain(composeNavigator = appComposeNavigator)
        }
    }
}