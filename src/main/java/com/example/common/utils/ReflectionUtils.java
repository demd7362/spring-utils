package com.example.common.utils;

import com.example.common.annotation.Excel;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

@UtilityClass
public class ReflectionUtils {
    public static final Map<String, String> CLASS_MAP = new HashMap<>();
    public static final Set<Class<?>> EXCEL_CLASSES = new HashSet<>();
    static {
        Reflections reflections = new Reflections("com.example.common.model");
        Set<Class<?>> subTypes = reflections.getSubTypesOf(Object.class);
        for(Class<?> subType : subTypes){
            CLASS_MAP.put(subType.getSimpleName(), subType.getName());
            boolean isExcelClass = subType.getAnnotation(Excel.class) != null;
            if(isExcelClass){
                EXCEL_CLASSES.add(subType);
            }
        }
    }

    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = clazz;
        while (currentClass != null) {
            fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
            currentClass = currentClass.getSuperclass();
        }
        return fields;
    }
    public static List<Field> getOwnFields(Class<?> clazz){
        return List.of(clazz.getDeclaredFields());
    }


    public static Map<Field, Object> getFieldMap(Object instance) {
        Map<Field, Object> fieldMap = new HashMap<>();
        if (instance != null) {
            List<Field> fields = getAllFields(instance.getClass());
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    fieldMap.put(field, field.get(instance));
                } catch (IllegalAccessException e) {
                    // 예외 처리 로직 추가 필요함
                }
            }
        }
        return fieldMap;
    }


    @SneakyThrows
    public static <T> T copyObject(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }

        T target = createInstance(targetClass);
        Map<Field, Object> sourceFieldMap = getFieldMap(source);

        for (Map.Entry<Field, Object> entry : sourceFieldMap.entrySet()) {
            Field sourceField = entry.getKey();
            Object sourceFieldValue = entry.getValue();

            try {
                Field targetField = targetClass.getDeclaredField(sourceField.getName());
                targetField.setAccessible(true);
                if (targetField.getType().isAssignableFrom(sourceField.getType())) {
                    targetField.set(target, sourceFieldValue);
                }
            } catch (NoSuchFieldException ignored) {
                // 타겟 클래스에 해당 필드가 없는 경우 무시
            }
        }

        return target;
    }


    public static <T> T createInstance(Class<T> targetClass) throws Exception {
        return targetClass.getDeclaredConstructor().newInstance();
    }

    public static List<Field> getAnnotatedFields(Class<?> clazz, Class<? extends Annotation> targetAnnotation){
        List<Field> fields = new ArrayList<>();
        for (Field field : getAllFields(clazz)) {
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                if (annotation.annotationType() == targetAnnotation) {
                    fields.add(field);
                }
            }
        }
        return fields;
    }
}
