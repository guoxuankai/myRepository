package com.brandslink.cloud.common.aspect;

import com.brandslink.cloud.common.annotation.RequestRequire;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


@Aspect
@Component
public class RequestRequireAspect {


    public RequestRequireAspect() {
    }

    @Pointcut("@annotation(com.brandslink.cloud.common.annotation.RequestRequire)")
    public void controllerInteceptor() {
    }

    @Around("controllerInteceptor()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        MethodInvocationProceedingJoinPoint mjp = (MethodInvocationProceedingJoinPoint) pjp;
        MethodSignature signature = (MethodSignature) mjp.getSignature();
        Method method = signature.getMethod();
        RequestRequire require = method.getAnnotation(RequestRequire.class);
        String fieldNames=require.require().replace("，", ",").replaceAll("\\s*", "");
        if(require.parameter() == String.class){
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = requestAttributes.getRequest();
        	Map map = getParameterMap(request);
        	for(String fieldName:fieldNames.split(",")){
                if(map.get(fieldName) == null || StringUtils.isBlank(String.valueOf(map.get(fieldName)))){
                	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, Utils.i18n("参数") + "：" + fieldName + " " + Utils.i18n("不允许为空"));
                }
            }
        }else if(require.parameter() == Map.class){//判断是否map json
        	Map map = (Map) args[0];
        	for(String fieldName:fieldNames.split(",")){
                if(map.get(fieldName) == null || StringUtils.isBlank(String.valueOf(map.get(fieldName)))){
                	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, Utils.i18n("参数") + "：" + fieldName + " " + Utils.i18n("不允许为空"));
                }
            }
        } else {//普通javabean
	        Object parameter=null;
	        for(Object pa:args){
	            if (pa.getClass()==require.parameter() ) {
	                parameter=pa;
	            }
	        }
	        Class cl=parameter.getClass();
	        for(String fieldName:fieldNames.split(",")){
	            Field f=cl.getDeclaredField(fieldName);
	            f.setAccessible(true);
	            String value=String.valueOf(f.get(parameter));
	            if(StringUtils.isBlank(value) || StringUtils.equalsIgnoreCase(value,"null")){
	            	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, Utils.i18n("参数") + "：" + fieldName + " " + Utils.i18n("不允许为空"));
	            }
	        }
        }
        return pjp.proceed();
    }




    /**
     * request请求map转普通map
     * */
    public static Map getParameterMap(HttpServletRequest request) {
        // 参数Map
        Map properties = request.getParameterMap();
        // 返回值Map
        Map returnMap = new HashMap();
        Iterator entries = properties.entrySet().iterator();
        Map.Entry entry;
        String name = "";
        String value = "";
        while (entries.hasNext()) {
            entry = (Map.Entry) entries.next();
            name = (String) entry.getKey();
            Object valueObj = entry.getValue();
            if(null == valueObj){
                value = "";
            }else if(valueObj instanceof String[]){
                String[] values = (String[])valueObj;
                for(int i=0;i<values.length;i++){
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length()-1);
            }else{
                value = valueObj.toString();
            }
            returnMap.put(name, value);
        }
        return returnMap;
    }
}
