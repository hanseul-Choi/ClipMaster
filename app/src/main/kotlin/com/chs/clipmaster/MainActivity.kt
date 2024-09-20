package com.chs.clipmaster

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.chs.clipmaster.core.navigation.AppComposeNavigator
import com.chs.clipmaster.permission.PermissionHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    internal lateinit var appComposeNavigator: AppComposeNavigator

    private lateinit var permissionHelper: PermissionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionHelper = PermissionHelper(this)

        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
           arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.READ_MEDIA_IMAGES,
            )
        } else {
             arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO,
            )
        }

        permissionHelper.requestPermissions(permissions) { allGranted ->
            if (allGranted) {
                enableEdgeToEdge()
                setContent {
                    ClipMasterMain(composeNavigator = appComposeNavigator)
                }
            } else {
                // 권한이 거부된 경우 사용자에게 알림
                Toast.makeText(this, "권한이 허용되어야 사용이 가능합니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}