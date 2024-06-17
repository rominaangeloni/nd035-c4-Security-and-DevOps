package com.wallapop.iam.keycloak.extensions.monolithusers.password;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptHasher {

    public static Boolean isValid(ClearTextPassword clearTextPassword, HashedPassword hashedPassword)  {
        return BCrypt.checkpw(clearTextPassword.getValue(), hashedPassword.getValue());
    }

    public static String encrypt(String plainPsw) {
        return new BCryptPasswordEncoder().encode(plainPsw);
    }
}