package com.example.stationdatacollector;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.example.stationdatacollector.StationDataCollectorController;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

public class Collector {

    public static void listenForMessages(String queueName, String brokerAddress, StationDataCollectorController controller) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(brokerAddress);
        factory.setPort(30003);
//https://www.rabbitmq.com/tutorials/tutorial-two-java
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare("sendID", "direct");
        channel.queueDeclare(queueName, false, false, false, null);

        System.out.println(" [x] Listening on queue '" + queueName + "' at broker '" + brokerAddress + "'");

        channel.queueBind(queueName, "sendID", queueName);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received message: " + message);

            controller.processStationData(message);
        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
        System.out.println(" [x] Consumer started and waiting for messages...");
    }
}
