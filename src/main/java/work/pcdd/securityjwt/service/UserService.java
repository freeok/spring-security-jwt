package work.pcdd.securityjwt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.pcdd.securityjwt.model.dto.LoginDTO;
import work.pcdd.securityjwt.model.entity.User;
import work.pcdd.securityjwt.model.vo.Result;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 1907263405@qq.com
 * @since 2021-03-26
 */
public interface UserService extends IService<User> {

    /**
     * 用户登录
     *
     * @param loginDto 用户名和密码dto
     * @param resp     HttpServletResponse
     * @return json
     */
    Result login(LoginDTO loginDto, HttpServletResponse resp);


}
