package de.uriegel.synctopc

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

suspend fun uploadFile(urlString: String, file: File) {
    return withContext(Dispatchers.IO) {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doInput = true

        val inputStream = file.inputStream()
        inputStream.copyTo(connection.outputStream, 8000)

        val result = connection.responseCode
        if (result != 200)
            throw java.lang.Exception("$result ${connection.responseMessage}")
    }
}


private fun readStream(inString: InputStream): String {
    val response = StringBuffer()
    val reader = BufferedReader(InputStreamReader(inString))
    var line: String?
    while (reader.readLine().also { line = it } != null)
        response.append(line)
    reader.close()
    return response.toString()
}