package com.wallapop.iam.keycloak.extensions.monolithusers.password;

public class ClearTextPassword {
    public ClearTextPassword(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }
}
