
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class WeatherWebServer {

    public static void main(String[] args) throws IOException {

        WeatherService weatherService = new WeatherService();

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0);

        // 1. Send the HTML frontend to the browser
        server.createContext("/", exchange -> {
            try {
                String html = Files.readString(
                        Path.of("src", "index.html"),
                        StandardCharsets.UTF_8);

                send(exchange, 200, "text/html; charset=UTF-8", html);

            } catch (IOException e) {
                send(exchange, 500, "text/plain; charset=UTF-8",
                        "Could not read src/index.html. Check that the file exists.");
            }
        });

        // 2. Connect browser requests to the Java weather service
        server.createContext("/api/weather", exchange -> {

            if (!exchange.getRequestMethod().equals("GET")) {
                send(exchange, 405, "application/json; charset=UTF-8",
                        "{\"error\":\"GET requests only\"}");
                return;
            }

            String city = "Chennai";
            String query = exchange.getRequestURI().getRawQuery();

            if (query != null) {
                for (String part : query.split("&")) {
                    String[] pair = part.split("=", 2);

                    if (pair.length == 2 && pair[0].equals("city")) {
                        city = URLDecoder.decode(
                                pair[1], StandardCharsets.UTF_8);
                    }
                }
            }

            try {
                String current = weatherService.getWeatherData(city);
                String forecast = weatherService.getForecastData(city);

                if (current == null || forecast == null) {
                    send(exchange, 502,
                            "application/json; charset=UTF-8",
                            "{\"error\":\"Weather data unavailable. Check your API key and connection.\"}");
                    return;
                }

                String json = "{\"current\":" + current
                        + ",\"forecast\":" + forecast + "}";

                send(exchange, 200,
                        "application/json; charset=UTF-8", json);

            } catch (Exception e) {
                send(exchange, 502,
                        "application/json; charset=UTF-8",
                        "{\"error\":\"Unable to retrieve weather data.\"}");
            }
        });

        server.start();

        System.out.println(
                "Weather website running at http://localhost:8080");
        System.out.println("Keep this terminal open.");
    }

    private static void send(
            HttpExchange exchange,
            int status,
            String contentType,
            String body) throws IOException {

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }
}
