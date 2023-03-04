package work.pcdd.securityjwt.model.dto;

import lombok.Data;

/**
 * @author pcdd
 * @date 2023/03/05 07:07
 */
@Data
public class LoginSuccess {

    private TokenInfo tokenInfo;
    private UserInfoDTO userInfoDTO;

}
