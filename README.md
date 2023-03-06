### Spring Boot 3 + Spring Security + JWT(auth0) 整合代码示例（仅适用于前后端分离）

### 核心依赖

| 依赖                  | 版本      |
|---------------------|---------|
| JDK                 | 17      |
| Spring Boot         | 3.0.4   |
| Spring Security     | 6.0.2   |
| java-jwt(com.auth0) | 4.3.0   |
| MySQL               | 8       |
| MyBatis-Plus        | 3.5.3.1 |

#### 安装教程

1. 手动导入sql脚本（[init.sql](https://github.com/pcdd-group/spring-security-jwt/blob/main/init.sql)）
2. 修改 application.yml 数据库配置
3. 运行 Application 类

#### 使用说明

1. 调用 [AuthenticationController.java](https://github.com/pcdd-group/spring-security-jwt/blob/main/src/main/java/work/pcdd/securityjwt/controller/AuthenticationController.java)
中的方法，获取token
2. token放入header（key为Authorization，value为Bearer
   jwt字符串），然后调用 [TestController.java](https://github.com/pcdd-group/spring-security-jwt/blob/main/src/main/java/work/pcdd/securityjwt/controller/TestController.java)
   中的方法，观察结果
3. 所有用户明文密码均为6个0

#### 补充说明

从 Spring Boot 2.7.0（Spring Security 5.7.1）开始，WebSecurityConfigurerAdapter
已弃用，最新配置类写法请参考[SecurityLatestConfig.java](https://github.com/pcdd-group/spring-security-jwt/blob/main/src/main/java/work/pcdd/securityjwt/config/SecurityLatestConfig.java)
；若仍要使用旧的配置类，请参考[SecurityConfig.java](https://github.com/pcdd-group/spring-security-jwt/blob/main/src/main/java/work/pcdd/securityjwt/config/SecurityConfig.java)
