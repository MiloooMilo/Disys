package com.example.springbootapp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;

@RestController
public class Controller {
    private final static String broker = "localhost";
    private final static String queueName = "DISPATCHER";
    private static final String PDF_DIRECTORY = "PDF_Files";

    @PostMapping("/invoices/{customerId}")
    public ResponseEntity<String> create(@PathVariable String customerId) {
        System.out.println(customerId);
        Producer.send(customerId, queueName, broker);
        return ResponseEntity.ok("Invoice sent for Id: " + customerId);
    }

    @GetMapping("/invoices/{customerId}")
    public ResponseEntity<String> getInvoices(@PathVariable String customerId) throws FileNotFoundException {
        String pdfFilePath = Paths.get(PDF_DIRECTORY, "Invoice_" + customerId + ".pdf").toAbsolutePath().toString();
        File pdfFile = new File(pdfFilePath);
        System.out.println(pdfFilePath);

        if (!pdfFile.exists()) {
            return new ResponseEntity<>("PDF file not found for customer ID: " + customerId, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(pdfFilePath, HttpStatus.OK);
    }
}
