package grozdan.test.election.utils;

import grozdan.test.election.core.ElectionException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeUtils {
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static LocalDateTime parse(String dateTime) throws ElectionException {
        try {
            return LocalDateTime.parse(dateTime, formatter);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }
        throw new ElectionException("Date " + dateTime + " can not be parsed to a date with the available formats!");
    }
}