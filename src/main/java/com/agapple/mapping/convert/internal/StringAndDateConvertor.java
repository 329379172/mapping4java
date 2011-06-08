package com.agapple.mapping.convert.internal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.agapple.mapping.convert.Convertor;
import com.agapple.mapping.core.BeanMappingException;

/**
 * string <-> Date/Calendar 之间的转化
 * 
 * @author jianghang 2011-5-26 上午09:50:31
 */
public class StringAndDateConvertor {

    public static final String DAY_FORMAT  = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * string(格式为："2010-10-01") -> Calendar
     */
    public static class StringToDateDay implements Convertor {

        @Override
        public Object convert(Object src, Class destClass) {
            if (String.class.isInstance(src)) { // 必须是字符串
                try {
                    return new SimpleDateFormat(DAY_FORMAT).parse((String) src);
                } catch (ParseException e) {
                    throw new BeanMappingException("convert failed: [" + src + "," + destClass.getName() + "]", e);
                }
            }

            throw new BeanMappingException("Unsupported convert: [" + src + "," + destClass.getName() + "]");
        }
    }

    /**
     * string(格式为："2010-10-01 00:00:00") -> Date
     */
    public static class StringToDateTime implements Convertor {

        @Override
        public Object convert(Object src, Class destClass) {
            if (String.class.isInstance(src)) { // 必须是字符串
                try {
                    return new SimpleDateFormat(TIME_FORMAT).parse((String) src);
                } catch (ParseException e) {
                    throw new BeanMappingException("convert failed: [" + src + "," + destClass.getName() + "]", e);
                }
            }

            throw new BeanMappingException("Unsupported convert: [" + src + "," + destClass.getName() + "]");
        }

    }

    /**
     * Date -> string(格式为："2010-10-01")
     */
    public static class DateDayToString implements Convertor {

        @Override
        public Object convert(Object src, Class destClass) {
            if (Date.class.isInstance(src)) { // 必须是Date对象
                return new SimpleDateFormat(DAY_FORMAT).format((Date) src);
            }

            throw new BeanMappingException("Unsupported convert: [" + src + "," + destClass.getName() + "]");
        }

    }

    /**
     * Date -> string(格式为："2010-10-01 00:00:00")
     */
    public static class DateTimeToString implements Convertor {

        @Override
        public Object convert(Object src, Class destClass) {
            if (Date.class.isInstance(src)) { // 必须是Date对象
                return new SimpleDateFormat(TIME_FORMAT).format((Date) src);
            }

            throw new BeanMappingException("Unsupported convert: [" + src + "," + destClass.getName() + "]");
        }

    }

    /**
     * string(格式为："2010-10-01") -> Calendar
     */
    public static class StringToCalendarDay implements Convertor {

        @Override
        public Object convert(Object src, Class destClass) {
            if (String.class.isInstance(src)) { // 必须是字符串
                Date dest = (Date) new StringToDateDay().convert(src, Date.class);
                Calendar result = new GregorianCalendar();
                result.setTime(dest);
                return result;
            }

            throw new BeanMappingException("Unsupported convert: [" + src + "," + destClass.getName() + "]");
        }
    }

    /**
     * string(格式为："2010-10-01 00:00:00") -> Calendar
     */
    public static class StringToCalendarTime implements Convertor {

        @Override
        public Object convert(Object src, Class destClass) {
            if (String.class.isInstance(src)) { // 必须是字符串
                Date dest = (Date) new StringToDateTime().convert(src, Date.class);
                Calendar result = new GregorianCalendar();
                result.setTime(dest);
                return result;
            }

            throw new BeanMappingException("Unsupported convert: [" + src + "," + destClass.getName() + "]");
        }
    }

    /**
     * Calendar -> string(格式为："2010-10-01")
     */
    public static class CalendarDayToString implements Convertor {

        @Override
        public Object convert(Object src, Class destClass) {
            if (Calendar.class.isInstance(src)) { // 必须是Calendar
                Calendar ca = (Calendar) src;
                return new DateDayToString().convert(ca.getTime(), String.class);
            }

            throw new BeanMappingException("Unsupported convert: [" + src + "," + destClass.getName() + "]");
        }
    }

    /**
     * Calendar -> string(格式为："2010-10-01 00:00:00")
     */
    public static class CalendarTimeToString implements Convertor {

        @Override
        public Object convert(Object src, Class destClass) {
            if (Calendar.class.isInstance(src)) { // 必须是Calendar
                Calendar ca = (Calendar) src;
                return new DateTimeToString().convert(ca.getTime(), String.class);
            }

            throw new BeanMappingException("Unsupported convert: [" + src + "," + destClass.getName() + "]");
        }
    }
}
