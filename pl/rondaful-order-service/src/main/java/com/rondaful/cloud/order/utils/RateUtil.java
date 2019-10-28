package com.rondaful.cloud.order.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.utils.DateUtils;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.order.entity.RateMessage;
import com.rondaful.cloud.order.enums.CurrencyEnum;
import com.rondaful.cloud.order.mapper.RateMessageMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author 汇率接口
 */

@Component
public class RateUtil {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private RateMessageMapper rateMessageMapper;

    private static final Logger _log = LoggerFactory.getLogger(RateUtil.class);

    private static final String appkey = "38639";

    private static final String sign = "32cad2d7cbd7b7bd2f2630f036d4a518";

    private static final String url = "http://api.k780.com/?app=finance.rate&";

    public volatile static Map<String, String> rateMap = new HashMap<>();

    /**
     * 返回两个币种代码中的汇率转换
     *
     * @param currencyCode   要转换的币种
     * @param toCurrencyCode 被转换成的币种
     * @return 汇率大小
     */
    public String remoteExchangeRateByCurrencyCode(String currencyCode, String toCurrencyCode) {
        try {
            if (StringUtils.isBlank(currencyCode) || StringUtils.isBlank(toCurrencyCode)) {
                _log.error("输入的转换前后币种为空");
                return null;
            }

//            if (CurrencyEnum.CurrencyCode.getValueByCode(currencyCode)==null) {
//                _log.error("输入币种CODE错误。。。");
//                return null;
//            }
            List<String> rateList = Arrays.asList(rateArrays);
            if (!(rateList.contains(currencyCode) && rateList.contains(toCurrencyCode))) {
                _log.error("输入币种CODE错误。。。");
                return null;
            }

            String rate = rateMap.get(currencyCode + "##" + toCurrencyCode);
            if (StringUtils.isNotBlank(rate))
                return rate;

            RateMessage lastRate = rateMessageMapper.findLastRate();
            HashMap hashMap = null;
            boolean flag = false;
            if (lastRate != null && StringUtils.isNotBlank(lastRate.getRateMessage())) {
                hashMap = JSONObject.parseObject(lastRate.getRateMessage(), HashMap.class);
                flag = true;
                Object o = hashMap.get(currencyCode + "##" + toCurrencyCode);
                if (o != null) {
                    rate = (String) o;
                    if (StringUtils.isNotBlank(rate)) {
                        rateMap.put(currencyCode + "##" + toCurrencyCode, rate);
                        return rate;
                    }
                }
            }

            rate = this.getRate(currencyCode, toCurrencyCode);
            if (StringUtils.isNotBlank(rate)) {
                rateMap.put(currencyCode + "##" + toCurrencyCode, rate);
                if (flag) {
                    hashMap.put(currencyCode + "##" + toCurrencyCode, rate);
                    lastRate.setRateMessage(FastJsonUtils.toJsonString(hashMap));
                    rateMessageMapper.updateByPrimaryKeyWithBLOBs(lastRate);
                } else {
                    this.getExchangeRate();
                    rateMessageMapper.insert(new RateMessage() {{
                        setRateMessage(FastJsonUtils.toJsonString(rateMap));
                    }});
                }
                return rate;
            }
            _log.error("远程调用查询汇率为空，原币种和目标币种为：" + currencyCode + "##" + toCurrencyCode);
            return null;
        } catch (Exception e) {
            _log.error("查询指定汇率异常", e);
            return null;
        }
    }

