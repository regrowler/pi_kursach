package schedules

import cities.City
import flights.Flight
import org.joda.time.DateTime
import utils.gson
import utils.printDateTime
import utils.toDateTime
import utils.use
import java.sql.Date

class Schedule {
    companion object {
        fun schedule(date: DateTime): String {
            var t = gson.toJson(DbWorker.getSchedule(date))
            return t
        }
    }
}

private fun DbWorker.Companion.getSchedule(date: DateTime): List<Flight> {
    var flights = ArrayList<Flight>()
    val query="select *from $schemaName.flights where arrival_time::date ='${Date(date.millis)}';"
    connection.use {
        var res =
            it.prepareStatement(query)
                .executeQuery()
        while (res.next()) {
            flights.add(
                Flight(
                    id = res.getInt(1),
                    planeId = res.getInt(2),
                    departureTime = res.getTimestamp(3).toDateTime().printDateTime(),
                    arrivalTime = res.getTimestamp(4).toDateTime().printDateTime(),
                    destinationId = res.getInt(5),
                    departureId = res.getInt(6)
                )
            )
        }
    }
    return flights
}