package com.rondaful.cloud.order.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ClearBufferThread implements Runnable{
	 private InputStream inputStream;
	    private String type;
	 
	    public ClearBufferThread(String type, InputStream inputStream){
	        this.inputStream = inputStream;
	        this.type = type;
	    }
	 
	    public void run() {
	    	BufferedReader br = null;
	    	String line = null;
	        try{
	        	br = new BufferedReader(new InputStreamReader(inputStream));
	            while((line = br.readLine()) != null) {
	            	System.out.println(type+"===>"+line);
	            };
	        } catch(Exception e){
	        	e.printStackTrace();
	            throw new RuntimeException(e);
	        }finally {
	        	if(br != null) {
	        		try {
						br.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	}
	        	if(inputStream != null){
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
	        }
	    }
}
