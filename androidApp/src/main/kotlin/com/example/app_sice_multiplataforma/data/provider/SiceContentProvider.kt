package com.example.app_sice_multiplataforma.data.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.example.app_sice_multiplataforma.data.local.SicenetDatabase

class SiceContentProvider : ContentProvider() {

    // Identificadores para el UriMatcher
    private val KARDEX = 100
    private val CARGA_ACADEMICA = 200
    private val PARCIALES = 300
    private val FINALES = 400

    // Configuración del UriMatcher para saber qué tabla nos están pidiendo
    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(AUTHORITY, "kardex", KARDEX)
        addURI(AUTHORITY, "carga_academica", CARGA_ACADEMICA)
        addURI(AUTHORITY, "parciales", PARCIALES)
        addURI(AUTHORITY, "finales", FINALES)
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return null // Deshabilitado temporalmente debido a la migración a Multiplatform
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException("Operación de inserción bloqueada por seguridad")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        throw UnsupportedOperationException("Operación de actualización bloqueada por seguridad")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException("Operación de borrado bloqueada por seguridad")
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            KARDEX -> "vnd.android.cursor.dir/vnd.$AUTHORITY.kardex"
            CARGA_ACADEMICA -> "vnd.android.cursor.dir/vnd.$AUTHORITY.carga_academica"
            PARCIALES -> "vnd.android.cursor.dir/vnd.$AUTHORITY.parciales"
            FINALES -> "vnd.android.cursor.dir/vnd.$AUTHORITY.finales"
            else -> throw IllegalArgumentException("URI desconocida: $uri")
        }
    }

    companion object {
        // La autoridad debe ser exactamente igual a la del AndroidManifest
        const val AUTHORITY = "com.example.marsphotos.provider"

        // URIs públicas para que otras apps las consulten (como tu Cliente_Sice)
        val URI_KARDEX: Uri = Uri.parse("content://$AUTHORITY/kardex")
        val URI_CARGA: Uri = Uri.parse("content://$AUTHORITY/carga_academica")
        val URI_PARCIALES: Uri = Uri.parse("content://$AUTHORITY/parciales")
        val URI_FINALES: Uri = Uri.parse("content://$AUTHORITY/finales")
    }
}