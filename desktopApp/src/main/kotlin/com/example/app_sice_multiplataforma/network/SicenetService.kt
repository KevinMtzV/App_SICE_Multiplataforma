package com.example.app_sice_multiplataforma.network

import com.example.app_sice_multiplataforma.model.*
import kotlinx.serialization.json.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit

// ─── SOAP Bodies: IDÉNTICOS al Android (incluyendo espacios extra) ──────────

private val bodyAcceso = """
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

private val bodyAlumno = """
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

private val bodyKardex = """
    <?xml version="1.0" encoding="utf-8"?>
    <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
      <soap:Body>
        <getAllKardexConPromedioByAlumno xmlns="http://tempuri.org/">
          <aluLineamiento>%s</aluLineamiento>
        </getAllKardexConPromedioByAlumno>
      </soap:Body>
    </soap:Envelope>
""".trimIndent()

private val bodyCalifUnidades = """
    <?xml version="1.0" encoding="utf-8"?>
    <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
      <soap:Body>
        <getCalifUnidadesByAlumno xmlns="http://tempuri.org/" />
      </soap:Body>
    </soap:Envelope>
""".trimIndent()

private val bodyCalifFinales = """
    <?xml version="1.0" encoding="utf-8"?>
    <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
      <soap:Body>
        <getAllCalifFinalByAlumnos xmlns="http://tempuri.org/">
          <bytModEducativo>%d</bytModEducativo>
        </getAllCalifFinalByAlumnos>
      </soap:Body>
    </soap:Envelope>
""".trimIndent()

private val bodyCargaAcademica = """
    <?xml version="1.0" encoding="utf-8"?>
    <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
      <soap:Body>
        <getCargaAcademicaByAlumno xmlns="http://tempuri.org/" />
      </soap:Body>
    </soap:Envelope>
""".trimIndent()

object SicenetConfig {
    var proxyHost: String? = null
    var proxyPort: Int = 8888
    val baseUrl: String get() = if (proxyHost != null)
        "http://$proxyHost:$proxyPort"
    else
        "https://sicenet.surguanajuato.tecnm.mx"
}

class SicenetService {

    private val cookieManager = CookieManager().also {
        it.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
    }

    private fun buildClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(cookieManager))
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)

        SicenetConfig.proxyHost?.let { host ->
            builder.proxy(java.net.Proxy(
                java.net.Proxy.Type.HTTP,
                java.net.InetSocketAddress(host, SicenetConfig.proxyPort)
            ))
        }
        return builder.build()
    }

    private val xmlMediaType = "text/xml; charset=utf-8".toMediaType()
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private fun post(soapAction: String, body: String): String {
        val client = buildClient()
        val url = "${SicenetConfig.baseUrl}/ws/wsalumnos.asmx"
        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "text/xml; charset=utf-8")
            .addHeader("SOAPAction", "\"http://tempuri.org/$soapAction\"")
            .addHeader("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 14)")
            .post(body.toRequestBody(xmlMediaType))
            .build()
        return client.newCall(request).execute().use { resp ->
            val code = resp.code
            val text = resp.body?.string() ?: ""
            if (code == 403) throw Exception("BLOCKED_403")
            text
        }
    }

    // ─── Parseo ─────────────────────────────────────────────

    private fun limpiarJson(xml: String) = xml
        .replace("&quot;", "\"").replace("&lt;", "<")
        .replace("&gt;", ">").replace("\\\"", "\"").trim()

    private fun extraerJson(xml: String, ini: String, fin: String): String {
        val s = xml.indexOf(ini); val e = xml.lastIndexOf(fin)
        return if (s != -1 && e != -1) xml.substring(s, e + 1)
        else if (ini == "[") "[]" else "{}"
    }

    /**
     * Detecta si la respuesta indica login exitoso.
     * IGUAL que Android: loginResponse.contains("true")
     * Funciona aunque haya namespace prefix en el tag XML.
     */
    private fun esLoginExitoso(response: String): Boolean {
        // Mismo criterio que Android LoginWorker: contains("true")
        return response.contains("true", ignoreCase = false)
    }

    private inline fun <reified T> parseList(xml: String): List<T> = try {
        val js = limpiarJson(extraerJson(xml, "[", "]"))
        if (js == "[]" || !js.contains("{")) emptyList()
        else json.parseToJsonElement(js).jsonArray.map { json.decodeFromJsonElement(it) }
    } catch (e: Exception) { emptyList() }

    private inline fun <reified T> parseObject(xml: String): T? = try {
        val js = limpiarJson(extraerJson(xml, "{", "}"))
        if (!js.contains("{")) null else json.decodeFromString<T>(js)
    } catch (e: Exception) { null }

    // ─── API ────────────────────────────────────────────────

    /**
     * Login. Usa String.format() igual que Android.
     * La contraseña NO se modifica (case-sensitive, acepta caracteres especiales).
     */
    suspend fun acceso(matricula: String, contrasenia: String, tipo: String = "ALUMNO"): Boolean {
        val body = bodyAcceso.format(matricula, contrasenia, tipo)
        val response = post("accesoLogin", body)
        return esLoginExitoso(response)
    }

    suspend fun getPerfil(matricula: String, contrasenia: String): ProfileStudent? =
        parseObject(post("getAlumnoAcademicoWithLineamiento",
            bodyAlumno.format(matricula, contrasenia)))

    suspend fun getKardex(lineamiento: String = "1"): List<KardexItem> =
        parseList(post("getAllKardexConPromedioByAlumno", bodyKardex.format(lineamiento)))

    suspend fun getCalifUnidades(): List<CalificacionParcial> =
        parseList(post("getCalifUnidadesByAlumno", bodyCalifUnidades))

    suspend fun getCalifFinales(modoEducativo: Int = 1): List<CalificacionFinal> =
        parseList(post("getAllCalifFinalByAlumnos", bodyCalifFinales.format(modoEducativo)))

    suspend fun getCargaAcademica(): List<MateriaCarga> =
        parseList(post("getCargaAcademicaByAlumno", bodyCargaAcademica))

    fun clearCookies() = cookieManager.cookieStore.removeAll()
}
