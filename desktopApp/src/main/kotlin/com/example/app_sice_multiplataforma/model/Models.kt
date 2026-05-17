package com.example.app_sice_multiplataforma.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileStudent(
    val nombre: String = "",
    val matricula: String = "",
    val estatus: String = "",
    val carrera: String = ""
)

@Serializable
data class KardexItem(
    @SerialName("Materia") val materia: String = "",
    @SerialName("Calif") val calificacion: Int = 0,
    @SerialName("P1") val periodo: String? = null,
    @SerialName("A1") val anio: String? = null,
    @SerialName("S1") val semestre: String? = null
)

@Serializable
data class MateriaCarga(
    @SerialName("Materia") val materia: String = "",
    @SerialName("Docente") val docente: String = "",
    @SerialName("clvOficial") val clvOficial: String = "",
    @SerialName("Grupo") val grupo: String = "",
    @SerialName("CreditosMateria") val creditos: Int = 0,
    @SerialName("Lunes") val lunes: String = "",
    @SerialName("Martes") val martes: String = "",
    @SerialName("Miercoles") val miercoles: String = "",
    @SerialName("Jueves") val jueves: String = "",
    @SerialName("Viernes") val viernes: String = ""
)

@Serializable
data class CalificacionParcial(
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
data class CalificacionFinal(
    @SerialName("Materia") val materia: String = "",
    @SerialName("Calificacion") val calificacion: String = "",
    @SerialName("Acreditacion") val acreditacion: String = ""
)

// Estado de cache local
@Serializable
data class CacheData(
    val perfil: ProfileStudent? = null,
    val kardex: List<KardexItem> = emptyList(),
    val carga: List<MateriaCarga> = emptyList(),
    val parciales: List<CalificacionParcial> = emptyList(),
    val finales: List<CalificacionFinal> = emptyList(),
    val ultimaSincro: String = ""
)
