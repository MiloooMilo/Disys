package com.example.communication;

import com.example.controller.PDFController;
import com.itextpdf.text.DocumentException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.nio.charset.StandardCharsets;
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
        channel.exchangeDeclare("kwh", "direct");
        channel.queueDeclare(queueName, false, false, false, null);

        System.out.println("Dispatcher listening to  " + queueName);
        channel.queueBind(queueName, "kwh", queueName);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
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
