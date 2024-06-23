package com.example.collectionreceiver.Controller;

import com.example.collectionreceiver.communication.Consumer;
import com.example.collectionreceiver.communication.Producer;
import com.example.collectionreceiver.model.Charge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Controller {

    private static String broker;
    private static ArrayList<Charge> charges = new ArrayList<>();

    public void run(String queueName, String brokerUrl) throws IOException, TimeoutException {
        broker = brokerUrl;
        Consumer.receive(queueName, brokerUrl);
    }

    public static void sendDataForInvoice(String customerID, String msg){
        if(msg.toLowerCase().equals("start")) {
            Producer.send("start", customerID, "PDFGENERATOR", broker);
            System.out.println("Yarak0" + customerID);
        } else if (msg.toLowerCase().equals("end")) {
            send(customerID);
            System.out.println("Yarak1" + customerID);
        } else {
            Charge charge = new Charge(msg, customerID);
            charges.add(charge);
            System.out.println("Yarak2" + customerID);
        }
    }

    public static List<Charge> getCharges(){
        return charges;
    }

    private static void send(String customerID){
        ArrayList<Charge> chargesForSpecificCustomerID = new ArrayList<>();
        for (Charge charge : charges) {
            if(charge.getCustomerID().equals(customerID))
                chargesForSpecificCustomerID.add(charge);
        }

        for (int i = 0; i < chargesForSpecificCustomerID.size(); i++) {
            Charge charge = chargesForSpecificCustomerID.get(i);
            Producer.send(charge.getKwh(), customerID, "PDFGENERATOR", broker);
        }

        // After sending all charges, send the "end" message
        Producer.send("end", customerID, "PDFGENERATOR", broker);

        // Remove processed charges from the main list
        charges.removeAll(chargesForSpecificCustomerID);
    }
}
