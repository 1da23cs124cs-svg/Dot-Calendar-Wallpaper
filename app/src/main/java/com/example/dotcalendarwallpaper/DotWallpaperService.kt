package com.example.dotcalendarwallpaper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import java.util.*
import kotlin.math.ceil

class DotWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return DotEngine()
    }

    inner class DotEngine : Engine() {

        private val handler = Handler(Looper.getMainLooper())
        private val updateRunnable = Runnable {
            drawDots()
            scheduleNextUpdate()
        }

        // JS Colors translated to Android Colors
        private val colorPast = Color.parseColor("#D10000")   // Gray
        private val colorToday = Color.parseColor("#FF5C5C")  // Maroon
        private val colorFuture = Color.parseColor("#A9A9A9") // DarkGray
        private val colorBg = Color.BLACK

        // Grid Settings matching your JS
        private val cols = 14

        init {
            scheduleNextUpdate()
        }

        private fun scheduleNextUpdate() {
            val now = Calendar.getInstance()
            val nextMidnight = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                add(Calendar.DAY_OF_MONTH, 1)
            }
            val delay = nextMidnight.timeInMillis - now.timeInMillis
            // Update immediately, then schedule next
            handler.removeCallbacks(updateRunnable)
            handler.postDelayed(updateRunnable, delay)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            if (visible) {
                drawDots()
                scheduleNextUpdate()
            } else {
                handler.removeCallbacks(updateRunnable)
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            drawDots()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            handler.removeCallbacks(updateRunnable)
        }

        private fun drawDots() {
            val holder = surfaceHolder
            var canvas: Canvas? = null

            try {
                canvas = holder.lockCanvas()
                if (canvas != null) {
                    // 1. Clear Screen
                    canvas.drawColor(colorBg)

                    // 2. Date Calculations
                    val now = Calendar.getInstance()
                    val dayOfYear = now.get(Calendar.DAY_OF_YEAR)
                    val year = now.get(Calendar.YEAR)
                    val calCalc = Calendar.getInstance()
                    calCalc.set(Calendar.YEAR, year)
                    val totalDays = calCalc.getActualMaximum(Calendar.DAY_OF_YEAR)

                    // 3. Smart Layout Calculations
                    val screenWidth = canvas.width.toFloat()
                    val screenHeight = canvas.height.toFloat()

                    // Define your "Safe Zones"
                    // We request 20% top margin, but we can shrink it if the screen is too small
                    val desiredTopMargin = screenHeight * 0.30f
                    val bottomMargin = screenHeight * 0.15f
                    val sideMargin = screenWidth * 0.05f

                    // Calculate the area available for the grid
                    val usableWidth = screenWidth - (sideMargin * 2)
                    val usableHeight = screenHeight - desiredTopMargin - bottomMargin

                    // Calculate Grid Dimensions
                    val rows = ceil(totalDays.toDouble() / cols).toInt()

                    // 4. Calculate Dot Size (The Key Fix)
                    // Check how big dots can be based on WIDTH
                    val spacingByWidth = usableWidth / (cols - 1)
                    // Check how big dots can be based on HEIGHT
                    val spacingByHeight = usableHeight / (rows - 1)

                    // Pick the SMALLER size so it fits in both dimensions
                    // (Requires import kotlin.math.min)
                    val spacing = kotlin.math.min(spacingByWidth, spacingByHeight)
                    val dotRadius = spacing * 0.25f

                    // 5. Center the Grid
                    val totalGridWidth = (cols - 1) * spacing
                    val totalGridHeight = (rows - 1) * spacing

                    // Center Horizontally
                    val offsetX = (screenWidth - totalGridWidth) / 2
                    // Place Vertically: Start at top margin, but center within the remaining space if there's extra room
                    val offsetY = desiredTopMargin + (usableHeight - totalGridHeight) / 2

                    val paint = Paint().apply { isAntiAlias = true }

                    // 6. Draw Loop
                    for (i in 0 until totalDays) {
                        val col = i % cols
                        val row = i / cols

                        val x = offsetX + (col * spacing)
                        val y = offsetY + (row * spacing)

                        val todayIndex = dayOfYear - 1
                        paint.color = when {
                            i < todayIndex -> colorPast
                            i == todayIndex -> colorToday
                            else -> colorFuture
                        }

                        canvas.drawCircle(x, y, dotRadius, paint)
                    }
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas)
                }
            }
        }
    }
}