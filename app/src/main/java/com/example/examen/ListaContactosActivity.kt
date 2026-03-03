package com.example.examen
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class ListaContactosActivity : AppCompatActivity() {

    private lateinit var etBuscar: EditText
    private lateinit var listView: ListView
    private lateinit var dbHelper: ContactosDbHelper
    private lateinit var adapter: ContactoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_contactos)

        dbHelper = ContactosDbHelper(this)

        etBuscar = findViewById(R.id.etBuscar)
        listView = findViewById(R.id.listViewContactos)

        cargarLista()

        // Buscador
        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }
        })

        // Al hacer clic en un contacto, vamos a la actividad de detalle/edición (opcional)
        // En este ejemplo, al hacer clic en el item, abrimos AgregarContactoActivity en modo edición
        listView.setOnItemClickListener { _, _, position, _ ->
            val contacto = adapter.getItem(position)
            val intent = Intent(this, AgregarContactoActivity::class.java)
            intent.putExtra("contacto_id", contacto.id)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        cargarLista() // Recargar al volver
    }

    private fun cargarLista() {
        val contactos = dbHelper.obtenerTodos()
        adapter = ContactoAdapter(this, contactos.toMutableList(), dbHelper) { contacto ->
            // Aquí podrías abrir un detalle o edición, ya lo manejamos en el setOnItemClickListener
            // Pero también podemos usarlo para otra acción
        }
        listView.adapter = adapter
    }
}