package com.example.passwordmanager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.security.SecureRandom

class MainActivity : AppCompatActivity() {

    private lateinit var passwordAdapter: PasswordAdapter
    private var fullList: List<PasswordEntity> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Σύνδεση με το UI
        val etService = findViewById<EditText>(R.id.editTextServiceName)
        val etPass = findViewById<EditText>(R.id.editTextPassword)
        val tvSuggested = findViewById<TextView>(R.id.tvSuggestedPassword)
        val btnSave = findViewById<Button>(R.id.buttonSave)
        val rvPasswords = findViewById<RecyclerView>(R.id.recyclerViewPasswords)
        val searchView = findViewById<SearchView>(R.id.searchView)
        val btnChangePin = findViewById<Button>(R.id.btnChangePin)

        // 2. Ρύθμιση Spinner (ΣΥΣΤΑΣΗ SPINNER)
        val spinnerCategory = findViewById<Spinner>(R.id.spinnerCategory)
        val categories = arrayOf("Social Media", "Banking", "Work", "Personal", "Other")
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spinnerCategory.adapter = adapterSpinner

        val database = AppDatabase.getDatabase(this)
        val passwordDao = database.passwordDao()

        // 3. Λογική Password Generator
        etPass.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && etPass.text.isEmpty()) {
                val newSuggestion = generateStrongPassword(16)
                tvSuggested.text = "Πρόταση ισχυρού κωδικού:\n$newSuggestion"
                tvSuggested.visibility = View.VISIBLE

                tvSuggested.setOnClickListener {
                    etPass.setText(newSuggestion)
                    tvSuggested.visibility = View.GONE
                    // Εμφάνιση κωδικού (όχι τελείες) για να τον δει ο χρήστης
                    etPass.transformationMethod = null
                }
            } else {
                tvSuggested.visibility = View.GONE
            }
        }

        // 4. Ρύθμιση του Adapter
        passwordAdapter = PasswordAdapter(emptyList()) { passwordToDelete ->
            lifecycleScope.launch {
                passwordDao.delete(passwordToDelete)
                loadPasswords(passwordDao)
            }
        }

        rvPasswords.layoutManager = LinearLayoutManager(this)
        rvPasswords.adapter = passwordAdapter

        loadPasswords(passwordDao)

        // 5. Λειτουργία Αποθήκευσης
        btnSave.setOnClickListener {
            val inputService = etService.text.toString()
            val inputPassword = etPass.text.toString()
            // Εδώ παίρνουμε την τιμή από το spinnerCategory που ορίσαμε παραπάνω
            val selectedCategory = spinnerCategory.selectedItem.toString()

            if (inputService.isNotEmpty() && inputPassword.isNotEmpty()) {
                val newEntry = PasswordEntity(
                    title = inputService,
                    password = inputPassword,
                    category = selectedCategory, // Χρήση της κατηγορίας
                    username = "",
                    websiteUrl = ""
                )

                lifecycleScope.launch {
                    passwordDao.insert(newEntry)
                    etService.text.clear()
                    etPass.text.clear()
                    // Επαναφορά σε τελείες (Password Dots)
                    etPass.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
                    loadPasswords(passwordDao)
                    Toast.makeText(this@MainActivity, "Αποθηκεύτηκε!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Συμπλήρωσε όλα τα πεδία", Toast.LENGTH_SHORT).show()
            }
        }

        // 6. Λειτουργία Αναζήτησης
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })

        // 7. Αλλαγή PIN
        btnChangePin.setOnClickListener {
            val sharedPref = getSharedPreferences("SecurePrefs", Context.MODE_PRIVATE)
            sharedPref.edit().remove("master_pin").apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun generateStrongPassword(length: Int): String {
        val charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%&*-_"
        val secureRandom = SecureRandom()
        return (1..length)
            .map { charset[secureRandom.nextInt(charset.length)] }
            .joinToString("")
    }

    private fun filterList(query: String?) {
        val filteredList = if (query.isNullOrEmpty()) {
            fullList
        } else {
            fullList.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.category.contains(query, ignoreCase = true)
            }
        }
        passwordAdapter.updateData(filteredList)
        updateEmptyView(filteredList)
    }

    private fun loadPasswords(dao: PasswordDao) {
        lifecycleScope.launch {
            val list = dao.getAllPasswords()
            fullList = list
            passwordAdapter.updateData(list)
            updateEmptyView(list)
        }
    }

    private fun updateEmptyView(list: List<PasswordEntity>) {
        val emptyView = findViewById<LinearLayout>(R.id.emptyView)
        val rvPasswords = findViewById<RecyclerView>(R.id.recyclerViewPasswords)
        if (list.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            rvPasswords.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            rvPasswords.visibility = View.VISIBLE
        }
    }
}