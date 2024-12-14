package net.pointofviews.club.utils;

import net.pointofviews.club.exception.InviteCodeException;

import java.security.SecureRandom;

public class InviteCodeGenerator {
    private InviteCodeGenerator() {
    }

    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateInviteCode(int length) {
        if (length <= 0) {
            throw InviteCodeException.invalidLength(length);
        }

        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            code.append(CHAR_POOL.charAt(RANDOM.nextInt(CHAR_POOL.length())));
        }
        return code.toString();
    }
}
