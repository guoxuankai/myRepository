package com.rondaful.cloud.order.utils;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.JavaBeanDeserializer;
import com.google.common.collect.Lists;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.model.vo.freight.LogisticsCostEnum;
import com.rondaful.cloud.common.model.vo.freight.LogisticsCostVo;
import com.rondaful.cloud.common.model.vo.freight.SkuGroupVo;
import com.rondaful.cloud.common.model.vo.freight.SupplierGroupVo;
import com.rondaful.cloud.order.constant.Constants;
import com.rondaful.cloud.order.entity.Time;
import com.rondaful.cloud.order.entity.system.SysOrderNew;
import com.rondaful.cloud.order.enums.SkuBindEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrderUtils {

    private static final Logger logger = LoggerFactory.getLogger(ParamCheckUtil.class);
    public static void main(String[] args) {
        /*String str = "阿萨德发生发斯蒂芬   是啊啊发生\\xF0\\x9F\\x8C\\x99阿萨德发生发斯蒂芬   是啊啊发生";
        String string = "Hello, please double check and send me a proper working one. Thank you \uD83D\uDE4F";
        System.out.println(filterEmoji(str));
        System.out.println(filterEmoji(string));

        String string = "编辑发货仓库变更为【金华仓】；邮寄方式变更为【中邮小包-平件】";
        if (string.contains("跟踪号【")) {

            String str1 = string.substring(0, string.indexOf("跟踪号"));
            String str2 = string.substring(str1.length(), string.length());
            String trackNum = str2.substring(str2.lastIndexOf("【") + 1, str2.lastIndexOf("】"));
            System.out.println("_____________" + trackNum + "____________");

       }*/

        /*List<SkuGroupVo> skuGroupVoList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            long n = i % 3;
            SkuGroupVo skuGroupVo = new SkuGroupVo(n, "sku" + i,
                    new Random().nextInt(10),
                    new BigDecimal(new Random().nextInt(10)),
                    new Random().nextInt(2));
            skuGroupVoList.add(skuGroupVo);
        }

        LogisticsCostVo entity = getLogisticsBySkuGroup(skuGroupVoList);
        entity.getSellers().get(0).setSupplierId(0L);
        entity.getSellers().get(0).setSupplierCost(new BigDecimal(20));
        for(SupplierGroupVo item:entity.getSupplier()){
            item.setSupplierId(new Random().nextLong());
            item.setSupplierCost(new BigDecimal(new Random().nextInt(50)));
        }
        String result=FastJsonUtils.toJsonString(entity);
        entity= JSONObject.parseObject(result,LogisticsCostVo.class);

        calcLogisticsCost(entity);

        System.out.println(FastJsonUtils.toJsonString(entity));*/
//
//        BigDecimal one = new BigDecimal("0.1");
//        System.out.println("one" + one.setScale(2, BigDecimal.ROUND_CEILING));
//        System.out.println("one" + one.setScale(2, BigDecimal.ROUND_UP));
//        System.out.println("one" + calculateMoney(one, true));
//        System.out.println("one" + calculateMoney(one, true));
//        System.out.println("one" + calculateMoney(one, false));
//
//        BigDecimal two = new BigDecimal("0.19");
//        System.out.println("two" + two.setScale(2, BigDecimal.ROUND_CEILING));
//        System.out.println("two" + two.setScale(2, BigDecimal.ROUND_UP));
//        System.out.println("two" + calculateMoney(two, true));
//        System.out.println("two" + calculateMoney(two, false));
//        BigDecimal three = new BigDecimal("1");
//        System.out.println("three" + three.setScale(2, BigDecimal.ROUND_CEILING));
//        System.out.println("three" + three.setScale(2, BigDecimal.ROUND_UP));
//        System.out.println("three" + calculateMoney(three, true));
//        System.out.println("three" + calculateMoney(three, false));
//        BigDecimal four = new BigDecimal("0.1901");
//        System.out.println("four" + four.setScale(2, BigDecimal.ROUND_CEILING));
//        System.out.println("four" + four.setScale(2, BigDecimal.ROUND_UP));
//        System.out.println("four" + calculateMoney(four, true));
//        System.out.println("four" + calculateMoney(four, false));
//        BigDecimal five = new BigDecimal("0.01");
//        System.out.println("five" + five.setScale(2, BigDecimal.ROUND_CEILING));
//        System.out.println("five" + five.setScale(2, BigDecimal.ROUND_CEILING));
//        System.out.println("five" + five.setScale(2, BigDecimal.ROUND_UP));
//        System.out.println("five" + calculateMoney(five, true));
//        System.out.println("five" + calculateMoney(five, false));
//        BigDecimal six = new BigDecimal("0.01000000");
//        System.out.println("six" + six.setScale(2, BigDecimal.ROUND_CEILING));
//        System.out.println("six" + six.setScale(2, BigDecimal.ROUND_CEILING));
//        System.out.println("six" + six.setScale(2, BigDecimal.ROUND_UP));
//        System.out.println("six" + calculateMoney(six, true));
//        System.out.println("six" + calculateMoney(six, false));

    }

    public static void checkOrderIsOperate(SysOrderNew orderNew) {
        if (orderNew == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        } else {
            if (orderNew.getIsErrorOrder().equalsIgnoreCase(Constants.isErrorOrder.YES)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "异常订单无法操作，请联系客服进行人工处理。");
            } else {
                orderNew.getSysOrderPackageList().forEach(orderPackage -> {
                    orderPackage.getSysOrderPackageDetailList().forEach(sysOrderPackageDetail -> {
                        if (sysOrderPackageDetail.getBindStatus().equalsIgnoreCase(SkuBindEnum.UNBIND.getValue())) {
                            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "该订单还有未绑定的SKU，无法操作！请联系客服进行人工处理。");
                        }
                    });
                });
            }
        }
    }

    /**
     * 时区转换(按标准时间计算)
     *
     * @param time           时间字符串
     * @param pattern        格式 "yyyy-MM-dd HH:mm"
     * @param nowTimeZone    eg:+8，0，+9，-1 等等
     * @param targetTimeZone 同nowTimeZone
     * @return
     */
    public static String timeZoneTransfer(String time, String pattern, String nowTimeZone, String targetTimeZone) {
        if (StringUtils.isBlank(time)) {
            return "";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT" + nowTimeZone));
        Date date;
        try {
            date = simpleDateFormat.parse(time);
        } catch (Exception e) {
            return "";
        }
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT" + targetTimeZone));
        return simpleDateFormat.format(date);
    }

    /**
     * 订单金额计算
     *
     * @param money                参与计算的金额
     * @param isPlPayToOtherOrShow 品连向外付钱or作展示用：true（直接截取小数点后两位）；品连向外收钱的：false（超过小数点后两位，第三位不为0向前进1）
     * @return
     */
    public static BigDecimal calculateMoney(BigDecimal money, Boolean isPlPayToOtherOrShow) {
        if (null == money) {
            return null;
        }

        if (money.compareTo(new BigDecimal("0")) == 0) {
            return money.setScale(2, BigDecimal.ROUND_DOWN);
        }
        BigDecimal OneHundred = new BigDecimal(100);
        String[] moneyStrArray = StringUtils.split(money.toString(), ".");
        if (moneyStrArray.length < 2) {
            // 只有个位数
            return money.multiply(OneHundred).divide(OneHundred, 2, BigDecimal.ROUND_HALF_UP);
        }

        // 小数位
        String decimalPlaceStr = moneyStrArray[1];
        if (decimalPlaceStr.length() == 1) {
            // 只有十分位
            return money.multiply(OneHundred).divide(OneHundred, 2, BigDecimal.ROUND_HALF_UP);
        }

        if (decimalPlaceStr.length() == 2) {
            return money;
        }

//        // 截取百分位之后的数据
//        String afterPercentile = decimalPlaceStr.substring(1,decimalPlaceStr.length());
//        if (new BigDecimal(afterPercentile).compareTo(new BigDecimal("0")) == 0) {
//            // 百分位之后都是0
//            return money.multiply(OneHundred).divide(OneHundred, 2, BigDecimal.ROUND_HALF_UP);
//        }

        if (isPlPayToOtherOrShow) {  //向外付钱或者作展示用
            return money.setScale(2, BigDecimal.ROUND_DOWN);  //小数点后超过两位的直接截取两位
        } else {  //向外收钱
            return money.setScale(2, BigDecimal.ROUND_UP);  //超过2位 小数点后第三位不为0的进1
        }
    }

    /**
     * 过滤Emoji表情字符
     *
     * @param str
     * @return
     */
    public static String filterEmoji(String str) {

        if (str.trim().isEmpty()) {
            return str;
        }
        String pattern = "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]";
        String reStr = "";
        Pattern emoji = Pattern.compile(pattern);
        Matcher emojiMatcher = emoji.matcher(str);
        str = emojiMatcher.replaceAll(reStr);
        return str;
    }

    /**
     * 将时间转为格尼尼治标准时间ISO8601
     *
     * @param date
     * @return
     */
    public static String getISO8601Timestamp(Date date) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(date);
        return nowAsISO;
    }

    /**
     * 验证当前时间是否在指定时间后60天内
     *
     * @param str
     * @return
     */
    public static boolean isValidDate(String str) {
        boolean convertSuccess = true;
        //时间格式定义
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取当前时间日期--nowDate
        String nowDate = format.format(new Date());
        //获取30天后的时间日期--maxDate
        Calendar calc = Calendar.getInstance();
        try {
            calc.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str));
            calc.add(Calendar.DAY_OF_MONTH, +60);
            String maxDate = format.format(calc.getTime());
            //设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
