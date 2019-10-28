package com.rondaful.cloud.seller.task;

import com.rondaful.cloud.seller.constants.UpcValidateConstant;
import com.rondaful.cloud.seller.entity.PrefixCode;
import com.rondaful.cloud.seller.entity.UpcGenerate;
import com.rondaful.cloud.seller.service.PrefixCodeService;
import com.rondaful.cloud.seller.service.UpcGenerateService;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 生成upc码任务
 *
 * @author guoxuankai
 * @date 2019/6/10
 */
public class GenerateUpcThread implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(GenerateUpcTask.class);

    private PrefixCodeService prefixCodeService;

    private UpcGenerateService upcGenerateService;

    private PrefixCode p;

    public GenerateUpcThread(PrefixCode p, PrefixCodeService prefixCodeService, UpcGenerateService upcGenerateService) {
        this.p = p;
        this.prefixCodeService = prefixCodeService;
        this.upcGenerateService = upcGenerateService;
    }


    @Override
    public void run() {


        String prefixCode = p.getPrefixcode();
        int count = p.getCount();

        try {
            //根据前缀码生成UPC码，并且将其插入数据库表中，每次生成20个
            for (int i = 0; i < 20; i++, count++) {

                String str = prefixCode + complete(count);
                String upc = str + getValidateCode(str);

                List<String> amazonValidateUrl = UpcValidateConstant.AMAZON_VALIDATE_URL;

                boolean flag = true;

                for (String s : amazonValidateUrl) {
                    String url = s.replace("{upcCode}", upc);

                    Connection conn = Jsoup.connect(url).timeout(5000);
                    conn.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                    conn.header("Accept-Encoding", "gzip, deflate, sdch");
                    conn.header("Accept-Language", "en-US,en;q=0.5");
                    conn.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");

                    Document doc = conn.get();

                    Elements elements = doc.select(UpcValidateConstant.RESULT_CLASS);

                    if (!(elements.size() > 2 && elements.get(0).text().trim().equals(UpcValidateConstant.VALIDATE_FLAG) && elements.get(1).text().trim().equals(upc))) {
                        flag = false;
                        break;
                    }
                }

                if (flag) {
                    //将合格的upc码插入表中
                    UpcGenerate upcGenerate = new UpcGenerate();
                    upcGenerate.setStatus(0);
                    upcGenerate.setUseStatus(0);
                    upcGenerate.setNumber(upc);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    upcGenerate.setNumberBatch(sdf.format(new Date()) + prefixCode.substring(0, 4));
                    upcGenerateService.insertSelective(upcGenerate);
                }


            }
        } catch (Exception e) {
            logger.error("生成upc码失败", e);
        }

        p.setCount(count);
        if (count > 99999) {
            p.setStatus(0);
        }

        prefixCodeService.updateByPrimaryKeySelective(p);


    }

    //将5位以内的数字转为5位字符串，比如2转成00002
    public static String complete(int i) {
        String s = String.valueOf(i);
        int length = s.length();
        StringBuilder stringBuilder = new StringBuilder();
        for (int j = 0; j < (5 - length); j++) {
            stringBuilder.append("0");

        }
        stringBuilder.append(s);
        return stringBuilder.toString();
    }


    //根据前11位计算校验码
    public static String getValidateCode(String s) {
        int length = s.length();
        int odd = 0;
        int even = 0;
        for (int i = 1; i <= length; i++) {
            if (i % 2 != 0) {
                odd = odd + Integer.parseInt(s.substring(i - 1, i));
            } else {
                even = even + Integer.parseInt(s.substring(i - 1, i));
            }
        }
        //校验码：10-（奇数位之和*3+偶数位之和）%10
        int result = 10 - (odd * 3 + even) % 10;
        if (result == 10) {
            result = 0;
        }
        return String.valueOf(result);
    }


}
