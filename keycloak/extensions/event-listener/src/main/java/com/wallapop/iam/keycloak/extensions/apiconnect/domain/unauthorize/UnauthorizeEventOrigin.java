package com.wallapop.iam.keycloak.extensions.apiconnect.domain.unauthorize;

import org.keycloak.events.Event;

public enum UnauthorizeEventOrigin {
    LOGOUT,
    REVOKE,
    UNKNOWN;

    public static UnauthorizeEventOrigin fromEvent(Event event) {
        return switch (event.getType()) {
            case LOGOUT -> UnauthorizeEventOrigin.LOGOUT;
            case REVOKE_GRANT -> UnauthorizeEventOrigin.REVOKE;
            default -> UnauthorizeEventOrigin.UNKNOWN;
        };
    }
}
