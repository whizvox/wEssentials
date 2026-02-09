package me.whizvox.wessentials.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
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

    public static String snakeToCamelCase(String str) {
        StringBuilder sb = new StringBuilder();
        boolean uppercaseNext = false;
        for (char c : str.toCharArray()) {
            if (c == '_') {
                uppercaseNext = true;
            } else {
                if (uppercaseNext) {
                    sb.append(Character.toUpperCase(c));
                    uppercaseNext = false;
                } else {
                    sb.append(Character.toLowerCase(c));
                }
            }
        }
        return sb.toString();
    }

    public static LogRecord logParamsException(Level level, Throwable t, String format, Object arg1, Object... otherArgs) {
        LogRecord lr = new LogRecord(level, format);
        lr.setThrown(t);
        if (otherArgs.length == 0) {
            lr.setParameters(new Object[] {arg1});
        } else {
            Object[] args = new Object[otherArgs.length + 1];
            args[0] = arg1;
            System.arraycopy(otherArgs, 0, args, 1, otherArgs.length);
            lr.setParameters(args);
        }
        return lr;
    }

}
