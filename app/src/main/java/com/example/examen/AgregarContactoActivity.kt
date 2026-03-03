package com.example.examen
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class AgregarContactoActivity : AppCompatActivity() {

    private lateinit var etPais: EditText
    private lateinit var etNombre: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etNota: EditText
    private lateinit var ivImagen: ImageView
    private lateinit var btnSeleccionar: Button
    private lateinit var btnGuardar: Button

    private var imagenUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1

    // Para edición, si recibimos un ID
    private var contactoId: Int? = null
    private lateinit var dbHelper: ContactosDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_contacto)

        dbHelper = ContactosDbHelper(this)

        initViews()
        setupListeners()

        // Verificar si venimos a editar
        contactoId = intent.getIntExtra("contacto_id", -1).takeIf { it != -1 }
        contactoId?.let { cargarDatosContacto(it) }
    }

    private fun initViews() {
        etPais = findViewById(R.id.etPais)
        etNombre = findViewById(R.id.etNombre)
        etTelefono = findViewById(R.id.etTelefono)
        etNota = findViewById(R.id.etNota)
        ivImagen = findViewById(R.id.ivImagen)
        btnSeleccionar = findViewById(R.id.btnSeleccionarImagen)
        btnGuardar = findViewById(R.id.btnGuardar)
    }

    private fun setupListeners() {
        btnSeleccionar.setOnClickListener {
            abrirGaleria()
        }

        btnGuardar.setOnClickListener {
            if (validarCampos()) {
                if (contactoId == null) {
                    guardarContacto()
                } else {
                    actualizarContacto()
                }
            }
        }
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imagenUri = data.data
            ivImagen.setImageURI(imagenUri) // Vista previa
        }
    }

    private fun validarCampos(): Boolean {
        val pais = etPais.text.toString().trim()
        val nombre = etNombre.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()

        return when {
            pais.isEmpty() -> {
                mostrarAlerta("Campo vacío", "El país es obligatorio")
                false
            }
            nombre.isEmpty() -> {
                mostrarAlerta("Campo vacío", "El nombre es obligatorio")
                false
            }
            telefono.isEmpty() -> {
                mostrarAlerta("Campo vacío", "El teléfono es obligatorio")
                false
            }
            !telefono.matches(Regex("^[\\d+\\-\\s]+$")) -> {
                etTelefono.error = "Teléfono inválido (solo dígitos, +, -, espacio)"
                false
            }
            else -> true
        }
    }

    private fun mostrarAlerta(titulo: String, mensaje: String) {
        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(mensaje)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun guardarContacto() {
        val contacto = Contacto(
            pais = etPais.text.toString().trim(),
            nombre = etNombre.text.toString().trim(),
            telefono = etTelefono.text.toString().trim(),
            nota = etNota.text.toString().trim(),
            imagenUri = imagenUri?.toString() ?: ""
        )
        val id = dbHelper.insertarContacto(contacto)
        if (id != -1L) {
            Toast.makeText(this, "Contacto guardado", Toast.LENGTH_SHORT).show()
            finish() // Volver a la actividad anterior (lista)
        } else {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarDatosContacto(id: Int) {
        val contacto = dbHelper.obtenerContactoPorId(id)
        contacto?.let {
            etPais.setText(it.pais)
            etNombre.setText(it.nombre)
            etTelefono.setText(it.telefono)
            etNota.setText(it.nota)
            if (it.imagenUri.isNotEmpty()) {
                imagenUri = Uri.parse(it.imagenUri)
                ivImagen.setImageURI(imagenUri)
            }
        }
    }

    private fun actualizarContacto() {
        val contacto = Contacto(
            id = contactoId!!,
            pais = etPais.text.toString().trim(),
            nombre = etNombre.text.toString().trim(),
            telefono = etTelefono.text.toString().trim(),
            nota = etNota.text.toString().trim(),
            imagenUri = imagenUri?.toString() ?: ""
        )
        val filas = dbHelper.actualizarContacto(contacto)
        if (filas > 0) {
            Toast.makeText(this, "Contacto actualizado", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
        }
    }
}