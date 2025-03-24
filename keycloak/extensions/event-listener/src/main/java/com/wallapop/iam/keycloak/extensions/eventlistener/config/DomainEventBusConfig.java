package com.wallapop.iam.keycloak.extensions.eventlistener.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.time.Duration;

public class DomainEventBusConfig {
    private String user;
    private String password;
    private String virtualHost;
    private String host;
    private int port;
    private String queuePrefix;
    private Duration delayedRetry;
    private int publisherThreads;

    public DomainEventBusConfig() {
        Config config = ConfigFactory.load();
        Config rabbitConfig = config.getConfig("application.domain-event-bus");

        this.user = rabbitConfig.getString("user");
        this.password = rabbitConfig.getString("password");
        this.virtualHost = rabbitConfig.getString("virtual-host");
        this.host = rabbitConfig.getString("host");
        this.port = rabbitConfig.getInt("port");
        this.queuePrefix = rabbitConfig.getString("queue-prefix");
        this.delayedRetry = Duration.ofSeconds(rabbitConfig.getInt("delayed-retry"));
        this.publisherThreads = rabbitConfig.getInt("publisher-threads");
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getQueuePrefix() {
        return queuePrefix;
    }

    public Duration getDelayedRetry() {
        return delayedRetry;
    }

    public int getPublisherThreads() {
        return publisherThreads;
    }

    public int delayedRetryInMillis() {
        return (int) delayedRetry.toMillis();
    }
}
