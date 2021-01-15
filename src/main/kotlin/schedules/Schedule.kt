package schedules

import cities.City
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.annotations.SerializedName
import com.sun.net.httpserver.HttpExchange
import flights.Flight
import org.joda.time.DateTime
import planes.Plane
import utils.*
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Serializable
import java.lang.Exception
import java.sql.Date
import kotlin.math.abs

data class Schedule(
    @SerializedName("date") val date: String,
    @SerializedName("flights") val flights: List<Flight>
) : Serializable {
    fun save() {
        check()
        Flight.deleteFlights(parseDate(date))
        flights.forEach { it.save() }
    }

    companion object {
        fun readSchedule(httpExchange: HttpExchange): Schedule {
            val req= String(httpExchange.requestBody.readBytes())
            var schedule = gson.fromJson<Schedule>(req, Schedule::class.java)
            val dateTime = parseDate(schedule.date)
            val flights = schedule.flights.map {
                var arrivalTime = parseTime(it.arrivalTime)
                arrivalTime = DateTime(
                    dateTime.year,
                    dateTime.monthOfYear,
                    dateTime.dayOfMonth,
                    arrivalTime.hourOfDay,
                    arrivalTime.minuteOfHour
                )
                var departureTime = parseTime(it.departureTime)
                departureTime = DateTime(
                    dateTime.year,
                    dateTime.monthOfYear,
                    dateTime.dayOfMonth,
                    departureTime.hourOfDay,
                    departureTime.minuteOfHour
                )
                it.copy(
                    arrivalTime = arrivalTime.printDateTime(),
                    departureTime = departureTime.printDateTime()
                )
            }
            schedule = schedule.copy(
                flights = flights
            )
            return schedule
        }

        fun schedule(date: DateTime): String {
            val jsonObject = JsonObject()
            jsonObject.add("date", JsonPrimitive(date.printDate()))
            jsonObject.add("flights", gson.toJsonTree(DbWorker.getSchedule(date)))
            return jsonObject.toString()
        }
    }
}

private fun Schedule.check() {
    var right = true
    val cities = City.getCitiesObjects()
    val planes = Plane.getPlanesObjects()
    var currentCityId = cities.find { it.distance == 0 } ?: throw Exception("текущий город не установлен")
    var arrivalFlights = flights.filter { it.destinationId == currentCityId.id }
    var departureFlights = flights.filter { it.departureId == currentCityId.id }
    for (i in arrivalFlights.indices) {
        arrivalFlights.forEachIndexed { index, flight ->
            if (index != i) {
                var d1 = parseDateTime(flight.arrivalTime)
                var d2 = parseDateTime(arrivalFlights[i].arrivalTime)
                val diff = abs(d1.millis - d2.millis) / 1000.0 / 60.0
                if (diff < 20) right = false
            }
        }
    }
    if (!right) throw Exception("прибывающие рейсы в расписании пересекаются по времени")
    for (i in departureFlights.indices) {
        departureFlights.forEachIndexed { index, flight ->
            if (index != i) {
                var d1 = parseDateTime(flight.departureTime)
                var d2 = parseDateTime(departureFlights[i].departureTime)
                val diff = abs(d1.millis - d2.millis) / 1000.0 / 60.0
                if (diff < 20) right = false
            }
        }
    }
    if (!right) throw Exception("отбывающие рейсы в расписании пересекаются по времени")
    for (i in flights.indices) {
        val d1 = cities.find { it.id == flights[i].departureId }
            ?: throw Exception("город с id =${flights[i].departureId} не найден")
        val d2 = cities.find { it.id == flights[i].destinationId }
            ?: throw Exception("город с id =${flights[i].destinationId} не найден")
        val distance = abs(d1.distance - d2.distance)
        val plane =
            planes.find { it.id == flights[i].planeId } ?: throw Exception("самолет с id =${flights[i].planeId} не найден")
        if (distance > plane.distance) throw Exception("для рейса ${i+1} расстояние между городами превышает дальность полета самолета")
    }
}

private fun DbWorker.Companion.getSchedule(date: DateTime): List<Flight> {
    var flights = ArrayList<Flight>()
    val query = "select *from $schemaName.flights where arrival_time::date ='${Date(date.millis)}';"
    connection.use {
        var res =
            it.prepareStatement(query)
                .executeQuery()
        while (res.next()) {
            flights.add(
                Flight(
                    id = res.getInt(1),
                    planeId = res.getInt(2),
                    departureTime = res.getTimestamp(3).toDateTime().printTime(),
                    arrivalTime = res.getTimestamp(4).toDateTime().printTime(),
                    destinationId = res.getInt(5),
                    departureId = res.getInt(6)
                )
            )
        }
    }
    return flights
}

fun DbWorker.Companion.deleteFlightsByPlaneId(id:Int){
    connection.use {
        it.prepareStatement("delete " +
                "from  $schemaName.flights " +
                "where plane_id = $id;").execute()
    }
}

fun DbWorker.Companion.deleteFlightsByCityId(id:Int){
    connection.use {
        it.prepareStatement("delete " +
                "from $schemaName.flights " +
                "where departure_id=$id or destination_id=$id;").execute()
    }
}