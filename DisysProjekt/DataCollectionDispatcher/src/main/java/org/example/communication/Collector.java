package org.example.communication;


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

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare("sendID", "direct");
        channel.queueDeclare(queueName, false, false, false, null);

        System.out.println(" [x] Listening on queue '" + queueName + "' at broker '" + brokerAddress + "'");

        channel.queueBind(queueName, "sendID", queueName);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received message: " + message);

            try {
                dispatcher.startDispatching(message);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
        System.out.println(" [x] Consumer started and waiting for messages...");
    }
}

