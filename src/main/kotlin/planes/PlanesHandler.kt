package planes

import HTTPMethod
import cities.City
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import utils.sendResponse
import java.lang.Exception

class PlanesHandler :HttpHandler {
    override fun handle(httpExchange: HttpExchange) {
        when (HTTPMethod(httpExchange.requestMethod)) {
            HTTPMethod.POST -> processPost(httpExchange)
            HTTPMethod.PUT -> processPut(httpExchange)
            HTTPMethod.DELETE -> processDelete(httpExchange)
            HTTPMethod.GET -> processGet(httpExchange)
            else->httpExchange.sendResponse(200)
        }
    }

    private fun processPost(httpExchange: HttpExchange) {
        try {
            val plane = Plane.readPlane(httpExchange)
            plane.save()
            httpExchange.sendResponse(201)
        } catch (e: Exception) {
            httpExchange.sendResponse(422, e.localizedMessage)
        }

    }

    private fun processPut(httpExchange: HttpExchange) {
        try {
            var id = httpExchange.requestURI.path.split("/").last().toInt()
            var plane = Plane.readPlane(httpExchange)
            plane = plane.copy(
                id = id
            )
            plane.update()
            httpExchange.sendResponse(200)
        } catch (e: Exception) {
            httpExchange.sendResponse(422, e.localizedMessage)
        }
    }

    private fun processDelete(httpExchange: HttpExchange) {
        try {
            val id = httpExchange.requestURI.path.split("/").last().toInt()
           Plane.deletePlane(id)
            httpExchange.sendResponse(204)
        } catch (e: Exception) {
            httpExchange.sendResponse(500, e.localizedMessage)
        }
    }

    private fun processGet(httpExchange: HttpExchange) {
        try {
            httpExchange.sendResponse(200, Plane.getPlanes())
        } catch (e: Exception) {
            httpExchange.sendResponse(500, e.localizedMessage)
        }
    }
}