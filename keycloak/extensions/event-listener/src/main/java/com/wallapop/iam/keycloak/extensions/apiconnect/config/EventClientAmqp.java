package com.wallapop.iam.keycloak.extensions.apiconnect.config;

import com.wallapop.domaineventbus.infrastructure.amqp.ClientAmqp;

public class EventClientAmqp {
    private static ClientAmqp client;

    public static ClientAmqp getInstance() {
        if (client == null) {
            DomainEventBusConfig config = new DomainEventBusConfig();
            client = new ClientAmqp(
                    config.getUser(),
                    config.getPassword(),
                    config.getVirtualHost(),
                    config.getHost(),
                    config.getPort()
            );
        }
        return client;
    }
}
