package com.wallapop.iam.keycloak.extensions.monolithusers.password;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptHasher {

    public static Boolean isValid(ClearTextPassword clearTextPassword, HashedPassword hashedPassword)  {
        return BCrypt.checkpw(clearTextPassword.getValue(), hashedPassword.getValue());
    }
}