package com.wallapop.iam.keycloak.extensions.apiconnect.infra;

import com.wallapop.domaineventbus.domain.domainevent.DomainEvent;
import com.wallapop.domaineventbus.domain.publisher.DomainEventBusPublisher;
import com.wallapop.iam.keycloak.extensions.apiconnect.domain.authorize.AuthorizedDomainEvent;
import com.wallapop.iam.keycloak.extensions.apiconnect.domain.unauthorize.UnauthorizedDomainEvent;
import com.wallapop.iam.keycloak.extensions.apiconnect.domain.authorize.AuthorizeEventOrigin;
import com.wallapop.iam.keycloak.extensions.apiconnect.domain.unauthorize.UnauthorizeEventOrigin;
import org.keycloak.events.Event;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;

public class AuthenticationEventListenerProvider implements EventListenerProvider{
    private final DomainEventBusPublisher eventPublisher;

    public AuthenticationEventListenerProvider(DomainEventBusPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void onEvent(Event event) {
        if(isWallapopConnectThirdPartyClient(event)) {
            switch (event.getType()) {
                case CODE_TO_TOKEN:
                    publishEvent(new AuthorizedDomainEvent(getWallapopUserId(event), event.getSessionId(), AuthorizeEventOrigin.LOGIN, event.getClientId(), event.getRealmId()));
                    break;
                case LOGOUT:
                case REVOKE_GRANT:
                    publishEvent(new UnauthorizedDomainEvent(getWallapopUserId(event), event.getSessionId(), UnauthorizeEventOrigin.fromEvent(event), event.getClientId(), event.getRealmId()));
                    break;
                default:
                    break;
            }
        }
    }

    private boolean isWallapopConnectThirdPartyClient(Event event) {
        return (!(event.getClientId().equals("security-admin-console") || event.getClientId().equals("admin-cli")) && event.getRealmName().equals("wallapop-connect"));
    }

    public String getWallapopUserId(Event event) {
        return event.getUserId().split(":")[2];
    }

    private void publishEvent(DomainEvent event) {
        eventPublisher.publish(event);
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
       //TODO: Implement DELETE user session
    }

    @Override
    public void close() {

    }
}

