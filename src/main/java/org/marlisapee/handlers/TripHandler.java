package org.marlisapee.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.marlisapee.models.TripWithUser;
import org.marlisapee.models.User;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TripHandler implements HttpHandler {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/SoloNoMo-db";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathComponents = path.split("/");

        System.out.println("Method: " + method);
        System.out.println("Path: " + path);
        System.out.println("Path components: " + Arrays.toString(pathComponents));

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if ("GET".equals(method)) {
                handleGet(exchange, pathComponents, conn);
            } else {
                sendResponse(exchange, "Method not supported", 405);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sendResponse(exchange, "Internal server error", 500);
        }
    }

    private void handleGet(HttpExchange exchange, String[] pathComponents, Connection conn) throws IOException, SQLException {
        // /trips/user/{userId}
        if (pathComponents.length == 4 && "user".equals(pathComponents[2])) {
            int userId = Integer.parseInt(pathComponents[3]);
            List<TripWithUser> trips = getAllUsersTrips(userId, conn);
            if (!trips.isEmpty()) {
                JSONArray jsonArray = new JSONArray(trips);
                sendResponse(exchange, jsonArray.toString(), 200);
            } else {
                sendResponse(exchange, "No trips found for user", 404);
            }
        } else {
            // Handle /trips
            List<TripWithUser> trips = getAllTrips(conn);
            JSONArray jsonArray = new JSONArray(trips);
            sendResponse(exchange, jsonArray.toString(), 200);
        }
    }

    private List<TripWithUser> getAllTrips(Connection conn) throws SQLException {
        List<TripWithUser> trips = new ArrayList<>();
        String query = "SELECT trips.id AS trip_id, trips.user_id, trips.destination, trips.start_date, trips.end_date, trips.description, " +
                "users.first_name, users.last_name, users.email, users.profile_picture, COUNT(user_trips.id) AS trip_count " +
                "FROM trips " +
                "JOIN users ON trips.user_id = users.id " +
                "LEFT JOIN trips user_trips ON users.id = user_trips.user_id " +
                "GROUP BY trips.id, users.id";
        try (PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                TripWithUser tripWithUser = new TripWithUser(
                        rs.getInt("trip_id"),
                        rs.getInt("user_id"),
                        rs.getString("destination"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getString("description"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("profile_picture"),
                        rs.getInt("trip_count")
                );
                trips.add(tripWithUser);
            }
        }
        return trips;
    }

    private Optional<User> getUserById(int id, Connection conn) throws SQLException {
        return UserHandler.getUserById(id, conn);
    }

    private List<TripWithUser> getAllUsersTrips(int userId, Connection conn) throws SQLException {
        List<TripWithUser> trips = new ArrayList<>();
        String query = "SELECT trips.id AS trip_id, trips.user_id, trips.destination, trips.start_date, trips.end_date, trips.description, " +
                "users.first_name, users.last_name, users.email, users.profile_picture, COUNT(user_trips.id) AS trip_count " +
                "FROM trips " +
                "JOIN users ON trips.user_id = users.id " +
                "LEFT JOIN trips user_trips ON users.id = user_trips.user_id " +
                "WHERE trips.user_id = ? " +
                "GROUP BY trips.id, users.id, trips.user_id, trips.destination, trips.start_date, trips.end_date, trips.description, users.first_name, users.last_name, users.email, users.profile_picture";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                TripWithUser tripWithUser = new TripWithUser(
                        rs.getInt("trip_id"),
                        rs.getInt("user_id"),
                        rs.getString("destination"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getString("description"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("profile_picture"),
                        rs.getInt("trip_count")
                );
                trips.add(tripWithUser);
            }
        }
        return trips;
    }

    private void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}

