package cities

import com.google.gson.annotations.SerializedName
import com.sun.net.httpserver.HttpExchange
import flights.Flight
import utils.gson
import utils.use
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Serializable
import java.sql.Connection
import java.sql.Timestamp

data class City(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("distance") val distance: Int
) : Serializable {
    fun save() {
        DbWorker.saveCity(this)
    }

    companion object {
        fun readCity(httpExchange: HttpExchange): City {
            val req= String(httpExchange.requestBody.readBytes())
            return gson.fromJson(req,City::class.java)
        }

        fun getCities(): String = gson.toJson(DbWorker.getCities())
        fun getCitiesObjects(): List<City> = DbWorker.getCities()
        fun deleteCity(id: Int) {
            DbWorker.deleteCity(id)
        }
    }
}

internal fun DbWorker.Companion.prepareCitiesTable(connection: Connection) {
    connection.prepareStatement(
        "create table $schemaName.cities\n" +
                "(" +
                "id serial not null," +
                "name varchar(50) not null," +
                "distance int not null" +
                ");" +
                "create unique index cities_id_uindex " +
                "on $schemaName.cities(id); " +
                "alter table $schemaName.cities " +
                "add constraint city_pk " +
                "primary key (id);"
    ).execute()
}

private fun DbWorker.Companion.getCities(): List<City> {
    var cities = ArrayList<City>()
    connection.use {
        var res = it.prepareStatement("select * from $schemaName.cities;").executeQuery()
        while (res.next()) {
            cities.add(
                City(
                    id = res.getInt(1),
                    name = res.getString(2),
                    distance = res.getInt(3)
                )
            )
        }
    }
    return cities
}

private fun DbWorker.Companion.saveCity(city: City) {
    connection.use {
        if (city.id == 0) {
            it.prepareStatement(
                "insert into $schemaName.cities (" +
                        "   name, " +
                        "   distance) " +
                        "VALUES (" +
                        "   '${city.name}'," +
                        "   ${city.distance});"
            ).execute()
        } else {
            it.prepareStatement(
                "update $schemaName.cities set " +
                        "name='${city.name}'," +
                        "distance=${city.distance} " +
                        "where id=${city.id};"
            ).execute()
        }
    }
}

private fun DbWorker.Companion.deleteCity(id: Int) {
    connection.use {
        it.prepareStatement("delete from $schemaName.cities where id=$id;").execute()
    }
}