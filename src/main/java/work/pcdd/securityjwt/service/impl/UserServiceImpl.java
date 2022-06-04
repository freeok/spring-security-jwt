package work.pcdd.securityjwt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import work.pcdd.securityjwt.mapper.UserMapper;
import work.pcdd.securityjwt.model.dto.LoginDTO;
import work.pcdd.securityjwt.model.dto.PersonalDTO;
import work.pcdd.securityjwt.model.entity.User;
import work.pcdd.securityjwt.model.vo.Result;
import work.pcdd.securityjwt.service.UserService;
import work.pcdd.securityjwt.util.JwtUtils;

import javax.servlet.http.HttpServletResponse;

/**
 * @author pcdd
 * @date 2021-03-26
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    JwtUtils jwtUtils;

    @Value("${jwt.tokenHeader}")
    private String header;

    @Override
    public Result login(LoginDTO loginDTO, HttpServletResponse resp) {

        User user = this.getOne(new QueryWrapper<User>()
                .eq("username", loginDTO.getUsername())
                .eq("password", loginDTO.getPassword()));

        Assert.notNull(user, "用户名或密码错误");
        Assert.isTrue(user.getStatus() != -1, "该账户被锁定");
        Assert.isTrue(user.getStatus() != 0, "该账户被禁用");
        log.info("user：{}", user);

        // 生成jwt
        String token = jwtUtils.generateToken(user);
        resp.addHeader(header, token);

        PersonalDTO personalDTO = new PersonalDTO();
        BeanUtils.copyProperties(user, personalDTO);
        log.info("personalDto：{}", personalDTO);

        return Result.success("登录成功", personalDTO);
    }

}
