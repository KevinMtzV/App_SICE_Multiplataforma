package com.example.app_sice_multiplataforma.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "carga_academica")
data class MateriaCarga(
    @PrimaryKey(autoGenerate = true) val idLocal: Int = 0,
    @SerializedName(value = "Materia", alternate = ["materia", "MATERIA"]) val materia: String = "",
    @SerializedName(value = "Docente", alternate = ["docente", "DOCENTE"]) val docente: String = "",
    @SerializedName(value = "clvOficial", alternate = ["ClvOficial", "clvoficial"]) val clvOficial: String = "",
    @SerializedName(value = "Grupo", alternate = ["grupo", "GRUPO"]) val grupo: String = "",
    @SerializedName(value = "CreditosMateria", alternate = ["creditosMateria", "creditos", "Creditos"]) val creditos: Int = 0,
    @SerializedName(value = "Lunes", alternate = ["lunes", "LUNES"]) val lunes: String = "",
    @SerializedName(value = "Martes", alternate = ["martes", "MARTES"]) val martes: String = "",
    @SerializedName(value = "Miercoles", alternate = ["miercoles", "MIERCOLES"]) val miercoles: String = "",
    @SerializedName(value = "Jueves", alternate = ["jueves", "JUEVES"]) val jueves: String = "",
    @SerializedName(value = "Viernes", alternate = ["viernes", "VIERNES"]) val viernes: String = "",
    val lastUpdated: String = ""
)