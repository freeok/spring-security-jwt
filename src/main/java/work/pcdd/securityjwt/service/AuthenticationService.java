package work.pcdd.securityjwt.service;

import work.pcdd.securityjwt.common.util.R;
import work.pcdd.securityjwt.model.dto.LoginDTO;

/**
 * @author pcdd
 * @date 2023/03/05 16:14
 */
public interface AuthenticationService {

    /**
     * 用户登录
     *
     * @param loginDto 登录DTO
     * @return token
     */
    R login(LoginDTO loginDto);

    R checkToken();

}
