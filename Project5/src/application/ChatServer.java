/*Jett Kopalek
 * 
 * CSCI400
 * 
 * This program is the server side for a client server messaging service
 * 
 * */

package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer extends Application {
    private static final int PORT = 5990;
    private ServerSocket serverSocket;
    private ObservableList<ClientHandler> clients;
    private TextArea logArea;

    @Override
    public void start(Stage primaryStage) {
        // TextArea for server logs
        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefHeight(150);

        // ListView of connected clients
        clients = FXCollections.observableArrayList();
        ListView<ClientHandler> clientListView = new ListView<>(clients);

        // Buttons
        Button dropSelected = new Button("Drop Selected");
        Button dropAll = new Button("Drop All");
        Button exitBtn = new Button("Exit");
        dropSelected.setMaxWidth(Double.MAX_VALUE);
        dropAll.setMaxWidth(Double.MAX_VALUE);
        exitBtn.setMaxWidth(Double.MAX_VALUE);

        dropSelected.setOnAction(e -> {
            ClientHandler sel = clientListView.getSelectionModel().getSelectedItem();
            if (sel != null) sel.disconnect();
        });
        dropAll.setOnAction(e -> {
            List<ClientHandler> copy = new ArrayList<>(clients);
            copy.forEach(ClientHandler::disconnect);
        });
        exitBtn.setOnAction(e -> {
            stopServer();
            Platform.exit();
        });

        HBox buttonBox = new HBox(10, dropSelected, dropAll, exitBtn);
        buttonBox.setPadding(new Insets(10));
        HBox.setHgrow(dropSelected, Priority.ALWAYS);
        HBox.setHgrow(dropAll, Priority.ALWAYS);
        HBox.setHgrow(exitBtn, Priority.ALWAYS);
        dropSelected.setMinWidth(100);
        dropAll.setMinWidth(100);
        exitBtn.setMinWidth(100);

        // Spacer region
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        VBox.setVgrow(clientListView, Priority.ALWAYS);

        // Layout
        VBox root = new VBox(10, logArea, clientListView, spacer, buttonBox);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 400, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chat Server");
        primaryStage.show();

        // Start server thread
        new Thread(this::startServer).start();
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            log("Server started on port " + PORT);
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket);
                handler.start();
            }
        } catch (IOException e) {
            log("Server error: " + e.getMessage());
        }
    }

    private void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
            List<ClientHandler> copy = new ArrayList<>(clients);
            copy.forEach(ClientHandler::disconnect);
            log("Server stopped.");
        } catch (IOException e) {
            log("Error shutting down: " + e.getMessage());
        }
    }

    private void broadcast(String message) {
        log("Broadcast: " + message);
        List<ClientHandler> copy = new ArrayList<>(clients);
        copy.forEach(client -> {
            if (!client.send(message)) {
                client.disconnect();
            }
        });
    }

    private void log(String msg) {
        System.out.println(msg);
        Platform.runLater(() -> logArea.appendText(msg + "\n"));
    }

    private class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private String username;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Read username and add to UI
                username = in.readLine();
                Platform.runLater(() -> clients.add(this));
                broadcast(username + " has joined the chat");
                log(username + " connected.");

                // Read messages
                String msg;
                while ((msg = in.readLine()) != null) {
                    broadcast(username + ": " + msg);
                }
            } catch (IOException e) {
                log("Error with " + username + ": " + e.getMessage());
            } finally {
                disconnect();
            }
        }

        boolean send(String message) {
            if (out != null) {
                out.println(message);
                return true;
            }
            return false;
        }

        void disconnect() {
            try {
                if (username != null) broadcast(username + " has left the chat");
                Platform.runLater(() -> clients.remove(this));
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException ignored) {}
        }

        @Override
        public String toString() {
            return username;
        }
    }

    @Override
    public void stop() throws Exception {
        stopServer();
        super.stop();
    }
}