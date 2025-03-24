package com.wallapop.iam.keycloak.extensions.eventlistener.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.wallapop.domaineventbus.infrastructure.amqp.ClientAmqp;

import java.io.IOException;

public class EventListenerClientAmqp {
    private static ClientAmqp client;
    private static Connection connection;

    public static synchronized ClientAmqp getInstance()  {
        if (client == null) {
            DomainEventBusConfig config = new DomainEventBusConfig();
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUsername(config.getUser());
            factory.setPassword(config.getPassword());
            factory.setVirtualHost(config.getVirtualHost());
            factory.setHost(config.getHost());
            factory.setPort(config.getPort());
            factory.setRequestedHeartbeat(0);
            factory.setAutomaticRecoveryEnabled(true);
            factory.setTopologyRecoveryEnabled(true);
            factory.setNetworkRecoveryInterval(100);


            try {
                connection = factory.newConnection("event-listener-client");
                client = new ClientAmqp(config.getUser(), config.getPassword(), config.getVirtualHost(), config.getHost(), config.getPort()) {
                    @Override
                    public Channel createChannel() throws IOException {
                        return connection.createChannel();
                    }

                    @Override
                    public boolean isConnected() {
                        return connection.isOpen();
                    }

                    @Override
                    public void close() {
                        if (isConnected()) {
                            try {
                                connection.close();
                            } catch (IOException e) {
                                throw new RuntimeException("Error closing connection", e);
                            }
                        }
                    }
                };
            } catch (Exception e) {
                throw new RuntimeException("Failed to create AMQP client", e);
            }

            handleShutdownHook();
        }
        return client;
    }

    private static void handleShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Thread.sleep(5000);
                if (client != null) {
                    client.close();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("Error handling shutdown", e);
            }
        }));
    }
}
