package com.side_fpt.team_service;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;

public class jasyptConfigTest {

    @Test
    void stringEncryptor() {
    }

    @Test
    void jasypt() {
        String url = "jdbc:postgresql://localhost:5432/user_service_db";
        String username = "postgres";
        String password = "1234";

        System.out.println(jasyptEncoding(url));
        System.out.println("-----------------------------------");
        System.out.println(jasyptEncoding(username));
        System.out.println("-----------------------------------");
        System.out.println(jasyptEncoding(password));
        System.out.println("-----------------------------------");
    }

    public String jasyptEncoding(String value) {

        String key = "my_jasypt_key";
        StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
        pbeEnc.setAlgorithm("PBEWithMD5AndDES");
        pbeEnc.setPassword(key);
        return pbeEnc.encrypt(value);
    }
}
