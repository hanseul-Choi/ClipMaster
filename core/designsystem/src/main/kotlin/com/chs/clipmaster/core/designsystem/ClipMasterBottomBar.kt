package com.chs.clipmaster.core.designsystem

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ClipMasterBottomBar(
    recentImageUri: Uri?,
    onGalleryClick: () -> Unit,
    onCaptureClick: () -> Unit,
    onFilterClick: () -> Unit,
) {
    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {

        if (recentImageUri != null) {
//            Image(
//                painter = rememberImagePainter(recentImageUri),
//                contentDescription = "Gallery Preview",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .padding(8.dp)
//                    .size(48.dp)
//                    .clickable(onClick = onGalleryClick)
//                    .background(Color.Gray, shape = RoundedCornerShape(8.dp))
//            )
        }

        Box(
            modifier = Modifier
                .padding(8.dp)
                .size(48.dp)
                .background(Color.Gray, shape = RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(64.dp)
                .background(Color.White, shape = CircleShape)
                .clickable(onClick = onCaptureClick)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Capture",
                tint = Color.Black,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = onFilterClick
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = "",
                modifier = Modifier.size(32.dp),
            )
        }
    }
}

@Composable
fun FilterSelectedBar() {
    BottomAppBar(
        containerColor = Color.Blue
    ) {  }
}