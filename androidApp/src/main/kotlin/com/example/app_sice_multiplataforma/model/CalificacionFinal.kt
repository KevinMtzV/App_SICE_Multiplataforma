package com.example.app_sice_multiplataforma.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "finales")
data class CalificacionFinal(
    @PrimaryKey(autoGenerate = true) val idLocal: Int = 0,
    @SerializedName(value = "Materia", alternate = ["materia", "MATERIA"]) val materia: String = "",
    @SerializedName(value = "Calificacion", alternate = ["calificacion", "CALIFICACION", "calif", "Calif"]) val calificacion: String = "",
    @SerializedName(value = "Acreditacion", alternate = ["acreditacion", "ACREDITACION", "observaciones", "Observaciones"]) val acreditacion: String = "",
    val lastUpdated: String = ""
)