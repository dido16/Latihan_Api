package com.example.latihan_api

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.latihan_api.databinding.ActivityEditCatatanBinding
import com.example.latihan_api.entities.Catatan
import kotlinx.coroutines.launch

class EditCatatanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditCatatanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEditCatatanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupEvents()
    }

    override fun onStart() {
        super.onStart()
        loadData()
    }

    fun setupEvents() {
        binding.TombolEdit.setOnClickListener{
            val id = intent.getIntExtra("id_catatan", 0)
            val judul = binding.InputJudul.text.toString()
            val isi = binding.InputIsi.text.toString()

            if (isi.isEmpty() || judul.isEmpty()) {
                displayMassage("Judul dan Isi catatan harus di isi")
                return@setOnClickListener
            }

            lifecycleScope.launch {

                val catatan = Catatan(id, judul, isi, 1)

                try {
                    val data = RetrofitClient.catatanRepository.editCatatan(id, catatan)

                    if (data.isSuccessful) {
                        displayMassage("Catatan berhasil diubah")
                        val intent = Intent(this@EditCatatanActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        displayMassage("Gagal Update: ${data.message()}")
                    }
                } catch (e: Exception) {
                    displayMassage("Error Koneksi: ${e.message}")
                }
            }
        }
    }

    fun loadData() {
        val id = intent.getIntExtra("id_catatan", 0)

        if (id == 0){
            displayMassage("Error: id Catatan Tidak Terkirim")
            finish()
            return
        }

        lifecycleScope.launch {
            try {
                val data = RetrofitClient.catatanRepository.getCatatan(id)
                if (data.isSuccessful) {
                    val catatan = data.body()
                    binding.InputJudul.setText(catatan?.judul)
                    binding.InputIsi.setText(catatan?.isi)
                } else {
                    displayMassage("Error Load Data: ${data.message()}")
                }
            } catch (e: Exception) {
                displayMassage("Error Koneksi: ${e.message}")
            }
        }
    }

    fun displayMassage(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}