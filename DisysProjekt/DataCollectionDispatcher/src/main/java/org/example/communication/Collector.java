package org.example.communication;

import java.nio.charset.StandardCharsets;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.example.controller.DataDispatcherController;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

public class Collector {

    public static void listenForMessages(String queueName, String brokerAddress, DataDispatcherController dispatcher) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(brokerAddress);
        factory.setPort(30003);
//https://www.rabbitmq.com/tutorials/tutorial-one-java
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare("sendID", "direct");
        channel.queueDeclare(queueName, false, false, false, null);

        System.out.println("Listening to '" + queueName + "' at broker '" + brokerAddress + "'");

        channel.queueBind(queueName, "sendID", queueName);
//https://www.rabbitmq.com/tutorials/tutorial-two-java
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("Received message: " + message);

            try {
                dispatcher.startDispatching(message);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
        System.out.println("Consumer started and waiting for messages...");
    }
}

