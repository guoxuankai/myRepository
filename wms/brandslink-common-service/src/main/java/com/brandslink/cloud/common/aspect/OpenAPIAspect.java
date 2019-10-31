package com.brandslink.cloud.common.aspect;

import com.alibaba.fastjson.JSONObject;
import com.brandslink.cloud.common.annotation.OpenAPI;
import com.brandslink.cloud.common.constant.CustomerConstant;
import com.brandslink.cloud.common.entity.CustomerInfoEntity;
import com.brandslink.cloud.common.enums.OpenAPICodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.utils.MD5;
import com.brandslink.cloud.common.utils.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.TreeMap;


/**
 * @author yangzefei
 * @Classname OpenAPIAspect
 * @Description 接口验证切面
 * @Date 2019/7/31 15:19
 */
@Aspect
@Component
public class OpenAPIAspect {

    /**
     * 签名有效时间
     */
    private Long expires=1000*60L;
    /**
     * form表单中的参数名称
     */
    private String dataJsonName="dataJson";
    /**
     * access_token的参数名称
     */
    private String accessTokenName="access_token";

    /**
     * 版本号参数名
     */
    private String versionName="version";

    private final static Logger logger = LoggerFactory.getLogger(OpenAPIAspect.class);

    @Autowired
    private RedisUtils redisUtils;

    @Pointcut("@annotation(com.brandslink.cloud.common.annotation.OpenAPI)")
    public void validate() {
    }

    @Around("validate()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request=attributes.getRequest();
        //获取参数排序后的map
        TreeMap<String,String> sortMap=getQueryParams(request);
        String version=sortMap.get(versionName);
        if(StringUtils.isEmpty(version)){
            throw new GlobalException(OpenAPICodeEnum.RETURN_CODE_200100);
        }
        String accessToken=sortMap.get(accessTokenName);
        if(StringUtils.isEmpty(accessToken)){
            throw new GlobalException(OpenAPICodeEnum.RETURN_CODE_200200);
        }
        if(accessToken.length()<=32){
            throw new GlobalException(OpenAPICodeEnum.RETURN_CODE_200201);
        }
        if(request.getMethod().toLowerCase().equals("post")){
            changeArgs(joinPoint,sortMap.get(dataJsonName));
        }
        String requestSign=accessToken.substring(0,32);
        String appId=accessToken.substring(32);
        String secretKey=getSecretKey(appId);
        sortMap.remove(accessTokenName); //移除access_token不参与签名算法
        String serviceSign=MD5.createSign(sortMap,secretKey,""); //服务端生成签名
        if(!serviceSign.equals(requestSign)){
            logger.info("签名验证失败,requestSign:{},appId:{},secretKey:{},serviceSign:{}",requestSign,appId,secretKey,serviceSign);
            throw new GlobalException(OpenAPICodeEnum.RETURN_CODE_200400);
        }
        //设置方法上的appId的参数
        setMethodAppId(joinPoint,appId);
        //如果没有报错，放行
        return joinPoint.proceed(joinPoint.getArgs());
    }

    private void setMethodAppId(ProceedingJoinPoint joinPoint,String appId){
        MethodInvocationProceedingJoinPoint mjp = (MethodInvocationProceedingJoinPoint) joinPoint;
        MethodSignature signature = (MethodSignature) mjp.getSignature();
        // 通过这获取到方法的所有参数名称的字符串数组
        String[] parameterNames = signature.getParameterNames();
        Integer index=Arrays.asList(parameterNames).indexOf("appId");
        if(index>=0){
            joinPoint.getArgs()[index]=appId;
        }
    }
    /**
     * 验证post请求时是否需要把form-data中的参数
     * 绑定到方法的第一个参数上
     * @param joinPoint
     * @return
     */
    private OpenAPI getOpenAPI(ProceedingJoinPoint joinPoint){
        //获取被注解的方法
        MethodInvocationProceedingJoinPoint mjp = (MethodInvocationProceedingJoinPoint) joinPoint;
        MethodSignature signature = (MethodSignature) mjp.getSignature();
        Method method = signature.getMethod();
        //获取方法上的注解
        OpenAPI openAPI = method.getAnnotation(OpenAPI.class);
        return openAPI;
    }

    /**
     * post请求时，把form-data中的数据赋值给方法中的第一个参数
     * @param joinPoint
     * @param data form-data中的数据
     */
    private void changeArgs(ProceedingJoinPoint joinPoint,String data){
        OpenAPI openAPI = getOpenAPI(joinPoint);
        //不绑定参数则返回
        if(!openAPI.isRequire()) return;
        //绑定参数为空，则提示
        if(StringUtils.isEmpty(data)){
            throw new GlobalException(OpenAPICodeEnum.RETURN_CODE_200300);
        }
        try{
            if(openAPI.listType()==Object.class){
                joinPoint.getArgs()[0]= JSONObject.parseObject(data,joinPoint.getArgs()[0].getClass());
            }else{
                joinPoint.getArgs()[0]=JSONObject.parseArray(data,openAPI.listType());
            }

        }catch(Exception e){
            throw new GlobalException(OpenAPICodeEnum.RETURN_CODE_200301);
        }
    }

    /**
     * 获取请求参数
     */
    public TreeMap<String,String> getQueryParams(HttpServletRequest request){
        TreeMap<String,String> sortMap=new TreeMap<>();
        Enumeration<String> params=request.getParameterNames();
        while(params.hasMoreElements()){
            String name=params.nextElement();
            String value=request.getParameter(name);
            sortMap.put(name,value);
        }
        return sortMap;
    }






    /**
     * 获取应用密钥
     * @param appId 应用ID
     * @return
     */
    private String getSecretKey(String appId){
        CustomerInfoEntity appDto= (CustomerInfoEntity)redisUtils.get(CustomerConstant.REDIS_PREFIX + appId);
        if(appDto==null){
            throw new GlobalException(OpenAPICodeEnum.RETURN_CODE_200202);
        }
        if(!"1".equals(appDto.getStatus())){
            throw new GlobalException(OpenAPICodeEnum.RETURN_CODE_200203);
        }
        //CustomerInfoEntity appDTO=(CustomerInfoEntity)appDto;
        return appDto.getCustomerSecretKey();
    }

//    /**
//     * 验证时间戳
//     * @param timestampStr
//     * @return
//     */
//    public String validTimestamp(String timestampStr){
//        if(StringUtils.isEmpty(timestampStr)){
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"缺少签名验证参数[TIMESTAMP]");
//        }
//        try{
//            Long timestamp=Long.parseLong(timestampStr);
//            if(System.currentTimeMillis()-timestamp>expires){
//                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, Utils.i18n("签名已过期"));
//            }
//        }catch (Exception e){
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, Utils.i18n("签名已过期"));
//        }
//
//    }
//    /**
//     * 获取post请求的json字符串
//     */
//    public String getStrRequest(HttpServletRequest request) {
//        InputStream inputStream;
//        StringBuilder strRequest = new StringBuilder();
//        try {
//            inputStream = request.getInputStream();
//            String strMessage;
//            BufferedReader reader;
//            reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
//            while ((strMessage = reader.readLine()) != null) {
//                strRequest.append(strMessage);
//            }
//            reader.close();
//            inputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return strRequest.toString();
//    }


}
