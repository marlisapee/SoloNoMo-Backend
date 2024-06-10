package org.marlisapee.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import org.marlisapee.models.User;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserHandler implements HttpHandler {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/SoloNoMo-db";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathComponents = path.split("/");

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if ("GET".equals(method)) {
                handleGet(exchange, pathComponents, conn);
            } else if ("POST".equals(method)) {
                handlePost(exchange, conn);
            } else if ("PUT".equals(method)) {
                handlePut(exchange, pathComponents, conn);
            } else if ("DELETE".equals(method)) {
                handleDelete(exchange, pathComponents, conn);
            } else {
                sendResponse(exchange, "Method not supported", 405);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sendResponse(exchange, "Internal server error", 500);
        }
    }

    private void handleGet(HttpExchange exchange, String[] pathComponents, Connection conn) throws IOException, SQLException {
        if(pathComponents.length == 3){
            int id = Integer.parseInt(pathComponents[2]);
            Optional<User> user = getUserById(id, conn);
            if(user.isPresent()){
                sendResponse(exchange, new JSONObject(user.get()).toString(), 200);
            } else {
                sendResponse(exchange, "User not found...", 404);
            }
        } else {
            List<User> users = getAllUsers(conn);
            JSONArray jsonArray = new JSONArray(users);
            sendResponse(exchange, jsonArray.toString(), 200);
        }
    }

    private void handlePost(HttpExchange exchange, Connection conn) throws IOException, SQLException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes());
        JSONObject json = new JSONObject(requestBody);
        String firstName = json.getString("firstName");
        String lastName = json.getString("lastName");
        String email = json.getString("email");
        String profilePicture = json.getString("profilePicture");
        String password = json.getString("password");
        String bio = json.getString("bio");
        User user = new User(firstName, lastName, email, profilePicture, password, bio);
        createUser(user, conn);
        sendResponse(exchange, "User created", 201);
    }


    private void handlePut(HttpExchange exchange, String[] pathComponents, Connection conn) throws IOException, SQLException {
        if (pathComponents.length == 3) {
            int id = Integer.parseInt(pathComponents[2]);
            Optional<User> userOptional = getUserById(id, conn);
            if (userOptional.isPresent()) {
                String requestBody = new String(exchange.getRequestBody().readAllBytes());
                JSONObject json = new JSONObject(requestBody);
                User user = userOptional.get();
                user.setFirstName(json.getString("firstName"));
                user.setLastName(json.getString("lastName"));
                user.setEmail(json.getString("email"));
                user.setProfilePicture(json.getString("profilePicture"));
                user.setPassword(json.getString("password"));
                user.setBio(json.getString("bio"));
                updateUser(user, conn);
                sendResponse(exchange, "User updated", 200);
            } else {
                sendResponse(exchange, "User not found", 404);
            }
        } else {
            sendResponse(exchange, "Invalid request", 400);
        }
    }

    private void handleDelete(HttpExchange exchange, String[] pathComponents, Connection conn) throws IOException, SQLException {
        if (pathComponents.length == 3) {
            int id = Integer.parseInt(pathComponents[2]);
            Optional<User> user = getUserById(id, conn);
            if(user.isPresent()) {
                deleteUser(id, conn);
                sendResponse(exchange, "User deleted", 200);
            } else {
                sendResponse(exchange, "user does not exist...", 404);
            }
        } else {
            sendResponse(exchange, "Invalid request", 400);
        }
    }

    private Optional<User> getUserById(int id, Connection conn) throws SQLException {
        String query = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User(rs.getInt("id"), rs.getString("first_name"), rs.getString("last_name"), rs.getString("email"), rs.getString("profile_picture"), rs.getString("password"), rs.getString("bio"));
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    private List<User> getAllUsers(Connection conn) throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                User user = new User(rs.getInt("id"), rs.getString("first_name"), rs.getString("last_name"), rs.getString("email"), rs.getString("profile_picture"), rs.getString("password"), rs.getString("bio"));
                users.add(user);
            }
        }
        return users;
    }

    private void createUser(User user, Connection conn) throws SQLException {
        String query = "INSERT INTO users (first_name, last_name, email, profile_picture, password, bio) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getProfilePicture());
            stmt.setString(5, user.getPassword());
            stmt.setString(6, user.getBio());
            stmt.executeUpdate();
        }
    }

    private void updateUser(User user, Connection conn) throws SQLException {
        String query = "UPDATE users SET first_name = ?, last_name = ?, email = ?, profile_picture = ?, password = ?, bio = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getProfilePicture());
            stmt.setString(5, user.getPassword());
            stmt.setString(6, user.getBio());
            stmt.setInt(7, user.getId());
            stmt.executeUpdate();
        }
    }

    private void deleteUser(int id, Connection conn) throws SQLException {
        String query = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
