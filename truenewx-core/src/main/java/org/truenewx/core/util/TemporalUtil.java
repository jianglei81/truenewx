package org.truenewx.core.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;

/**
 * Java 8 新版时间相关类的工具类
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class TemporalUtil {

    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();

    private TemporalUtil() {
    }

    public static Instant toInstant(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.atZone(TemporalUtil.DEFAULT_ZONE_ID).toInstant();
    }

    public static Instant toInstant(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atStartOfDay(TemporalUtil.DEFAULT_ZONE_ID).toInstant();
    }

    public static LocalDateTime toLocalDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }
        return LocalDateTime.ofInstant(instant, TemporalUtil.DEFAULT_ZONE_ID);
    }

    /**
     * 按照指定格式格式化时间点对象为字符串型日期
     *
     * @param temporal 时间点
     * @param pattern  日期格式
     * @return 字符串型日期
     */
    public static String format(Temporal temporal, String pattern) {
        return DateTimeFormatter.ofPattern(pattern).withZone(TemporalUtil.DEFAULT_ZONE_ID)
                .format(temporal);
    }

    public static String format(Temporal temporal) {
        if (temporal instanceof Instant) {
            return formatLong((Instant) temporal);
        } else if (temporal instanceof LocalDateTime) {
            return format(temporal, DateUtil.LONG_DATE_PATTERN);
        } else if (temporal instanceof LocalDate) {
            return format(temporal, DateUtil.SHORT_DATE_PATTERN);
        } else if (temporal instanceof LocalTime) {
            return format(temporal, DateUtil.TIME_PATTERN);
        } else if (temporal instanceof ZonedDateTime) {
            return ((ZonedDateTime) temporal)
                    .format(DateTimeFormatter.ofPattern(DateUtil.LONG_DATE_PATTERN));
        }
        return temporal.toString();
    }

    /**
     * 按照短日期格式(yyyy-MM-dd)格式化时间点对象为字符串型日期
     *
     * @param instant 时间点
     * @return 字符串型日期
     */
    public static String formatShort(Instant instant) {
        return format(instant, DateUtil.SHORT_DATE_PATTERN);
    }

    public static String format(LocalDate date) {
        return format(date, DateUtil.SHORT_DATE_PATTERN);
    }

    /**
     * 按照长日期格式(yyyy-MM-dd HH:mm:ss)转换时间点对象为字符串型日期
     *
     * @param instant 时间点
     * @return 字符串型日期
     */
    public static String formatLong(Instant instant) {
        return format(instant, DateUtil.LONG_DATE_PATTERN);
    }

    /**
     * 按照长日期格式(yyyyMMddHHmmss)转换时间点对象为字符串型日期
     *
     * @param instant 时间点
     * @return 字符串型日期
     */
    public static String formatLongNoDelimiter(Instant instant) {
        return format(instant, DateUtil.LONG_DATE_NO_DELIMITER_PATTERN);
    }

    /**
     * 计算指定两个日期之间的相差天数。如果earlierDate晚于laterDate，则返回负值
     *
     * @param earlierDate 较早日期
     * @param laterDate   较晚日期
     * @return 相差天数
     */
    public static int daysBetween(LocalDate earlierDate, LocalDate laterDate) {
        return (int) (laterDate.toEpochDay() - earlierDate.toEpochDay());
    }

    public static Instant addYears(Instant instant, int years) {
        return toInstant(toLocalDateTime(instant).plusYears(years));
    }

    public static Instant addMonths(Instant instant, int months) {
        return toInstant(toLocalDateTime(instant).plusMonths(months));
    }

    public static Instant addDays(Instant instant, int days) {
        return toInstant(toLocalDateTime(instant).plusDays(days));
    }

    /**
     * 为指定时间点设置时分秒纳秒，返回新日期
     *
     * @param instant      原时间点
     * @param hour         时
     * @param minute       分
     * @param second       秒
     * @param nanoOfSecond 纳秒
     * @return 新时间点
     */
    public static Instant setTime(Instant instant, int hour, int minute, int second,
            int nanoOfSecond) {
        LocalDateTime dateTime = toLocalDateTime(instant);
        dateTime = LocalDateTime.of(dateTime.getYear(), dateTime.getMonthValue(),
                dateTime.getDayOfMonth(), hour, minute, second, nanoOfSecond);
        return toInstant(dateTime);
    }

}
