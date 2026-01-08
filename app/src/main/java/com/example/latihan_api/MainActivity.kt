package com.example.latihan_api

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.latihan_api.adapter.CatatanAdapter
import com.example.latihan_api.databinding.ActivityMainBinding
import com.example.latihan_api.entities.Catatan
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: CatatanAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupEvents()
    }

    fun setupEvents() {
        adapter = CatatanAdapter(mutableListOf(), object : CatatanAdapter.CatatanItemEvents {
            override fun onEdit(catatan: Catatan) {
                val intent = Intent(this@MainActivity, EditCatatanActivity::class.java)
                intent.putExtra("id_catatan", catatan.id)
                startActivity(intent)
            }

            override fun onDelete(catatan: Catatan) {
                showConfirmationDialog(catatan)
            }
        })

        binding.container.adapter = adapter
        binding.container.layoutManager = LinearLayoutManager(this)

                binding.btnNavigate.setOnClickListener{
            val intent = Intent(this, CreateCatatan::class.java)
            startActivity(intent)
        }

    }


    private fun showConfirmationDialog(catatan: Catatan) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Hapus Catatan")
        builder.setMessage("Apakah kamu yakin ingin menghapus '${catatan.judul}'?")

        builder.setPositiveButton("Hapus") { dialog, _ ->
            deleteData(catatan.id?: 0)
            dialog.dismiss()
        }

        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun deleteData(id: Int) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.catatanRepository.deleteCatatan(id)

                if (response.isSuccessful) {
                    displayMessage("Berhasil menghapus data")
                    loadData()
                } else {
                    displayMessage("Gagal menghapus: ${response.message()}")
                }
            } catch (e: Exception) {
                displayMessage("Error koneksi: ${e.message}")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        loadData()
    }

    fun loadData() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.catatanRepository.getCatatan()
                if (!response.isSuccessful){
                    displayMessage("Gagal load data: ${response.message()}")
                    return@launch
                }

                val data = response.body()
                if (data == null) {
                    displayMessage("Tidak ada Data")
                    return@launch
                }
                adapter.updateDataset(data)
            } catch (e: Exception) {
                displayMessage("Error koneksi: ${e.message}")
            }
        }
    }

    fun displayMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}