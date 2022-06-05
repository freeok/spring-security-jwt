### Spring Boot + Spring Security + JWT 整合代码示例

### 核心依赖

| 依赖                  | 版本     |
|---------------------|--------|
| JDK                 | 17     |
| Spring Boot         | 2.6.7  |
| Spring Security     | 5.6.3  |
| java-jwt(com.auth0) | 3.19.2 |
| MySQL               | 8      |
| MyBatis-Plus        | 3.5.1  |

#### 安装教程

1. 手动导入sql脚本，位于根目录下的init.sql
2. 修改application.yml数据库配置
3. 运行Application

#### 使用说明

1. AuthController.login()用于申请token
2. 调用TestController中的方法，观察结果
