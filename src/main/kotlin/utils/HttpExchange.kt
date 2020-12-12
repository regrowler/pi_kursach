package utils

import com.sun.net.httpserver.HttpExchange
import java.io.OutputStream


fun HttpExchange.sendResponse(code: Int, response: String) {
    sendResponseHeaders(code, response.takeIf { it.isNotEmpty() }?.length?.toLong() ?: 0)
    val os: OutputStream = responseBody
    os.write(response.toByteArray())
    os.close()
}

fun HttpExchange.sendResponse(code: Int) {
    sendResponse(code, "")
}