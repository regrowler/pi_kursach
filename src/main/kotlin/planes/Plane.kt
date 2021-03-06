package planes

import com.google.gson.annotations.SerializedName
import com.sun.net.httpserver.HttpExchange
import schedules.deleteFlightsByPlaneId
import utils.gson
import utils.use
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Serializable
import java.lang.Exception
import java.sql.Connection

data class Plane(
    @SerializedName("id") val id: Int,
    @SerializedName("make") val make: String,
    @SerializedName("distance") val distance: Int,
    @SerializedName("seats_number") val seatsNumber: Int,
    @SerializedName("tank_volume") val tankVolume: Int,
    @SerializedName("load_capacity") val loadCapacity: Int
) : Serializable {
    fun save() {
        if (!DbWorker.checkIfPlaneExits(make))
            DbWorker.savePlane(this)
        else throw Exception("Самолет уже существует")
    }

    fun update(){
        DbWorker.savePlane(this)
    }

    companion object {
        fun readPlane(httpExchange: HttpExchange): Plane {
            val req = String(httpExchange.requestBody.readBytes())
            return gson.fromJson<Plane>(req, Plane::class.java)
        }

        fun getPlanes(): String = gson.toJson(DbWorker.getPlanes())
        fun getPlanesObjects(): List<Plane> = DbWorker.getPlanes()
        fun deletePlane(id: Int) {
            DbWorker.deleteFlightsByPlaneId(id)
            DbWorker.deletePlane(id)
        }
    }
}

internal fun DbWorker.Companion.preparePlanesTable(connection: Connection) {
    connection.prepareStatement(
        "create table $schemaName.planes" +
                "(" +
                "id serial not null," +
                "make varchar(50) not null," +
                "distance int not null," +
                "seats_number int not null," +
                "tank_volume int not null," +
                "load_capacity int not null" +
                ");" +
                "create unique index planes_id_uindex " +
                "on $schemaName.planes (id); " +
                "alter table $schemaName.planes " +
                "add constraint planes_pk " +
                "primary key (id);"
    ).execute()
}

private fun DbWorker.Companion.getPlanes(): List<Plane> {
    var planes = ArrayList<Plane>()
    connection.use {
        var res = it.prepareStatement("select * from $schemaName.planes;").executeQuery()
        while (res.next()) {
            planes.add(
                Plane(
                    id = res.getInt(1),
                    make = res.getString(2),
                    distance = res.getInt(3),
                    seatsNumber = res.getInt(4),
                    tankVolume = res.getInt(5),
                    loadCapacity = res.getInt(6)
                )
            )
        }
    }
    return planes
}

private fun DbWorker.Companion.savePlane(plane: Plane) {
    connection.use {
        if (plane.id == 0) {
            it.prepareStatement(
                "insert into $schemaName.planes (" +
                        "   make, " +
                        "   distance," +
                        "   seats_number," +
                        "   tank_volume," +
                        "   load_capacity) " +
                        "VALUES (" +
                        "   '${plane.make}'," +
                        "   ${plane.distance}," +
                        "   ${plane.seatsNumber}," +
                        "   ${plane.tankVolume}," +
                        "   ${plane.loadCapacity});"
            ).execute()
        } else {
            it.prepareStatement(
                "update $schemaName.planes set " +
                        "make='${plane.make}'," +
                        "distance=${plane.distance}, " +
                        "seats_number=${plane.seatsNumber}," +
                        "tank_volume=${plane.tankVolume}," +
                        "load_capacity=${plane.loadCapacity}" +
                        "where id=${plane.id};"
            ).execute()
        }
    }
}

private fun DbWorker.Companion.deletePlane(id: Int) {
    connection.use {
        it.prepareStatement("delete from $schemaName.planes where id=$id;").execute()
    }
}

private fun DbWorker.Companion.checkIfPlaneExits(name: String): Boolean {
    var flag = false
    connection.use {
        var res = it.prepareStatement("select 1 from $schemaName.planes where make='$name';").executeQuery()
        flag = res.next()
    }
    return flag
}