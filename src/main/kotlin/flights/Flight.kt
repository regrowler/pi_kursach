package flights

import DbWorker
import DbWorker.Companion.connection
import DbWorker.Companion.schemaName
import com.google.gson.annotations.SerializedName
import com.sun.net.httpserver.HttpExchange
import org.joda.time.DateTime
import utils.gson
import utils.parseDateTime
import utils.use
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Serializable
import java.sql.Connection
import java.sql.Date
import java.sql.Timestamp
import java.util.*

data class Flight(
    @SerializedName("id") val id: Int,
    @SerializedName("plane_id") val planeId: Int,
    @SerializedName("departure_time") val departureTime: String,
    @SerializedName("arrival_time") val arrivalTime: String,
    @SerializedName("destination_id") val destinationId: Int,
    @SerializedName("departure_id") val departureId: Int
) : Serializable {
    fun save() {
        DbWorker.saveFlight(this)
    }

    companion object {
        fun readFlight(httpExchange: HttpExchange): Flight {
            val req = String(httpExchange.requestBody.readBytes())
            return gson.fromJson<Flight>(req, Flight::class.java)
        }


        fun deleteFlight(id: Int) {
            DbWorker.deleteFlight(id)
        }

        fun deleteFlights(dateTime: DateTime) {
            DbWorker.deleteFlights(dateTime)
        }

    }
}

fun DbWorker.Companion.prepareFlightsTable(connection: Connection) {
    connection.prepareStatement(
        "create table $schemaName.flights" +
                "(" +
                "id serial not null," +
                "plane_id int not null," +
                "departure_time timestamp not null," +
                "arrival_time timestamp not null," +
                "destination_id int not null," +
                "departure_id int not null" +
                ");" +
                "create unique index flights_id_uindex " +
                "on $schemaName.flights (id); " +
                "alter table $schemaName.flights " +
                "add constraint flights_pk " +
                "primary key (id);"
    ).execute()
}

private fun DbWorker.Companion.saveFlight(flight: Flight) {
    connection.use {
        it.prepareStatement(
            "insert into $schemaName.flights (" +
                    "   plane_id, " +
                    "   departure_time, " +
                    "   arrival_time, " +
                    "   destination_id, " +
                    "   departure_id) " +
                    "VALUES (" +
                    "   ${flight.planeId}," +
                    "   '${Timestamp(parseDateTime(flight.departureTime).millis)}'," +
                    "   '${Timestamp(parseDateTime(flight.arrivalTime).millis)}'," +
                    "   ${flight.destinationId}," +
                    "   ${flight.departureId});"
        ).execute()

    }
}

private fun DbWorker.Companion.deleteFlight(flightId: Int) {
    connection.use {
        it.prepareStatement("delete from main_schema.flights where id=$flightId;").execute()
    }
}

private fun DbWorker.Companion.deleteFlights(dateTime: DateTime) {
    connection.use {
        it.prepareStatement("delete from main_schema.flights where arrival_time::date ='${Date(dateTime.millis)}';")
            .execute()
    }
}
