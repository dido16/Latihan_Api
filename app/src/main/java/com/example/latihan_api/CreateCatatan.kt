package com.example.latihan_api

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.latihan_api.databinding.ActivityCreateCatatanBinding
import com.example.latihan_api.entities.Catatan
import kotlinx.coroutines.launch

class CreateCatatan : AppCompatActivity() {
    private lateinit var binding: ActivityCreateCatatanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCreateCatatanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupEvent()
    }

    fun setupEvent() {
        binding.TombolSimpan.setOnClickListener{
            val judul = binding.InputJudul.text.toString()
            val isi = binding.InputIsi.text.toString()

            if (judul.isEmpty() || isi.isEmpty())  {
                displayMessage("Judul dan Isi Catatan Harus diisi")
                return@setOnClickListener
            }

            val payload = Catatan(
                id = null,
                judul = judul,
                isi = isi,
                user_id = 1
            )

            lifecycleScope.launch{
                try {
                    val response = RetrofitClient.catatanRepository.createCatatan(payload)

                    if (response.isSuccessful) {
                        displayMessage("Catatan Berhasil Dibuat")
                        val intent = Intent(this@CreateCatatan, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("API_ERROR", "Error: $errorBody")
                        displayMessage("Gagal: ${response.code()} - Cek Logcat")
                    }
                } catch (e: Exception) {
                    displayMessage("Error Koneksi: ${e.message}")
                }
            }
        }
    }

    fun displayMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}