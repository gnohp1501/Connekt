package com.example.connekt.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class DateUtils {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    public static String longToDate(long time) {
        Date date = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/YYYY");
        String format = formatter.format(date);
        return format;
    }


    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }
        long now = currentDate().getTime();
        if (time > now || time <= 0) {
            return "in the future";
        }
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "Moments ago";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "A minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "An hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Yesterday";
        } else if (diff < 3 * DAY_MILLIS) {
            return diff / DAY_MILLIS + " days ago";
        } else {
            Date date = new Date(time);
            SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy");
            String format = formatter.format(date);
            return format;
        }
    }

    public static String getTime(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }
        long now = currentDate().getTime();

        final long diff = now - time;
        if (diff < 1 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "An hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Yesterday";
        } else if (diff < 3 * DAY_MILLIS) {
            return diff / DAY_MILLIS + " days ago";
        } else {
            Date date = new Date(time);
            SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy");
            String format = formatter.format(date);
            return format;
        }
    }

    public static String getTimeMess(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }
        long now = currentDate().getTime();

        final long diff = now - time;
        if (diff < 24 * HOUR_MILLIS) {
            Date date = new Date(time);
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            String format = formatter.format(date);
            return "Today at " + format;
        } else {
            Date date = new Date(time);
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd MMMM yyyy");
            String format = formatter.format(date);
            return format;
        }
    }

    //
    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public static String formatNumber(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return formatNumber(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + formatNumber(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }
}
