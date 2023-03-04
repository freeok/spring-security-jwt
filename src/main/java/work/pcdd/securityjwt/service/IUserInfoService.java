package work.pcdd.securityjwt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.pcdd.securityjwt.common.util.R;
import work.pcdd.securityjwt.model.dto.LoginDTO;
import work.pcdd.securityjwt.model.entity.UserInfo;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author pcdd
 * @date 2021-03-26
 */
public interface IUserInfoService extends IService<UserInfo> {

    /**
     * 用户登录
     *
     * @param loginDto 登录DTO
     * @return token
     */
    R login(LoginDTO loginDto);

    R checkToken();

}