    /**
     * @param currencyCode   要转换的币种
     * @param toCurrencyCode 被转换成的币种
     * @return 汇率
     */
    private String getRateNew(String currencyCode, String toCurrencyCode) {
        try {
            toCurrencyCode = "CNY";
            String date = DateUtils.dateToString(new Date(),DateUtils.FORMAT_3);
            String result = RateNewUtil.getThirtyAvg(date, CurrencyEnum.CurrencyCode.getValueByCode(currencyCode));
            return result;
        } catch (Exception e) {
            _log.error("远程查询汇率异常", e);
            return null;
        }
    }
    /**
     * @param currencyCode   要转换的币种
     * @param toCurrencyCode 被转换成的币种
     * @return 汇率
     */
    private String getRate(String currencyCode, String toCurrencyCode) {
        try {
            URL u = new URL(url + "scur=" + currencyCode + "&tcur=" + toCurrencyCode + "&appkey="
                    + appkey + "&sign=" + sign + "&format=json");
            JSONObject result = this.writeStream(u);
            return (String) result.get("rate");
        } catch (Exception e) {
            _log.error("远程查询汇率异常", e);
            return null;
        }
    }


    /**
     * 获取所有转人民币的汇率
     *
     * @return 所有人民币汇率
     */
    public Map getExchangeRate() {
        HashMap<String, String> map = new HashMap<>();
        try {
            StringBuilder sourceStr = new StringBuilder();
            StringBuilder purposeStr = new StringBuilder();
            int i = 0;
            StringBuilder sourcesResult = this.handleString(rateArrays, sourceStr, i);
            StringBuilder purposeResult = this.handleString(rmb, purposeStr, i);

            URL u = new URL(url + "scur=" + sourcesResult.toString() + "&tcur=" + purposeResult.toString() + "&appkey="
                    + appkey + "&sign=" + sign + "&format=json");
            JSONObject result = this.writeStream(u);

            JSONArray array = result.getJSONArray("lists");
            for (int j = 0; j < array.size(); j++) {
                JSONObject jo = array.getJSONObject(j);
                map.put(jo.getString("scur"), jo.getString("rate"));
                rateMap.put(jo.getString("scur") + "##" + "CNY", jo.getString("rate"));
            }
            _log.info("所有的实时汇率： " + map.toString());
        } catch (Exception e) {
            _log.error("远程获取所有人民币汇率异常", e);
        }
        return map;
    }


        /**
     * 获取所有转人民币的汇率
     *
     * @return 所有人民币汇率
     */
    public Map getExchangeNewRate() {
        HashMap<String, String> map = new HashMap<>();
        try {
            for (CurrencyEnum.CurrencyCode code: CurrencyEnum.CurrencyCode.values()) {
                String date = DateUtils.dateToString(new Date(),DateUtils.FORMAT_3);
                String rate = RateNewUtil.getThirtyAvg(date, CurrencyEnum.CurrencyCode.getValueByCode(code.code.toString()));
                rateMap.put(code.getValue() + "##" + "CNY", rate);
            }
            _log.info("所有的实时汇率： " + map.toString());
        } catch (Exception e) {
            _log.error("远程获取所有人民币汇率异常", e);
        }
        return map;
    }


    /**
     * 将数据全部初始化为现在的转为人民币的汇率
     */
    public void initRateMessage() {
        try {
            rateMap.clear();
            this.getExchangeRate();
            rateMessageMapper.insert(new RateMessage() {{
                setRateMessage(FastJsonUtils.toJsonString(rateMap));
            }});
        } catch (Exception e) {
            _log.error("初始化汇率数据异常", e);
        }
    }


