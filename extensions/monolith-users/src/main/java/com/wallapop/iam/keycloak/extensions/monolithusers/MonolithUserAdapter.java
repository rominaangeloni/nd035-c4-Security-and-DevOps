package com.wallapop.iam.keycloak.extensions.monolithusers;

import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class MonolithUserAdapter extends AbstractUserAdapterFederatedStorage {
    private static final Logger logger = LoggerFactory.getLogger(MonolithUserAdapter.class);
    protected MonolithUser entity;
    protected String keycloakId;

    public MonolithUserAdapter(KeycloakSession session, RealmModel realm, ComponentModel model, MonolithUser entity) {
        super(session, realm, model);
        this.entity = entity;
        keycloakId = StorageId.keycloakId(model, entity.getUserId().toString());
    }

    public String getPassword() {
        return entity.getAuthPassword();
    }

    public void setPassword(String password) {
        entity.setPassword(password);
    }

    @Override
    public String getUsername() {
        return entity.getFirstName();
    }

    @Override
    public void setUsername(String username) {

    }

    @Override
    public void setEmail(String email) {
        entity.setEmail(email);
    }

    @Override
    public String getEmail() {
        return entity.getEmail();
    }

    @Override
    public String getId() {
        return keycloakId;
    }

    @Override
    public void setSingleAttribute(String name, String value) {
        super.setSingleAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        super.removeAttribute(name);
    }

    @Override
    public void setAttribute(String name, List<String> values) {
        super.setAttribute(name, values);
    }

    @Override
    public String getFirstAttribute(String name) {
        return super.getFirstAttribute(name);
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        Map<String, List<String>>          attrs = super.getAttributes();
        MultivaluedHashMap<String, String> all   = new MultivaluedHashMap<>();
        all.putAll(attrs);
        return all;
    }

    @Override
    public Stream<String> getAttributeStream(String name) {
        return super.getAttributeStream(name);
    }
}
