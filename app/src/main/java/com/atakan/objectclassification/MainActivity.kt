package com.atakan.objectclassification

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.atakan.objectclassification.ui.theme.ObjectClassificationTheme
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import org.tensorflow.lite.task.vision.detector.ObjectDetector.ObjectDetectorOptions


class MainActivity : ComponentActivity() {

    lateinit var imageBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val modelFileName = "mobilenetv1.tflite" // Replace with your actual model file name


        // Initialize
        val options = ObjectDetectorOptions.builder()
            .setBaseOptions(BaseOptions.builder().useGpu().build())
            .setMaxResults(1)
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
                        Button(onClick = {
                            val tensorImage = TensorImage.fromBitmap(imageBitmap)
                            val results: List<Detection> = objectDetector.detect(tensorImage)
                            println(results)
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

            // Handle the detection results
        }
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ObjectClassificationTheme {
        Greeting("Android")
    }
}