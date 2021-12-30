package de.uriegel.synctopc

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.GZIPInputStream

suspend fun getString(urlString: String): String {

    return withContext(Dispatchers.IO) {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("Accept-Encoding", "gzip")
        connection.connect()
        val result = connection.responseCode
        if (result != 200)
            throw Exception("$result ${connection.responseMessage}")
        val inStream =
            if (connection.contentEncoding == "gzip")
                GZIPInputStream(connection.inputStream)
            else
                connection.inputStream
        return@withContext readStream(inStream)
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