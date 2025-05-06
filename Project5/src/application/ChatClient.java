/*Jett Kopalek
 * 
 * CSCI400
 * 
 * This program is the client side for a client server messaging service
 * 
 * */

package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient extends Application {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private TextField usernameField;
    private TextArea messagesArea;
    private TextField messageField;
    private TextField ipField;
    private TextField portField;
    private Button connectButton;
    private Button disconnectButton;

    @Override
    public void start(Stage primaryStage) {
        usernameField = new TextField("User");
        usernameField.setPromptText("Username");

        ipField = new TextField("localhost");
        ipField.setPromptText("Server IP");

        portField = new TextField("5990");
        portField.setPromptText("Server Port");

        messagesArea = new TextArea();
        messagesArea.setEditable(false);

        messageField = new TextField();
        messageField.setPromptText("Enter message");

        connectButton = new Button("Connect");
        disconnectButton = new Button("Disconnect");
        disconnectButton.setDisable(true);

        connectButton.setOnAction(e -> connect());
        disconnectButton.setOnAction(e -> disconnect());
        messageField.setOnAction(e -> sendMessage());

        VBox root = new VBox(10,
            new VBox(5, new Label("Username:"), usernameField),
            new VBox(5, new Label("Server IP:"), ipField),
            new VBox(5, new Label("Server Port:"), portField),
            messagesArea,
            messageField,
            connectButton,
            disconnectButton
        );
        root.setPadding(new Insets(10));
        VBox.setVgrow(messagesArea, Priority.ALWAYS);

        Scene scene = new Scene(root, 300, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Socket Client");
        primaryStage.show();
    }

    private void connect() {
        String user = usernameField.getText();
        String serverAddress = ipField.getText();
        int serverPort;
        try {
            serverPort = Integer.parseInt(portField.getText());
        } catch (NumberFormatException e) {
            messagesArea.appendText("Invalid port number\n");
            return;
        }
        new Thread(() -> {
            try {
                socket = new Socket(serverAddress, serverPort);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Send username to server
                out.println(user);

                Platform.runLater(() -> {
                    connectButton.setDisable(true);
                    disconnectButton.setDisable(false);
                    usernameField.setDisable(true);
                    ipField.setDisable(true);
                    portField.setDisable(true);
                    messagesArea.appendText("Connected as " + user + " to " + serverAddress + ":" + serverPort + "\n");
                });

                String response;
                while ((response = in.readLine()) != null) {
                    String finalResponse = response;
                    Platform.runLater(() -> messagesArea.appendText(finalResponse + "\n"));
                }
            } catch (IOException e) {
                Platform.runLater(() -> messagesArea.appendText("Connection error: " + e.getMessage() + "\n"));
            }
        }).start();
    }

    private void sendMessage() {
        if (out != null) {
            String msg = messageField.getText();
            out.println(msg);
            messagesArea.appendText(usernameField.getText() + ": " + msg + "\n");
            messageField.clear();
        }
    }

    private void disconnect() {
        try {
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
        Platform.runLater(() -> {
            connectButton.setDisable(false);
            disconnectButton.setDisable(true);
            usernameField.setDisable(false);
            ipField.setDisable(false);
            portField.setDisable(false);
            messagesArea.appendText("Disconnected\n");
        });
    }

    @Override
    public void stop() {
        disconnect();
    }

    public static void main(String[] args) {
        launch(args);
    }
}