package com.it52.eventservice.util;

import java.lang.reflect.Field;

public class ReflectionUtils {
    public static void mergeNonNullFields(Object source, Object target) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Source and target must not be null");
        }

        Field[] fields = source.getClass().getDeclaredFields();
        for (Field sourceField : fields) {
            try {
                sourceField.setAccessible(true);
                Object value = sourceField.get(source);

                if (value != null) {
                    String fieldName = sourceField.getName();
                    Field targetField = getField(target.getClass(), fieldName);
                    if (targetField != null) {
                        targetField.setAccessible(true);
                        targetField.set(target, value);
                    }
                }

            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to merge field: " + sourceField.getName(), e);
            }
        }
    }

    private static Field getField(Class<?> clazz, String fieldName) {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }
}
