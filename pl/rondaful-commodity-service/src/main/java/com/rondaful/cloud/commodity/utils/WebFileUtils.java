package com.rondaful.cloud.commodity.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.client.methods.CloseableHttpResponse;


public class WebFileUtils {

	/**
     * 根据url拿取file
     * @param url
     * @param suffix 文件后缀名
     * */
    public static File createFileByUrl(String url, String suffix) {
        byte[] byteFile = getImageFromNetByUrl(url);
        if (byteFile != null) {
            File file = getFileFromBytes(byteFile, suffix);
            return file;
        } else {
            return null;
        }
    }
    
    public static byte[] getFileByteByUrl(String url) {
        byte[] byteFile = getImageFromNetByUrl(url);
        return byteFile;
    }
    
    public static InputStream getFileInputStreamByUrl(String urlStr) {
    	 InputStream inStream = null;
    	 try {
    		 URL url = new URL(urlStr);
    	     HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    	     conn.setRequestMethod("GET");
    	     conn.setConnectTimeout(5 * 1000);
    	     if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
    	    	 inStream = conn.getInputStream();
    	     }
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return inStream;
    }
    
    public static void getImg(List<String> filePath, String downloadPath) {
        FileOutputStream fos = null;
        InputStream inputStream = null;
        CloseableHttpResponse response = null;
        if (filePath.size() > 20) {
        	filePath = filePath.subList(0, 20);
        }
        try {
            File file = new File(downloadPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            for (String path : filePath) {
                String filename = Utils.uuid() + ".jpg";
                
                inputStream=getFileInputStreamByUrl(path);
                if (inputStream != null) {
                	fos = new FileOutputStream(file + File.separator  + filename);// 会自动创建文件
                    int len = 0;
                    byte[] buf = new byte[1024];
                    while ((len = inputStream.read(buf)) != -1) {
                        fos.write(buf, 0, len);// 写入流中
                    }
				}
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null)
                    response.close();
                if (inputStream != null) {
                    inputStream.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
            	e.printStackTrace();
            }
        }

    }

    
    
    /**
     * 根据地址获得数据的字节流
     * @param strUrl 网络连接地址
     * @return
     */
    private static byte[] getImageFromNetByUrl(String strUrl) {
    	byte[] btImg = null;
        try {
            URL url = new URL(strUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 1000);
            InputStream inStream = null;
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            	inStream = conn.getInputStream();// 通过输入流获取图片数据
            }
            if (inStream != null) {
            	btImg=readInputStream(inStream);// 得到图片的二进制数据
			}
            return btImg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return btImg;
    }
    
    /**
     * 从输入流中获取数据
     * @param inStream 输入流
     * @return
     * @throws Exception
     */
    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }
    
    /**
     * @Description:创建临时文件
     * @param b
     * @param suffix
     * @return
     */
    private static File getFileFromBytes(byte[] b, String suffix) {
        BufferedOutputStream stream = null;
        File file = null;
        try {
            file = File.createTempFile("pattern", "." + suffix);
            //System.out.println("临时文件位置："+file.getCanonicalPath());
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    
    public static void main(String[] args) {
        WebFileUtils.createFileByUrl("https://img.rondaful.com/100/038/6554064ec203b14a962c285219786699.jpg?sku=BF9922202","jpg");
    }

}
