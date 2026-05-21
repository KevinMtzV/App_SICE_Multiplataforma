package com.example.app_sice_multiplataforma.data.network

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

val bodyacceso = """
    <?xml version="1.0" encoding="utf-8"?>
    <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
      <soap:Body>
        <accesoLogin xmlns="http://tempuri.org/">
          <strMatricula>%s</strMatricula>
          <strContrasenia>%s</strContrasenia>   
          <tipoUsuario>%s</tipoUsuario> 
        </accesoLogin>
      </soap:Body>
    </soap:Envelope>
""".trimIndent()

val bodyAlumno = """
    <?xml version="1.0" encoding="utf-8"?>
    <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
      <soap:Body>
        <getAlumnoAcademicoWithLineamiento xmlns="http://tempuri.org/">
          <strMatricula>%s</strMatricula>
          <strContrasenia>%s</strContrasenia>
        </getAlumnoAcademicoWithLineamiento>
      </soap:Body>
    </soap:Envelope>
""".trimIndent()

val bodyKardex = """
    <?xml version="1.0" encoding="utf-8"?>
    <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
      <soap:Body>
        <getAllKardexConPromedioByAlumno xmlns="http://tempuri.org/">
          <aluLineamiento>%s</aluLineamiento>
        </getAllKardexConPromedioByAlumno>
      </soap:Body>
    </soap:Envelope>
""".trimIndent()

val bodyCalifFinales = """
    <?xml version="1.0" encoding="utf-8"?>
    <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
      <soap:Body>
        <getAllCalifFinalByAlumnos xmlns="http://tempuri.org/">
          <bytModEducativo>%d</bytModEducativo>
        </getAllCalifFinalByAlumnos>
      </soap:Body>
    </soap:Envelope>
""".trimIndent()

val bodyCargaAcademica = """
    <?xml version="1.0" encoding="utf-8"?>
    <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
      <soap:Body>
        <getCargaAcademicaByAlumno xmlns="http://tempuri.org/" />
      </soap:Body>
    </soap:Envelope>
""".trimIndent()

val bodyVacio = """
    <?xml version="1.0" encoding="utf-8"?>
    <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
      <soap:Body>
        <getCalifUnidadesByAlumno xmlns="http://tempuri.org/" />
      </soap:Body>
    </soap:Envelope>
""".trimIndent()

class SICENETService(private val client: HttpClient) {
    private val baseUrl = "https://sicenet.surguanajuato.tecnm.mx"

    private suspend fun soapRequest(action: String, body: String): String {
        val response = client.post("$baseUrl/ws/wsalumnos.asmx") {
            header("Content-Type", "text/xml; charset=utf-8")
            header("SOAPAction", "http://tempuri.org/$action")
            setBody(body)
        }
        return response.bodyAsText()
    }

    suspend fun acceso(m: String, p: String, t: String) = soapRequest("accesoLogin", bodyacceso.format(m, p, t))
    suspend fun getPerfil(m: String, p: String) = soapRequest("getAlumnoAcademicoWithLineamiento", bodyAlumno.format(m, p))
    suspend fun getKardex(lineamiento: String) = soapRequest("getAllKardexConPromedioByAlumno", bodyKardex.format(lineamiento))
    suspend fun getCalifUnidades() = soapRequest("getCalifUnidadesByAlumno", bodyVacio)
    suspend fun getCalifFinales(modo: Int) = soapRequest("getAllCalifFinalByAlumnos", bodyCalifFinales.format(modo))
    suspend fun getCargaAcademica() = soapRequest("getCargaAcademicaByAlumno", bodyCargaAcademica)
}
