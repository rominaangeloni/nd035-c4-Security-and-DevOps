package com.wallapop.iam.keycloak.extensions.apiconnect.domain.unauthorize;

import com.wallapop.domaineventbus.domain.domainevent.DomainEvent;

public class UnauthorizedDomainEvent extends DomainEvent {

    private String origin;
    private String clientId;
    private String realmId;

    public UnauthorizedDomainEvent(String aggregateId, String correlationId, UnauthorizeEventOrigin origin, String clientId, String realmId) {
        super(aggregateId, correlationId);
        this.origin = origin.name();
        this.clientId = clientId;
        this.realmId = realmId;
    }

    @Override
    public String name() {
        return "iam.user.unauthorized";
    }

    public String getOrigin() { return origin; }
    public String getClientId() { return clientId; }
    public String getRealmId() { return realmId; }
}
