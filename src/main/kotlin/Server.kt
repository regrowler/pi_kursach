import com.sun.net.httpserver.HttpServer
import handlers.FirstEndPointHandler
import java.net.InetSocketAddress

class Server {
    fun listenForAuth(state: String) {
        val server = HttpServer.create(InetSocketAddress(portNumber), 0)
        server.createContext("/$firstEndPoint", FirstEndPointHandler())
        server.executor = null // creates a default executor
        server.start()
    }
}