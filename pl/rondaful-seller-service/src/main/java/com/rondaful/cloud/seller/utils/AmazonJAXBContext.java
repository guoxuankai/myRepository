package com.rondaful.cloud.seller.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class AmazonJAXBContext {
	  private static Map<Class<?>, JAXBContext> contextStore = new ConcurrentHashMap<Class<?>, JAXBContext>();

	  protected static JAXBContext getContextInstance(Class<?> objectClass) throws JAXBException{
	        JAXBContext context = contextStore.get(objectClass);
	        if (context == null){
	            context = JAXBContext.newInstance(objectClass);
	            contextStore.putIfAbsent(objectClass, context);
	        }
	        return context;
	    }

}
