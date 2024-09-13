package com.wallapop.iam.keycloak.extensions.monolithusers.password;

import org.slf4j.Logger;

public class PasswordValidator {

    public static Boolean sameHash(ClearTextPassword clearTextPassword, HashedPassword hashedPassword, Logger logger) {
         if (hashedPassword.isBCryptHashed()) {
             logger.debug("The password is BCrypt");
             return BCryptHasher.isValid(clearTextPassword, hashedPassword);
        } else {
             logger.debug("The password is Sha");
             return ShaHasher.isValid(clearTextPassword, hashedPassword);
        }
    }
}
