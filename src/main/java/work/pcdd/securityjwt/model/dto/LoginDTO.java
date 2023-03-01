package work.pcdd.securityjwt.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * 用户登录实体类
 *
 * @author pcdd
 * @date 2021/3/24
 */
@Data
public class LoginDTO implements Serializable {

    @NotBlank(message = "用户名不能为空")
    @Length(max = 20, message = "用户名长度不能超过20位")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 18, message = "密码长度在6-18之间")
    private String password;

}
