package com.wallapop.iam.keycloak.extensions.monolithusers.password;

public class PasswordValidator {

    public static Boolean sameHash(ClearTextPassword clearTextPassword, HashedPassword hashedPassword) {
         if (hashedPassword.isBCryptHashed()) {
             return BCryptHasher.isValid(clearTextPassword, hashedPassword);
        } else {
             return ShaHasher.isValid(clearTextPassword, hashedPassword);
        }
    }
}
