### Linux中传入spring参数

```shell
export PATH=$PATH:/opt/myproject/jdk/jdk1.8.0_231/bin

nohup java -jar TestServer.jar --spring.profiles.active=prd >console.log 2>&1 &
```



### Idea中使用-Dspring传入参数

```shell
-Dspring.profiles.active=prd

```

