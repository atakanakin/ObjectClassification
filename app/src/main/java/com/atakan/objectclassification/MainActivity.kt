package com.atakan.objectclassification

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.atakan.objectclassification.ui.theme.ObjectClassificationTheme
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import org.tensorflow.lite.task.vision.detector.ObjectDetector.ObjectDetectorOptions
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {

    lateinit var imageBitmap: Bitmap

    lateinit var originalFileName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val modelFileName = "mobilenetv1.tflite" // Replace with your actual model file name


        // Initialize
        val options = ObjectDetectorOptions.builder()
            .setBaseOptions(BaseOptions.builder()//.useGpu() delete comment to use gpu
                .build())
            .build()
        val objectDetector = ObjectDetector.createFromFileAndOptions(
            this, modelFileName, options
        )

        setContent {
            ObjectClassificationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(){
                        Button(onClick = { pickImageFromGallery() }) {
                            Text("Choose")
                        }
                        // Inside your onClick block
                        Button(onClick = {
                            val tensorImage = TensorImage.fromBitmap(imageBitmap)
                            val results: List<Detection> = objectDetector.detect(tensorImage)

                            val personDetections = results.filter { detection ->
                                detection.categories[0].label.equals("person", ignoreCase = true)
                            }

                            var mutableBitmap = imageBitmap.copy(imageBitmap.config, true)

                            if (personDetections.isNotEmpty()) {
                                personDetections.forEach(){it
                                    val personBoundingBox =
                                        it.boundingBox // Assuming you want to blur the first detected person


                                    val canvas = Canvas(mutableBitmap)

                                    val blurPaint = Paint()
                                    blurPaint.color = Color.argb(128, 0, 0, 0)
                                    blurPaint.style = Paint.Style.FILL_AND_STROKE

                                    canvas.drawRect(personBoundingBox, blurPaint)

                                }
                                // Save the modified image to the Downloads folder
                                val downloadsDir =
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

                                val fileName = "${originalFileName.substringBeforeLast('.')}_edited.png" // Append "_edited" before the extension
                                val outputPath = File(downloadsDir, fileName)
                                val outputStream = FileOutputStream(outputPath)
                                mutableBitmap.compress(
                                    Bitmap.CompressFormat.PNG,
                                    100,
                                    outputStream
                                )
                                outputStream.close()

                                println("image saved to: ${outputPath.absolutePath}")
                            } else {
                                println("No person detected in the image.")
                            }

                        }) {
                            Text("Detect")
                        }


                    }
                }
            }
        }
    }
    // In your activity/fragment
    private val REQUEST_IMAGE_PICK = 2


    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data
            imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
            originalFileName = File(selectedImageUri!!.path!!).name // Get the original file name

            // Handle the detection results
        }
    }

}