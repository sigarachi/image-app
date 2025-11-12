package com.example.image_app

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast


class EditImageActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var originalBitmap: Bitmap
    private lateinit var currentBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_image)

        imageView = findViewById(R.id.imageView)

        val imageUriString = intent.getStringExtra("image_uri")
        val imageUri = Uri.parse(imageUriString)

        loadImage(imageUri)
        setupButtons()
    }

    private fun loadImage(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            originalBitmap = BitmapFactory.decodeStream(inputStream)!!
            currentBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
            imageView.setImageBitmap(currentBitmap)
            inputStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btnRotate).setOnClickListener {
            rotateImage()
        }

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            saveImage()
        }
    }

    private fun rotateImage() {
        val matrix = Matrix()
        matrix.postRotate(90f)
        currentBitmap = Bitmap.createBitmap(
            currentBitmap, 0, 0, currentBitmap.width, currentBitmap.height, matrix, true
        )
        imageView.setImageBitmap(currentBitmap)
    }

    private fun saveImage() {
        try {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "edited_${System.currentTimeMillis()}.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            if (uri != null) {
                val outputStream = contentResolver.openOutputStream(uri)
                if (outputStream != null) {
                    currentBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                    outputStream.close()
                    Toast.makeText(this, "Изображение сохранено в галерею", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Ошибка: не удалось создать файл", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Ошибка: не удалось сохранить изображение", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Ошибка сохранения: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}