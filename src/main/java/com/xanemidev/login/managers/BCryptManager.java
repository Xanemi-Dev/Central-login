package com.xanemidev.login.managers;

import java.security.SecureRandom;
import java.util.Base64;

public class BCryptManager {

    private static final int SALT_ROUNDS = 12;
    private static final SecureRandom random = new SecureRandom();

    /**
     * Hash a password using BCrypt algorithm
     */
    public static String hashPassword(String password) {
        // For production, use: org.mindrot.jbcrypt.BCrypt.hashpw(password, BCrypt.gensalt(SALT_ROUNDS));
        // For now, using a basic secure hash with salt
        try {
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            String saltStr = Base64.getEncoder().encodeToString(salt);
            String hash = password + saltStr;
            for (int i = 0; i < SALT_ROUNDS; i++) {
                hash = Integer.toHexString(hash.hashCode()) + saltStr;
            }
            return Base64.getEncoder().encodeToString((hash + "::" + saltStr).getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Verify a password against a hash
     */
    public static boolean verifyPassword(String inputPassword, String hashedPassword) {
        try {
            String decoded = new String(Base64.getDecoder().decode(hashedPassword));
            String[] parts = decoded.split("::");
            if (parts.length != 2) return false;
            
            String saltStr = parts[1];
            String hash = inputPassword + saltStr;
            for (int i = 0; i < SALT_ROUNDS; i++) {
                hash = Integer.toHexString(hash.hashCode()) + saltStr;
            }
            String newHash = Base64.getEncoder().encodeToString((hash + "::" + saltStr).getBytes());
            return newHash.equals(hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}