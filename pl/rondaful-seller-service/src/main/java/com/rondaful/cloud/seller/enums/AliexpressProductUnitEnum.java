package com.rondaful.cloud.seller.enums;


import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum AliexpressProductUnitEnum {

    /**
     * 速卖通单位
     */
    BAG(100000000,"袋(bag/bags)"),
    BARREL(100000001,"桶(barrel/barrels)"),
    BUSHEL(100000002,"蒲式耳(bushel/bushels)"),
    CARTON(100078580,"箱(carton)"),
    CENTIMETER(100078581,"厘米(centimeter)"),
    CUBIC_METER(100000003,"立方米(cubic meter)"),
    DOZEN(100000004,"打(dozen)"),
    FEET(100078584,"英尺(feet)"),
    GALLON(100000005,"加仑(gallon)"),
    GRAM(100000006,"克(gram)"),
    INCH(100078587,"英寸(inch)"),
    KILOGRAM(100000007,"千克(kilogram)"),
    KILOLITER(100078589,"千升(kiloliter)"),
    KILOMETER(100000008,"千米(kilometer)"),
    LITER(100078559,"升(liter/liters)"),
    LONG_TON(100000009,"英吨(long ton)"),
    METER(100000010,"米(meter)"),
    METRIC_TON(100000011,"公吨(metric ton)"),
    MILLIGRAM(100078560,"毫克(milligram)"),
    MILLILITER(100078596,"毫升(milliliter)"),
    MILLIMETER(100078597,"毫米(millimeter)"),
    OUNCE(100000012,"盎司(ounce)"),
    PACK(100000014,"包(pack/packs)"),
    PAIR(100000013,"双(pair)"),
    PIECE(100000015,"件/个(piece/pieces)"),
    POUND(100000016,"磅(pound)"),
    QUART(100078603,"夸脱(quart)"),
    SET(100000017,"套(set/sets)"),
    SHORT_TON(100000018,"美吨(short ton)"),
    SQUARE_FEET(100078606,"平方英尺(square feet)"),
    SQUARE_INCH(100078607,"平方英寸(square inch)"),
    SQUARE_METER(100000019,"平方米(square meter)"),
    SQUARE_YARD(100078609,"平方码(square yard)"),
    TON(100000020,"吨(ton)"),
    YARD(100078558,"码(yard/yards)")
    ;
    private int code;
    private String msg;
    private AliexpressProductUnitEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public int getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }

    //讲枚举转换成list格式，这样前台遍历的时候比较容易，列如 下拉框 后台调用toList方法，你就可以得到code 和msg了
    public static List toList() {
        List list = Lists.newArrayList();
        for (AliexpressProductUnitEnum unitEnum : AliexpressProductUnitEnum.values()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("code", unitEnum.getCode());
            map.put("name", unitEnum.getMsg());
            list.add(map);
        }
        return list;
    }

    public static String getValueByCode(int code){
        for(AliexpressProductUnitEnum aliexpress:AliexpressProductUnitEnum.values()){
            if(code==aliexpress.getCode()){
                return aliexpress.getMsg();
            }
        }
        return  null;
    }

}
