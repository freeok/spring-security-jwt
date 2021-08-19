package work.pcdd.securityjwt.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import work.pcdd.securityjwt.entity.User;

import java.util.Date;

/**
 * @author 1907263405@qq.com
 * @date 2021/4/3 23:22
 */
@Component
public class JwtUtils {

    @Value("${jwt.expire}")
    private Long expire;

    @Value("${jwt.secret}")
    private String secret;

    /**
     * 根据用户id，role 生成token
     *
     * @param user 用户
     * @return 生成的token
     */
    public String generateToken(User user) {
        System.out.println("------------开始生成token");
        JWTCreator.Builder builder = JWT.create();

        Date nowDate = new Date();
        Date expireDate = new Date(nowDate.getTime() + expire * 1000);

        try {
            // 签名是有由谁生成 例如 服务器(可选)
            builder.withIssuer("SERVICE");

            // 签名的观众 也可以理解谁接受签名的
            builder.withAudience(String.valueOf(user.getId()));

            // 生成签名的时间(可选)
            builder.withIssuedAt(nowDate);

            // 签名过期的时间
            builder.withExpiresAt(expireDate);

            // 生成携带自定义信息 这里为角色权限
            builder.withClaim("role", user.getRole());

            // 使用HMAC256构建密钥信息,密钥是secret
            return builder.sign(Algorithm.HMAC256(secret));

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
        System.out.println("------------开始校验token");
        String userId;
        try {
            // 从解密的token中获取userId
            userId = JWT.decode(token).getAudience().get(0);
            DecodedJWT jwt = JWT.decode(token);
            System.out.println("userId:" + userId + "\n角色：" + jwt.getClaim("role"));

            // 验证上传的token私钥部分是否与密匙一致
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(secret)).build();
            jwtVerifier.verify(token);

        } catch (JWTDecodeException e) {
            // 引发情况：令牌的语法(必须是xxx.xxx.xxx格式)无效或者标头或有效负载不是JSON
            // 比如篡改了header或payload
            throw new JWTVerificationException("token格式不正确，请重新登录");

        } catch (TokenExpiredException e) {

            // 引发情况：当前时间戳大于ExpiresAt的时间戳
            throw new JWTVerificationException("token已过期，请重新登录");

        } catch (SignatureVerificationException e) {
            // 引发情况：篡改了signature（第三部分）
            // 或 JWT.require(Algorithm.HMAC256(secret))时的密钥和生成时的密钥不一致
            throw new JWTVerificationException("token签名不正确，请重新登录");

        } catch (JWTVerificationException e) {
            throw new JWTVerificationException("token解析错误，请重新登录");
        }
    }


}
