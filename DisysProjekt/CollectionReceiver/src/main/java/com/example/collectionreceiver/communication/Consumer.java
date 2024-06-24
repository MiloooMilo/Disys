package com.example.collectionreceiver.communication;

import java.nio.charset.StandardCharsets;
import com.example.collectionreceiver.Controller.Controller;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer {

    public static void receive(String queueName, String brokerUrl) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(brokerUrl);
        factory.setPort(30003);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare("kwh", "direct");
            channel.queueDeclare(queueName, false, false, false, null);

            System.out.println("Dispatcher listening to  '" + queueName + "'");
            channel.queueBind(queueName, "kwh", queueName);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("Received: " + message);
                String[] msg = message.split(";");
                Controller.sendDataForInvoice(msg[0], msg[1]);
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});

            // Prevent the try-with-resources from closing the resources immediately
            synchronized (connection) {
                connection.wait();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Thread interrupted", e);
        }
    }
}
