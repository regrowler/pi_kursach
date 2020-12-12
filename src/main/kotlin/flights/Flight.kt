package flights

import com.google.gson.annotations.SerializedName
import utils.gson
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Serializable
import java.util.*

data class Flight(
    @SerializedName("id") val id: Int,
    @SerializedName("plane_id") val planeId: Int,
    @SerializedName("departure_time") val departureTime: Date,
    @SerializedName("arrival_time") val arrivalTime: Date,
    @SerializedName("destination_id") val destinationId: Int,
    @SerializedName("departure_id") val departureId: Int
) : Serializable {
    fun save() {
        DbWorker.saveFlight(this)
    }

    companion object {
        fun readFlight(inputStream: InputStream): Flight =
            gson.fromJson<Flight>(InputStreamReader(inputStream), Flight::class.java)

        fun deleteFlight(id: Int) {

        }
    }
}
//{
//    "plane_id": 123,
//    "departure_time": "08:35",
//    "arrival_time": "09:35",
//    "destination_id": 123,
//    "departure_id": 123
//}