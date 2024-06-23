package com.example.stationdatacollector;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class StationDataCollectorController {

    private final DataCollectionService dataCollectionService;
    private String broker;

    public StationDataCollectorController() {
        this.dataCollectionService = new DataCollectionService();
    }

    public void run(String queueName, String brokerUrl) throws IOException, TimeoutException {
        this.broker = brokerUrl;
        Collector.listenForMessages(queueName, brokerUrl, this);
    }

    public void processStationData(String message) {
        try {
            // Split the message to get customerId and stationDbUrl
            String[] parts = message.split(";");
            String customerId = parts[0];
            String stationDbUrl = parts[1];

            // Check if the message is "end"
            if (stationDbUrl.equals("end")) {
                // Send "end" message to the RECEIVER queue
                Sender.sendMessage("end", customerId, "RECEIVER", broker);
                return;
            }

            // Validierung der customerId
            int customerIdInt = Integer.parseInt(customerId);
            if (customerIdInt >= 4) {
                System.err.println(" [!] Invalid customer ID: " + customerId + ". Customer ID must be less than 4.");
                return; // Beende die Verarbeitung, wenn die customerId ungültig ist
            }

            // Collect data from the station's database
            List<String> chargingData = dataCollectionService.collectDataFromStationDb(stationDbUrl, customerId);

            // Send the collected data to the RECEIVER queue
            String dataMessage = String.join(",", chargingData);
            Sender.sendMessage(dataMessage, customerId, "RECEIVER", broker);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println(" [!] Invalid customer ID format: " + message);
        }
    }
}
