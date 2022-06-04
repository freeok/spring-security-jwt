package work.pcdd.securityjwt.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import work.pcdd.securityjwt.model.entity.UserInfo;

import java.util.Date;

/**
 * @author pcdd
 * @date 2021/4/3
 */
@Slf4j
@Component
public class JwtUtils {

    @Value("${jwt.expire}")
    private Long expire;

    @Value("${jwt.secret}")
    private String secret;

    /**
     * 根据用户id，role 生成token
     *
     * @param userInfo 用户
     * @return 生成的token
     */
    public String generateToken(UserInfo userInfo) {
        log.info("开始生成token");
        Date nowDate = new Date();
        Date expireDate = new Date(nowDate.getTime() + expire * 1000);

        try {
            return JWT.create()
                    // 签名由谁生成(可选)
                    .withIssuer("auth0")
                    // 生成签名的时间(可选)
                    .withIssuedAt(nowDate)
                    // 签名的观众 也可以理解谁接受签名的
                    .withAudience(String.valueOf(userInfo.getId()))
                    // 签名过期的时间
                    .withExpiresAt(expireDate)
                    // 生成携带自定义信息 这里为角色权限
                    .withClaim("role", userInfo.getRole())
                    // 使用HMAC256加密算法构建密钥信息,密钥是secret
                    .sign(Algorithm.HMAC256(secret));

        } catch (JWTCreationException e) {
            throw new JWTCreationException("无效的签名配置", e);
        }
    }

    /**
     * 校验token
     *
     * @param token 令牌
     */
    public void verifyToken(String token) {
        log.info("开始校验token");
        String userId;
        try {
            // 从解密的token中获取userId
            userId = JWT.decode(token).getAudience().get(0);
            DecodedJWT jwt = JWT.decode(token);
            log.info("userId:{} 角色：{}", userId, jwt.getClaim("role"));

            // 验证上传的token私钥部分是否与密匙一致
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(secret)).build();
            jwtVerifier.verify(token);

        } catch (JWTDecodeException e) {
            // 令牌的语法(必须是xxx.xxx.xxx格式)无效或者标头或有效负载不是JSON，比如篡改了header或payload
            throw new JWTVerificationException("token格式不正确，请重新登录");

        } catch (TokenExpiredException e) {
            // 当前时间戳大于ExpiresAt的时间戳
            throw new JWTVerificationException("token已过期，请重新登录");

        } catch (SignatureVerificationException e) {
            // 篡改了signature（第三部分），或 JWT.require(Algorithm.HMAC256(secret))时的密钥和生成时的密钥不一致
            throw new JWTVerificationException("token签名不正确，请重新登录");

        } catch (JWTVerificationException e) {
            throw new JWTVerificationException("token解析错误，请重新登录");
        }
    }


}
