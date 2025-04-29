package com.policene.voluntech.utils;

public class MaskEmail {

    public static String maskEmail(String email) {
        int index = email.indexOf("@");
        return index <= 2 ? "***" + email.substring(index) : email.substring(0, 2) + "***" + email.substring(index);
    }

}
