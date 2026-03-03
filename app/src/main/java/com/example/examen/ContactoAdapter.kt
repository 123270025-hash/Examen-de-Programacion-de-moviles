package com.example.examen
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class ContactoAdapter(
    context: Context,
    private var listaContactos: MutableList<Contacto>,
    private val dbHelper: ContactosDbHelper,
    private val onItemClick: (Contacto) -> Unit // Para editar al hacer clic en el item
) : ArrayAdapter<Contacto>(context, 0, listaContactos) {

    private var listaOriginal = listaContactos.toMutableList()

    override fun getCount(): Int = listaContactos.size

    override fun getItem(position: Int): Contacto = listaContactos[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_contacto, parent, false)
        val contacto = getItem(position)

        val ivContacto = view.findViewById<ImageView>(R.id.ivContacto)
        val tvNombre = view.findViewById<TextView>(R.id.tvNombre)
        val tvTelefono = view.findViewById<TextView>(R.id.tvTelefono)
        val btnCompartir = view.findViewById<Button>(R.id.btnCompartir)
        val btnEliminar = view.findViewById<Button>(R.id.btnEliminar)

        tvNombre.text = contacto.nombre
        tvTelefono.text = contacto.telefono

        // Cargar imagen si existe
        if (contacto.imagenUri.isNotEmpty()) {
            try {
                ivContacto.setImageURI(Uri.parse(contacto.imagenUri))
            } catch (e: Exception) {
                ivContacto.setImageResource(R.drawable.ic_placeholder)
            }
        } else {
            ivContacto.setImageResource(R.drawable.ic_placeholder)
        }

        // Click en el item para editar (luego abrirá la actividad de agregar con el ID)
        view.setOnClickListener {
            onItemClick(contacto)
        }

        // Compartir contacto
        btnCompartir.setOnClickListener {
            compartirContacto(contacto)
        }

        // Eliminar contacto
        btnEliminar.setOnClickListener {
            mostrarDialogoEliminar(contacto, position)
        }

        return view
    }

    private fun compartirContacto(contacto: Contacto) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Contacto: ${contacto.nombre}\nTeléfono: ${contacto.telefono}\nPaís: ${contacto.pais}\nNota: ${contacto.nota}")
        }
        context.startActivity(Intent.createChooser(shareIntent, "Compartir contacto"))
    }

    private fun mostrarDialogoEliminar(contacto: Contacto, position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Eliminar contacto")
            .setMessage("¿Estás seguro de eliminar a ${contacto.nombre}?")
            .setPositiveButton("Sí") { _, _ ->
                dbHelper.eliminarContacto(contacto.id)
                listaContactos.removeAt(position)
                listaOriginal.remove(contacto)
                notifyDataSetChanged()
            }
            .setNegativeButton("No", null)
            .show()
    }

    // Método para filtrar (buscador)
    fun filter(text: String) {
        listaContactos.clear()
        if (text.isEmpty()) {
            listaContactos.addAll(listaOriginal)
        } else {
            listaOriginal.forEach {
                if (it.nombre.contains(text, ignoreCase = true) ||
                    it.telefono.contains(text, ignoreCase = true)) {
                    listaContactos.add(it)
                }
            }
        }
        notifyDataSetChanged()
    }
}