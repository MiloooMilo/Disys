package com.example.collectionreceiver.Controller;

import com.example.collectionreceiver.communication.Consumer;
import com.example.collectionreceiver.communication.Producer;
import com.example.collectionreceiver.model.Charge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class Controller {

    private static String broker;
    private static ArrayList<Charge> charges = new ArrayList<>();

    public void run(String queueName, String brokerUrl) throws IOException, TimeoutException {
        broker = brokerUrl;
        Consumer.receive(queueName, brokerUrl);
    }

    public static void sendDataForInvoice(String customerID, String msg){
        if(msg.equalsIgnoreCase("start")) {
            Producer.send("start", customerID, "PDFGENERATOR", broker);
        } else if (msg.equalsIgnoreCase("end")) {
            send(customerID);
        } else {
            Charge charge = new Charge(msg, customerID);
            charges.add(charge);
        }
    }

    private static void send(String customerID){
        ArrayList<Charge> chargesForSpecificCustomerID = new ArrayList<>();
        for (Charge charge : charges) {
            if(charge.getCustomerID().equals(customerID))
                chargesForSpecificCustomerID.add(charge);
        }

        for (Charge charge : chargesForSpecificCustomerID) {
            Producer.send(charge.getKwh(), customerID, "PDFGENERATOR", broker);
        }


        // After sending all charges, send the "end" message
        Producer.send("end", customerID, "PDFGENERATOR", broker);

        // Remove processed charges from the main list
        charges.removeAll(chargesForSpecificCustomerID);
    }
}
