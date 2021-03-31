## 1、docker安装



docker的安装要求：

```shell
Docker 运行在 CentOS 7 上，要求系统为64位、系统内核版本为 3.10 以上。通过 uname -r 命令查看你当前的内核版本。
```



安装docker之前先更新系统：

```shell
yum update
```



安装需要的软件包

```shell
yum install -y yum-utils device-mapper-persistent-data lvm2
```



设置docker的yum源（阿里）

```shell
yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
```



查看docker的版本并选择其中一个版本进行安装

```shell
yum list docker-ce --showduplicates | sort -r
#选择上面其中一个版本进行安装（中间一直按y确认即可）：
yum install docker-ce-18.03.1.ce
```



启动docker:

```shell
#启动docker
systemctl start docker
#加入开机自启动
systemctl enable docker
```

 

输入docker -version命令查看是否安装成功

```shell
docker -version
```



设置docker的镜像源，加速镜像下载，这里我用的是阿里的专属加速地址。

进入阿里云的控制台，选择  容器镜像服务 

然后选择下面的镜像加速器，这时候镜像加速器里会有一个镜像加速配置。

按照配置内容，配置镜像加速器即可：

```shell
mkdir -p /etc/docker 

tee /etc/docker/daemon.json
{
	"registry-mirrors": ["https://xxxx.mirror.aliyuncs.com"]
}

#重启守护进程和docker
systemctl daemon-reload
systemctl restart docker
```



## 2、docker中修改端口允许远端tcp访问，设置端口号

docker远程连接设置：

```shell
vi /lib/systemd/system/docker.service
#实际在上面的路径没有找到，通过find在下面路径找到
vi /etc/systemd/system/docker.service

#找到:
ExecStart=/usr/bin/dockerd-current \
#改成： 
ExecStart=/usr/bin/dockerd -H tcp://0.0.0.0:2375 -H unix:///var/run/docker.sock \

```

保存退出，然后输入命令来重加载和重启docker:

```shell
systemctl daemon-reload
systemctl restart docker
```



要是想测试验证刚刚设置是否生效那就输入

```shell
docker -H tcp://服务器IP:2375

#注意：2375端口号，要到阿里云或者自己的服务器官网的安全组配置开放端口号
```



如果开启了防火墙需要自己开放端口，命令如下：

```shell
firewall-cmd --zone=public --add-port=2375/tcp --permanent

#加载刚刚添加开放的端口的命令：
firewall-cmd –reload
```

 

## 3、idea中以插件+Dockerfile构建并发布镜像

Idea安装docker 插件：

```
快捷键Ctrl+Alt+S 或者点file->settings->plugins->Marketplace
在搜索框里输入docker，找到一个Docker的插件安装并重启。
```



Idea配置docker插件：

```
快捷键Ctrl+Alt+S 或者点file->settings
Build,Execution,Deployment->找到Docker

点击+添加一个Docker:
Name:	Docker
Connect to Docker Daemon with:	选择TCP socket
Engine API URL:	tcp://192.168.1.101:2375
```



Idea 配置 Run/Debug Configurations

```shell
点击运行上面的Edit Configuration..

+Docker->Dockerfile

Name:	my_project

Server:		Docker

Dockerfile:		选择一个Dockerfile

Image tag:	vfenv:1.0

然后在需要发布镜像的时候，选择一下这个自己创建的Dockerfile构建的插件
注意此时一定是已经install打包好的jar包。
```





在Dockerfile中编写：注意add的是在target路径下的，每次打镜像要先做项目的打包

```dockerfile
FROM openjdk:8-jre

MAINTAINER author <test@qq.com>

ENV LANG=C.UTF-8 LC_ALL=C.UTF-8

VOLUME /tmp

EXPOSE 8080

WORKDIR /

#拷贝target下的到 工作目录下
ADD target/service-test.jar service-test.jar

#如果用脚本记得一定不能后台运行
ADD startup.sh startup.sh

#给脚本增加执行权限
RUN chmode +x startup.sh

#启动执行命令
#ENTRYPOINT ["sh","coco.sh"]
```

 

## 4、docker-maven-plugin

加入打包插件 docker maven的pom依赖

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <executions>
                <execution>
                    <goals>
                        <goal>repackage</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
        <plugin>
            <groupId>com.spotify</groupId>
            <artifactId>docker-maven-plugin</artifactId>
            <version>1.0.0</version>
            <configuration>
                <dockerHost>http://192.168.1.100:2375</dockerHost>
                <imageName>docker/${project.artifactId}</imageName>
                <imageTags>
                    <imageTag>latest</imageTag>
                </imageTags>
                <!--dockerfile folder ***requirement***-->
                <dockerDirectory>${basedir}/src/main/docker</dockerDirectory>
                <resources>
                    <resource>
                        <targetPath>/</targetPath>
                        <directory>${project.build.directory}</directory>
                        <!--                            <include>${project.build.finalName}.jar</include>-->
                    </resource>
                </resources>
            </configuration>
        </plugin>
    </plugins>
    <finalName>${project.artifactId}</finalName>
