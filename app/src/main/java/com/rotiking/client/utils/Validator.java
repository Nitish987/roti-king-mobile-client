package com.rotiking.client.utils;

public class Validator {
    public static final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    public static boolean isEmpty(String s) {
        return s.equals("");
    }

    public static boolean isMinLength(String s, int minLength) {
        return s.length() >= minLength;
    }

    public static boolean isMaxLength(String s, int maxLength) {
        return s.length() <= maxLength;
    }

    public static boolean isEqualLength(String s, int length) {
        return s.length() == length;
    }

    public static boolean isEmail(String email) {
        return email.trim().matches(EMAIL_PATTERN);
    }

    public static Object[] isPassword(String password) {
        if (password.length() > 15 || password.length() <= 8)
            return new Object[]{false, "Password must be less than 20 and more than 8 characters in length."};

        if (!password.matches("(.*[A-Z].*)"))
            return new Object[]{false, "Password must have at least one uppercase character"};

        if (!password.matches("(.*[a-z].*)"))
            return new Object[]{false, "Password must have at least one lowercase character"};

        if (!password.matches("(.*[0-9].*)"))
            return new Object[]{false, "Password must have at least one number"};

        if (!password.matches("(.*[@,#,$,%].*$)"))
            return new Object[]{false, "Password must have at least one special character among @,#,$,%"};

        return new Object[]{true, null};
    }
}
