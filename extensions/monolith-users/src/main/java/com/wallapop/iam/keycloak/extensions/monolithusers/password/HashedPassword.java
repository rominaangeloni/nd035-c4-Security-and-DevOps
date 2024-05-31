package com.wallapop.iam.keycloak.extensions.monolithusers.password;

public class HashedPassword {

    private String value;

    public HashedPassword(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Boolean isBCryptHashed() {
        return value.startsWith("$2a$10$");
    }

}
