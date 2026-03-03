package com.example.examen
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnAgregar = findViewById<Button>(R.id.btnAgregar)
        val btnLista = findViewById<Button>(R.id.btnLista)
        val btnLlamar = findViewById<Button>(R.id.btnLlamar)

        btnAgregar.setOnClickListener {
            startActivity(Intent(this, AgregarContactoActivity::class.java))
        }

        btnLista.setOnClickListener {
            startActivity(Intent(this, ListaContactosActivity::class.java))
        }

        btnLlamar.setOnClickListener {
            startActivity(Intent(this, LlamarActivity::class.java))
        }
    }
}