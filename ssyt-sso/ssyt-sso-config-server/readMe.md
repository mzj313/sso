##这是认证中心的实现
###**1.**首先启动配置服务，提供了sso-server,以及sso-service-registry两个应用依赖的配置。
###**2.**启动sso认证服务，默认会从json文件初始化一个注册的服务，即注册中心服务。
###**3.**启动sso服务注册。使用默认管理账号zhangsan,123456登录。即可管理打算接入认证中心的服务。
###**4.** app-demo是一个基于rest或者前后端分离场景做的demo,实现了rest登录（通过sso），以及本地的权限控制，给出了自定义Realm的例子，只需将权限获取方式替换成实际应用的即可。


注意事项：1. 在向注册中心注册时，只支持域名。
        2. rest方式登出的时候，客户端直接丢弃token即可。
        3. 注册中心页面。依赖google域名下的js文件，请下载到本地，或者连接vpn后访问。
        4. 后续考虑增加多点同时登陆策略的支持。

http://localhost:8888/config/sso/dev
http://localhost:8443/cas/login
http://localhost:8081/cas-management
http://localhost:8082/user/detail

db.createUser(
    {
      user: "root",
      pwd: "root123",
      roles: [ {role:"root", db:"cas-mongo-database"} ]
    }
)
mongo.exe
show dbs
use cas-mongo-database
show users
db.auth("root", "root123");


CREATE TABLE `users` (
`id`  int(11) NULL DEFAULT NULL ,
`username`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
`password`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
ROW_FORMAT=DYNAMIC
;
INSERT INTO `sso-cas`.`users` (`id`, `username`, `password`) VALUES ('1', 'admin', 'admin123');
