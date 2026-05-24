package com.example.physicaltraining.Ai

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class WorkoutModelManager(private val context: Context) {
    private var interpreter: Interpreter? = null

    init {
        try {
            interpreter = Interpreter(loadModelFile())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd("workout_model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength)
    }

    fun predict(inputData: FloatArray): FloatArray {

      if (interpreter == null) {
          return  floatArrayOf(0f,0f,0f,0f)
      }

        try {
            val inputArray = arrayOf(inputData)

            val outputArray = Array(1) { FloatArray(4) }

            interpreter?.run (inputArray, outputArray)

            return outputArray[0]

        } catch (e: Exception) {
            return  floatArrayOf(0f,0f,0f,0f)
        }

    }
    fun close() {
        interpreter?.close()
        interpreter = null
    }

}