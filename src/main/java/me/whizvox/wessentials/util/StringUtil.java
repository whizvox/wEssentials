package me.whizvox.wessentials.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class StringUtil {

    private static final Pattern NAME_PATTERN = Pattern.compile("\\w{1,32}");

    public static boolean isValidGenericName(String name) {
        return NAME_PATTERN.matcher(name).matches();
    }

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static LocalDateTime parseDateTime(String str) {
        return LocalDateTime.from(DATE_TIME_FORMATTER.parse(str));
    }

    public static String formatDateTime(LocalDateTime ldt) {
        return DATE_TIME_FORMATTER.format(ldt);
    }

}
