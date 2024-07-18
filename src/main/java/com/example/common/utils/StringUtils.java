package com.example.common.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {
    public static boolean containsIgnoreCase(@NonNull String target, @NonNull String word) {
        return target.toLowerCase().contains(word.toLowerCase());
    }

    public static boolean hasText(String str) {
        return org.springframework.util.StringUtils.hasText(str);
    }

}
