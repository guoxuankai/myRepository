package com.rondaful.cloud.seller.constants;


import java.util.ArrayList;
import java.util.List;

/**
 * 验证生成的UPC码常量
 */
public class UpcValidateConstant {

    // 亚马逊验证upc码是否合格的URL
    public static final List AMAZON_VALIDATE_URL;

    static {
        AMAZON_VALIDATE_URL = new ArrayList<String>();
        AMAZON_VALIDATE_URL.add("https://www.amazon.com/s?k={upcCode}&ref=nb_sb_noss");
//        AMAZON_VALIDATE_URL.add("https://www.amazon.co.uk/s?k={upcCode}&ref=nb_sb_noss");
//        AMAZON_VALIDATE_URL.add("https://www.amazon.com.au/s?k={upcCode}&ref=nb_sb_noss");

    }
    // 验证结果的文本中是否包含的样式
    public static final String RESULT_CLASS = "[class='a-size-medium a-color-base']";
    // 验证结果的文本中是否包含字符串
    public static final String VALIDATE_FLAG = "No results for";


}
