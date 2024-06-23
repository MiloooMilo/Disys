package com.example.pdfgenerator.communication;

import com.itextpdf.text.DocumentException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.example.pdfgenerator.controller.PDFController;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

public class Consumer {

    public static void receive(String queueName, String brokerUrl) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(brokerUrl);
        factory.setPort(30003);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare("fuel", "direct");
        channel.queueDeclare(queueName, false, false, false, null);

        System.out.println(" [x] Dispatcher listening to  '" + queueName + "'");
        channel.queueBind(queueName, "fuel", queueName);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            String[] msg = message.split(";");
            System.out.println("Received: " + msg[0] + " " + msg[1]);
            try {
                PDFController.generatePdf(msg[0], msg[1]);
            } catch (TimeoutException | SQLException | DocumentException e) {
                e.printStackTrace();
            }
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
    }
}
