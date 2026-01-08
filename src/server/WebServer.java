package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import dao.AthleteDAO;
import model.Athlete;
import service.DataGeneratorService;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class WebServer {

    private static final String SITE_DIR = "src/resources/site/";

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        // ===== STATIC PAGES =====
        server.createContext("/", e -> {
            try { serve(e, "home.html"); }
            catch (Exception ex) { error(e, ex); }
        });

        server.createContext("/athletes", e -> {
            try { serve(e, "index.html"); }
            catch (Exception ex) { error(e, ex); }
        });

        server.createContext("/dashboard", e -> {
            try { serve(e, "dashboard.html"); }
            catch (Exception ex) { error(e, ex); }
        });

        server.createContext("/recommendations", e -> {
            try { serve(e, "recommendations.html"); }
            catch (Exception ex) { error(e, ex); }
        });

        // ===== STATIC FILES =====
        server.createContext("/style.css", e -> {
            try { serveFile(e, "style.css", "text/css"); }
            catch (Exception ex) { error(e, ex); }
        });

        server.createContext("/script.js", e -> {
            try { serveFile(e, "script.js", "application/javascript"); }
            catch (Exception ex) { error(e, ex); }
        });

        // ===== API : GENERATE DATA =====
        server.createContext("/api/generate-data", e -> {
            try {
                if ("POST".equalsIgnoreCase(e.getRequestMethod())) {
                    DataGeneratorService gen = new DataGeneratorService();
                    gen.generate(30);
                    send(e, "{\"ok\":true}");
                } else {
                    send(e, "{\"error\":\"POST only\"}");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                error(e, ex);
            }
        });

        // ===== API : DASHBOARD =====
        server.createContext("/api/dashboard/extended", e -> {
            try {
                AthleteDAO dao = new AthleteDAO();
                List<Athlete> list = dao.findAll();

                int total = list.size();
                double avgIMC = 0;
                double avgWeight = 0;

                for (Athlete a : list) {
                    double imc = a.getWeight() / (a.getHeight() * a.getHeight());
                    avgIMC += imc;
                    avgWeight += a.getWeight();
                }

                if (total > 0) {
                    avgIMC /= total;
                    avgWeight /= total;
                }

                String json = "{"
                        + "\"total\":" + total + ","
                        + "\"avgIMC\":" + round(avgIMC) + ","
                        + "\"avgWeight\":" + round(avgWeight)
                        + "}";

                send(e, json);

            } catch (Exception ex) {
                ex.printStackTrace();
                error(e, ex);
            }
        });

        server.start();
        System.out.println("✅ Athlete360 running on http://localhost:8000");
    }

    // ===== UTILITIES =====

    private static void serve(HttpExchange e, String file) throws Exception {
        serveFile(e, file, "text/html");
    }

    private static void serveFile(HttpExchange e, String file, String type) throws Exception {
        byte[] data = Files.readAllBytes(Path.of(SITE_DIR + file));
        e.getResponseHeaders().set("Content-Type", type);
        e.sendResponseHeaders(200, data.length);
        e.getResponseBody().write(data);
        e.close();
    }

    private static void send(HttpExchange e, String json) throws Exception {
        byte[] b = json.getBytes(StandardCharsets.UTF_8);
        e.getResponseHeaders().set("Content-Type", "application/json");
        e.sendResponseHeaders(200, b.length);
        e.getResponseBody().write(b);
        e.close();
    }

    private static void error(HttpExchange e, Exception ex) {
        try {
            ex.printStackTrace();
            e.sendResponseHeaders(500, -1);
            e.close();
        } catch (Exception ignored) {}
    }

    private static double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
