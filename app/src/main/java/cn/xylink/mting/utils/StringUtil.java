package cn.xylink.mting.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

import java.io.File;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.xylink.mting.R;

import static okhttp3.internal.Util.closeQuietly;

public class StringUtil {
    private static final String TAG = StringUtil.class.getSimpleName();

    public static final String sRegPhoneNumber = "^[0-9]*$";
    public static final String sRegMatcherNumber = "^[A-Za-z0-9]+$";
    public static final String EMAIL_PATTERN_CODE = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
    public static final String sRegEx = "[`~!@#$%^&*()+=\\-\\s*|\t|\r|\n|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
    public static final String sRegEx1 = "\\+|(?<=\\d)-|\\*|/|&|=|(>=)|(<=)";

    /**
     * 去除浮点类型无效的0和"."
     *
     * @param value
     * @return
     */
    public static String getFormateValue(float value) {
        try {
            String strValue = value + "";
            int len = strValue.length();
            if (strValue.contains(".")) {
                for (int i = len; i > 0; i--) {
                    if (strValue.endsWith("0")) {
                        strValue = strValue.substring(0, strValue.length() - 1);
                        continue;
                    } else if (strValue.endsWith(".")) {
                        strValue = strValue.substring(0, strValue.length() - 1);
                        break;
                    } else {
                        break;
                    }
                }
            }
            return strValue;
        } catch (Exception e) {
            e.printStackTrace();
            return String.valueOf(value);
        }
    }

    // 设置错误提示内容的文本颜色
    public static CharSequence setTextColor(CharSequence text, int color, int start, int end) {
        final ForegroundColorSpan fc = new ForegroundColorSpan(color);
        final SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        ssb.setSpan(fc, start, end, 0);
        return ssb;
    }

    //设置本文字体大小与颜色
    public static SpannableString setTextSizeAndColor(CharSequence text, int color, int size, int start, int end) {
        SpannableString msp = new SpannableString(text);
        msp.setSpan(new AbsoluteSizeSpan(size, true), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return msp;
    }

    // 设置错误提示内容的文本颜色
    public static CharSequence setTextColor(CharSequence text, int color) {
        return setTextColor(text, color, 0, text.length());
    }

    /*
     * Convert byte[] to hex
     * string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
     * @param src byte[] data
     * @return hex string
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /*
         * Convert byte[] to hex
         * string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
         * @param src byte[] data
         * @return hex string
         */
    public static String bytesToDecString(byte[] src) {
        return new BigInteger(1, src).toString(10);// 这里的1代表正数
    }

    /**
     * Convert hex string to byte[]
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 生成6字节交易流水号
     *
     * @return
     */
    public static String generateSeq() {
        return generateRandom(6);
    }

    /**
     * 生成指定字节的随机数
     *
     * @param byteSize
     * @return
     */
    public static String generateRandom(int byteSize) {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[byteSize];
        random.nextBytes(bytes);
        // return new String(bytes);
        return bytesToHexString(bytes);
    }

    /**
     * String转MD5
     *
     * @param inStr
     * @return
     */
    public static String string2MD5(String inStr) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();

    }

