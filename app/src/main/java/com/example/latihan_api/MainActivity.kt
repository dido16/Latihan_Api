package com.example.latihan_api

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
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
        })

        binding.container.adapter = adapter
        binding.container.layoutManager = LinearLayoutManager(this)


//        binding.btnNavigate.setOnClickListener{
//            val intent = Intent(this, CreateCatatan::class.java)
//            startActivity(intent)
//        }
    }

    override fun onStart() {
        super.onStart()
        loadData()
    }

    fun loadData() {
        lifecycleScope.launch {
            val response = RetrofitClient.catatanRepository.getCatatan()
            if (!response.isSuccessful){
                displayMessage("Gagal: ${response.message()}")
                return@launch
            }

            val data = response.body()
            if (data == null) {
                displayMessage("Tidak ada Data")
                return@launch
            }
            adapter.updateDataset(data)
        }
    }

    fun displayMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}