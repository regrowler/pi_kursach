package utils

import com.sun.net.httpserver.HttpExchange
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.Charset


fun HttpExchange.sendResponse(code: Int, response: String) {
    var length = response.takeIf { it.isNotEmpty() }?.toByteArray()?.size?.toLong() ?: 0L
    responseHeaders.add("Content-type", "application/json;charset=UTF-8")
    responseHeaders.add("Access-Control-Allow-Origin", "*")
    responseHeaders.add("Access-Control-Allow-Credentials", "true")
    responseHeaders.add("Access-Control-Allow-Methods", "GET,HEAD,OPTIONS,POST,PUT")
    responseHeaders.add(
        "Access-Control-Allow-Headers",
        "Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, content-type, Access-Control-Request-Method, Access-Control-Request-Headers"
    )
    sendResponseHeaders(code, length)
    val os = OutputStreamWriter(responseBody, Charset.forName("UTF8"))
    os.write(response)
    os.close()
}

fun HttpExchange.sendResponse(code: Int) {
    sendResponse(code, "")
}