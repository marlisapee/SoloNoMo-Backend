package org.marlisapee;

import com.sun.net.httpserver.HttpServer;
import org.marlisapee.handlers.UserHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class SoloHttpServer {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/SoloNoMo-db";

    public static void main(String[] args) throws IOException, IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/users", new UserHandler());
//      server.createContext("/trips", new TripHandler());
        server.setExecutor(null);
        System.out.println(STR."server listening on port \{server.getAddress().getPort()}...");
        server.start();
    }
}
