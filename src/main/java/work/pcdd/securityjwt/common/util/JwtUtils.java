package work.pcdd.securityjwt.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import work.pcdd.securityjwt.model.dto.AuthenticationRequest;
import work.pcdd.securityjwt.model.dto.TokenInfo;
import work.pcdd.securityjwt.model.dto.UserInfoDTO;

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
    @Value("${jwt.token-prefix}")
    private String tokenPrefix;
    @Value("${jwt.token-name}")
    private String tokenName;

    public String generateToken(UserInfoDTO userInfoDTO, AuthenticationRequest authenticationRequest) {
        return generateToken(userInfoDTO, authenticationRequest, expire);
    }

    /**
     * 生成token
     *
     * @param userInfoDTO           用户信息
     * @param authenticationRequest 登录信息
     * @param timeout               token有效期，单位秒
     */
    public String generateToken(UserInfoDTO userInfoDTO, AuthenticationRequest authenticationRequest, Long timeout) {
        log.info("开始生成token");
        Date nowDate = new Date();
        Date expireDate = new Date(nowDate.getTime() + timeout * 1000);

        try {
            String tokenValue = JWT.create()
                    // jwt的id，此处为用户id
                    .withJWTId(String.valueOf(userInfoDTO.getId()))
                    // 签名过期的时间
                    .withExpiresAt(expireDate)
                    // 生成签名的时间(可选)
                    .withIssuedAt(nowDate)
                    // 签名由谁生成(可选)
                    .withIssuer("auth0")
                    // 携带自定义信息，禁止存放敏感信息！
                    .withClaim("tokenName", tokenName)
                    .withClaim("tokenPrefix", tokenPrefix)
                    .withClaim("loginType", authenticationRequest.getLoginType())
                    .withClaim("loginDevice", "PC")
                    // 使用HMAC256加密算法构建密钥信息,密钥是secret
                    .sign(Algorithm.HMAC256(secret));

            return tokenPrefix + " " + tokenValue;

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
        try {
            // 从解密的token中获取userId
            String userId = JWT.decode(token).getId();
            log.info("jwt中的userId:{}", userId);

            // 验证上传的token私钥部分是否与密匙一致
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(secret))
                    // 指定一个具体的验证项
                    .withIssuer("auth0")
                    .build();
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

    public TokenInfo getTokenInfo() {
        HttpServletRequest request = ServletUtils.getHttpServletRequest();
        return getTokenInfo(request.getHeader(tokenName));
    }

    public TokenInfo getTokenInfo(String token) {
        if (token == null) {
            return null;
        }
        String tokenValue = token.substring(token.indexOf(" ") + 1);
        DecodedJWT jwt = JWT.decode(tokenValue);
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setTokenName(jwt.getClaim("tokenName").asString());
        tokenInfo.setTokenPrefix(jwt.getClaim("tokenPrefix").asString());
        tokenInfo.setTokenValue(tokenValue);
        tokenInfo.setTokenTimeout((jwt.getExpiresAt().getTime() - System.currentTimeMillis()) / 1000);
        tokenInfo.setLoginId(Long.valueOf(jwt.getId()));
        tokenInfo.setLoginType(jwt.getClaim("loginType").asString());
        tokenInfo.setLoginDevice(jwt.getClaim("loginDevice").asString());

        return tokenInfo;
    }

}
