package work.pcdd.securityjwt.common.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author pcdd
 * @date 2022/12/12 22:58
 */
@Data
public class TokenInfo implements Serializable {

    private String tokenName;
    private String tokenPrefix;
    private String tokenValue;
    /**
     * 当前登录者的 token 剩余有效时间（单位：秒）
     */
    private Long tokenTimeout;
    private Long loginId;
    /**
     * 用户名、邮箱、手机号
     */
    private String loginType;
    /**
     * pc、webapp、app
     */
    private String loginDevice;

}
