package com.it52.eventservice.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class SlugUtil {

    public static String createSlug(LocalDateTime startedAt, String title) {
        String datePart = startedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String titlePart = transliterate(title)
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s]", "") // убираем всё, кроме латинских букв, цифр и пробелов
                .trim()
                .replaceAll("\\s+", "-");

        return datePart + "-" + titlePart;
    }

    // Простейшая ручная транслитерация (можно заменить на библиотечную)
    private static String transliterate(String input) {
        return input
                .replace("А", "A").replace("а", "a")
                .replace("Б", "B").replace("б", "b")
                .replace("В", "V").replace("в", "v")
                .replace("Г", "G").replace("г", "g")
                .replace("Д", "D").replace("д", "d")
                .replace("Е", "E").replace("е", "e")
                .replace("Ё", "E").replace("ё", "e")
                .replace("Ж", "Zh").replace("ж", "zh")
                .replace("З", "Z").replace("з", "z")
                .replace("И", "I").replace("и", "i")
                .replace("Й", "Y").replace("й", "y")
                .replace("К", "K").replace("к", "k")
                .replace("Л", "L").replace("л", "l")
                .replace("М", "M").replace("м", "m")
                .replace("Н", "N").replace("н", "n")
                .replace("О", "O").replace("о", "o")
                .replace("П", "P").replace("п", "p")
                .replace("Р", "R").replace("р", "r")
                .replace("С", "S").replace("с", "s")
                .replace("Т", "T").replace("т", "t")
                .replace("У", "U").replace("у", "u")
                .replace("Ф", "F").replace("ф", "f")
                .replace("Х", "Kh").replace("х", "kh")
                .replace("Ц", "Ts").replace("ц", "ts")
                .replace("Ч", "Ch").replace("ч", "ch")
                .replace("Ш", "Sh").replace("ш", "sh")
                .replace("Щ", "Sch").replace("щ", "sch")
                .replace("Ъ", "").replace("ъ", "")
                .replace("Ы", "Y").replace("ы", "y")
                .replace("Ь", "").replace("ь", "")
                .replace("Э", "E").replace("э", "e")
                .replace("Ю", "Yu").replace("ю", "yu")
                .replace("Я", "Ya").replace("я", "ya");
    }
}
