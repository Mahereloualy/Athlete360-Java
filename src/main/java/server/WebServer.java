package server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;

public class WebServer {

    public static void start() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        server.createContext("/", e -> serveFile(e, "home.html"));
        server.createContext("/athletes", e -> serveFile(e, "athletes.html"));
        server.createContext("/dashboard", e -> serveFile(e, "dashboard.html"));
        server.createContext("/recommendations", e -> serveFile(e, "recommendations.html"));

        // Static assets
        server.createContext("/style.css", e -> serveFile(e, "style.css"));
        server.createContext("/app.js", e -> serveFile(e, "app.js"));

        server.createContext("/api", ApiController::handle);
        server.createContext("/site", WebServer::staticFile);

        server.start();
    }

    private static void serveFile(HttpExchange e, String file) throws IOException {
        Path p = Path.of("src/main/resources/site/" + file);
        if (Files.exists(p)) {
            String mime = "text/html";
            if (file.endsWith(".css"))
                mime = "text/css";
            else if (file.endsWith(".js"))
                mime = "application/javascript";

            e.getResponseHeaders().add("Content-Type", mime);
            byte[] data = Files.readAllBytes(p);
            e.sendResponseHeaders(200, data.length);
            e.getResponseBody().write(data);
        } else {
            e.sendResponseHeaders(404, 0);
        }
        e.close();
    }

    private static void staticFile(HttpExchange e) throws IOException {
        Path p = Path.of("src/main/resources" + e.getRequestURI().getPath());
        byte[] data = Files.readAllBytes(p);
        e.sendResponseHeaders(200, data.length);
        e.getResponseBody().write(data);
        e.close();
    }

    public static void json(HttpExchange e, String json) throws IOException {
        e.getResponseHeaders().add("Content-Type", "application/json");
        e.sendResponseHeaders(200, json.length());
        e.getResponseBody().write(json.getBytes());
        e.close();
    }

    public static void ok(HttpExchange e) throws IOException {
        e.sendResponseHeaders(200, 0);
        e.close();
    }

    public static void notFound(HttpExchange e) throws IOException {
        e.sendResponseHeaders(404, 0);
        e.close();
    }
}
