package com.xq.study.demoworkdataforbigdata.demoworkdataforbigdata.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.concurrent.TimeUnit;

/**
 * java8时间工具类
 * Created By sk-tengfeiwang on 2019/9/3.
 */
public class TimeUtilForJavaEight {

    private static final Logger logger = LoggerFactory.getLogger(TimeUtilForJavaEight.class);

    /**
     * 带T带时区日期格式
     */
    public static final String PATTERN_YYYY_MM_DDTHH_MM_SS_XXX = "yyyy-MM-dd'T'HH:mm:ssXXX";
    public static final String PATTERN_YYYY_MM_DDTHH_MM_SS_SSSXXX = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    public static final String PATTERN_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String PATTERN_YYYYMMDDHHMM = "yyyyMMddHHmm";
    public static final String PATTERN_YYYYMMDDHH = "yyyyMMddHH";
    public static final String PATTERN_YYYYMMDD = "yyyyMMdd";
    public static final String PATTERN_YYYYMM = "yyyyMM";

    /**
     * 通过字符串时间数据获取秒数
     *
     * @param time         时间
     * @param inputPattern 时间pattern
     * @return
     */
    public static Long getSeconds(String time, String inputPattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(inputPattern);
        TemporalAccessor temporalAccessor = formatter.parse(time);
        if(temporalAccessor.isSupported(ChronoField.INSTANT_SECONDS)){
            return Instant.from(temporalAccessor).getEpochSecond();
        }else{
            LocalDateTime localDateTime = LocalDateTime.parse(time, formatter);
            Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
            return instant.getEpochSecond();
        }
    }

    /**
     * 通过时间字符串数据获取毫秒数
     *
     * @param time         时间
     * @param inputPattern 时间pattern
     * @return
     */
    public static Long getMilliSeconds(String time, String inputPattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(inputPattern);
        TemporalAccessor temporalAccessor = formatter.parse(time);
        if(temporalAccessor.isSupported(ChronoField.INSTANT_SECONDS)){
            return Instant.from(temporalAccessor).toEpochMilli();
        }else{
            LocalDateTime localDateTime = LocalDateTime.parse(time, formatter);
            Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
            return instant.toEpochMilli();
        }
    }

    /**
     * 通过时间字符串数据与数据格式pattern获取指定pattern日期格式
     *
     * @param time          时间
     * @param inputPattern  输入时间pattern
     * @param outputPattern 输出时间pattern
     * @return
     */
    public static String getFormatDate(String time, String inputPattern, String outputPattern) {
        TemporalAccessor ta = DateTimeFormatter.ofPattern(inputPattern).parse(time);
        String format = DateTimeFormatter.ofPattern(outputPattern).format(ta);
        return format;
    }

    /**
     * 通过时间字符串数据（带时区）与数据格式pattern获取指定pattern日期格式
     *
     * @param time          时间（带时区）
     * @param zoneId        输出时区
     * @param inputPattern  输入时间pattern（带时区）
     * @param outputPattern 输出时间pattern
     * @return
     */
    public static String getFormatDate(String time, String zoneId, String inputPattern, String outputPattern) {
        TemporalAccessor ta = DateTimeFormatter.ofPattern(inputPattern).parse(time);
        String format = DateTimeFormatter.ofPattern(outputPattern).withZone(ZoneId.of(zoneId)).format(ta);
        return format;
    }

    /**
     * 通过时间字符串数据（带时区）与数据格式pattern获取指定pattern日期格式
     *
     * @param time          时间（带时区）
     * @param offsetHour   时区小时差值
     * @param inputPattern  输入时间pattern（带时区）
     * @param outputPattern 输出时间pattern
     * @return
     */
    public static String getFormatDate(String time, Integer offsetHour, String inputPattern, String outputPattern) {
        return getFormatDate(time, ZoneOffset.ofHours(offsetHour).getId(), inputPattern, outputPattern);
    }

    /**
     * 通过时间字符串数据（不带时区）与数据格式pattern获取指定pattern日期格式，使用系统时区做为输入时区
     *
     * @param time          时间（不带时区）
     * @param zoneId        输出时区ID
     * @param inputPattern  输入时间pattern（不带时区）
     * @param outputPattern 输出时间pattern
     * @return
     */
    public static String getFormatDateByDefaultZone(String time, String zoneId, String inputPattern, String outputPattern) {
        TemporalAccessor ta = DateTimeFormatter.ofPattern(inputPattern).withZone(ZoneId.systemDefault()).parse(time);
        String format = DateTimeFormatter.ofPattern(outputPattern).withZone(ZoneId.of(zoneId)).format(ta);
        return format;
    }

    /**
     * 通过时间字符串数据（不带时区）与数据格式pattern获取指定pattern日期格式，使用系统时区做为输入时区
     *
     * @param time          时间（不带时区）
     * @param offsetHour    时区小时差值
     * @param inputPattern  输入时间pattern（不带时区）
     * @param outputPattern 输出时间pattern
     * @return
     */
    public static String getFormatDateByDefaultZone(String time, Integer offsetHour, String inputPattern, String outputPattern) {
        return getFormatDateByDefaultZone(time, ZoneOffset.ofHours(offsetHour).getId(), inputPattern, outputPattern);
    }

    /**
     * 使用系统默认时区格式化时间
     *
     * @param time          SECONDS or MILLISECONDS
     * @param timeUnit      TimeUnit.SECONDS or TimeUnit.MILLISECONDS
     * @param outputPattern 输出时间pattern
     * @return
     */
    public static String getFormatDate(Long time, TimeUnit timeUnit, String outputPattern) {
        Instant instant = getInstant(time, timeUnit);

        String format = DateTimeFormatter.ofPattern(outputPattern).withZone(ZoneId.systemDefault()).format(instant);
        return format;
    }

    /**
     * 使用指定时区格式化时间
     *
     * @param time          SECONDS or MILLISECONDS
     * @param timeUnit      TimeUnit.SECONDS or TimeUnit.MILLISECONDS
     * @param zoneId        时区ID
     * @param outputPattern 输出时间pattern
     * @return
     */
    public static String getFormatDate(Long time, TimeUnit timeUnit, String zoneId, String outputPattern) {
        Instant instant = getInstant(time, timeUnit);
        String format = DateTimeFormatter.ofPattern(outputPattern).withZone(ZoneId.of(zoneId)).format(instant);
        return format;
    }

    /**
     * 使用指定时区格式化时间
     *
     * @param time          SECONDS or MILLISECONDS
     * @param timeUnit      TimeUnit.SECONDS or TimeUnit.MILLISECONDS
     * @param offsetHour    时区小时差值
     * @param outputPattern 输出时间pattern
     * @return
     */
    public static String getFormatDate(Long time, TimeUnit timeUnit, Integer offsetHour, String outputPattern) {
        return getFormatDate(time, timeUnit, ZoneOffset.ofHours(offsetHour).getId(), outputPattern);
    }

    /**
     * 获取Instant
     *
     * @param time     SECONDS or MILLISECONDS
     * @param timeUnit TimeUnit.SECONDS or TimeUnit.MILLISECONDS
     * @return
     */
    public static Instant getInstant(Long time, TimeUnit timeUnit) {
        Instant instant;
        if (TimeUnit.SECONDS == timeUnit) {
            instant = Instant.ofEpochSecond(time);
        } else if (TimeUnit.MILLISECONDS == timeUnit) {
            instant = Instant.ofEpochMilli(time);
        } else {
            throw new RuntimeException("unsupport TimeUnit...,only support TimeUnit.SECONDS and TimeUnit.MILLISECONDS");
        }
        return instant;
    }


}
