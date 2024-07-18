package com.example.common.utils;

import com.example.common.strategy.NamingStrategy;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RandomUtils {
    private static final char[] CHARS = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    public static String generateRandomText(int length, NamingStrategy strategy) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = (int) Math.floor(Math.random() * CHARS.length);
            builder.append(CHARS[randomIndex]);
        }
        if(strategy == null){
            return builder.toString();
        }
        return strategy.convert(builder.toString());
    }


}
