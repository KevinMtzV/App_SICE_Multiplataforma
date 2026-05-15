package com.example.app_sice_multiplataforma.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "parciales")
data class CalificacionParcial(
    @PrimaryKey(autoGenerate = true) val idLocal: Int = 0,
    @SerializedName(value = "Materia", alternate = ["materia", "MATERIA"]) val materia: String = "",
    @SerializedName(value = "C1", alternate = ["c1"]) val p1: String? = null,
    @SerializedName(value = "C2", alternate = ["c2"]) val p2: String? = null,
    @SerializedName(value = "C3", alternate = ["c3"]) val p3: String? = null,
    @SerializedName(value = "C4", alternate = ["c4"]) val p4: String? = null,
    @SerializedName(value = "C5", alternate = ["c5"]) val p5: String? = null,
    @SerializedName(value = "C6", alternate = ["c6"]) val p6: String? = null,
    @SerializedName(value = "C7", alternate = ["c7"]) val p7: String? = null,
    @SerializedName(value = "C8", alternate = ["c8"]) val p8: String? = null,
    @SerializedName(value = "C9", alternate = ["c9"]) val p9: String? = null,
    @SerializedName(value = "C10", alternate = ["c10"]) val p10: String? = null,
    @SerializedName(value = "C11", alternate = ["c11"]) val p11: String? = null,
    @SerializedName(value = "C12", alternate = ["c12"]) val p12: String? = null,
    @SerializedName(value = "C13", alternate = ["c13"]) val p13: String? = null,
    val lastUpdated: String = ""
)