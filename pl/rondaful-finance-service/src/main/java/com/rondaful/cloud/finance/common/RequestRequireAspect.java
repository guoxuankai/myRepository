package com.rondaful.cloud.finance.common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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

import com.rondaful.cloud.finance.enums.ResponseCodeEnum;
import com.rondaful.cloud.finance.exception.GlobalException;


@Aspect
@Component
public class RequestRequireAspect {


    public RequestRequireAspect() {
    }
    
    @Pointcut("@annotation(com.rondaful.cloud.finance.common.RequestRequire)")
    public void controllerInteceptor() {
    }

    @Around("controllerInteceptor()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        //获取注解的方法参数列表
        Object[] args = pjp.getArgs();
        //获取被注解的方法
        MethodInvocationProceedingJoinPoint mjp = (MethodInvocationProceedingJoinPoint) pjp;
        MethodSignature signature = (MethodSignature) mjp.getSignature();
        Method method = signature.getMethod();
        //获取方法上的注解
        RequestRequire require = method.getAnnotation(RequestRequire.class);
        //以防万一，将中文的逗号替换成英文的逗号
        String fieldNames=require.require().replace("，", ",").replaceAll("\\s*", "");
        //判断参数是否普通表单提交
        if(require.parameter() == String.class){
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = requestAttributes.getRequest();
        	Map map = getParameterMap(request);
        	for(String fieldName:fieldNames.split(",")){
                //非空判断
                if(map.get(fieldName) == null || StringUtils.isBlank(String.valueOf(map.get(fieldName)))){
                	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "参数："+fieldName+"不允许为空");
                }
            }
        }else if(require.parameter() == Map.class){//判断是否map json
        	Map map = (Map) args[0];
        	for(String fieldName:fieldNames.split(",")){
                //非空判断
                if(map.get(fieldName) == null || StringUtils.isBlank(String.valueOf(map.get(fieldName)))){
                	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "参数："+fieldName+"不允许为空");
                }
            }
        } else {//普通javabean
	        //从参数列表中获取参数对象
	        Object parameter=null;
	        for(Object pa:args){
	            //class相等表示是同一个对象
	            if (pa.getClass()==require.parameter() ) {
	                parameter=pa;
	            }
	        }
	        //通过反射去和指定的属性值判断是否非空
	        Class cl=parameter.getClass();
	        for(String fieldName:fieldNames.split(",")){
	            //根据属性名获取属性对象
	            Field f=cl.getDeclaredField(fieldName);
	            //设置可读写权限
	            f.setAccessible(true);
	            //获取参数值，因为我的参数都是String型所以直接强转
	            String value=(String)f.get(parameter);
	            //非空判断
	            if(!StringUtils.isNotBlank(value)){
	            	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "参数："+fieldName+"不允许为空");
	            }
	        }
        }
        
        //如果没有报错，放行
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
