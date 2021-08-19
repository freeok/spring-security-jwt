package work.pcdd.securityjwt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import work.pcdd.securityjwt.common.dto.LoginDto;
import work.pcdd.securityjwt.common.dto.PersonalDto;
import work.pcdd.securityjwt.common.vo.Result;
import work.pcdd.securityjwt.entity.User;
import work.pcdd.securityjwt.mapper.UserMapper;
import work.pcdd.securityjwt.service.UserService;
import work.pcdd.securityjwt.util.JwtUtils;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 1907263405@qq.com
 * @since 2021-03-26
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    JwtUtils jwtUtils;

    @Value("${jwt.tokenHeader}")
    private String header;

    @Override
    public Result login(LoginDto loginDto, HttpServletResponse resp) {

        User user = this.getOne(new QueryWrapper<User>()
                .eq("username", loginDto.getUsername())
                .eq("password", loginDto.getPassword()));

        Assert.notNull(user, "用户名或密码错误");
        Assert.isTrue(user.getStatus() != -1, "该账户被锁定");
        Assert.isTrue(user.getStatus() != 0, "该账户被禁用");
        System.out.println(user);

        // 生成jwt
        String token = jwtUtils.generateToken(user);
        resp.addHeader(header, token);

        PersonalDto personalDto = new PersonalDto();
        BeanUtils.copyProperties(user, personalDto);
        System.out.println(personalDto);

        return Result.success("登录成功", personalDto);
    }

}
