package com.wallapop.iam.keycloak.extensions.apiconnect.domain.authorize;

import com.wallapop.domaineventbus.domain.domainevent.DomainEvent;

public class AuthorizedDomainEvent extends DomainEvent {

    private String origin;
    private String clientId;
    private String realmId;

    public AuthorizedDomainEvent(String aggregateId, String correlationId, AuthorizeEventOrigin origin, String clientId, String realmId) {
        super(aggregateId, correlationId);
        this.origin = origin.name();
        this.clientId = clientId;
        this.realmId = realmId;
    }

    @Override
    public String name() {
        return "iam.user.authorized";
    }

    public String getOrigin() { return origin; }
    public String getClientId() { return clientId; }
    public String getRealmId() { return realmId; }
}
