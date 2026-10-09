import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("=================================");
        System.out.println("           WEATHER APP");
        System.out.println("=================================");

        System.out.print("Enter a city name: ");
        String city = scanner.nextLine().trim();

        if (city.isEmpty()) {
            System.out.println("Please enter a city name.");
            scanner.close();
            return;
        }

        WeatherService service = new WeatherService();
        String data = service.getWeatherData(city);

        if (data == null) {
            System.out.println("Unable to retrieve weather for " + city);
            scanner.close();
            return;
        }

        try {
            double temperature = Double.parseDouble(
                data.split("\"temp\":")[1].split(",")[0]
            );

            double feelsLike = Double.parseDouble(
                data.split("\"feels_like\":")[1].split(",")[0]
            );

            int humidity = Integer.parseInt(
                data.split("\"humidity\":")[1].split(",")[0]
            );

            double windSpeed = Double.parseDouble(
                data.split("\"speed\":")[1].split("[,}]")[0]
            );

            String conditions = data
                .split("\"description\":\"")[1]
                .split("\"")[0];

            System.out.println("\nWeather in " + city + ":");
            System.out.printf("1. Temperature: %.2f°C%n", temperature);
            System.out.printf("2. Feels like: %.2f°C%n", feelsLike);
            System.out.println("3. Humidity: " + humidity + "%");
            System.out.printf("4. Wind speed: %.2f m/s%n", windSpeed);
            System.out.println("5. Conditions: " + conditions);

        } catch (Exception e) {
            System.out.println("Could not format the weather data.");
        }

        scanner.close();
    }
}
