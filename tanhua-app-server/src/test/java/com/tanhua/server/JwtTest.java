package com.tanhua.server;

import io.jsonwebtoken.*;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {

    @Test
    /**
     * 生成token
     */
    public void testCreateToken() {
        //生成token
        //1、准备数据
        Map map = new HashMap();
        map.put("id",1);
        map.put("mobile","13800138000");
        //2、使用JWT的工具类生成token
        long now = System.currentTimeMillis();
        String token = Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, "itcast") //指定加密算法
                .setClaims(map) //写入数据
                .setExpiration(new Date(now + 300000)) //失效时间
                .compact();
        System.out.println(token);
    }

    @Test
    /** ExpiredJwtException:token失效
     *  MalformedJwtException:token不合法
     * 解析token
     */
    public void testParseToken() {
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJtb2JpbGUiOiIxMzgwMDEzODAwMCIsImlkIjoxLCJleHAiOjE2NDE3MzM1NjF9.qDaKgcZEiCE9YVEOePqBHE2qQNy6vT9ZyozqLg1KmTjCV5_ckR0w0Rh67sxhmjc6dTNa6DEN0RFybmzbAflR2w";
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey("itcast")
                    .parseClaimsJws(token)
                    .getBody();
            Object id = claims.get("id");
            Object mobile = claims.get("mobile");
            System.out.println(id + "--" + mobile);
        }catch (ExpiredJwtException e) {
            System.out.println("token已过期");
        }catch (SignatureException e) {
            System.out.println("token不合法");
        }

    }
}
