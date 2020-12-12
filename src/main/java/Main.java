public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
        System.out.println("Server started on port " + server.getPort());
    }
}
