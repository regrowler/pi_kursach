import com.sun.net.httpserver.HttpServer
import flights.FlightsHandler
import java.net.InetSocketAddress

class Server {
    val port = portNumber
    fun startServer(){
        val server = HttpServer.create(InetSocketAddress(port), 0)
        server.createContext(flightsEndPoint, FlightsHandler())
        server.executor = null // creates a default executor
        server.start()
    }
}