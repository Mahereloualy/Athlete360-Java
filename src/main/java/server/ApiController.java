package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import dao.AthleteDAO;
import model.Athlete;
import service.RecommendationService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

public class ApiController {

    private static final AthleteDAO dao = new AthleteDAO();
    private static final Gson gson = new Gson();

    public static void handle(HttpExchange e) throws IOException {

        String path = e.getRequestURI().getPath();
        String method = e.getRequestMethod();

        if (path.equals("/api/athletes")) {
            handleAthletes(e, method);
            return;
        }

        if (path.equals("/api/stats")) {
            handleStats(e);
            return;
        }

        if (path.equals("/api/weights")) {
            handleWeights(e);
            return;
        }

        if (path.equals("/api/recommendations")) {
            handleRecommendations(e);
            return;
        }

        if (path.equals("/api/sports")) {
            handleSports(e);
            return;
        }

        if (path.equals("/api/seed")) {
            dao.seed(20);
            WebServer.ok(e);
            return;
        }

        WebServer.notFound(e);
    }

    private static void handleAthletes(HttpExchange e, String method) throws IOException {

        if (method.equals("GET")) {
            String json = gson.toJson(dao.findAll());
            WebServer.json(e, json);
        }

        if (method.equals("POST")) {
            String body = new String(e.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Athlete a = gson.fromJson(body, Athlete.class);

            // Re-construct with ID if needed, or assume default constructor
            // Since original code parsed manually and set ID:
            Athlete newAthlete = new Athlete(
                    dao.count() + 1,
                    a.name,
                    a.age,
                    a.height,
                    a.weight,
                    a.sport);

            dao.insert(newAthlete);
            WebServer.ok(e);
        }

        if (method.equals("DELETE")) {
            String query = e.getRequestURI().getQuery();
            if (query != null && query.startsWith("id=")) {
                int id = Integer.parseInt(query.substring(3));
                dao.delete(id);
            }
            WebServer.ok(e);
        }
    }

    private static void handleStats(HttpExchange e) throws IOException {
        Map<String, Object> stats = Map.of(
                "total", dao.count(),
                "avgWeight", dao.avgWeight(),
                "avgImc", dao.avgImc());
        WebServer.json(e, gson.toJson(stats));
    }

    private static void handleWeights(HttpExchange e) throws IOException {
        WebServer.json(e, gson.toJson(dao.weightSeries()));
    }

    private static void handleRecommendations(HttpExchange e) throws IOException {
        var recs = dao.findAll().stream()
                .map(RecommendationService::recommend)
                .collect(Collectors.toList());
        WebServer.json(e, gson.toJson(recs));
    }

    private static void handleSports(HttpExchange e) throws IOException {
        WebServer.json(e, gson.toJson(dao.getSportsDistribution()));
    }
}
