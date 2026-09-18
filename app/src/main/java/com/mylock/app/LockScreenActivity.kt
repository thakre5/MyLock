package com.mylock.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LockScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock)

        val etPin = findViewById<EditText>(R.id.etPin)
        val btnUnlock = findViewById<Button>(R.id.btnUnlock)

        val sharedPreferences = getSharedPreferences("MyLockPrefs", Context.MODE_PRIVATE)
        // Default PIN is set to "1234" initially
        val savedPin = sharedPreferences.getString("APP_PIN", "1234")

        btnUnlock.setOnClickListener {
            val enteredPin = etPin.text.toString()
            if (enteredPin == savedPin) {
                Toast.makeText(this, "Unlocked Successfully", Toast.LENGTH_SHORT).show()
                finish() // Close lock screen and return to the app
            } else {
                Toast.makeText(this, "Incorrect PIN! (Default is 1234)", Toast.LENGTH_SHORT).show()
                etPin.text.clear()
            }
        }
    }

    // Prevent the user from bypassing the lock by pressing back
    override fun onSupportNavigateUp(): Boolean {
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Redirect to Home screen instead of letting them back into the locked app
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(homeIntent)
    }
}
