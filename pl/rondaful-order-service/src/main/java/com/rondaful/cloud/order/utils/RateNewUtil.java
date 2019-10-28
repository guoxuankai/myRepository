package com.rondaful.cloud.order.utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class RateNewUtil {
    /**
     * 获取url生成的html
     * @param url
     * @return
     */
    public static Document getDataByJsoup(String url) {
        Document doc2 = null;
        try {
            doc2 = Jsoup.connect(url).timeout(5000).get();
            //String title = doc2.body().toString();
        } catch (SocketTimeoutException e) {
            System.out.println("Socket连接超时");
        } catch (IOException e){
            e.printStackTrace();
        }
        return doc2;
    }
    /**
     * 获取币种对人民币的汇率
     * @param dateTime 时间 yyyy-MM-dd
     * @param code 1314 英镑
    1315 港币
    1316 美元
    1317 瑞士法郎
    1318 德国马克
    1319 法国法郎
    1375 新加坡元
    1320 瑞典克朗
    1321 丹麦克朗
    1322 挪威克朗
    1323 日元
    1324 加拿大元
    1325 澳大利亚元
    1326 欧元
    1327 澳门元
    1328 菲律宾比索
    1329 泰国铢
    1330 新西兰元
    1331 韩元
    1843 卢布
    2890 林吉特
    2895 新台币
    1370 西班牙比塞塔
    1371 意大利里拉
    1372 荷兰盾
    1373 比利时法郎
    1374 芬兰马克
    3030 印尼卢比
    3253 巴西里亚尔
    3899 阿联酋迪拉姆
    3900 印度卢比
    3901 南非兰特
    4418 沙特里亚尔
    4560 土耳其里拉
     * @return
     */
    public static String getThirtyAvg(String dateTime, String code) {
        BigDecimal exchangeRate = BigDecimal.ZERO;
        Document document = RateNewUtil
                .getDataByJsoup("http://srh.bankofchina.com/search/whpj/search.jsp?erectDate="+dateTime+"&nothing="+dateTime+"&pjname="+code);
        String htmlStr=document.select("table").get(1).html();
        String patternStr="<td>(.*)</td>";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(htmlStr);
        int i=1;
        while(matcher.find()) {
            if(i==9){
                break;
            }
            if(i==2){//现汇买入价
                exchangeRate=BigDecimal.valueOf(Double.valueOf(matcher.group(1)));
            }
            i++;
        }
        if(exchangeRate.doubleValue()>0){
            exchangeRate = exchangeRate.divide(new BigDecimal(100));
        }else{
            return  null;
        }
        return exchangeRate.toString();
    }
    public static void main(String[] args) {
        System.out.println(RateNewUtil.getThirtyAvg("2019-09-20","1316"));
    }
}
