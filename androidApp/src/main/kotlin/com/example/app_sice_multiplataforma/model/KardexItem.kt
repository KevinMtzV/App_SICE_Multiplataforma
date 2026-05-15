package com.example.app_sice_multiplataforma.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "kardex")
data class KardexItem(
    @PrimaryKey(autoGenerate = true) val idLocal: Int = 0,
    @SerializedName("Materia") val materia: String = "",
    @SerializedName("Calif") val calificacion: Int = 0,
    @SerializedName("P1") val periodo: String? = null,
    @SerializedName("A1") val anio: String? = null,
    @SerializedName("S1") val semestre: String? = null,
    val lastUpdated: String = ""
)
//5q$S_B