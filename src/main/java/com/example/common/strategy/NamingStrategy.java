package com.example.common.strategy;

@FunctionalInterface
public interface NamingStrategy {
    String convert(String input);
}
