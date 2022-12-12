### Spring Boot + Spring Security + JWT(java-jwt) 整合代码示例

### 核心依赖

| 依赖                  | 版本    |
|---------------------|-------|
| JDK                 | 17    |
| Spring Boot         | 2.7.6 |
| Spring Security     | 5.7.5 |
| java-jwt(com.auth0) | 4.x.x |
| MySQL               | 8     |
| MyBatis-Plus        | 3.5.x |

#### 安装教程

1. 手动导入sql脚本（根目录下的init.sql）
2. 修改 application.yml 数据库配置
3. 运行Application类

#### 使用说明

1. 调用TokenController中的方法，获取token
2. token放入header(格式为Authorization:Bearer jwt字符串)，然后调用TestController中的方法，观察结果
