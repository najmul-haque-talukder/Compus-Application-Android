package com.example.compusapplication

import android.hardware.Sensor
import android.hardware.Sensor.*
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.w3c.dom.Text

class MainActivity : AppCompatActivity(), SensorEventListener {

        private lateinit var display: TextView
        private lateinit var image: ImageView
        private var currentDegree = 0f

        private var sensorManager: SensorManager? = null
        private var accelerometer: Sensor? = null
        private var magnetometer: Sensor? = null

        private var gravity: FloatArray? = null
        private var geomagnetic: FloatArray? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            display = findViewById(R.id.textView)
            image = findViewById(R.id.imageView2)

            sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
            accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            magnetometer = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        }

        override fun onResume() {
            super.onResume()
            // Register both sensors
            accelerometer?.let {
                sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            }
            magnetometer?.let {
                sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            }
        }

        override fun onPause() {
            super.onPause()
            sensorManager?.unregisterListener(this)
        }

        override fun onSensorChanged(event: SensorEvent?) {
            if (event == null) return

            when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> gravity = event.values
                Sensor.TYPE_MAGNETIC_FIELD -> geomagnetic = event.values
            }

            if (gravity != null && geomagnetic != null) {
                val R = FloatArray(9)
                val I = FloatArray(9)
                if (SensorManager.getRotationMatrix(R, I, gravity, geomagnetic)) {
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(R, orientation)
                    val azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                    val degree = (azimuth + 360) % 360

                    // Update display text
                    display.text = "Heading: ${degree.toInt()}°"

                    // Rotate the compass image
                    val rotateAnimation = RotateAnimation(
                        currentDegree,
                        -degree,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f
                    )
                    rotateAnimation.duration = 210
                    rotateAnimation.fillAfter = true
                    image.startAnimation(rotateAnimation)

                    currentDegree = -degree
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // Not used
        }
    }