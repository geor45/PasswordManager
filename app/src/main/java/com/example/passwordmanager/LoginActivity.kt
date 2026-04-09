package com.example.passwordmanager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etPin = findViewById<EditText>(R.id.etMasterPin)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        val sharedPref = getSharedPreferences("SecurePrefs", android.content.Context.MODE_PRIVATE)
        val savedPin = sharedPref.getString("master_pin", null)

        // 1. ΑΥΤΟΜΑΤΗ ΕΜΦΑΝΙΣΗ (αν υπάρχει ήδη PIN)
        if (savedPin != null) {
            // Περιμένουμε μισό δευτερόλεπτο για να προλάβει να φορτώσει η οθόνη
            etPin.postDelayed({
                showBiometricPrompt()
            }, 500)
        }

        btnLogin.setOnClickListener {
            val enteredPin = etPin.text.toString()

            if (savedPin == null) {
                // Πρώτη φορά: Ορισμός PIN
                if (enteredPin.length >= 4) {
                    sharedPref.edit().putString("master_pin", enteredPin).apply()

                    // ΠΡΟΑΙΡΕΤΙΚΟ: Μόλις ορίσει το PIN, του ζητάμε να σκανάρει
                    // το δάχτυλο για επιβεβαίωση πριν μπει
                    showBiometricPrompt()

                } else {
                    Toast.makeText(this, "Το PIN πρέπει να είναι τουλάχιστον 4 ψηφία", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Επόμενες φορές: Έλεγχος PIN
                if (enteredPin == savedPin) {
                    startMainActivity()
                } else {
                    Toast.makeText(this, "Λάθος PIN!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    startMainActivity()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // Αν ακυρώσει το δακτυλικό, μένει στην οθόνη για να βάλει το PIN
                    Toast.makeText(applicationContext, "Σύνδεση με PIN", Toast.LENGTH_SHORT).show()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Ασφαλής Είσοδος")
            .setSubtitle("Χρησιμοποιήστε το δακτυλικό σας αποτύπωμα")
            // Αυτή η γραμμή επιτρέπει και το δακτυλικό και το PIN της συσκευής
            .setAllowedAuthenticators(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG or androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}