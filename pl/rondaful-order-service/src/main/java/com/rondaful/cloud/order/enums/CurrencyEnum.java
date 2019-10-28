package com.rondaful.cloud.order.enums;

/**
 * 币种
 */
public class CurrencyEnum {
    public enum CurrencyCode {
        AUD(1325,"AUD","澳大利亚元"),
        CAD(1324,"CAD","加拿大元"),
        CHF(1317,"CHF","瑞士法郎"),
        DKK(1321,"DKK","丹麦克朗"),
        EUR(1326,"EUR","欧元"),
        GBP(1314,"GBP","英镑"),
        HKD(1315,"HKD","港币"),
        INR(3900,"INR","印度卢比"),
        MYR(2890,"MYR","林吉特马元"),
        JPY(1323,"JPY","日元"),
        MOP(1325,"1327","澳门元"),
        NOK(1322,"NOK","挪威克朗"),
        NZD(1330,"NZD","新西兰元"),
        PHP(1328,"PHP","菲律宾比索"),
        RUB(1843,"RUB","卢布"),
        SEK(1320,"SEK","瑞典克朗"),
        SGD(1375,"SGD","新加坡元"),
        THB(1329,"THB","泰铢"),
        TWD(2895,"TWD","新台币"),
        USD(1316,"USD","美元"),
        SAR(4418,"SAR","沙特里亚尔");
                //CNY(1325,"AUD","人民币"),
               // PLN(1325,"PLN","波兰"),     //波兰
               // MXN(1325,"MXN","墨西哥");

//        港币
//     美元
//     瑞士法郎
//    1318 德国马克
//    1319 法国法郎
//     新加坡元
//     瑞典克朗
//     丹麦克朗
//     挪威克朗
//     日元
//     加拿大元
//    1325 澳大利亚元
//     欧元
//     澳门元
//     菲律宾比索
//     泰国铢
//     新西兰元
//    1331 韩元
//     卢布
//     林吉特
//     新台币
//    1370 西班牙比塞塔
//    1371 意大利里拉
//    1372 荷兰盾
//    1373 比利时法郎
//    1374 芬兰马克
//    3030 印尼卢比
//    3253 巴西里亚尔
//    3899 阿联酋迪拉姆
//     印度卢比
//    3901 南非兰特
//     沙特里亚尔
//    4560 土耳其里拉

        public Integer code;
        public String value;
        public String msg;

        CurrencyCode(Integer code, String value ,String msg) {
            this.code = code;
            this.msg = msg;
            this.value = value;
        }

        public static String getValueByCode(String value){
            for(CurrencyCode currency:CurrencyCode.values()){
                if(value.equalsIgnoreCase(currency.getValue())){
                    return currency.getCode().toString();
                }
            }
            return  null;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }
    }

}