package com.example.painttool2

import android.content.ContentValues
import android.net.Uri
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import android.content.ContentResolver
import android.content.pm.ActivityInfo
import android.widget.ImageView
import androidx.navigation.findNavController
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.text.Document
import com.itextpdf.text.Rectangle


class acceptSaveFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_accept_save, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Далаем ориентацию вертикальной
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        val saveBtn = view.findViewById<Button>(R.id.acceptSaveButton)
        val backBtn = view.findViewById<Button>(R.id.backButton)
        val drawnImage = view.findViewById<ImageView>(R.id.drawnImage)

        // Получаем нашу картинку
        val byteArray: ByteArray? = arguments?.getByteArray("image")
        // И конвертим обратно в картинку
        val bitMapPaint = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)

        // Отрисовываем нашу картинку как предпросмотр сохранения
        drawnImage.setImageBitmap(bitMapPaint)

        // Кнопка сохранения
        saveBtn.setOnClickListener {
            // Сохраняем картинку
            saveImage(bitMapPaint)
            // И переходим в начало
            view.findNavController().navigate(R.id.action_acceptSaveFragment_to_mainFragment)
        }

        // Кнопка возврата
        backBtn.setOnClickListener {
            // Если юзеру не понравилось - пусть идёт и перерисует либо дорисует, его проблема чё он там понаделал
            view.findNavController().navigate(R.id.action_acceptSaveFragment_to_paintFragment)
        }
    }


    private fun saveImage(bitmap: Bitmap) {
        val contentResolver = requireActivity().getContentResolver()
        
        // Получаем время на телефоне (чтобы картинка не улетела к херам в галерее)
        val timestamp = System.currentTimeMillis()

        // Данная структура будет хранить в себе всю нужную информацию для сохранения картинки
        val values = ContentValues()

        // Задаём MIME_TYPE
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        // Задаём дату
        values.put(MediaStore.Images.Media.DATE_ADDED, timestamp)
        values.put(MediaStore.Images.Media.DATE_TAKEN, timestamp)
        // Задаём название папки конкретного сохранения (Pictures (когда ставлю другую - у меня крашит)) / Название подпапки (вот тут уже можно чё хочешь)
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + getString(R.string.app_name))
        // Кидаем значения в ожидание
        values.put(MediaStore.Images.Media.IS_PENDING, true)

        // Внесение значений в конечную модель данных
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            // Если модель данных создалась, то выполняем этот блок кода
            try {
                // Получение конечного пути и значений для сохранения
                val outputStream = contentResolver.openOutputStream(uri)
                if (outputStream != null) {
                    try {
                        // Конвертит мапу в пнг
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                        // Закрывает модель данных для использования
                        outputStream.close()
                    } catch (e: Exception) {
                        Log.e(ContentValues.TAG, "saveBitmapImage: ", e)
                    }
                }
                // Убираем значения из ожидания
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                // Очищаем модель данных
                contentResolver.update(uri, values, null, null)

                //Toast.makeText(this, "Saved1...", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e(ContentValues.TAG, "saveBitmapImage: ", e)
            }
        } else {
            // Если же модель данных не создалась, то выполняем этот блок кода

            // Задаём путь сохранения в общий (внешний) каталог хранилища / наша подпапка
            val imageFileFolder = File(Environment.getExternalStorageDirectory().toString() + '/' + getString(R.string.app_name))
            if (!imageFileFolder.exists()) {
                // Если даже так нет пути сохранение, то используем метод mkdirs()
                // Создание нового каталога, обозначенного абстрактным путем что бы это не значило
                imageFileFolder.mkdirs()
            }
            // Переменная названия картинки текущей датой.png
            val mImageName = "$timestamp.png"
            // Переменная конечного результата сохранения нашей картинки
            val imageFile = File(imageFileFolder, mImageName)
            try {
                // Получение конечного пути и значений для сохранения
                val outputStream: OutputStream = FileOutputStream(imageFile)
                try {
                    // Конвертит мапу в пнг
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    // Закрывает модель данных для использования
                    outputStream.close()
                } catch (e: Exception) {
                    Log.e(ContentValues.TAG, "saveBitmapImage: ", e)
                }
                // Помещаем в значения абсолютный путь нашей imageFile
                // An absolute path is a path that starts at a root of the file system. On Android, there is only one root: /.
                values.put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
                // В модель данных помещаем значения
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

                //Toast.makeText(this, "Saved2...", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e(ContentValues.TAG, "saveBitmapImage: ", e)
            }
        }
    }
}