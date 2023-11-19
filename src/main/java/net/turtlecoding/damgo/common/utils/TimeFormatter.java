package net.turtlecoding.damgo.common.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TimeFormatter {

    public static String formatToString(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
