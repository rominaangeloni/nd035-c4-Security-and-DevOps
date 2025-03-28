package com.wallapop.iam.keycloak.extensions.apiconnect.config;

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
        this.user = System.getenv("RABBITMQ_USERNAME");
        this.password = System.getenv("RABBITMQ_PASSWORD");
        this.virtualHost = System.getenv("RABBITMQ_VIRTUAL_HOST");
        this.host = System.getenv("RABBITMQ_HOST");
        this.port = 5672;
        this.queuePrefix = "iam";
        this.delayedRetry = Duration.ofSeconds(5);
        this.publisherThreads = 3;
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
