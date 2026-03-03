package com.example.examen
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class LlamarActivity : AppCompatActivity() {

    private lateinit var etNumero: EditText
    private lateinit var btnLlamar: Button
    private val REQUEST_CALL = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_llamar)

        etNumero = findViewById(R.id.etNumero)
        btnLlamar = findViewById(R.id.btnLlamar)

        // Si venimos de la lista con un número, lo recibimos
        val numero = intent.getStringExtra("numero")
        numero?.let {
            etNumero.setText(it)
        }

        btnLlamar.setOnClickListener {
            val numero = etNumero.text.toString().trim()
            if (numero.isNotEmpty()) {
                mostrarDialogoConfirmacion(numero)
            } else {
                Toast.makeText(this, "Ingrese un número", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarDialogoConfirmacion(numero: String) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar llamada")
            .setMessage("¿Desea llamar al número $numero?")
            .setPositiveButton("Llamar") { _, _ ->
                realizarLlamada(numero)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun realizarLlamada(numero: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$numero"))
            startActivity(intent)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CALL)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CALL) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val numero = etNumero.text.toString().trim()
                realizarLlamada(numero)
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}