package com.rondaful.cloud.commodity.utils;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

    /**
     * 压缩并递归子文件夹
     */
    public static void compressZip(ZipOutputStream zipOutput, File file, String base) throws IOException {
        if(file.isDirectory()){
            File[] listFiles = file.listFiles();
            for(File fi : listFiles){
                if(fi.isDirectory()){
                    compressZip(zipOutput, fi, base + "/" + fi.getName());
                }else{
                    zip(zipOutput, fi, base);
                }
            }
        }else{
            zip(zipOutput, file, base);
        }
    }


    /**
     * 压缩
     */
    public static void zip(ZipOutputStream zipOutput, File file, String base) throws IOException, FileNotFoundException {
        ZipEntry zEntry = new ZipEntry(base + File.separator + file.getName());
        zipOutput.putNextEntry(zEntry);
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        byte[] buffer = new byte[1024];
        int read = 0;
        while((read =bis.read(buffer)) != -1){
            zipOutput.write(buffer, 0, read);
        }
        bis.close();
    }
}
