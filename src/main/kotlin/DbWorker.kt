import flights.Flight
import org.postgresql.jdbc.TimestampUtils
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Timestamp
import java.util.*


class DbWorker {
    companion object {
        private const val databaseName = "flexair"
        private const val schemaName = "main_schema"
        private const val baseUrl = "jdbc:postgresql://localhost:5432/"
        private const val databaseUrl = "jdbc:postgresql://localhost:5432/$databaseName"

        val connection: Connection
            get() {
                return getConnection()
            }

        private fun getConnection(url: String = databaseUrl): Connection {
            val user = "postgres"
            val passwd = "123456"
            return DriverManager.getConnection(url, user, passwd)
        }

        private fun Connection.use(block: (connection: Connection) -> Unit) {
            block.invoke(this)
            close()
        }

        fun saveFlight(flight: Flight) {
            connection.use {
                if (flight.id == 0) {
                    it.prepareStatement(
                        "insert into $schemaName.flights (" +
                                "   plane_id, " +
                                "   departure_time, " +
                                "   arrival_time, " +
                                "   destination_id, " +
                                "   departure_id) " +
                                "VALUES (" +
                                "   ${flight.planeId}," +
                                "   '${Timestamp(flight.departureTime.time)}'," +
                                "   '${Timestamp(flight.arrivalTime.time)}'," +
                                "   ${flight.destinationId}," +
                                "   ${flight.departureId});"
                    ).execute()
                } else {
                    it.prepareStatement(
                        "update $schemaName.flights set " +
                                "plane_id=${flight.planeId}," +
                                "departure_time='${Timestamp(flight.departureTime.time)}'," +
                                "arrival_time='${Timestamp(flight.arrivalTime.time)}'," +
                                "destination_id=${flight.destinationId}," +
                                "departure_id=${flight.departureId} where id=${flight.id};"
                    ).execute()
                }
            }
        }

        fun initializeDataBase() {
            var stamp = Timestamp(Date().time)
            var stampV = stamp.toString()
            getConnection(baseUrl).use { con ->
                val resSet = con.prepareStatement(
                    "select exists(\n" +
                            " SELECT datname FROM pg_catalog.pg_database WHERE lower(datname) = lower('$databaseName')\n" +
                            ");"
                ).executeQuery()
                resSet.next()
                if (!resSet.getBoolean(1)) {
                    con.prepareStatement("create database $databaseName;").execute()
                    connection.use {
                        it.prepareStatement("create schema $schemaName;").execute()
                        it.prepareStatement(
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
                        it.prepareStatement(
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
                        it.prepareStatement(
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
                }

            }
        }
    }
}