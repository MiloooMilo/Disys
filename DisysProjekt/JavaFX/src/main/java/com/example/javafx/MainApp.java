package com.example.javafx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Timer;
import java.util.TimerTask;

public class MainApp extends Application {

    @FXML
    private TextField customerIdField;

    @FXML
    private Label statusLabel;

    @FXML
    private Button generateInvoice;

    @FXML
    private Button downloadPdfButton;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private Timer timer;
    private String currentCustomerId = "";

    private static final String PDF_DIRECTORY = "../PDF_Files/";

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javafx/Customer.fxml"));
        loader.setController(this); // Set this class as the controller
        Parent root = loader.load();

        // Set up the primary stage
        primaryStage.setTitle("Invoice Generator");
        primaryStage.setScene(new Scene(root, 400, 200));
        primaryStage.show();

        // Set the button action for generateInvoice
        generateInvoice.setOnAction(event -> {
            String customerId = customerIdField.getText().trim();
            if (!customerId.isEmpty() && !customerId.equals(currentCustomerId)) {
                currentCustomerId = customerId;
                stopStatusCheck();
                sendPostRequest(customerId);
            } else {
                statusLabel.setText("Please enter a different customer ID.");
            }
        });

        // Set the button action for downloadPdfButton
        downloadPdfButton.setOnAction(event -> {
            String pdfFilePath = PDF_DIRECTORY + "Invoice_" + currentCustomerId + ".pdf";
            downloadPDF(pdfFilePath);
        });
    }

    private void sendPostRequest(String customerId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/invoices/" + customerId))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        Platform.runLater(() -> {
                            System.out.println("POST Response: " + response.body());
                            statusLabel.setText("Invoice request sent for Customer ID: " + customerId);
                        });
                        if (response.statusCode() == 200) {
                            // Delay before starting to check the status
                            timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    startStatusCheck(customerId);
                                }
                            }, 10000); // 10 seconds delay before first check
                        } else {
                            Platform.runLater(() -> statusLabel.setText("Failed to create PDF for customer ID: " + customerId));
                        }
                    }).exceptionally(e -> {
                        Platform.runLater(() -> statusLabel.setText("Error: " + e.getMessage()));
                        e.printStackTrace();
                        return null;
                    });
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Platform.runLater(() -> statusLabel.setText("Invalid URI: " + e.getMessage()));
        }
    }

    private void startStatusCheck(String customerId) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(new URI("http://localhost:8080/invoices/" + customerId))
                            .GET()
                            .build();

                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                            .thenAccept(response -> {
                                String invoicePath = response.body();
                                Platform.runLater(() -> System.out.println("GET Response: " + response.body()));
                                if (!invoicePath.startsWith("PDF file not found")) {
                                    Platform.runLater(() -> {
                                        statusLabel.setText("Invoice is ready for Customer ID: " + customerId);
                                        downloadPdfButton.setVisible(true); // Show button when PDF exists
                                    });
                                    stopStatusCheck();
                                } else {
                                    Platform.runLater(() -> {
                                        statusLabel.setText("PDF not yet created for ID: " + customerId);
                                        downloadPdfButton.setVisible(false); // Hide button when PDF does not exist
                                    });
                                }
                            }).exceptionally(e -> {
                                Platform.runLater(() -> statusLabel.setText("Error: " + e.getMessage()));
                                e.printStackTrace();
                                return null;
                            });
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    Platform.runLater(() -> statusLabel.setText("Invalid URI: " + e.getMessage()));
                }
            }
        }, 0, 5000); // Check every 5 seconds
    }

    private void downloadPDF(String localFilePath) {
        try {
            File pdfFile = new File(localFilePath);

            if (pdfFile.exists()) {
                Desktop.getDesktop().open(pdfFile);
                Platform.runLater(() -> statusLabel.setText("PDF opened: " + localFilePath));
            } else {
                Platform.runLater(() -> statusLabel.setText("PDF file not found at: " + localFilePath));
            }
        } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> statusLabel.setText("Error opening PDF: " + e.getMessage()));
        }
    }

    private void stopStatusCheck() {
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public void stop() throws Exception {
        stopStatusCheck();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}