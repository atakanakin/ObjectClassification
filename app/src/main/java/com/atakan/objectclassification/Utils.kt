package com.atakan.objectclassification

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.hoko.blur.HokoBlur
import org.tensorflow.lite.task.vision.detector.Detection

object ImageUtils {

    fun applyBlur(context: Context, image: Bitmap, persons: List<Detection>): Bitmap {
        var finalBitmap = image.copy(image.config, true)

        for(person in persons){
            val boundingBox = person.boundingBox
            val cropped = cropBitmap(originalBitmap = finalBitmap, bound = boundingBox)
            val blurred = blurPart(context, cropped)
            finalBitmap = overlayBitmap(originalBitmap = finalBitmap, blurred = blurred, region = boundingBox)
        }

        return finalBitmap
    }

    private fun cropBitmap(originalBitmap: Bitmap, bound: RectF) : Bitmap {
        val croppedBitmap = Bitmap.createBitmap(
            originalBitmap,
            bound.left.toInt(),
            bound.top.toInt(),
            bound.width().toInt(),
            bound.height().toInt(),
            null,
            false
        )
        return croppedBitmap
    }

    private fun blurPart(context: Context, region: Bitmap): Bitmap {
        return HokoBlur.with(context).radius(20).blur(region)
    }

    private fun overlayBitmap(originalBitmap: Bitmap, blurred: Bitmap, region: RectF): Bitmap {
        val combinedBitmap = originalBitmap.copy(originalBitmap.config, true) // Create a copy to avoid modifying the original

        val canvas = Canvas(combinedBitmap)
        val paint = Paint()
        paint.alpha = 255 // Adjust the transparency of the overlay

        canvas.drawBitmap(blurred, region.left, region.top, paint)

        return combinedBitmap
    }

}