package com.micewine.emu.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Debug
import android.util.AttributeSet
import android.view.View
import com.micewine.emu.activities.MainActivity.Companion.d3dxRenderer
import com.micewine.emu.activities.MainActivity.Companion.enableCpuCounter
import com.micewine.emu.activities.MainActivity.Companion.enableDebugInfo
import com.micewine.emu.activities.MainActivity.Companion.enableRamCounter
import com.micewine.emu.activities.MainActivity.Companion.memoryStats
import com.micewine.emu.activities.MainActivity.Companion.miceWineVersion
import com.micewine.emu.activities.MainActivity.Companion.selectedDXVK
import com.micewine.emu.activities.MainActivity.Companion.selectedWineD3D
import com.micewine.emu.activities.MainActivity.Companion.totalCpuUsage
import com.micewine.emu.activities.MainActivity.Companion.vulkanDriverDeviceName
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException

class OnScreenInfoView @JvmOverloads constructor (context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): View(context, attrs, defStyleAttr) {
    private val paint: Paint = Paint().apply {
        textSize = 30F
        strokeWidth = 8F
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (enableRamCounter) {
            drawText("RAM: $memoryStats", 20F, 40F, canvas)
        }

        if (enableCpuCounter) {
            drawText("CPU: $totalCpuUsage", 20F, 80F, canvas)
        }

        if (enableDebugInfo) {
            onScreenInfo(canvas)
        }

        if (enableCpuTemperature()) {
            val cpuTemperature = getCpuTemperature()
            drawText("CPU Temp: $cpuTemperature°C", 20F, 120F, canvas)
        }

        invalidate()
    }

    private fun onScreenInfo(c: Canvas) {
        drawText(miceWineVersion, getTextEndX(c, miceWineVersion), 40F, c)

        if (d3dxRenderer == "DXVK") {
            drawText(selectedDXVK!!, getTextEndX(c, selectedDXVK!!), 80F, c)
        } else if (d3dxRenderer == "WineD3D") {
            drawText(selectedWineD3D!!, getTextEndX(c, selectedWineD3D!!), 80F, c)
        }

        drawText(vulkanDriverDeviceName!!, getTextEndX(c, vulkanDriverDeviceName!!), 120F, c)
    }

    private fun drawText(text: String, x: Float, y: Float, c: Canvas) {
        paint.style = Paint.Style.STROKE
        paint.color = Color.BLACK
        c.drawText(text, x, y, paint)

        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        c.drawText(text, x, y, paint)
    }

    private fun getTextEndX(canvas: Canvas, string: String): Float {
        return canvas.width - paint.measureText(string) - 20F
    }

    // Проверка наличия доступа к температуре процессора
    private fun enableCpuTemperature(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }

    // Чтение температуры процессора
    private fun getCpuTemperature(): String {
        val thermalFile = "/sys/class/thermal/thermal_zone0/temp"
        var temperature = "N/A"
        try {
            val bufferedReader = BufferedReader(FileReader(thermalFile))
            temperature = bufferedReader.readLine()
            bufferedReader.close()
            // Температура обычно в миллиградусах, поэтому делим на 1000
            temperature = (temperature.toInt() / 1000).toString()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return temperature
    }
}
