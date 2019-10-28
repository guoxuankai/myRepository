package com.rondaful.cloud.seller.utils;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.util.ResourceUtils;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

public class IOUtils {

    private static CloseableHttpClient httpsClient;

    static {
        HttpClientConnectionManager clientConnectionManager = init();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();//设置请求和传输超时时间
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(200);//设置连接池的最大连接数
        cm.setDefaultMaxPerRoute(100);//设置每个路由上的默认连接个数
        httpsClient = HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(requestConfig).setConnectionManager(clientConnectionManager).build();
    }


    /**
     * 下载图片文件
     * @param filePath
     * @param downloadPath
     */
    public static void getImg(List<String> filePath, String downloadPath) {
        FileOutputStream fos = null;
        InputStream inputStream = null;
        CloseableHttpResponse response = null;
        if (filePath.size() > 20) filePath = filePath.subList(0, 20);
        try {
            File file = new File(downloadPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            for (String path : filePath) {
                String filename = Utils.uuid() + ".jpg";
                HttpGet get = new HttpGet(path);
                response = httpsClient.execute(get);
                if (response.getStatusLine().getStatusCode() == 200) {
                    inputStream = response.getEntity().getContent();
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
            } catch (IOException e) {}
        }

    }


    public static HttpClientConnectionManager init(){
        try {
            SSLContext sslContext  = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {

                @Override
                public boolean isTrusted(X509Certificate[] arg0, String arg1)
                        throws CertificateException {
                    // TODO Auto-generated method stub
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory( sslContext, new String[] { "TLSv1" }, null,
                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            Registry registry = RegistryBuilder
                    . create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", sslsf).build();
            PoolingHttpClientConnectionManager pool = new PoolingHttpClientConnectionManager(registry);
            // 设置连接池大小
            pool.setMaxTotal(500);
            pool.setDefaultMaxPerRoute(pool.getMaxTotal());
            return pool;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getJarRootPath() throws FileNotFoundException {
        String path = ResourceUtils.getURL("classpath:").getPath();
        File rootFile = new File(path);
        if(!rootFile.exists()) {
            rootFile = new File("");
        }
        return rootFile.getAbsolutePath();
    }


    public static void download(File file, String filename, HttpServletResponse response) throws IOException {
        if (file.exists()) {
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            OutputStream os = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            } catch (Exception e) {
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    public static void deleteAllFilesOfDir(File path) {
        if (null != path) {
            if (!path.exists())
                return;
            if (path.isFile()) {
                boolean result = path.delete();
                int tryCount = 0;
                while (!result && tryCount++ < 10) {
                    System.gc(); // 回收资源
                    result = path.delete();
                }
            }
            File[] files = path.listFiles();
            if (null != files) {
                for (int i = 0; i < files.length; i++) {
                    deleteAllFilesOfDir(files[i]);
                }
            }
            path.delete();
        }
    }

}
