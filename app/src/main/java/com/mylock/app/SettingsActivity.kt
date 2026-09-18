package com.mylock.app

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val etOldPin = findViewById<EditText>(R.id.etOldPin)
        val etNewPin = findViewById<EditText>(R.id.etNewPin)
        val btnSavePin = findViewById<Button>(R.id.btnSavePin)

        val prefs = getSharedPreferences("MyLockPrefs", Context.MODE_PRIVATE)

        btnSavePin.setOnClickListener {
            val enteredOld = etOldPin.text.toString()
            val enteredNew = etNewPin.text.toString()
            val savedPin = prefs.getString("APP_PIN", "1234")

            if (enteredOld != savedPin) {
                Toast.makeText(this, "Current PIN is incorrect", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (enteredNew.length != 4) {
                Toast.makeText(this, "New PIN must be 4 digits", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            prefs.edit().putString("APP_PIN", enteredNew).apply()
            Toast.makeText(this, "PIN successfully updated!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
