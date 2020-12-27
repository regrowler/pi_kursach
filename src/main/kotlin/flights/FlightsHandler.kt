package flights


import HTTPMethod
import com.google.gson.GsonBuilder
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import utils.sendResponse
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception

class FlightsHandler : HttpHandler {
    override fun handle(httpExchange: HttpExchange) {
        when (HTTPMethod(httpExchange.requestMethod)) {
            HTTPMethod.POST -> processPost(httpExchange)
            HTTPMethod.PUT -> processPut(httpExchange)
            HTTPMethod.DELETE -> processDelete(httpExchange)
            else->httpExchange.sendResponse(200)
        }
    }

    private fun processPost(httpExchange: HttpExchange) {
        try {
            val flight = Flight.readFlight(httpExchange)
            flight.save()
            httpExchange.sendResponse(201)
        } catch (e: Exception) {
            httpExchange.sendResponse(422, e.localizedMessage)
        }

    }

    private fun processPut(httpExchange: HttpExchange) {
        try {
            var id = httpExchange.requestURI.path.split("/").last().toInt()
            var flight = Flight.readFlight(httpExchange)
            flight = flight.copy(
                id = id
            )
            flight.save()
            httpExchange.sendResponse(200)
        } catch (e: Exception) {
            httpExchange.sendResponse(422, e.localizedMessage)
        }
    }

    private fun processDelete(httpExchange: HttpExchange) {
        try {
            val id = httpExchange.requestURI.path.split("/").last().toInt()
            Flight.deleteFlight(id)
            httpExchange.sendResponse(204)
        } catch (e: Exception) {
            httpExchange.sendResponse(500, e.localizedMessage)
        }
    }
}