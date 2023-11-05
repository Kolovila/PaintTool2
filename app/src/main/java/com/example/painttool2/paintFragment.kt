package com.example.painttool2

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import java.io.ByteArrayOutputStream




class paintFragment : Fragment() {
    companion object{
        var path = Path()
        var paintBrush = Paint()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_paint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Делаем ориентацию горизонтальной
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

        val blueBtn = view.findViewById<ImageButton>(R.id.blueColorImgButton)
        val blackBtn = view.findViewById<ImageButton>(R.id.blackColorImgButton)
        val eraserBtn = view.findViewById<ImageButton>(R.id.eraserButton)
        val saveBtn = view.findViewById<ImageButton>(R.id.saveButton)
        val paintCanvas = view.findViewById<RelativeLayout>(R.id.paintCanvas)

        // Кнопка синего цвета
        blueBtn.setOnClickListener {
            //Toast.makeText(this, "Classic", Toast.LENGTH_SHORT).show()
            paintBrush.setColor(Color.BLUE)
            currentColor(paintBrush.color)
        }

        // Кнопка чёрного цвета
        blackBtn.setOnClickListener {
            //Toast.makeText(this, "Чёрный бумер", Toast.LENGTH_SHORT).show()
            paintBrush.setColor(Color.BLACK)
            currentColor(paintBrush.color)
        }

        // Кнопка стирания
        eraserBtn.setOnClickListener {
            //Toast.makeText(this, "Стёр", Toast.LENGTH_SHORT).show()
            PaintView.pathList.clear()
            PaintView.colorList.clear()
            path.reset()
        }

        // Кнопка сохранения
        saveBtn.setOnClickListener {
            // Конверт холста в битовую мапу
            val bitmap = getBitmapFromUiView(paintCanvas)

            // Конверт битовой мапы в картинку и затем в байтовый массив
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()

            // Закидываем битовый массив в бандл
            val bundle = Bundle()
            bundle.putByteArray("image", byteArray);

            // Делаем переход в подтверждение сохранения и передаём наш бандл
            view.findNavController().navigate(R.id.action_paintFragment_to_acceptSaveFragment, bundle)
        }
    }

    private fun getBitmapFromUiView(view: View?): Bitmap {
        // Определение битмапы по размерам оригинального холста
        val returnedBitmap = Bitmap.createBitmap(requireView().width, requireView().height, Bitmap.Config.ARGB_8888)
        // Привязываем canvas к нашей мапе
        val canvas = Canvas(returnedBitmap)

        /*
        // Получаем background нашего холста (view)
        val bgDrawable = view.background
        if (bgDrawable != null) {
            // Если бэкграунд есть, то отрисовываем его на canvas
            //bgDrawable.draw(canvas)
        } else {
            // Если же нет - просто закрашиваем его белым, а то хули он
            //canvas.drawColor(Color.WHITE)
        }
        */

        // Делаем задний фон прозрачным
        canvas.drawColor(Color.alpha(0))

        // Отрисовываем view на canvas
        requireView().draw(canvas)

        // Возвращаем битмапу
        return returnedBitmap
    }

    private fun currentColor(color: Int) {
        PaintView.currentBrush = color

        path = Path()
    }
}