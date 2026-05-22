package com.example.app_sice_multiplataforma.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Serializable
@Entity(tableName = "perfil")
data class ProfileStudent(
    @PrimaryKey
    val matricula: String = "",
    val nombre: String = "",
    val estatus: String = "",
    val carrera: String = ""
)

@Serializable
@Entity(tableName = "kardex")
data class KardexItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @SerialName("Materia") val materia: String = "",
    @SerialName("Calif") val calificacion: Int = 0,
    @SerialName("P1") val periodo: String? = null,
    @SerialName("A1") val anio: String? = null,
    @SerialName("S1") val semestre: String? = null
)

@Serializable
@Entity(tableName = "carga_academica")
data class MateriaCarga(
    @PrimaryKey val clvOficial: String = "",
    @SerialName("Materia") val materia: String = "",
    @SerialName("Docente") val docente: String = "",
    @SerialName("Grupo") val grupo: String = "",
    @SerialName("CreditosMateria") val creditos: Int = 0,
    @SerialName("Lunes") val lunes: String = "",
    @SerialName("Martes") val martes: String = "",
    @SerialName("Miercoles") val miercoles: String = "",
    @SerialName("Jueves") val jueves: String = "",
    @SerialName("Viernes") val viernes: String = ""
)

@Serializable
@Entity(tableName = "parciales")
data class CalificacionParcial(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @SerialName("Materia") val materia: String = "",
    @SerialName("C1") val p1: String? = null,
    @SerialName("C2") val p2: String? = null,
    @SerialName("C3") val p3: String? = null,
    @SerialName("C4") val p4: String? = null,
    @SerialName("C5") val p5: String? = null,
    @SerialName("C6") val p6: String? = null,
    @SerialName("C7") val p7: String? = null,
    @SerialName("C8") val p8: String? = null,
    @SerialName("C9") val p9: String? = null,
    @SerialName("C10") val p10: String? = null,
    @SerialName("C11") val p11: String? = null,
    @SerialName("C12") val p12: String? = null,
    @SerialName("C13") val p13: String? = null
)

@Serializable
@Entity(tableName = "finales")
data class CalificacionFinal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @SerialName("Materia") val materia: String = "",
    @SerialName("Calificacion") val calificacion: String = "",
    @SerialName("Acreditacion") val acreditacion: String = ""
)
