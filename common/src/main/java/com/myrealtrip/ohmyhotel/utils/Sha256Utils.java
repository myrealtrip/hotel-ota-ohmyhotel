package com.myrealtrip.ohmyhotel.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha256Utils {

    /**
     * 입력 문자열을 SHA-256으로 암호화하여 해시 값을 반환하는 메서드
     */
    public static String encrypt(String text) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(text.getBytes());
        return bytesToHex(md.digest());
    }

    /**
     * 바이트 배열을 16진수 문자열로 변환하는 메서드
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}

