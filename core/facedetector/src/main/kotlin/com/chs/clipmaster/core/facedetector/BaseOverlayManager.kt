package com.chs.clipmaster.core.facedetector

import android.graphics.RectF
import androidx.compose.ui.graphics.drawscope.DrawScope

interface BaseOverlayManager {
    fun drawOverlay(
        canvasScope: DrawScope,
        faceBoundingBoxes: List<RectF> // 좌표값 (RectF)을 받아서 그리기
    )
}