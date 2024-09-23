package com.chs.clipmaster.core.facedetector

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import javax.inject.Inject

class OverlayManager @Inject constructor() : BaseOverlayManager {

    private lateinit var headbandBitmap: Bitmap // 머리띠 이미지 비트맵
    private val paint = Paint()

    fun setHeadbandBitmap(bitmap: Bitmap) {
        headbandBitmap = bitmap
    }

    override fun drawOverlay(
        canvasScope: DrawScope,
        faceBoundingBoxes: List<RectF> // 얼굴 좌표 값
    ) {
        if (!::headbandBitmap.isInitialized) return // 이미지가 초기화되지 않았다면 그리지 않음

        canvasScope.drawIntoCanvas { canvas ->
            faceBoundingBoxes.forEach { rect ->
                // 얼굴의 가장 위 좌표를 기준으로 머리띠의 시작 좌표 설정
                // 머리띠의 크기를 얼굴에 맞춰 조정
                val headbandX = rect.left
                val headbandY = rect.top - rect.height() / 5f // 머리띠를 얼굴 위에서 약간 아래로 위치시킴
                val headbandWidth = rect.width()
                val headbandHeight = headbandBitmap.height * (headbandWidth / headbandBitmap.width) // 비율 유지

                // 목적지 사각형 (머리띠를 그릴 영역)
                val destRect = RectF(headbandX, headbandY, headbandX + headbandWidth, headbandY + headbandHeight)

                // Bitmap(머리띠) 그리기
                canvas.nativeCanvas.drawBitmap(headbandBitmap, null, destRect, paint)
            }
        }
    }
}