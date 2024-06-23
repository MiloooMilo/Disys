package com.example.stationdatacollector;


import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class StationDataCollectorApplication {
    private final static String QUEUE_NAME = "COLLECTOR";
    private final static String BROKER = "localhost";

    public static void main(String[] args) {
        try {
            StationDataCollectorController collectorController = new StationDataCollectorController();
            collectorController.run(QUEUE_NAME, BROKER);
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}