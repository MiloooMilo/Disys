package org.example;

import org.example.controller.DataDispatcherController;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class DataCollectionDispatcherApplication {
    private final static String QUEUE_NAME = "DISPATCHER";
    private final static String BROKER = "localhost";


    public static void main(String[] args) {
        try {
            DataDispatcherController dispatcherController = new DataDispatcherController();
            dispatcherController.run(QUEUE_NAME, BROKER);
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

}
