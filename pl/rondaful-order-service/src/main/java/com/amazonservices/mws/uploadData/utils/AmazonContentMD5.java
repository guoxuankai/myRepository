package com.amazonservices.mws.uploadData.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 上传数据到amazon时的Content-MD5 标头，及下载数据的校验和
 * @author ouxiangfeng
 */
public class AmazonContentMD5 {
	/**
	 * Calculate content MD5 header values for feeds stored on disk.
	 * 提交到亚马逊的上传数据计算 Content-MD5
	 */
	public static String computeContentMD5HeaderValue( FileInputStream fis ) 
	    throws IOException, NoSuchAlgorithmException {

	    DigestInputStream dis = new DigestInputStream( fis,
	        MessageDigest.getInstance( "MD5" ));

	    byte[] buffer = new byte[8192];
	    while( dis.read( buffer ) > 0 );

	    String md5Content = new String(
	        org.apache.commons.codec.binary.Base64.encodeBase64(
	            dis.getMessageDigest().digest()) ); 

	    // Effectively resets the stream to be beginning of the file
	    // via a FileChannel.
	    fis.getChannel().position( 0 );
	    return md5Content;
	}
	/**
	 * Calculate content MD5 header values for feeds stored on disk.
	 * 提交到亚马逊的上传数据计算 Content-MD5
	 */
	public static String computeContentMD5HeaderValue(InputStream fis ) 
	    throws IOException, NoSuchAlgorithmException {

	    DigestInputStream dis = new DigestInputStream( fis,
	        MessageDigest.getInstance( "MD5" ));

	    byte[] buffer = new byte[8192];
	    while( dis.read( buffer ) > 0 );

	    String md5Content = new String(
	        org.apache.commons.codec.binary.Base64.encodeBase64(
	            dis.getMessageDigest().digest()) ); 
	    fis.reset();
	    // Effectively resets the stream to be beginning of the file
	    // via a FileChannel.
	    //fis..getChannel().position( 0 );
	    return md5Content;
	}
	
	/**
	 * Consume the stream and return its Base-64 encoded MD5 checksum.
	 */
	public static String computeContentMD5Header(InputStream inputStream) {
	    // Consume the stream to compute the MD5 as a side effect.
	    DigestInputStream s;
	    try {
	        s = new DigestInputStream(inputStream,
	                                  MessageDigest.getInstance("MD5"));
	        // drain the buffer, as the digest is computed as a side-effect
	        byte[] buffer = new byte[8192];
	        while(s.read(buffer) > 0);
	        return new String(
	            org.apache.commons.codec.binary.Base64.encodeBase64(
	                s.getMessageDigest().digest()),
	                "UTF-8");
	    } catch (NoSuchAlgorithmException e) {
	        throw new RuntimeException(e);
	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    }
	}
	
	
	/**
	 * string 转 InputStream
	 * @param text
	 * @return
	 */
	public static  InputStream toInputStream(String text)
	{
		return new ByteArrayInputStream(text.getBytes());
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException, FileNotFoundException, IOException {
		String md5 = AmazonContentMD5.computeContentMD5HeaderValue(new FileInputStream(new File("D:\\workspace\\MaWSJava\\resources\\1227_product.xml")));
		System.out.println(md5);
		
	}
	
}
