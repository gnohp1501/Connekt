package com.example.connekt.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
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
        if (diff < 2 * MINUTE_MILLIS) {
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
}
