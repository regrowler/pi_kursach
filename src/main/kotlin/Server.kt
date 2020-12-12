import com.sun.net.httpserver.HttpServer
import flights.Flight
import flights.FlightsHandler
import utils.gson
import java.net.InetSocketAddress
import java.sql.Timestamp
import java.util.*

class Server {
    val port = portNumber

    fun startServer() {
        DbWorker.initializeDataBase()
        val server = HttpServer.create(InetSocketAddress(port), 0)
        server.createContext(flightsEndPoint, FlightsHandler())
        server.executor = null // creates a default executor
        server.start()
    }
}