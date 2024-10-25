import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.google.gson.Gson;


/**
 * Servlet implementation class WeatherServlet
 */
@WebServlet("/WeatherServlet")
public class WeatherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WeatherServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    private static final String API_KEY = "34aad624e11e3f75c964e6efc48d3d43"; // Replace with your actual API key
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("WeatherServlet: doGet method started");

        String lat = request.getParameter("lat");
        String lon = request.getParameter("lon");
        System.out.println("WeatherServlet: Requested coordinates: lat=" + lat + ", lon=" + lon);

        String apiUrl = "https://api.openweathermap.org/data/2.5/weather" +
                        "?lat=" + lat +
                        "&lon=" + lon +
                        "&appid=" + API_KEY;

        System.out.println("WeatherServlet: API URL (without key): " + apiUrl.replace(API_KEY, "API_KEY_HIDDEN"));

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            System.out.println("WeatherServlet: API response code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Set response content type
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                // Read the API response and write it directly to the servlet response
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                     PrintWriter out = response.getWriter()) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        out.println(inputLine);
                    }
                }

                System.out.println("WeatherServlet: API response forwarded to client");
            } else {
                System.out.println("WeatherServlet: API request failed. Response Code: " + responseCode);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Error fetching weather data");
            }

        } catch (Exception e) {
            System.out.println("WeatherServlet: Exception occurred: " + e.getMessage());
            e.printStackTrace(System.out);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error processing weather data");
        }
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("WeatherServlet: doPost method started");

        // Parse the JSON request
        JsonObject requestBody = new Gson().fromJson(request.getReader(), JsonObject.class);
        String lat = requestBody.get("lat").getAsString();
        String lon = requestBody.get("lon").getAsString();
        boolean isLoggedIn = requestBody.has("isLoggedIn") && requestBody.get("isLoggedIn").getAsBoolean();
        int userId = requestBody.has("userId") ? requestBody.get("userId").getAsInt() : -1;  // Assume userId is passed in request if logged in
        
        // Fetch the weather data
        String weatherData = fetchWeatherData(lat, lon);

        if (isLoggedIn && userId != -1) {
            saveSearchQuery(userId, lat, lon);
        }

        // Respond with weather data
        PrintWriter pw = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        pw.write(weatherData);
        pw.flush();
	}
	
	/**
     * Fetch weather data from OpenWeather API using latitude and longitude.
     */
    private String fetchWeatherData(String lat, String lon) throws IOException {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + API_KEY;

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
            return response.toString();
        } else {
            throw new IOException("Error fetching weather data. Response Code: " + responseCode);
        }
    }
    
    /**
     * Save the search query to the database if the user is logged in.
     */
    private void saveSearchQuery(int userId, String lat, String lon) {
        try (Connection conn = DBConnection.initializeDatabase()) {
            String insertQuery = "INSERT INTO searches (user_id, search_query, timestamp) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertQuery);
            stmt.setInt(1, userId);
            stmt.setString(2, "lat=" + lat + ", lon=" + lon);
            stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            stmt.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
