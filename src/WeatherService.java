
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class WeatherService {

    private final HttpClient client = HttpClient.newHttpClient();

    private String fetchData(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                System.out.println("Weather API error: "
                        + response.statusCode());
                return null;
            }

            return response.body();
        } catch (Exception e) {
            System.out.println("Error connecting to weather API.");
            return null;
        }
    }

    public String getWeatherData(String city) {
        String apiKey = System.getenv("OPENWEATHER_API_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            System.out.println("OPENWEATHER_API_KEY is not set.");
            return null;
        }

        String encodedCity =
                URLEncoder.encode(city, StandardCharsets.UTF_8);

        String url =
                "https://api.openweathermap.org/data/2.5/weather"
                + "?q=" + encodedCity
                + "&appid=" + apiKey
                + "&units=metric";

        return fetchData(url);
    }

    public String getForecastData(String city) {
        String apiKey = System.getenv("OPENWEATHER_API_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            System.out.println("OPENWEATHER_API_KEY is not set.");
            return null;
        }

        String encodedCity =
                URLEncoder.encode(city, StandardCharsets.UTF_8);

        String url =
                "https://api.openweathermap.org/data/2.5/forecast"
                + "?q=" + encodedCity
                + "&appid=" + apiKey
                + "&units=metric";

        return fetchData(url);
    }
}

