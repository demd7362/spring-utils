package com.example.common.annotation;

import java.lang.reflect.Field;

// 필드 후처리 프로세서
public interface Processor {

    void process(Object instance, Field field) throws Exception;
}
