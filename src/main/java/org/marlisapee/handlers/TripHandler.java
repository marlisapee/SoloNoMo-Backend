package org.marlisapee.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.marlisapee.models.Trip;
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

        System.out.println(STR."Method: \{method}");
        System.out.println(STR."Path: \{path}");
        System.out.println(STR."Path components: \{Arrays.toString(pathComponents)}");

        try(Connection conn = DriverManager.getConnection(DB_URL)){
            if("GET".equals(method)){
                handleGet(exchange, pathComponents, conn);
            } else {
                sendResponse(exchange, "Method not supported", 405);
            }
        } catch(SQLException e) {
            e.printStackTrace();
            sendResponse(exchange, "Internal server error", 500);
        }
    }

    private void handleGet(HttpExchange exchange, String[] pathComponents, Connection conn) throws IOException, SQLException {
        // /trips/user/{userId}
        if (pathComponents.length == 4 && "user".equals(pathComponents[2])) {
            int userId = Integer.parseInt(pathComponents[3]);
            List<Trip> trips = getAllUsersTrips(userId, conn);
            if (!trips.isEmpty()) {
                JSONArray jsonArray = new JSONArray(trips);
                sendResponse(exchange, jsonArray.toString(), 200);
            } else {
                sendResponse(exchange, "No trips found for user", 404);
            }
        } else {
            // Handle /trips
            List<Trip> trips = getAllTrips(conn);
            JSONArray jsonArray = new JSONArray(trips);
            sendResponse(exchange, jsonArray.toString(), 200);
        }
    }

    private List<Trip> getAllTrips(Connection conn) throws SQLException {
        List<Trip> trips = new ArrayList<>();
        String query = "SELECT * FROM trips";
        try(PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()){
            while (rs.next()){
                Trip trip = new Trip(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("destination"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getString("description")
                        );
                trips.add(trip);
            }
        }
        return trips;
    }

    private Optional<User> getUserById(int id, Connection conn) throws SQLException {
        return UserHandler.getUserById(id, conn);
    }

    private List<Trip> getAllUsersTrips(int userId, Connection conn) throws SQLException {
        List<Trip> trips = new ArrayList<>();
        String query = "SELECT * FROM trips WHERE user_id = ?";
        try(PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                Trip trip = new Trip(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("destination"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getString("description"));
                trips.add(trip);
            }
        }
        return trips;
    }


    private void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()){
            os.write(response.getBytes());
        }
    }
}