    /**
     * 获得文件的md5值
     */
    public static String getFileMd5(byte[] bytes) {
        String fileMd5 = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bytes);
            BigInteger bigInt = new BigInteger(1, md.digest());
            fileMd5 = bigInt.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return fileMd5;
    }

    //file文件读取成byte[]
    public static byte[] file2bytes(File file) {
        RandomAccessFile rf = null;
        byte[] data = null;
        try {
            rf = new RandomAccessFile(file, "r");
            data = new byte[(int) rf.length()];
            rf.readFully(data);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            closeQuietly(rf);
        }
        return data;
    }

    /**
     * 获取字节数组对应的字节的长度
     *
     * @return
     */
    public static int getBALength(byte[] bytes) {
        if ((bytes.length % 8) == 0) {
            return bytes.length / 8;
        } else {
            return bytes.length / 8 + 1;
        }
    }

    public static String stringToHexString(String strPart) {
        String hexString = "";
        for (int i = 0; i < strPart.length(); i++) {
            int ch = (int) strPart.charAt(i);
            String strHex = Integer.toHexString(ch);
            hexString = hexString + strHex;
        }
        return hexString;
    }

    public static String bigIntegerAddOne(String seq) {
        BigInteger seqBI = new BigInteger(seq, 16);
        BigInteger oneHex = new BigInteger("1", 16);
        return seqBI.add(BigInteger.ONE).toString(16);
//        return seqBI.add(oneHex).toString(16);
    }

    public static String getSeidFormat(String seid) {

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(System.getProperty("MD5.algorithm", "MD5"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] seidBytes = hexStringToBytes(seid);
        byte[] tempBytes = md.digest(seidBytes);
        byte[] resultBytes = new byte[7];
        System.arraycopy(tempBytes, 5, resultBytes, 0, resultBytes.length);
        return "e0" + bytesToHexString(resultBytes);

//        String temp = null;
//        try {
//            temp = md5(seid, 32);
//            Log.i(TAG, "temp:" + temp);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        byte[] tempBytes = hexStringToBytes(temp);
//        byte[] resultBytes = new byte[7];
//        System.arraycopy(tempBytes, 5, resultBytes, 0, resultBytes.length);
//        return "E0" + bytesToHexString(resultBytes);
    }

    /**
     * @param input
     * @param bit   表示希望得到的字符串的长度
     *              * @return
     * @throws Exception
     */
    public static String md5(String input, int bit) throws Exception {
        try {
            MessageDigest md = MessageDigest.getInstance(System.getProperty("MD5.algorithm", "MD5"));
            if (bit == 16)
                return bytesToHexString(md.digest(input.getBytes("utf-8"))).substring(8, 24);
            return bytesToHexString(md.digest(input.getBytes("utf-8")));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new Exception("Could not found MD5 algorithm.", e);
        }
    }

    public static boolean checkPhone(Context context, String phone) {
//        if (TextUtils.isEmpty(phone)) {
//            T.l(context, context.getString(R.string.tel_not_empty));
//            return false;
//        }
//        Pattern pattern = Pattern.compile("[0-9]{11}");
//        Matcher isNum = pattern.matcher(phone);
//        if (!isNum.matches()) {
//            T.l(context, context.getString(R.string.tel_rule));
//            return false;
//        }
        return true;
    }

    public static boolean checkEmail(Context context, String email) {
//        if (TextUtils.isEmpty(email)) {
//            return true;
//        }
//        Pattern p = Pattern.compile(EMAIL_PATTERN_CODE);
//        Matcher isNum = p.matcher(email);
//
//        if (!isNum.matches()) {
//            T.l(context, context.getString(R.string.email_rule));
//            return false;
//        }
        return true;
    }

    public static boolean checkPhoneNumber(String phoneNumber) {
        String regEx = "^((13[0-9])|(15[0-9])|(18[0-9])|(14[0-9])|(17[0-9]))\\d{8}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(phoneNumber);
        // 正则匹配，匹配成功返回true，否则false
        return matcher.matches();
    }

    public static boolean checkPwd(String pwd) {
        String regEx = "^[a-zA-Z][~!@#\\$%\\^&\\*\\?a-zA-Z0-9_]{5,19}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(pwd);
        // 正则匹配，匹配成功返回true，否则false
        return matcher.matches();
    }

    /**
     * 判断字符数是否由字母，数字组成
     *
     * @param str
     * @return
     */
    public static boolean isPw(String str) {
        String pipeRegex = "^[0-9A-Za-z]{6,10}$";
        Pattern pipePattern = Pattern.compile(pipeRegex, Pattern.CASE_INSENSITIVE);
        return pipePattern.matcher(str).matches();
    }

    /**
     * 判断字符串数是否url
     *
     * @param str
     * @return
     */
    public static boolean isURL(String str) {
        String pipeRegex = "(http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>;]*)?|([a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>;]*)?)";
        Pattern pipePattern = Pattern.compile(pipeRegex, Pattern.CASE_INSENSITIVE);
        return pipePattern.matcher(str).matches();
    }

    /**
     * 是否是空的字符串
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    /**
     * 是否是非空的字符串
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !"".equals(str);
    }

    /**
     * 获得汉语拼音首字母
     *
     * @param str
     * @return
     */
    public static String getAlpha(String str) {
        if (str == null) {
            return "#";
        }

        if (str.trim().length() == 0) {
            return "#";
        }

        char c = str.trim().substring(0, 1).charAt(0);
        // 正则表达式，判断首字母是否是英文字母
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c + "").matches()) {
            return (c + "").toUpperCase();
        } else {
            return "#";
        }
    }

    /**
     * 根据日期解析出当前星期几
     *
     * @param data
     * @return
     */
    public static String dayForWeek(Date data) {
        Calendar c = Calendar.getInstance();
        c.setTime(data);
        String dayForWeek = null;
        switch (c.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                dayForWeek = "日";
                break;
            case 2:
                dayForWeek = "一";
                break;
            case 3:
                dayForWeek = "二";
                break;
            case 4:
                dayForWeek = "三";
                break;
            case 5:
                dayForWeek = "四";
                break;
            case 6:
                dayForWeek = "五";
                break;
            case 7:
                dayForWeek = "六";
                break;
            default:
                break;
        }
        return dayForWeek;
    }

    /**
     * 截取日期
     *
     * @param time   日期格式:yyyy-MM-dd HH:mm:ss
     * @param length 从左截取个数
     * @return
     */
    public static String getSubTime(String time, int length) {
        String newSubTime = "";
        if (StringUtil.isNotEmpty(time)) {
            newSubTime = time.substring(0, length);
        }
        return newSubTime;
    }

    /**
     * 日期转换成字符串
     *
     * @param date 日期
     * @return str 转换的格式
     */
    public static String DateToStr(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String str = sdf.format(date);
        return str;
    }

    /**
     * 把date类型转换成yyyy-MM-dd HH:mm:ss格式的字符串
     *
     * @param date
     * @return
     */
    public static String DateToStr(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = sdf.format(date);
        return str;
    }

    /**
     * 把long类型转换成yyyy-MM-dd HH:mm:ss格式的字符串
     *
     * @param date
     * @return
     */
    public static String longToStr(long date) {
        return DateToStr(new Date(date));
    }

    /**
     * 字符串转换成日期
     *
     * @param str
     * @return date
     */
    public static Date strToDate(String str, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static final SimpleDateFormat sdf_ = new SimpleDateFormat("HHmmss");

    public static String formatTime(String str) {
        try {
            Date date = sdf_.parse(str);
            return DateToStr(date, "HH:mm:ss");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    public static String formatDate(String str) {
        try {
            Date date = sdf.parse(str);
            return DateToStr(date, "yyyy-MM-dd");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 打电话
     *
     * @param context
     * @param tel
     */
    public static void call(Context context, String tel) {
        if (StringUtil.isNotEmpty(tel)) {
            String sz = "tel:" + tel;
            Uri uri = Uri.parse(sz);
            Intent intent = new Intent(Intent.ACTION_DIAL, uri);
            context.startActivity(intent);
        }
    }

    /**
     * 格式化价格
     *
     * @return
     */
    public static String formatPrice(String price) {
        String newprice = "0.00";
        if (StringUtil.isNotEmpty(price)) {
            DecimalFormat df = new DecimalFormat("#######0.00");
            newprice = df.format(Double.valueOf(price));
        }
        return newprice;
    }

    /**
     * 格式化价格
     *
     * @return
     */
    public static String formatPrice(Double price) {
        String newprice = "0.00";
        DecimalFormat df = new DecimalFormat("#######0.00");
        newprice = df.format(price);
        return newprice;
    }

    /**
     * 字符串转换成日期 (默认格式：yyyy-MM-dd HH:mm:ss)
     *
     * @param str
     * @return date
     */
    public static Date strToDate(String str) {
        if (StringUtil.isNotEmpty(str)) {
            return strToDate(str, "yyyy-MM-dd HH:mm:ss");
        }
        return null;
    }

    /**
     * 比较时间大小
     *
     * @param firsttime
     * @param secondtime
     * @return true:fristtime>secondtime,false:fristtime<=secondtime
     */
    public static Boolean isgt(String firsttime, String secondtime)// is greater
    // than
    {
        Boolean isFlag = false;
        Date firstdate = strToDate(firsttime);
        Date seconddate = strToDate(secondtime);
        if (firstdate.getTime() > seconddate.getTime()) {
            isFlag = true;
        }
        return isFlag;

    }

    /**
     * 截取字符串（超过一定长度加...）
     *
     * @param text
     * @param len  个数
     * @return
     */
    public static String getEllipsisString(String text, int len) {
        String newString = text;
        if (StringUtil.isNotEmpty(text) && text.length() > len) {
            newString = text.subSequence(0, len - 1) + "...";
        }
        return newString;
    }

    /**
     * 不够2位用0补齐
     *
     * @param str
     * @return
     */
    public static String LeftPad_Tow_Zero(int str) {
        DecimalFormat format = new DecimalFormat("00");
        return format.format(str);

    }

    /**
     * 转换大小写
     *
     * @param str
     * @return
     */
    public static String getCnString(String str) {
        Double n = StringToDouble(str);
        String fraction[] = {"角", "分"};
        String digit[] = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
        String unit[][] = {{"元", "万", "亿"}, {"", "拾", "佰", "仟"}};

        String head = n < 0 ? "负" : "";
        n = Math.abs(n);

        String s = "";
        for (int i = 0; i < fraction.length; i++) {
            s += (digit[(int) (Math.floor(n * 10 * Math.pow(10, i)) % 10)] + fraction[i]).replaceAll("(零.)+", "");
        }
        if (s.length() < 1) {
            s = "整";
        }
        int integerPart = (int) Math.floor(n);

        for (int i = 0; i < unit[0].length && integerPart > 0; i++) {
            String p = "";
            for (int j = 0; j < unit[1].length && n > 0; j++) {
                p = digit[integerPart % 10] + unit[1][j] + p;
                integerPart = integerPart / 10;
            }
            s = p.replaceAll("(零.)*零$", "").replaceAll("^$", "零") + unit[0][i] + s;
        }
        return head + s.replaceAll("(零.)*零元", "元").replaceFirst("(零.)+", "").replaceAll("(零.)+", "零").replaceAll("^整$", "零元整");
    }

    /**
     * String转Double
     *
     * @param str
     * @return
     */
    public static Double StringToDouble(String str) {

        if (StringUtil.isEmpty(str)) {
            return 0.0;
        }
        try {
            return Double.valueOf(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * 摘自commons-lang.jar
     *
     * @return String
     */
    public static String replaceEach(String text, String[] searchList, String[] replacementList) {
        return replaceEach(text, searchList, replacementList, false, 0);
    }

    private static String replaceEach(String text, String[] searchList, String[] replacementList, boolean repeat, int timeToLive) {

        // mchyzer Performance note: This creates very few new objects (one
        // major goal)
        // let me know if there are performance requests, we can create a
        // harness to measure

        if (text == null || text.length() == 0 || searchList == null || searchList.length == 0 || replacementList == null || replacementList.length == 0) {
            return text;
        }

        // if recursing, this shouldn't be less than 0
        if (timeToLive < 0) {
            throw new IllegalStateException("Aborting to protect against StackOverflowError - " + "output of one loop is the input of another");
        }

        int searchLength = searchList.length;
        int replacementLength = replacementList.length;

        // make sure lengths are ok, these need to be equal
        if (searchLength != replacementLength) {
            throw new IllegalArgumentException("Search and Replace array lengths don't match: " + searchLength + " vs " + replacementLength);
        }

        // keep track of which still have matches
        boolean[] noMoreMatchesForReplIndex = new boolean[searchLength];

        // index on index that the match was found
        int textIndex = -1;
        int replaceIndex = -1;
        int tempIndex = -1;

        // index of replace array that will replace the search string found
        // NOTE: logic duplicated below START
        for (int i = 0; i < searchLength; i++) {
            if (noMoreMatchesForReplIndex[i] || searchList[i] == null || searchList[i].length() == 0 || replacementList[i] == null) {
                continue;
            }
            tempIndex = text.indexOf(searchList[i]);

            // see if we need to keep searching for this
            if (tempIndex == -1) {
                noMoreMatchesForReplIndex[i] = true;
            } else {
                if (textIndex == -1 || tempIndex < textIndex) {
                    textIndex = tempIndex;
                    replaceIndex = i;
                }
            }
        }
        // NOTE: logic mostly below END

        // no search strings found, we are done
        if (textIndex == -1) {
            return text;
        }

        int start = 0;

        // get a good guess on the size of the result buffer so it doesn't have
        // to double if it goes over a bit
        int increase = 0;

        // count the replacement text elements that are larger than their
        // corresponding text being replaced
        for (int i = 0; i < searchList.length; i++) {
            if (searchList[i] == null || replacementList[i] == null) {
                continue;
            }
            int greater = replacementList[i].length() - searchList[i].length();
            if (greater > 0) {
                increase += 3 * greater; // assume 3 matches
            }
        }
        // have upper-bound at 20% increase, then let Java take over
        increase = Math.min(increase, text.length() / 5);

        StringBuilder buf = new StringBuilder(text.length() + increase);

        while (textIndex != -1) {

            for (int i = start; i < textIndex; i++) {
                buf.append(text.charAt(i));
            }
            buf.append(replacementList[replaceIndex]);

            start = textIndex + searchList[replaceIndex].length();

            textIndex = -1;
            replaceIndex = -1;
            tempIndex = -1;
            // find the next earliest match
            // NOTE: logic mostly duplicated above START
            for (int i = 0; i < searchLength; i++) {
                if (noMoreMatchesForReplIndex[i] || searchList[i] == null || searchList[i].length() == 0 || replacementList[i] == null) {
                    continue;
                }
                tempIndex = text.indexOf(searchList[i], start);

                // see if we need to keep searching for this
                if (tempIndex == -1) {
                    noMoreMatchesForReplIndex[i] = true;
                } else {
                    if (textIndex == -1 || tempIndex < textIndex) {
                        textIndex = tempIndex;
                        replaceIndex = i;
                    }
                }
            }
            // NOTE: logic duplicated above END

        }
        int textLength = text.length();
        for (int i = start; i < textLength; i++) {
            buf.append(text.charAt(i));
        }
        String result = buf.toString();
        if (!repeat) {
            return result;
        }

        return replaceEach(result, searchList, replacementList, repeat, timeToLive - 1);
    }

    /**
     * 去除json串里面的符号
     *
     * @param json
     * @return
     */
    public static String jsonToString(String json) {
        if (StringUtil.isNotEmpty(json)) {
            return json.replace("[", "").replace("]", "").replace("\"", "");
        }
        return null;
    }


    // 分割公交卡号 1234 2345 3456 4567
    public static String splitCardAsn(String cardAsn) {
        String target = "";
        int len = cardAsn.length();
        for (int i = 0; i < len; i++) {
            if ((i + 1) % 4 == 0 && (i + 1) != len) {
                target = target + cardAsn.substring(i, i + 1) + " ";
            } else {
                target = target + cardAsn.substring(i, i + 1);
            }
        }
        return target;
    }

    /**
     * 获取公交卡号
     *
     * @param cardAsn
     * @return
     * @throws Exception
     */
    public static String getCardIdByCardAsn(String cardAsn) throws IllegalArgumentException {


        if (cardAsn == null || cardAsn.length() <= 8) {
            return null;
        }
        String cardId = "";
        int i = cardAsn.length() - 8;
        cardId = cardAsn.substring(0, i) + format2String2(String.valueOf(Integer.parseInt(cardAsn.substring(i), 16)), 8, "0");

        return format2String2(cardId, 16, "0");
    }

    /**
     * 位数不足左补fillStr
     */
    public static String format2String2(String temp, int length, String fillStr) throws IllegalArgumentException {
        if (temp.length() > length) {
            throw new IllegalArgumentException("字段长度异常");
        }
        int clength = length - temp.length();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < clength; i++) {
            sb.append(fillStr);
        }
        sb.append(temp);
        return sb.toString();
    }

    public static String decToHex(int i) {
        return Integer.toHexString(i).toUpperCase();
    }


    public static byte[] getSmailByte(byte[] bytes) {
        byte[] smallCardNoHex = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            smallCardNoHex[i] = bytes[bytes.length - i - 1];
        }
        return smallCardNoHex;
    }

    public static String formatListDate(long createAt) {
        long time = System.currentTimeMillis() - createAt;
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

    public static String matcherUrl(String str){
        if (!TextUtils.isEmpty(str)){
            String regex= "(https?)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";
            Pattern p = Pattern.compile(regex);
            Matcher matcher = p.matcher(str);
            if (matcher.find()){
                return matcher.group();
            }
        }
        return "";
    }

    public static boolean isShieldUrl(Context context,String str){
        if (!TextUtils.isEmpty(str)){
            String[] shield=context.getResources().getStringArray(R.array.shield_url);
            for (String s:shield) {
                if (str.contains(s))
                    return true;
            }
        }
        return false;
    }

}
