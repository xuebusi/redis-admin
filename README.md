# redis-admin
基于SpringBoot开发的redis缓存数据图形化管理工具

# 项目简介
一个简单好用的redis缓存图形化管理工具，包含redis的5种数据类型的CRUD操作;
由于该系统是在大名鼎鼎的JeeSite基础之上开发的，所有保留了原系统的用户/角色/权限/菜单等模块.

# 启动步骤
1. 创建名为x-redis-admin数据库
2. 在创建好的数据库中执行 sql/db.sql 脚本文件来初始化基础数据
3. 修改application-dev.yml中的数据库名称、用户名和密码 
4. 运行App.java中的main方法启动系统
5. 默认浏览器访问地址 http://localhost:18080
6. 默认系统登陆账号 admin 密码 admin

# 项目使用的技术
1. SpringBoot
2. SpringMVC
3. Mybatis
4. JSP
5. Freemarker
6. JQuery
7. Ehcache
8. MySQL
9. Redis

# 有图有真相
![](screenshot/1-1541241037174.png)

![](screenshot/2-1541241084179.png)

![](screenshot/3-1541241493839.png)

![](screenshot/4-1541241149816.png)

![](screenshot/4-1541241225580.png)

![](screenshot/5-1541241277696.png)

![](screenshot/6-1541241312722.png)

![](screenshot/7-1541241360744.png)

![](screenshot/8-1541241385881.png)

![](screenshot/9-1541241421674.png)

![](screenshot/10-1541241456056.png)

# 项目计划
1. 实现redis主机和端口的图形化配置功能
2. 实现集合中元素的修改操作
3. 实现Reids集群配置管理功能
4. 实现Redis集群监控

# 联系方式
QQ 490983587

# 其他说明
欢迎优秀的开发人员参与该项目, 贡献自己的代码, 一起做一个最好的Redis客户端工具!