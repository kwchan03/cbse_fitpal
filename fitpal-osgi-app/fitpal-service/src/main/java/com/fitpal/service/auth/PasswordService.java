package com.fitpal.service.auth;

import com.password4j.Password;
import org.osgi.service.component.annotations.Component;

@Component(service = PasswordService.class)
public class PasswordService {

    /**
     * Hash a password using BCrypt.
     * Password4j defaults to a cost factor of 10 or 12 and generates a secure salt.
     */
    public String hashPassword(String plainText) {
        // .withBcrypt() automatically handles salt generation
        return Password.hash(plainText).withBcrypt().getResult();
    }

    /**
     * Check a password.
     * Password4j automatically detects the algorithm (BCrypt) and version ($2a, $2b) from the hash.
     */
    public boolean checkPassword(String plainText, String hashed) {
        if (hashed == null || plainText == null) {
            return false;
        }

        // Returns true if matches, false otherwise
        return Password.check(plainText, hashed).withBcrypt();
    }
}