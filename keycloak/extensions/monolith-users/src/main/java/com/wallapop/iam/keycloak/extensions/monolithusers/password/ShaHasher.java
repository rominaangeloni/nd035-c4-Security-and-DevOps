package com.wallapop.iam.keycloak.extensions.monolithusers.password;

import org.jboss.logging.Logger;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class ShaHasher {
    private static final Logger logger = Logger.getLogger(ShaHasher.class);
    public static Boolean isValid(ClearTextPassword clearTextPassword, HashedPassword hashedPassword)  {
        Boolean isValid = false;
        try {
            isValid = hash(clearTextPassword) == hashedPassword;
        } catch (Exception e) {
            logger.error("Error validating the password", e);
        }
        return isValid;
    }

    private static HashedPassword hash(ClearTextPassword clearTextPassword)  {
        try {
            return new HashedPassword(Base64.getEncoder()
                                             .encodeToString(MessageDigest.getInstance("SHA")
                                                                     .digest(clearTextPassword.getValue()
                                                                                     .getBytes(StandardCharsets.UTF_8))));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
