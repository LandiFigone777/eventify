package org.example.eventify;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {
    

    public static String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10)); // Genera un numero casuale da 0 a 9
        }
        return code.toString();
    }

    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch (NoSuchAlgorithmException e){
            throw new NoSuchAlgorithmException("Errore durante l'hashing della password");
        }
    }

    public static String generateUid() {

        Random random = new Random();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        char[] array = characters.toCharArray();
        char[] uid = new char[24];

        for(int i=0;i<24;i++){
            uid[i] = array[Math.abs(random.nextInt() % 62)];
        }

        return new String(uid);
    }

    // per futuri utilizzi ci sono delle cose da capire
    public static List<String> viastringtolist(String viastring) {
        List<String> list = new ArrayList<>();
        String[] items = viastring.split(",");
        for (String item : items) {
            list.add(item.trim());
        }
        return list;
    }
}
