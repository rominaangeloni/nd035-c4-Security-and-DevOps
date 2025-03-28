package com.wallapop.iam.keycloak.extensions.apiconnect.infra;

import com.wallapop.domaineventbus.domain.publisher.DomainEventBusPublisher;
import com.wallapop.domaineventbus.infrastructure.amqp.publisher.DomainEventBusPublisherAmqpCreator;
import com.wallapop.iam.keycloak.extensions.apiconnect.config.DomainEventBusConfig;
import com.wallapop.iam.keycloak.extensions.apiconnect.config.EventClientAmqp;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;

public class AuthenticationEventListenerProviderFactory implements EventListenerProviderFactory {

    private DomainEventBusPublisher publisher;

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return new AuthenticationEventListenerProvider(publisher);
    }

    @Override
    public void init(Config.Scope config) {
        this.publisher = new DomainEventBusPublisherAmqpCreator(EventClientAmqp.getInstance())
                .create(new DomainEventBusConfig().getQueuePrefix());
    }

    @Override
    public void postInit(org.keycloak.models.KeycloakSessionFactory factory) {
    }

    @Override
    public void close() {
    }

    @Override
    public String getId() {
        return "api-connect-event-listener";
    }
}

