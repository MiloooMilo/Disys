package com.example.collectionreceiver.communication;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Producer {

    public static void send(String msg, String customerID, String queueName, String brokerUrl) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(brokerUrl);
        factory.setPort(30003);
        msg = customerID + ";" + msg;

        try (
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();
        ) {
            channel.exchangeDeclare("kwh", "direct");
            channel.queueDeclare(queueName, false, false, false, null);
            channel.queueBind(queueName, "kwh", queueName);
            channel.basicPublish("kwh", queueName, null, msg.getBytes());
            System.out.println(" [x] Sent '" + customerID + "' to queue: " + queueName);
        } catch (Exception e) {
            System.out.println(" [x] Unexpected exception at Producer: " + e.getMessage());
        }
    }
}
