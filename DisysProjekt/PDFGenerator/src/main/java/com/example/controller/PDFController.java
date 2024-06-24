package com.example.controller;

import com.itextpdf.text.DocumentException;
import com.example.communication.Consumer;
import com.example.model.Charge;
import com.example.model.Customer;
import com.example.service.CustomerService;
import com.example.service.PdfGeneratorService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.Arrays;

public class PDFController {
    private static String broker;
    private static String dburlCustomer = "localhost:30001";
    private static ArrayList<Charge> charges = new ArrayList<Charge>();
    private static Customer customer = null;

    public void run(String queueName, String brokerUrl) throws IOException, TimeoutException {
        broker = brokerUrl;
        Consumer.receive(queueName, brokerUrl);
    }

    public static void generatePdf(String customerId, String msg) throws IOException, TimeoutException, SQLException, DocumentException {
        if (msg.toLowerCase().equals("start")) {
            System.out.println("start: " + customerId);
            customer = CustomerService.getCustomerData(customerId, dburlCustomer);
        } else if (msg.toLowerCase().equals("end")) {
            PdfGeneratorService psg = new PdfGeneratorService();
            psg.generate(charges, customer);
            removeChargedFromList(charges, customer);
        } else {
            try {
                // Split the message by commas and parse each part as a double
                List<Double> kwhValues = Arrays.stream(msg.split(","))
                        .map(Double::parseDouble)
                        .collect(Collectors.toList());
                for (double kwh : kwhValues) {
                    Charge charge = new Charge(kwh, Integer.parseInt(customerId));
                    charges.add(charge);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                System.out.println("Error parsing kwh value: " + msg);
            }
        }
    }

    private static void removeChargedFromList(ArrayList<Charge> charges, Customer customer) {
        charges.removeIf(charge -> charge.getCustomerID() == customer.getCustomerId());
    }
}
