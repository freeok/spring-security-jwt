package work.pcdd.securityjwt.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author pcdd
 * @date 2023/03/05 07:07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {

    private TokenInfo tokenInfo;
    private UserInfoDTO userInfoDTO;

}