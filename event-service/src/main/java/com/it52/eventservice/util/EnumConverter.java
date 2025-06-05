package com.it52.eventservice.util;

public class EnumConverter {
    /**
     * Преобразует строку в ordinal enum значения.
     * @param <E> тип enum
     * @param enumClass класс enum
     * @param name строковое имя значения
     * @return порядковый номер enum
     * @throws IllegalArgumentException если значение не найдено
     */
    public static <E extends Enum<E>> int toOrdinal(Class<E> enumClass, String name) {
        return Enum.valueOf(enumClass, name.toUpperCase()).ordinal();
    }

    /**
     * Преобразует ordinal в строковое имя enum.
     * @param <E> тип enum
     * @param enumClass класс enum
     * @param ordinal порядковый номер значения
     * @return строковое имя enum
     * @throws IllegalArgumentException если ordinal некорректен
     */
    public static <E extends Enum<E>> String toName(Class<E> enumClass, int ordinal) {
        E[] constants = enumClass.getEnumConstants();
        if (ordinal < 0 || ordinal >= constants.length) {
            throw new IllegalArgumentException("Invalid ordinal: " + ordinal + " for enum " + enumClass.getSimpleName());
        }
        return constants[ordinal].name();
    }
}
