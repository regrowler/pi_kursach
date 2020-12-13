import cities.prepareCitiesTable
import flights.Flight
import flights.prepareFlightsTable
import org.postgresql.jdbc.TimestampUtils
import planes.preparePlanesTable
import utils.use
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Timestamp
import java.util.*


class DbWorker {
    companion object {
        private const val databaseName = "flexair"
        internal const val schemaName = "main_schema"
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

        fun initializeDataBase() {
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
                        prepareCitiesTable(it)
                        preparePlanesTable(it)
                        prepareFlightsTable(it)
                    }
                }

            }
        }
    }
}