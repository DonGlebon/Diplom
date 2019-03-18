package com.diplom.map.layers.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.GroundOverlay
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.LatLng

class LayerUtils {
    companion object {
        fun drawText(
            map: GoogleMap,
            text: String,
            position: LatLng,
            textSize: Float,
            textColor: Int,
            charSize: Float
        ): GroundOverlay {
            val groundOptions = GroundOverlayOptions()
                .image(
                    BitmapDescriptorFactory.fromBitmap(
                        textToBitmap(
                            text,
                            textSize,
                            textColor
                        )
                    )
                )
                .anchor(.5f, 1.0f)
                .position(position, text.length * charSize, charSize * 2.1f)
                .visible(false)
                .zIndex(1f)
            return map.addGroundOverlay(groundOptions)
        }

        private fun textToBitmap(text: String, textSize: Float, textColor: Int): Bitmap {
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                .also {
                    it.textSize = textSize
                    it.color = textColor
                    it.textAlign = Paint.Align.LEFT
                }
            val baseline = -paint.ascent()
            val width = (paint.measureText(text) + 0.5f).toInt()
            val height = (baseline + paint.descent() + 0.5f).toInt()
            val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(image)
            canvas.drawText(text, 0f, baseline, paint)
            return image
        }
    }
}