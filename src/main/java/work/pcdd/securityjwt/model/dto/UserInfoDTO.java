package work.pcdd.securityjwt.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author pcdd
 * @date 2021/4/3
 */
@Data
public class UserInfoDTO implements Serializable {

    private Long id;
    private String username;
    private String email;
    private String avatar;
    private String role;
    private String token;

}
