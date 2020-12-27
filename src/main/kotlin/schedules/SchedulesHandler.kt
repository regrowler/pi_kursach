package schedules

import HTTPMethod
import cities.City
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import planes.Plane
import utils.parseDate
import utils.sendResponse
import java.lang.Exception

class SchedulesHandler : HttpHandler {
    override fun handle(httpExchange: HttpExchange) {
        when (HTTPMethod(httpExchange.requestMethod)) {
            HTTPMethod.GET -> processGet(httpExchange)
            HTTPMethod.POST -> processPost(httpExchange)
            else->httpExchange.sendResponse(200)
        }
    }

    private fun processGet(httpExchange: HttpExchange) {
        try {
            var date = parseDate(httpExchange.requestURI.path.split("/").last())

            httpExchange.sendResponse(200, Schedule.schedule(date))
        } catch (e: Exception) {
            httpExchange.sendResponse(500, e.localizedMessage)
        }
    }

    private fun processPost(httpExchange: HttpExchange) {
        try {
            val schedule = Schedule.readSchedule(httpExchange)
            schedule.save()
            httpExchange.sendResponse(201)
        } catch (e: Exception) {
            httpExchange.sendResponse(422, e.localizedMessage)
        }

    }
}