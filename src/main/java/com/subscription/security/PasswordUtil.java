package com.subscription.security;

import java.io.Console;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;

public final class PasswordUtil {

    private static final int ITERATIONS = 120_000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {}

    public static String newSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hash(String password, String salt) {
        byte[] saltBytes = Base64.getDecoder().decode(salt);
        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(), saltBytes, ITERATIONS, KEY_LENGTH);
        try {
            byte[] hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec)
                    .getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("PBKDF2 password hashing is unavailable.", e);
        } finally {
            spec.clearPassword();
        }
    }

    public static boolean matches(String password, String salt, String expectedHash) {
        String actualHash = hash(password, salt);
        return MessageDigest.isEqual(
                actualHash.getBytes(StandardCharsets.US_ASCII),
                expectedHash.getBytes(StandardCharsets.US_ASCII));
    }

    public static void main(String[] args) {
        Console console = System.console();
        char[] password;
        if (console != null) {
            password = console.readPassword("New admin password: ");
        } else {
            JPasswordField passwordField = new JPasswordField();
            int choice = JOptionPane.showConfirmDialog(
                    null, passwordField, "New admin password",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (choice != JOptionPane.OK_OPTION) {
                return;
            }
            password = passwordField.getPassword();
        }

        if (password == null) {
            return;
        }
        if (password.length < 12) {
            java.util.Arrays.fill(password, '\0');
            if (console != null) {
                throw new IllegalArgumentException("Password must contain at least 12 characters.");
            }
            JOptionPane.showMessageDialog(
                    null, "Password must contain at least 12 characters.",
                    "Invalid password", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String rawPassword = new String(password);
        String salt = newSalt();
        String passwordHash = hash(rawPassword, salt);
        java.util.Arrays.fill(password, '\0');

        if (console != null) {
            console.printf("Password salt: %s%nPassword hash: %s%n", salt, passwordHash);
        } else {
            JTextArea result = new JTextArea(
                    "Password salt: " + salt + "\nPassword hash: " + passwordHash,
                    4, 60);
            result.setEditable(false);
            result.setLineWrap(true);
            result.setWrapStyleWord(true);
            JOptionPane.showMessageDialog(
                    null, result, "Admin password values", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
