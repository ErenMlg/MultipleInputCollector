package com.softcross.multipleinputcollector

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class SelectionShape(
    private val cornerRadius: Float,
    private val triangleHeight: Float = 30f,
    private val triangleWidth: Float = 60f
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(cornerRadius, 0f)
            lineTo(size.width * 0.5f - triangleWidth / 2, 0f)
            lineTo(size.width * 0.5f, -triangleHeight)
            lineTo(size.width * 0.5f + triangleWidth / 2, 0f)
            lineTo(size.width - cornerRadius, 0f)
            arcTo(
                Rect(size.width - cornerRadius * 2, 0f, size.width, cornerRadius * 2),
                270f,
                90f,
                false
            )
            lineTo(size.width, size.height - cornerRadius)
            arcTo(
                Rect(
                    size.width - cornerRadius * 2,
                    size.height - cornerRadius * 2,
                    size.width,
                    size.height
                ),
                0f,
                90f,
                false
            )
            lineTo(cornerRadius, size.height)
            arcTo(
                Rect(0f, size.height - cornerRadius * 2, cornerRadius * 2, size.height),
                90f,
                90f,
                false
            )
            lineTo(0f, cornerRadius)
            arcTo(
                Rect(0f, 0f, cornerRadius * 2, cornerRadius * 2),
                180f,
                90f,
                false
            )
            close()
        }
        return Outline.Generic(path)
    }
}