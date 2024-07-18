package com.example.common.strategy;

import com.example.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NamingStrategies {

    public static NamingStrategy getInstance(NamingStrategyEnum namingStrategyEnum) {
        return switch (namingStrategyEnum) {
            case CAMEL_CASE -> CamelCaseStrategy.INSTANCE;
            case SNAKE_UPPER_CASE -> SnakeUpperCaseStrategy.INSTANCE;
            case SNAKE_LOWER_CASE -> SnakeLowerCaseStrategy.INSTANCE;
        };
    }

    private static class CamelCaseStrategy implements NamingStrategy {
        private static final NamingStrategy INSTANCE = new CamelCaseStrategy();

        @Override
        public String convert(String input) {
            if (input == null || input.isEmpty()) {
                return input;
            }

            input = input.toLowerCase();
            Pattern pattern = Pattern.compile("[^a-zA-Z0-9]+(.)");
            Matcher matcher = pattern.matcher(input);
            StringBuilder result = new StringBuilder();

            while (matcher.find()) {
                matcher.appendReplacement(result, matcher.group(1).toUpperCase());
            }
            matcher.appendTail(result);

            return result.toString();
        }
    }

    private static class SnakeUpperCaseStrategy implements NamingStrategy {
        private static final NamingStrategy INSTANCE = new SnakeUpperCaseStrategy();

        @Override
        public String convert(String input) {
            if(!StringUtils.hasText(input)){
                return input;
            }
            StringBuilder sb = new StringBuilder();
            List<String> result = new ArrayList<>();
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                if(c == '-' || c == '_'){
                    result.add(sb.toString());
                    sb.setLength(0);
                    continue;
                }
                sb.append(Character.toUpperCase(c));
            }
            if(!sb.isEmpty()){
                result.add(sb.toString());
            }
            return String.join("_", result);
        }
    }

    private static class SnakeLowerCaseStrategy implements NamingStrategy {
        private static final NamingStrategy INSTANCE = new SnakeLowerCaseStrategy();


        @Override
        public String convert(String input) {
            if(!StringUtils.hasText(input)){
                return input;
            }
            StringBuilder sb = new StringBuilder();
            List<String> result = new ArrayList<>();
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                if(c == '-' || c == '_'){
                    result.add(sb.toString());
                    sb.setLength(0);
                    continue;
                }
                sb.append(Character.toLowerCase(c));
            }
            if(!sb.isEmpty()){
                result.add(sb.toString());
            }
            return String.join("_", result);
        }
    }


}