//            format.setLenient(false);
            //获取字符串转换后的时间--strDate
            String strDate = format.format(format.parse(str));
            //判断传的STR时间，是否在当前时间之前，且在60天日期之后-----测试的时候打印输出结果
            if (maxDate.compareTo(nowDate) >= 0 && nowDate.compareTo(strDate) >= 0) {
                convertSuccess = true;
            } else {
                convertSuccess = false;
            }
        } catch (ParseException e) {
            convertSuccess = false;
        }
        return convertSuccess;
    }

    /**
     * 毫秒转化时分秒毫秒
     */
    public static String formatTime(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuffer sb = new StringBuffer();
        if (day > 0) {
            sb.append(day + "天");
        }
        if (hour > 0) {
            sb.append(hour + "小时");
        }
        if (minute > 0) {
            sb.append(minute + "分");
        }
        if (second > 0) {
            sb.append(second + "秒");
        }
//        if(milliSecond > 0) {
//            sb.append(milliSecond+"毫秒");
//        }
        return sb.toString();
    }


    /*
     * 获取最后更新时间的时、分、秒、毫秒值（以传进来的时间为标准）
     * */
    public static Time getLastUpdateTimeDetail(String lastUpdateTime, String marketPlaceId) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date t = sdf.parse(lastUpdateTime);
        long time = t.getTime();
        if (marketPlaceId.equals("A2Q3Y263D00KWC")) {  //巴西站点
            time -= 36000000;
        }
        if (marketPlaceId.equals("A2EUQ1WTGCTBG2")) {  //加拿大站点
            time -= 46800000;
        }
        if (marketPlaceId.equals("A1AM78C64UM0Y8")) {  //墨西哥站点
            time -= 50400000;
        }
        if (marketPlaceId.equals("ATVPDKIKX0DER")) {  //美国站点
            time -= 46800000;
        }
        if (marketPlaceId.equals("A1PA6795UKMFR9")) {  //德国站点
            time -= 25200000;
        }
        if (marketPlaceId.equals("A1RKKUPIHCS9HS")) {  //西班牙站点
            time -= 25200000;
        }
        if (marketPlaceId.equals("A13V1IB3VIYZZH")) {  //法国站点
            time -= 25200000;
        }
        if (marketPlaceId.equals("A1F83G8C2ARO7P")) {  //英国站点
            time -= 28800000;
        }
        if (marketPlaceId.equals("A21TJRUUN4KGV")) {  //印度站点
            time -= 9000000;
        }
        if (marketPlaceId.equals("APJ6JRA9NG5V4")) {  //意大利站点
            time -= 25200000;
        }
        if (marketPlaceId.equals("A33AVAJ2PDY3EV")) {  //土耳其站点
            time -= 18000000;
        }
        if (marketPlaceId.equals("A39IBJ37TRP1C6")) {  //澳大利亚站点
            time += 10800000;
        }
        if (marketPlaceId.equals("A1VC38T7YXB528")) {  //日本站点
            time += 3600000;
        }
        if (marketPlaceId.equals("Greenwich")) {  //  转 格林尼治时间
            time -= 28800000;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time));
        return new Time(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get
                (Calendar.MINUTE), cal.get(Calendar.SECOND), cal.get(Calendar.MILLISECOND));
    }

    /*
     * ISO8601时间格式转为yyyy-MM-dd HH:mm:ss
     * */
    public static Date getTimeToDate(String ISO8601) throws ParseException {
        Date date = ISO8601Utils.parse(ISO8601, new ParsePosition(0));
        return date;
    }

    public static Date getTime(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        Date time = sdf.parse(date);
        return time;
    }

    public static Date getTime2(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date time = sdf.parse(date);
        return time;
    }


    /**
     * 品连系统订单项ID生成
     */
    public static String getPLOrderItemNumber() {
        return generateRandomString("ddHHmmssSSS");
    }

    /*
     * 品连系统订单号生成
     * */
    public static String getPLOrderNumber() {
        return "PL" + generateRandomString("yyyyMMddHHmmssSSS");
    }

    /**
     * ERP跟踪号生成
     *
     * @return
     */
    public static String getPLTrackNumber() {
        return "TK" + generateRandomString("HHmmssSSS");
    }

    /**
     * 星商跟踪号生成
     *
     * @return
     */
    public static String getXSTrackNumber() {
        return Constants.DistributionType.DistributionType_QT + generateRandomString("HHmmssSSS");
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

    /*
     * 将List集合平均分成几等份
     * */
    public static <T> List<List<T>> averageAssign(List<T> source, int n) {
        List<List<T>> result = new ArrayList<List<T>>();
        int remaider = source.size() % n;  //(先计算出余数)
        int number = source.size() / n;  //然后是商
        int offset = 0;//偏移量
        for (int i = 0; i < n; i++) {
            List<T> value = null;
            if (remaider > 0) {
                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                remaider--;
                offset++;
            } else {
                value = source.subList(i * number + offset, (i + 1) * number + offset);
            }
            result.add(value);
        }
        return result;
    }

    /**
     * 拆分集合
     *
     * @param <T>           泛型对象
     * @param resList       需要拆分的集合
     * @param subListLength 每个子集合的元素个数
     * @return 返回拆分后的各个集合组成的列表
     * 代码里面用到了guava和common的结合工具类
     **/
    public static <T> List<List<T>> split(List<T> resList, int subListLength) {
        if (CollectionUtils.isEmpty(resList) || subListLength <= 0) {
            return Lists.newArrayList();
        }
        List<List<T>> ret = Lists.newArrayList();
        int size = resList.size();
        if (size <= subListLength) {
            // 数据量不足 subListLength 指定的大小
            ret.add(resList);
        } else {
            int pre = size / subListLength;
            int last = size % subListLength;
            // 前面pre个集合，每个大小都是 subListLength 个元素
            for (int i = 0; i < pre; i++) {
                List<T> itemList = Lists.newArrayList();
                for (int j = 0; j < subListLength; j++) {
                    itemList.add(resList.get(i * subListLength + j));
                }
                ret.add(itemList);
            }
            // last的进行处理
            if (last > 0) {
                List<T> itemList = Lists.newArrayList();
                for (int i = 0; i < last; i++) {
                    itemList.add(resList.get(pre * subListLength + i));
                }
                ret.add(itemList);
            }
        }
        return ret;
    }

    /**
     * 响应输出
     */
    public static void print(Object object) throws IOException {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        HttpServletResponse response = requestAttributes.getResponse();
        response.addHeader("Cache-Control", "no-cache");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        out.print(object);
        out.flush();
        out.close();
    }


    /**
     * 判断用户是否移动端访问
     * android : 所有android设备
     * mac os : iphone ipad
     * windows phone:Nokia等windows系统的手机
     */
    public static boolean isMobileDevice() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String requestHeader = request.getHeader("user-agent");
        String[] deviceArray = {"android", "mac os", "windows phone"};
        if (requestHeader == null)
            return false;
        requestHeader = requestHeader.toLowerCase();
        for (String mobile : deviceArray) {
            if (requestHeader.indexOf(mobile) > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 随机流水号UUID
     */
    public static String getSerialNumber() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    /**
     * 判断是否为javabean
     *
     * @param type
     * @return
     */
    public static final boolean isJavaBean(Type type) {
        if (null == type) throw new NullPointerException();
        return ParserConfig.global.getDeserializer(type) instanceof JavaBeanDeserializer;
    }

    /**
     * 根据Sku信息,获取物流计算对象
     *
     * @param list
     * @return
     */
    public static LogisticsCostVo getLogisticsBySkuGroup(List<SkuGroupVo> list) {


        SupplierGroupVo supplier = new SupplierGroupVo();
        supplier.setItems(list);

        List<SupplierGroupVo> suppliers = new ArrayList<>();
        suppliers.add(supplier);

        LogisticsCostVo entity = new LogisticsCostVo();
        entity.setSellers(suppliers);
        entity.setSupplier(getSupplierGroup(list));
        return entity;
    }

    /**
     * 根据供应商ID对sku进行分组
     *
     * @param list 分组对象
     * @return 供应商分组
     */
    public static List<SupplierGroupVo> getSupplierGroup(List<SkuGroupVo> list) {
        Map<Long, List<SkuGroupVo>> map = getSkuGroupMap(list);
        List<SupplierGroupVo> suppliers = new ArrayList<>();
        for (Long key : map.keySet()) {
            SupplierGroupVo groupVo = new SupplierGroupVo();
            groupVo.setSupplierId(key);
            groupVo.setItems(map.get(key));
            suppliers.add(groupVo);
        }
        return suppliers;
    }

    /**
     * 根据供应商ID对sku进行分组
     *
     * @param list 分组对象
     * @return map key 为供应商ID,list分组后的对象
     */
    public static Map<Long, List<SkuGroupVo>> getSkuGroupMap(List<SkuGroupVo> list) {
        Map<Long, List<SkuGroupVo>> map = new HashMap<>();
        for (SkuGroupVo item : list) {
            if (!map.containsKey(item.getSupplierId())) {
                map.put(item.getSupplierId(), new ArrayList<>());
            }
            map.get(item.getSupplierId()).add(item);
        }
        return map;
    }

    /**
     * 计算物流费
     *
     * @param logisticsCostVo 计算对象
     * @return
     */
    public static void calcLogisticsCost(LogisticsCostVo logisticsCostVo) {
        calcLogisticsItems(logisticsCostVo.getSellers(), LogisticsCostEnum.sellers);
        calcLogisticsItems(logisticsCostVo.getSupplier(), LogisticsCostEnum.supplier);
        if (CollectionUtils.isNotEmpty(logisticsCostVo.getLogistics())) {
            calcLogisticsItems(logisticsCostVo.getLogistics(), LogisticsCostEnum.logistics);
        }

    }

    /**
     * 根据类型计算物流费用
     *
     * @param list     计算对象
     * @param costType 类型
     */
    public static void calcLogisticsItems(List<SupplierGroupVo> list, LogisticsCostEnum costType) {
        for (SupplierGroupVo cost : list) {
            //获取sku总预估费用
            BigDecimal costCount = getLogisticsCost(cost.getItems());
            logger.error("获取sku总预估费用{}", costCount);
            for (SkuGroupVo item : cost.getItems()) {

                if (costCount.compareTo(new BigDecimal("0")) != 1) {
                    // 小于0 或者 等于 0，都设置为0
                    item.setSkuPlCost(new BigDecimal(0));
                    break;
                }

                // 物流接口可能该数据没返回
                BigDecimal supplierCost = null == cost.getSupplierCost() ? BigDecimal.ZERO
                        : calculateMoney(cost.getSupplierCost(), false);

                logger.error("获取实际物流费supplierCost{}", supplierCost);
                //单个sku的物流费用=(sku预估费用/sku总费用)*供应商预估费用
                BigDecimal costSku = calculateMoney(
                        item.getSkuCost().divide(costCount, 8, BigDecimal.ROUND_HALF_UP)
                                .multiply(supplierCost), false);

                logger.error("单个sku的物流费用=(sku预估费用/sku总费用)*供应商预估费用，{}，{}，{}", item.getSkuCost(), costCount, supplierCost);
                if (LogisticsCostEnum.logistics == costType) {
                    // 物流商 的单个sku 实际物流费
                    item.setSkuPlCost(costSku);
                } else if (LogisticsCostEnum.sellers == costType) {
                    // 卖家 的单个sku 预估物流费
                    item.setSkuPlCost(costSku);
                } else {
                    // 供应商 的单个sku 预估物流费
                    item.setSkuPlCost(costSku);
                }
            }
        }
    }

    /**
     * 获取sku物流费用总和  -- 公式的分母
     *
     * @param list
     * @return
     */
    private static BigDecimal getLogisticsCost(List<SkuGroupVo> list) {
        BigDecimal costCount = new BigDecimal(0);
        for (SkuGroupVo item : list) {
            BigDecimal itemCost = item.getSkuCost().multiply(new BigDecimal(item.getSkuNumber()));
            costCount = costCount.add(itemCost);
        }

        return calculateMoney(costCount, false);
    }

}
