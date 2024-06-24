package com.example;

import com.example.controller.PDFController;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class PdfGeneratorApplication {

	private final static String queueName = "PDFGENERATOR";
	private final static String broker = "localhost";

	public static void main(String[] args) throws IOException, TimeoutException {
		PDFController controller = new PDFController();
		controller.run(queueName, broker);
	}

}