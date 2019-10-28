package com.amazonservices.mws.uploadData.utils;

/**
 *	字符转换
 * @author ouxiangfeng
 *
 */
public class DecapitalizeChar {
	
	public static final String CLASS_PATH_URI_FIX = "com.rondaful.cloud.seller.generated.";
	
	/**
	 *       将首字母改变为大写
	 * @param name
	 * @return
	 */
    public static String decapitalizeUpperCase(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        if (name.length() > 1 && /*Character.isLowerCase(name.charAt(1)) &&*/
                Character.isLowerCase(name.charAt(0))) {
        	char chars[] = name.toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);
            return new String(chars);
        }
        return name;
    }
    
    /**
	 *       将首字母改变为小写
	 * @param name
	 * @return
	 */
    public static String decapitalizeLowerCase(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        if (name.length() > 1 && /*Character.isUpperCase(name.charAt(1)) &&*/
                Character.isUpperCase(name.charAt(0))) {
        	char chars[] = name.toCharArray();
            chars[0] = Character.toLowerCase(chars[0]);
            return new String(chars);
        }
        return name;
    }
    
    /**
     * @param name
     * @return
     */
    public static String compile(String name)
    {
    	return CLASS_PATH_URI_FIX + name;
    }
    
    public static void main(String[] args) {
    	System.out.println(DecapitalizeChar.decapitalizeLowerCase("SDu"));
	}
}
