package work.pcdd.securityjwt.common.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author pcdd
 * create by 2021/4/3
 */
@Data
public class UserInfoDTO implements Serializable {

    private Long id;
    private String username;
    private String email;
    private String avatar;
    private String permissions;

}
