package com.wallapop.iam.keycloak.extensions.monolithusers;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonolithUserProviderFactory implements UserStorageProviderFactory<MonolithUserProvider> {
    public static final String PROVIDER_ID = "monolith-users-authorization-jpa";

    private static final Logger logger = LoggerFactory.getLogger(MonolithUserProviderFactory.class);

    @Override
    public MonolithUserProvider create(KeycloakSession session, ComponentModel model) {
        return new MonolithUserProvider(session, model);
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getHelpText() {
        return "Connection to authorize Monolith users";
    }

    @Override
    public void close() {
        logger.info("<<<<<< Closing factory");
    }
}
