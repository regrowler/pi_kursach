package utils

import com.sun.net.httpserver.HttpExchange
import java.io.OutputStreamWriter


fun HttpExchange.sendResponse(code: Int, response: String) {
    var length = response.takeIf { it.isNotEmpty() }?.toByteArray()?.size?.toLong() ?: 0L
    responseHeaders.add("Content-type", "text/json")
    sendResponseHeaders(code, length)
    val os = OutputStreamWriter(responseBody)
    os.write(response)
    os.close()
}

fun HttpExchange.sendResponse(code: Int) {
    sendResponse(code, "")
}