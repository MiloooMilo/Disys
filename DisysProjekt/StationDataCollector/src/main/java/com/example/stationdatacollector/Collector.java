package com.example.stationdatacollector;
//https://www.rabbitmq.com/tutorials/tutorial-two-java
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.nio.charset.StandardCharsets;

public class Collector {

    public static void listenForMessages(String queueName, String brokerAddress, StationDataCollectorController controller) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(brokerAddress);
        factory.setPort(30003);

        // Use try-with-resources to ensure connection and channel are closed properly
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.exchangeDeclare("sendID", "direct");
            channel.queueDeclare(queueName, false, false, false, null);

            System.out.println("Listening on queue " + queueName + " at broker " + brokerAddress);

            channel.queueBind(queueName, "sendID", queueName);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("Received message: " + message);

                controller.processStationData(message);
            };

            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
            System.out.println("Consumer started and waiting for messages...");

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