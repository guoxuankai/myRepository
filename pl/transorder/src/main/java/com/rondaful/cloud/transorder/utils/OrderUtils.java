package com.rondaful.cloud.transorder.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class OrderUtils {

    /**
     * ERP跟踪号生成
     *
     * @return
     */
    public static String getPLTrackNumber() {
        return "TK" + generateRandomString("HHmmssSSS");
    }

    /*
     * 品连系统订单号生成
     * */
    public static String getPLOrderNumber() {
        return "PL" + generateRandomString("yyyyMMddHHmmssSSS");
    }


    /**
     * 品连系统订单项ID生成
     */
    public static String getPLOrderItemNumber() {
        return generateRandomString("ddHHmmssSSS");
    }

    /**
     * 生成指定时间规则的随机字符串
     *
     * @param timePattern 时间格式
     * @return String
     */
    private static String generateRandomString(String timePattern) {
        String[] chars = new String[]{"a", "b", "c", "d", "e", "f",
                "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
                "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
                "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
                "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z"};
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timePattern);
        Date date = new Date();
        String str = simpleDateFormat.format(date);
        return str + shortBuffer.toString();
    }

    /**
     * 将0.0000#CNY#1.00：卖家填的平台物流费转成BigDecimal类型
     */
    public static BigDecimal stringToBigDecimal(String costStr) {
        if (StringUtils.isBlank(costStr)) return BigDecimal.valueOf(0);
        String[] split = StringUtils.split(costStr, "#");
        if (split.length < 3) {
            return new BigDecimal(split[0]);
        } else {
            return new BigDecimal(split[0]).multiply(new BigDecimal(split[2] == null ? "1.00" : split[2]));
        }
    }


}
