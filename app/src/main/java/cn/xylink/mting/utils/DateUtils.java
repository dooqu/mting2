package cn.xylink.mting.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author LIUHE
 */
public class DateUtils {
    public static final String YMD = "yyyyMMdd";
    public static final String YMD_YEAR = "yyyy";
    public static final String YMD_BREAK = "yyyy-MM-dd";
    public static final String YMDHMS = "yyyyMMddHHmmss";
    public static final String YMDHMS_BREAK = "yyyy-MM-dd HH:mm:ss";
    public static final String YMDHMS_BREAK_HALF = "yyyy-MM-dd HH:mm";
    public static final String YMD_M_ZH = "yyyy年MM月";

    /**
     * 计算时间差
     */
    public static final int CAL_MINUTES = 1000 * 60;
    public static final int CAL_HOURS = 1000 * 60 * 60;
    public static final int CAL_DAYS = 1000 * 60 * 60 * 24;

    /**
     * 获取当前时间格式化后的值
     *  
     *
     * @param pattern
     * @return
     */
    public static String getNowDateText(String pattern) {
        SimpleDateFormat sdf = getSimpleDateFormat(pattern);
        return sdf.format(new Date());
    }

    /**
     * 获取日期格式化后的值
     *  
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String getDateText(Date date, String pattern) {
        SimpleDateFormat sdf = getSimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 字符串时间转换成Date格式
     *  
     *
     * @param date
     * @param pattern
     * @return
     * @throws ParseException
     */
    public static Date getDate(String date, String pattern) throws ParseException {
        SimpleDateFormat sdf = getSimpleDateFormat(pattern);
        return sdf.parse(date);
    }

    private static SimpleDateFormat getSimpleDateFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    /**
     * 获取时间戳
     *
     * @param date
     * @return
     */
    public static Long getTime(Date date) {
        return date.getTime();
    }

    /**
     * 计算时间差
     *
     * @param startDate
     * @param endDate
     * @param calType   计算类型,按分钟、小时、天数计算
     * @return
     */
    public static int calDiffs(Date startDate, Date endDate, int calType) {
        Long start = DateUtils.getTime(startDate);
        Long end = DateUtils.getTime(endDate);
        int diff = (int) ((end - start) / calType);
        return diff;
    }

    /**
     * 计算时间差值以某种约定形式显示
     *  十朋帖子详情显示
     *
     * @param createAt
     * @return
     */
    public static String timeDiffText(long createAt) {
        long time = System.currentTimeMillis() - createAt;
        if (time < 0) {
            time = 0;
        }
        long timestapt = time / 1000;
        SimpleDateFormat sdf;
        if (timestapt == 0) {
            return "刚刚";
        } else if (timestapt > 0 && timestapt <= 60 * 60) {
            return (timestapt / 60) + "分钟前";
        } else if (timestapt > 60 * 60 && timestapt < 60 * 60 * 24) {
            return (timestapt / 60 / 60) + "小时前";
        } else if (timestapt >= 60 * 60 * 24 && timestapt < 60 * 60 * 24 * 2) {
            return "1天前";
        } else if (timestapt >= 60 * 60 * 24 * 2 && timestapt < 60 * 60 * 24 * 3) {
            return "2天前";
        } else if (timestapt >= 60 * 60 * 24 * 3 && timestapt < 60 * 60 * 24 * 4) {
            return "3天前";
        } else {
            sdf = new SimpleDateFormat("MM-dd");
        }
        return sdf.format(new Date(createAt));
    }

    /**
     * 时间间隔显示
     *
     * @param createAt
     * @return
     */
    public static String formatListDate(long createAt) {
        long time = System.currentTimeMillis() - createAt;
        if (time < 1) {
            time = 1;
        }
        long timestapt = time / 1000;
        if (timestapt >= 0 && timestapt < 60 * 60) {
            long l = timestapt / 60;
            if (l < 1) {
                l = 1;
            }
            return l + "分钟前";
        } else if (timestapt < 60 * 60 * 24) {
            long l = timestapt / 60 / 60;
            if (l < 1) {
                l = 1;
            }
            return l + "小时前";
        } else if (timestapt < 60 * 60 * 24 * 2) {
            return "昨天" + new SimpleDateFormat(" HH:mm").format(createAt);
        } else {
            SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
            String ca1 = sdfYear.format(createAt);
            String ca2 = sdfYear.format(System.currentTimeMillis());
            if (ca1.equals(ca2)) {
                return new SimpleDateFormat("MM-dd HH:mm").format(createAt);
            }
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(createAt);
        }
    }

    public static String formatListDateYY(long createAt) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
        return sdf.format(new Date(createAt));
    }

    public static String formatListDateYY2(long createAt) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-1");
        return sdf.format(new Date(createAt));
    }


    private static boolean isSameYear(long inputTime) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        final int startTime = cal.getActualMinimum(Calendar.DAY_OF_YEAR);
        cal.set(Calendar.DAY_OF_YEAR, startTime);
        long start = cal.getTime().getTime();

        final int endtime = cal.getActualMaximum(Calendar.DAY_OF_YEAR);
        cal.set(Calendar.DAY_OF_YEAR, endtime);
        long end = cal.getTime().getTime();
        if (inputTime > start && inputTime < end)
            return true;
        return false;
    }

}
