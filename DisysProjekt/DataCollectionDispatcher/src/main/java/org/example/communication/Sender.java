package org.example.communication;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Sender {
    public static void sendMessage(String msg, String customerID, String queueName, String brokerUrl) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(brokerUrl);
        factory.setPort(30003);
        msg = customerID + ";" + msg;

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare("sendID", "direct");
            channel.queueDeclare(queueName, false, false, false, null);
            channel.queueBind(queueName, "sendID", queueName);
            channel.basicPublish("sendID", queueName, null, msg.getBytes());
            System.out.println(" [x] Sent '" + msg + "' to queue: " + queueName);
        } catch (Exception e) {
            System.out.println(" [x] Unexpected exception at Sender: " + e.getMessage());
            e.printStackTrace();
        }
    }
}