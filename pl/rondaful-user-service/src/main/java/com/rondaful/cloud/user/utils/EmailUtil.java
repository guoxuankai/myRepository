package com.rondaful.cloud.user.utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.mail.HtmlEmail;

import java.util.Properties;

/**
 * 邮件工具类
 */
public class EmailUtil {
    /**
     * 发送邮件
     * @param to 给谁发
     * @param text 发送内容
     */
    public static void send_mail(String to,String text) throws MessagingException {
        //创建连接对象 连接到邮件服务器
        Properties properties = new Properties();
        //设置发送邮件的基本参数
        //发送邮件服务器
        properties.put("mail.smtp.host", "smtp.huic188.com");
        //发送端口
        properties.put("mail.smtp.port", "25");
        properties.put("mail.smtp.auth", "true");
        //设置发送邮件的账号和密码
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                //两个参数分别是发送邮件的账户和密码
                return new PasswordAuthentication("18475452065@163.com","Hzh846767");
            }
        });

        //创建邮件对象
        Message message = new MimeMessage(session);
        //设置发件人
        message.setFrom(new InternetAddress("18475452065@163.com"));
        //设置收件人
        message.setRecipient(Message.RecipientType.TO,new InternetAddress(to));
        //设置主题
        message.setSubject("这是一份测试邮件");
        //设置邮件正文  第二个参数是邮件发送的类型
        message.setContent(text,"text/html;charset=UTF-8");
        //发送一封邮件
        Transport.send(message);
    }

  //邮箱验证码
  	public static boolean sendEmail(String emailaddress,String code){
  		try {
  			HtmlEmail email = new HtmlEmail();//不用更改
  			email.setHostName("163.smtp.com");//需要修改，126邮箱为smtp.126.com,163邮箱为163.smtp.com，QQ为smtp.qq.com
  			email.setCharset("UTF-8");
  			email.addTo(emailaddress);// 收件地址
   
  			email.setFrom("18475452065@163.com", "aa");//此处填邮箱地址和用户名,用户名可以任意填写
   
  			email.setAuthentication("18475452065@163.com", "*******");//此处填写邮箱地址和客户端授权码
   
  			email.setSubject("大大通讯");//此处填写邮件名，邮件名可任意填写
  			email.setMsg("尊敬的用户您好,您本次注册的验证码是:" + code);//此处填写邮件内容
   
  			email.send();
  			return true;
  		}
  		catch(Exception e){
  			e.printStackTrace();
  			return false;
  		}
  	}
    
  	
  	
  	
    public static void main(String[] args) {
        try {
            send_mail("2769371993@qq.com", String.valueOf(Math.random() * 999));
            System.out.println("邮件发送成功!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
	
    
}
