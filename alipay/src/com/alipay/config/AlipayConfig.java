package com.alipay.config;


import java.io.FileWriter;
import java.io.IOException;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipayConfig {

//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2016092700607050";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCRwAtlJgl548Sevrd8UTSMTVvi/bzWp9+OWMSyzD1s6wH/SrGFTL83GkhD1lnk9BVHC02BIpFGi5yyTDnZofk2Sdu6P9FIWytkXmGNn0K20XijnDC7+iUD5Xdc+EkV+vgPuHbReOeNWLSqF39dzexG3tjHXt2SQUR3MKpXzsFg6Y6nNpONER8JhBXd0t4mvis8lH4anOQSi3BkPzn8EjxbS7gjhDszkOXNdHA8ToLFxOCxSuTFRNBdfGbnYiiynOknyD6BLa9g5cZPFQxfIXEYijQBQJttT5f/V+2LZr8ZCPbL/7TVED3Su5KYuR0ND9xAKBcAbAssQ3moO6v/CAqRAgMBAAECggEAP0jLqdeiNbKYJPVaZjg8QM82HpFd9TZe80fH4HqeETTZYSflfKOeQya+SfZAghEZTAe0V2XS/naQSzqdw8l4lAHtzij3jhtH9ASnBv8n0ImTgDbJUlWCzPZNpaiI02ptWyYVMU71+4GQsGoTe392IC+SoL6oJmEAgWA4FnhHrvQRdZQ/91KAfUPT3lUHIMX0uHxr2bI74w9sNeHSGeRR8m08Qh5+cPnM2ndWl6T1mSflciEI38yaQ9r73xFYobC47Y8zSYsHWnBjlPOsjA5+V7Cn62ZN/JPs/Sb8kUepQjuiiTM5sg43vVowlFWqMuXEB8Cw8D8u96PmaeqH7+40IQKBgQDZlBtO59KqLHsczhDtmO51z0DkL4NSvh5K1scySm2XuXEZ1QobSYAh5UbiMoRVYCkf/Dd3vrXohsWT73xQARYH5FjeqVJ2PfzpaAQaeZz8//pFIo8tO2JSt6/ESm1LJdo9E6ZtG9yhEPPMsJZmXqskSL6ktiFmZTnB/mRDSLvwKwKBgQCrfNooHOi9sy5jMvF2lVKS95J29R78vAjTdU3tZCUTjOr3szYbdPeF5thCjNEKE4Mmvl+wGEklur4GDJQXIwM4y5pC5304p8UcegmEuCYBm1eVX65AUt9LUNVX43JE3S/6YNo5yRuhQk2Da0g5Oysm0uFX8MJEIZ+T/E+6O5qWMwKBgQCPlynqih3ChvZ8zRXo5/u/rhaiQSUGz0eIlnQBoZLwBcc5iBpSZdB1Di4Yi8Q/3+VkIXytvyOoIIF2Hx/IYN8cODvaPp6YQFKP60CCBG5xnbGaPLOG42EBPA54mbHFXWbI1hYIfdG+TkTHembXinShzoxagYRLYlSazXt32CGH3wKBgQCBE7PdN41TpDgJQMIRIOoLZz+ePHh7gUkfaQ8j3RovMgV2NjVGy264If6rcPSzIea41diOYmlEdeAsC3G4TIlTSaUuMLHHYDh0a8MYW8d3tCZjb6ZoEjiypwA+bBuJi/dd/WKk1iPu6EoR3kF5mnire1VUbQq8X5aOu/6CzfRYkwKBgF4B/3j0dZorHXjTZ1lr4PXUk87D/vukoJtKAnH+fEQhHun5izjcf07asV1npQPdNarvAkAgaxloCfts/M8yRo1+b7kyDDXEOiBD8kn6iC3yknGMVin9BGRSaQtYLwoZxND3/Db7/B1wkpJqTO4/P4tJjrhNkwOh4adRxW/vzQsh";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2uW67ju82LYcAJt2xYlrgE3ERoc3keKjh88C6K1KdLnKVGbFAgIwWhMebSadpLUn8FaaH/LXGZvQSexe/y1FDscFBxSTyBgTxI3WgbiddoZsLWAJ/PdZVmkmYWPfNOElQ7Jbb8Yxy/2cH3ZPeIMWG/mh+8sCDgIVwbgS/hiqy3TtLm+w842s8ymdjO0/uH5AnBfQCGqPrw/ZAVJt34/7eu0mOnelTSmYcl0+ZMAfUwOiHltR0pfrijnsXgeLR10vntaUC6uNkY8BdaFmQi1MZq/wEbnfwj8DV2EMB2xZ7XBRBOr2Jck9jQC/xnwJ/Ypf7ERm9Yiid6hlNeWm7NgjDwIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://localhost:8080/notify_url.jsp";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://localhost:8080/return_url.jsp";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "C:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

