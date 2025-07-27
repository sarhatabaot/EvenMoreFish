package com.oheers.fish.utils;

import java.util.regex.Pattern;

public class Base64 {
    private static final Pattern BASE64_PATTERN = Pattern.compile("^[A-Za-z0-9+/]+={0,2}$");

    public static boolean isBase64(String input) {
        if (input == null || input.length() % 4 != 0 || !BASE64_PATTERN.matcher(input).matches()) {
            return false;
        }

        try {
            java.util.Base64.getDecoder().decode(input);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
