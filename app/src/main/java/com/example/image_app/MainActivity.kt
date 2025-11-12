package com.example.image_app

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var selectImageButton: Button
    private lateinit var imageView: ImageView
    private lateinit var editButton: Button

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initImagePickerLauncher()
        initViews()
    }

    private fun initImagePickerLauncher() {
        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    loadImage(uri)
                } else {
                    Toast.makeText(this, "Не удалось получить изображение", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Выбор изображения отменен", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initViews() {
        selectImageButton = findViewById(R.id.selectImageButton)
        imageView = findViewById(R.id.imageView)
        editButton = findViewById(R.id.editButton)

        selectImageButton.setOnClickListener {
            openImagePicker()
        }

        editButton.setOnClickListener {
            currentImageUri?.let { uri ->
                openEditActivity(uri)
            } ?: run {
                Toast.makeText(this, "Сначала выберите изображение", Toast.LENGTH_SHORT).show()
            }
        }

        editButton.isEnabled = false
        editButton.alpha = 0.5f
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }

        val chooser = Intent.createChooser(intent, "Выберите изображение")

        try {

            imagePickerLauncher.launch(chooser)
        } catch (e: Exception) {
            Toast.makeText(this, "Не удалось открыть галерею", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun loadImage(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                val bitmap = BitmapFactory.decodeStream(stream)
                imageView.setImageBitmap(bitmap)

                currentImageUri = uri
                editButton.isEnabled = true
                editButton.alpha = 1.0f

                Toast.makeText(this, "Изображение загружено", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun openEditActivity(imageUri: Uri) {
        val intent = Intent(this, EditImageActivity::class.java)
        intent.putExtra("image_uri", imageUri.toString())
        startActivity(intent)
    }
}