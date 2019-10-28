package com.brandslink.cloud.logistics.utils;

import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import org.apache.commons.codec.binary.Base64;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author guoxuankai
 * @date 2019/8/6 19:52
 */
public class PdfUtil {

    private final static Logger logger = LoggerFactory.getLogger(PdfUtil.class);


    public static List pdfToPng(InputStream in) {

        ArrayList<Object> list = new ArrayList<>();

        try {
            PDDocument doc = PDDocument.load(in);
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();


            for (int i = 0; i < pageCount; i++) {


                BufferedImage image = renderer.renderImageWithDPI(i, 144); // Windows native DPI

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                // BufferedImage srcImage = resize(image, 240, 240);//产生缩略图

                ImageIO.write(image, "PNG", byteArrayOutputStream);
                byte[] bytes = byteArrayOutputStream.toByteArray();
                String base64str = Base64.encodeBase64String(bytes);
                list.add("data:image/png;base64," + base64str);


            }
        } catch (IOException e) {
            logger.error("pdf文件转换图片失败:", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "pdf文件转换图片失败");
        }

        return list;
    }


}