    private JSONObject writeStream(URL u) throws IOException {
        InputStream in = null;
        try {
            in = u.openStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte buf[] = new byte[1024];
            int read = 0;
            while ((read = in.read(buf)) > 0) {
                out.write(buf, 0, read);
            }
            byte b[] = out.toByteArray();
            String result = new String(b, StandardCharsets.UTF_8);
            _log.info("汇率返回结果: " + out.toString());

            JSONObject json = JSONObject.parseObject(result);

            return json.getJSONObject("result");
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    private StringBuilder handleString(String[] sourceCur, StringBuilder sourceStr, int i) {
        for (String s : sourceCur) {
            sourceStr.append(s);
            if (i != sourceCur.length - 1) {
                sourceStr.append(',');
            }
            i++;
        }
        return sourceStr;
    }

    public static final String[] rmb = {"CNY"};


    public static final String[] rateArrays = {
            "AUD",   //澳大利亚元
            "CAD",   //加拿大元
            "CHF",   //瑞士法郎
            "DKK",   //丹麦克朗
            "EUR",   //欧元
            "GBP",   //英镑
            "HKD",   //港币
            "INR",   //印度卢比
            "MYR",   // 林吉特  马元
            "JPY",   //日元
            "MOP",   //澳门元
            "NOK",   //挪威克朗
            "NZD",   //新西兰元
            "PHP",   //菲律宾比索
            "RUB",   //卢布
            "SEK",   //瑞典克朗
            "SGD",   //新加坡元
            "THB",   //泰铢
            "TWD",   //新台币
            "USD",   //美元
            "SAR",   //沙特里亚尔
            "CNY",
            "PLN",     //波兰
            "MXN"    //墨西哥
    };


//	public static final String[] rateArrays = {
//			"AED",
//			"AFN",
//			"ALL",
//			"AMD",
//			"ANG",
//			"AOA",
//			"ARS",
//			"AUD",
//			"AWG",
//			"AZN",
//			"BAM",
//			"BBD",
//			"BDT",
//			"BGN",
//			"BHD",
//			"BIF",
//			"BMD",
//			"BND",
//			"BOB",
//			"BRL",
//			"BSD",
//			"BTN",
//			"BWP",
//			"BYR",
//			"BZD",
//			"CAD",
//			"CDF",
//			"CHF",
//			"CLF",
//			"CLP",
//			"CNH",
//			"CNY",
//			"COP",
//			"CRC",
//			"CUP",
//			"CVE",
//			"CZK",
//			"DJF",
//			"DKK",
//			"DOP",
//			"DZD",
//			"EGP",
//			"ERN",
//			"ETB",
//			"EUR",
//			"FJD",
//			"FKP",
//			"GBP",
//			"GEL",
//			"GHS",
//			"GIP",
//			"GMD",
//			"GNF",
//			"GTQ",
//			"GYD",
//			"HKD",
//			"HNL",
//			"HRK",
//			"HTG",
//			"HUF",
//			"IDR",
//			"ILS",
//			"INR",
//			"IQD",
//			"IRR",
//			"ISK",
//			"JMD",
//			"JOD",
//			"JPY",
//			"KES",
//			"KGS",
//			"KHR",
//			"KMF",
//			"KPW",
//			"KRW",
//			"KWD",
//			"KYD",
//			"KZT",
//			"LAK",
//			"LBP",
//			"LKR",
//			"LRD",
//			"LSL",
//			"LTL",
//			"LVL",
//			"LYD",
//			"MAD",
//			"MDL",
//			"MGA",
//			"MKD",
//			"MMK",
//			"MNT",
//			"MOP",
//			"MRO",
//			"MUR",
//			"MVR",
//			"MWK",
//			"MXN",
//			"MYR",
//			"MZN",
//			"NAD",
//			"NGN",
//			"NIO",
//			"NOK",
//			"NPR",
//			"NZD",
//			"OMR",
//			"PAB",
//			"PEN",
//			"PGK",
//			"PHP",
//			"PKR",
//			"PLN",
//			"PYG",
//			"QAR",
//			"RON",
//			"RSD",
//			"RUB",
//			"RWF",
//			"SAR",
//			"SBD",
//			"SCR",
//			"SDG",
//			"SEK",
//			"SGD",
//			"SHP",
//			"SLL",
//			"SOS",
//			"SRD",
//			"STD",
//			"SVC",
//			"SYP",
//			"SZL",
//			"THB",
//			"TJS",
//			"TMT",
//			"TND",
//			"TOP",
//			"TRY",
//			"TTD",
//			"TWD",
//			"TZS",
//			"UAH",
//			"UGX",
//			"USD",
//			"UYU",
//			"UZS",
//			"VEF",
//			"VND",
//			"VUV",
//			"WST",
//			"XAF",
//			"XAU",
//			"XCD",
//			"XDR",
//			"XOF",
//			"XPF",
//			"YER",
//			"ZAR",
//			"ZMW",
//			"ZWL"
//	};
}