</build>
 
```

编写 Dockerfile，注意这个文件名称的大小写，后面的 file是不大写开头的

```dockerfile
#文件路径在 src/main/docker/Dockerfile

FROM openjdk:8-jre
MAINTAINER chenwei <chenwei350@huawei.com>
ENV LANG=C.UTF-8 LC_ALL=C.UTF-8
VOLUME /tmp
EXPOSE 8080
WORKDIR /
ADD service-coco.jar service-coco.jar
ADD startup.sh startup.sh
RUN chmode +x startup.sh
#ENTRYPOINT ["sh","coco.sh"]
```



## 5、Linux使用docker配置harbor私有仓库

修改配置文件，并重启docker

 

```shell
vim /etc/docker/daemon.json
#添加如下配置
{
"insecure-registries":["harbor所在服务器的ip:端口号"]
}

#去掉代理的配置
vim /etc/systemd/system/docker.service.d/http-proxy.conf
#添加如下配置
Environment="NO_PROXY=harbor所在服务器的ip"

#重启docker
systemctl daemon-reload
systemctl restart docker
```

 

```shell
#docker登录私仓
docker login 192.168.1.222:80  #harbor所在服务器的ip:端口号
Username: root
Password：123456
```

 

```shell
#修改镜像tag
docker tag service-test:latest 192.168.1.222:80/myproject/service-test:1.0

#push镜像
docker push 192.168.1.222:80/myproject/service-test

#pull镜像
docker pull 192.168.1.222:80/myproject/service-test
```



## 5、Linux使用docker配置harbor私有仓库

使用harbor docker私仓，需要先配置本地D:\apache-maven-3.3.9\conf\settings.xml，在servers节点下server节点

 

```xml
<servers>
  <server>
   <id>my-harbor</id>
    <username>myproject</username>
    <password>123456</password>
       <!--
          <configuration>
        <email>chenwei350@huawei.com</email>
       </configuration>
          -->
  </server>
 </servers>
```

 

pom中配置

```xml
<properties>
  <!--docker插件-->
  <!-- docker私服地址-->
  <docker.repostory>192.168.1.222:80</docker.repostory>
  <!--项目名,需要和Harbor中的项目名称保持一致 -->
  <docker.registry.name>myproject</docker.registry.name>
</properties>
<!--serverId 指定之前在maven的settings.xml中配置的server节点，这样maven会去找其中配置的用户名密码和邮箱-->
<!--registryUrl 指定上面配置的properties属性，即是harbor私服的访问url，注意我设置的使用81端口，默认是80端口-->
<!--imageName 指定上传harbor私服的镜像名，必须和harbor上的url、镜像仓库名保持一致。其中的docker.registry.name就是上面配置的properties属性-->
<plugin>
  <groupId>com.spotify</groupId>
  <artifactId>docker-maven-plugin</artifactId>
  <version>1.0.0</version>
  <configuration>
    <serverId>aita-harbor</serverId>
    <registryUrl>http://${docker.repostory}</registryUrl>
    <!--必须配置dockerHost标签（除非配置系统环境变量DOCKER_HOST）-->
    <dockerHost>http://192.168.1.100:2375</dockerHost>
    <!--Building image 192.168.10.11/demo1-->
    <imageName>${docker.repostory}/${docker.registry.name}/${project.artifactId}:${project.version}</imageName>
    <!-- 指定 Dockerfile 路径-->
    <dockerDirectory>${basedir}/src/main/docker</dockerDirectory>
    <!-- jar包位置-->
    <resources>
      <resource>
        <targetPath>/</targetPath>
        <!-- target目录下-->
        <directory>${project.build.directory}</directory>
        <!--通过jar包名找到jar包-->
        <include>${pack-name}</include>
      </resource>
    </resources>
  </configuration>
</plugin>
```



## 6、maven插件dockerfile-maven-plugin(1.4.10)

查看镜像命令：

```shell
docker images
```

查看运行的容器：

```shell
docker ps
```

查看所有容器包括（运行和未运行）：

```shell
docker ps -a
```

后台运行镜像同时指定端口

```shell
docker run -itd -p 8080:8080 镜像名或镜像Id
```

停止运行的容器：

```shell
docker stop 容器ID
```

对未运行的容器进行启动操作

```shell
docker start 容器ID
```

删除容器:：注意删除的容器的在停止情况可以进行对下面操作：

```shell
docker rm 容器ID
```

删除镜像：注意：删除的镜像必须要先把用过镜像的容器（未运行和运行）删了容易才可以删镜像

```shell
docker rmi 镜像ID
```

 
