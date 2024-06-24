package com.example.collectionreceiver;

import com.example.collectionreceiver.Controller.Controller;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class DataCollectionReceiverApplication {

    private final static String queueName = "RECEIVER";
    private final static String broker = "localhost";

    public static void main(String[] args) throws IOException, TimeoutException {
        Controller receiverController = new Controller();
        receiverController.run(queueName, broker);
    }

}
