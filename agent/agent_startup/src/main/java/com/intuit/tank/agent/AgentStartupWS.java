package com.intuit.tank.agent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AgentStartupWS implements Runnable {
    private static final Logger logger = LogManager.getLogger(AgentStartupWS.class);

    private final int port;
    private final String token;

    public AgentStartupWS (int port, String token ) {
        this.port = port;
        this.token = token;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Server started. Listening on port {}...", port);

            // Wait for a client to connect and accept the connection
            Socket clientSocket = serverSocket.accept();
            logger.info("Client connected from: {}", clientSocket.getInetAddress().getHostAddress());

            // Get input stream to read data from the client
            DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());

            // Read the message sent by the client
            String message = inputStream.readUTF();
            logger.info("Message received from client: {}", message);

            // Close the client socket
            clientSocket.close();
            System.out.println("Client connection closed.");

        } catch (IOException e) {
            logger.error("Server exception: {}", e.getMessage(), e);
        }
    }
}
