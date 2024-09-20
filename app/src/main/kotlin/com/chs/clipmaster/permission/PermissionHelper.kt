package chs.clipmaster.permission

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class PermissionHelper(
    private val activity: ComponentActivity
) {
    private var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private var onPermissionResult: ((Boolean) -> Unit)? = null

    init {
        requestPermissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allGranted = permissions.values.all { it }
            onPermissionResult?.invoke(allGranted)
        }
    }

    fun hasPermissions(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestPermissions(permissions: Array<String>, callback: (Boolean) -> Unit) {
        onPermissionResult = callback

        if (hasPermissions(permissions)) {
            callback(true)
        } else {
            requestPermissionLauncher.launch(permissions)
        }
    }
}