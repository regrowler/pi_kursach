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
        private const val dbIp="140.82.36.93"
        private const val databaseName = "flexair"
        internal const val schemaName = "main_schema"
        private const val baseUrl = "jdbc:postgresql://$dbIp:5432/"
        private const val databaseUrl = "jdbc:postgresql://$dbIp:5432/$databaseName"

        val connection: Connection
            get() {
                return getConnection()
            }

        private fun getConnection(url: String = databaseUrl): Connection {
            val user = "postgres"
//            val passwd = "123456"
            val passwd =
                "0L|K7_n/f#T7%(hKPVo\\)9i?I^=Fa*5_L4(B;04Ma\\I[Fp&*U3++,8^I(qoAPUPaQ.Yp%5dXVk5gE\$}H&=:%I2;9gbg3e6kQl=WjkMr~U.\"W_o:XIsBdm~h|w\\sX_wjM"
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