package com.example.common.utils;


import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@UtilityClass
@Slf4j
public class AopUtils {
    public static Method getMethod(JoinPoint joinPoint) {
        return ((MethodSignature) joinPoint.getSignature()).getMethod();
    }

    public static Class<?> getClass(JoinPoint joinPoint) {
        return joinPoint.getTarget().getClass();
    }

    public static void logger(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        log.error(signature.toString());
    }

    public static <T extends Annotation> T getAnnotation(JoinPoint joinPoint, Class<T> clazz) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        T annotation = method.getAnnotation(clazz);
        if (annotation != null) {
            return annotation;
        }
        annotation = getClass(joinPoint).getAnnotation(clazz);
        if (annotation != null) {
            return annotation;
        }
        return (T) methodSignature.getDeclaringType().getAnnotation(clazz); // 인터페이스일 경우
    }



    public static <T> T getParameter(JoinPoint joinPoint, Class<T> clazz, String parameterName) {
        Method method = getMethod(joinPoint);
        Object[] args = joinPoint.getArgs();

        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getName().equals(parameterName) && clazz.isInstance(args[i])) {
                return clazz.cast(args[i]);
            }
        }
        return null;
    }
}
