package work.pcdd.securityjwt.service;

import work.pcdd.securityjwt.common.util.R;
import work.pcdd.securityjwt.model.dto.AuthenticationRequest;

/**
 * @author pcdd
 * @date 2023/03/05 16:14
 */
public interface AuthenticationService {

    /**
     * 用户登录
     *
     * @param authenticationRequest 登录DTO
     * @return token
     */
    R login(AuthenticationRequest authenticationRequest);

    R checkToken();

}
