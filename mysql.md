### 新建用户并配置
```mysql
grant all privileges on 数据库.表名 to 'username'@'%' identified by 'password' with grant option;
（grant insert,update on …）
```
### 刷新权限
flush privileges;

### 查找安装路径
whereis mysql

### 查找运行路径
which mysql
