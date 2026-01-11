package app;

public class App {
    public static void main(String[] args) throws Exception {
        dao.DatabaseSetup.init();
        server.WebServer.start();
        System.out.println("Athlete360 running on http://localhost:8000");
    }
}
