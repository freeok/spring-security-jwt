package work.pcdd.securityjwt.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import work.pcdd.securityjwt.model.dto.TokenInfo;
import work.pcdd.securityjwt.model.dto.UserInfoDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
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

    /**
     * 根据用户id，role 生成token
     */
    public String generateToken(UserInfoDTO userInfoDTO) {
        log.info("开始生成token");
        Date nowDate = new Date();
        Date expireDate = new Date(nowDate.getTime() + expire * 1000);

        try {
            String tokenValue = JWT.create()
                    // 签名由谁生成(可选)
                    .withIssuer("auth0")
                    // 生成签名的时间(可选)
                    .withIssuedAt(nowDate)
                    // jwt的id，此处为用户id
                    .withAudience(String.valueOf(userInfoDTO.getId()))
                    // 签名过期的时间
                    .withExpiresAt(expireDate)
                    // 携带自定义信息
                    //.withClaim("role", userInfoDTO.getRole())
                    //.withClaim("tokenName", tokenName)
                    //.withClaim("tokenPrefix", tokenPrefix)
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
            String userId = JWT.decode(token).getAudience().get(0);
            log.info("jwt中的userId:{}", userId);

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

    public TokenInfo getTokenInfo() {
        HttpServletRequest request = ServletUtils.getHttpServletRequest();
        return getTokenInfo(request.getHeader(tokenName));
    }

    public TokenInfo getTokenInfo(String s) {
        if (s == null) {
            return null;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String token = s.substring(s.indexOf(" ") + 1);
        DecodedJWT jwt = JWT.decode(token);
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setTokenName(jwt.getClaim("tokenName").asString());
        tokenInfo.setTokenPrefix(jwt.getClaim("tokenPrefix").asString());
        tokenInfo.setTokenValue(token);
        tokenInfo.setLoginId(Long.valueOf(jwt.getAudience().get(0)));
        tokenInfo.setAuthorities(authorities);
        tokenInfo.setTokenTimeout((jwt.getExpiresAt().getTime() - System.currentTimeMillis()) / 1000);

        return tokenInfo;
    }

}
