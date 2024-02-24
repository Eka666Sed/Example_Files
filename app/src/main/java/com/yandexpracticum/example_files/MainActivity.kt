package com.yandexpracticum.example_files

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import com.yandexpracticum.example_files.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //регистрируем событие, которое вызывает photo picker
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                //обрабатываем событие выбора пользователем фотографии
                if (uri != null) {
                    binding.pickerImage.setImageURI(uri)
                    saveImageToPrivateStorage(uri)
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }
        //по нажатию на кнопку pickImage запускаем photo picker
        binding.pickImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        //по нажатию на кнопку loadImageFromStorage пытаемся загрузить фотографию из нашего хранилища
        binding.loadImageFromStorage.setOnClickListener {
            val filePath = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
            val file = File(filePath, "first_cover.jpg")
            binding.storageImage.setImageURI(file.toUri())
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri) {
        //создаём экземпляр класса File, который указывает на нужный каталог
        val filePath = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        //создаем каталог, если он не создан
        if (!filePath.exists()){
            filePath.mkdirs()
        }
        //создаём экземпляр класса File, который указывает на файл внутри каталога
        val file = File(filePath, "first_cover.jpg")
        // создаём входящий поток байтов из выбранной картинки
        val inputStream = contentResolver.openInputStream(uri)
        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file)
        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

}