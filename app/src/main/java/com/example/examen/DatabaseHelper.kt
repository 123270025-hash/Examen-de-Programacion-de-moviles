package com.example.examen
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ContactosDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "contactos.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_CONTACTOS = "contactos"
        private const val COLUMN_ID = "id"
        private const val COLUMN_PAIS = "pais"
        private const val COLUMN_NOMBRE = "nombre"
        private const val COLUMN_TELEFONO = "telefono"
        private const val COLUMN_NOTA = "nota"
        private const val COLUMN_IMAGEN = "imagen"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_CONTACTOS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PAIS TEXT,
                $COLUMN_NOMBRE TEXT,
                $COLUMN_TELEFONO TEXT,
                $COLUMN_NOTA TEXT,
                $COLUMN_IMAGEN TEXT
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTOS")
        onCreate(db)
    }

    // Insertar contacto
    fun insertarContacto(contacto: Contacto): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PAIS, contacto.pais)
            put(COLUMN_NOMBRE, contacto.nombre)
            put(COLUMN_TELEFONO, contacto.telefono)
            put(COLUMN_NOTA, contacto.nota)
            put(COLUMN_IMAGEN, contacto.imagenUri)
        }
        return db.insert(TABLE_CONTACTOS, null, values)
    }

    // Obtener todos los contactos
    fun obtenerTodos(): List<Contacto> {
        val lista = mutableListOf<Contacto>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CONTACTOS", null)

        with(cursor) {
            while (moveToNext()) {
                val contacto = Contacto(
                    id = getInt(getColumnIndexOrThrow(COLUMN_ID)),
                    pais = getString(getColumnIndexOrThrow(COLUMN_PAIS)),
                    nombre = getString(getColumnIndexOrThrow(COLUMN_NOMBRE)),
                    telefono = getString(getColumnIndexOrThrow(COLUMN_TELEFONO)),
                    nota = getString(getColumnIndexOrThrow(COLUMN_NOTA)),
                    imagenUri = getString(getColumnIndexOrThrow(COLUMN_IMAGEN))
                )
                lista.add(contacto)
            }
        }
        cursor.close()
        db.close()
        return lista
    }

    // Actualizar contacto
    fun actualizarContacto(contacto: Contacto): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PAIS, contacto.pais)
            put(COLUMN_NOMBRE, contacto.nombre)
            put(COLUMN_TELEFONO, contacto.telefono)
            put(COLUMN_NOTA, contacto.nota)
            put(COLUMN_IMAGEN, contacto.imagenUri)
        }
        return db.update(TABLE_CONTACTOS, values, "$COLUMN_ID = ?", arrayOf(contacto.id.toString()))
    }

    // Eliminar contacto
    fun eliminarContacto(id: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_CONTACTOS, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    // Obtener un contacto por ID (para editar, por ejemplo)
    fun obtenerContactoPorId(id: Int): Contacto? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_CONTACTOS,
            null,
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null, null, null
        )
        return cursor.use {
            if (it.moveToFirst()) {
                Contacto(
                    id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                    pais = it.getString(it.getColumnIndexOrThrow(COLUMN_PAIS)),
                    nombre = it.getString(it.getColumnIndexOrThrow(COLUMN_NOMBRE)),
                    telefono = it.getString(it.getColumnIndexOrThrow(COLUMN_TELEFONO)),
                    nota = it.getString(it.getColumnIndexOrThrow(COLUMN_NOTA)),
                    imagenUri = it.getString(it.getColumnIndexOrThrow(COLUMN_IMAGEN))
                )
            } else {
                null
            }
        }
    }
}