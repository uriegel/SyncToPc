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
        connection.setFixedLengthStreamingMode(file.length())
        connection.doInput = true

        val inputStream = file.inputStream()
        inputStream.copyTo(connection.outputStream)
        inputStream.close()

        val result = connection.responseCode
        if (result != 200)
            throw java.lang.Exception("$result ${connection.responseMessage}")

        connection.inputStream.close()
    }
}

suspend fun sendeListe(urlString: String, liste: String) {
    return withContext(Dispatchers.IO) {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doInput = true

        val writer = BufferedWriter(OutputStreamWriter(connection.outputStream))
        writer.write(liste)
        writer.close()

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