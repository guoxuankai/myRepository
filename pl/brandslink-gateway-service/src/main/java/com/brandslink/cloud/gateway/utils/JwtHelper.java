package com.brandslink.cloud.gateway.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.Map;


/**
 * JWT工具类
 * */
public class JwtHelper {

        public static String base64Security = "zzx123456";

        public static long expiration = 300000;


        public static Claims parseJWT(String jsonWebToken, String base64Security){
            try{
                Claims claims = Jwts.parser()
                        .setSigningKey(DatatypeConverter.parseBase64Binary(base64Security))
                        .parseClaimsJws(jsonWebToken).getBody();
                return claims;
            }
            catch(Exception ex){
                return null;
            }
        }


        public static String createJWT(Map map, String issuer, long TTLMillis, String base64Security){
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

            long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);

            //生成签名密钥
            byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(base64Security);
            Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

            //添加构成JWT的参数
            JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JWT")
                    .claim("token", map)
                    .setIssuer(issuer)
                    .signWith(signatureAlgorithm, signingKey);
            //添加Token过期时间
            if (TTLMillis >= 0) {
                long expMillis = nowMillis + TTLMillis;
                Date exp = new Date(expMillis);
                builder.setExpiration(exp).setNotBefore(now);
            }
            //生成JWT
            return builder.compact();
        }


        public static Date tt(){
            long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);
            long expMillis = nowMillis + expiration;
            Date exp = new Date(expMillis);
            return exp;
        }
}
