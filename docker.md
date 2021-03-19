## 1.镜像源

```text
从网上找了几个速度比较快的镜像地址：
Docker官方中国区  https://registry.docker-cn.com
网易  http://hub-mirror.c.163.com
ustc    https://docker.mirrors.ustc.edu.cn
阿里云  https://cr.console.aliyun.com/
```

 

## 2.修改镜像源

```shell
1.直接设置 –registry-mirror 参数，仅对当前的命令有效 
 docker run hello-world --registry-mirror=https://docker.mirrors.ustc.edu.cn

2.修改 /etc/default/docker，加入 DOCKER_OPTS=”镜像地址”，可以有多个 
DOCKER_OPTS="--registry-mirror=https://docker.mirrors.ustc.edu.cn"

3.支持 systemctl 的系统，通过 sudo systemctl edit docker.service，
会生成 etc/systemd/system/docker.service.d/override.conf 覆盖默认的参数，在该文件中加入如下内容： 
 [Service] 
 ExecStart= 
 ExecStart=/usr/bin/docker -d -H fd:// --registry-mirror=https://docker.mirrors.ustc.edu.cn

4.新版的 Docker 推荐使用 json 配置文件的方式，默认为 /etc/docker/daemon.json
(非默认路径需要修改 dockerd 的 –config-file)
在该文件中加入如下内容： 
 { 
  "registry-mirrors": ["https://docker.mirrors.ustc.edu.cn"] 
 }
此种方法修改后需要重启docker
```

 

## 3.修改私仓-registry地址

**1、安装私仓**

```shell
很简单，只需要运行一个Registry容器即可（包括下载镜像和启动容器、服务）
docker run -d -p 5000:5000 -v /data/registry:/var/lib/registry --name registry --restart=always registry
```

 

**2、使用私仓**

```shell
由于Registry为了安全性考虑，默认是需要https证书支持的.
但是我们可以通过一个简单的办法解决：
修改/etc/docker/daemon.json文件
vi /etc/docker/daemon.json
{
	"insecure-registries": ["192.168.1.102:80"],
	"registry-mirrors": [
    	"http://192.168.1.102:80"
	]
}
```

*修改前*：

```json
{
 "registry-mirrors": [
  "https://dockerhub.azk8s.cn",
  "https://hub-mirror.c.163.com"
 ]
}
```

 

**3、配置docker代理：**

```shell
vim /etc/systemd/system/docker.service.d/http-proxy.conf
添加如下配置
Environment="NO_PROXY=192.168.1.102"
**重启docker**
systemctl daemon-reload
systemctl restart docker
```

 

**4、通过docker tag重命名镜像，使之与registry匹配**

```shell
docker tag inits/nginx1.8 192.168.1.102:80/aita/nginx1.8:latest
```

 

**5、上传镜像到Registry**

```shell
docker push 192.168.1.102:80/aita/nginx1.8:latest
```

 

**6、查看Registry中所有镜像信息**

```shell
浏览器中输入进入harbor->http://192.168.1.102:80    
Username: admin
Password：123456

docker login 192.168.1.102:80
Username: admin
Password：123456
```

  

**7、其他Docker服务器下载镜像**

```shell
docker pull 192.168.1.102:80/aita/nginx1.8:latest
```

 

## 4.docker常用命令:

### 4.1 启动关闭docker

```shell
service docker start 或者 systemctrl start docker
service docker stop 或者 systemctrl stop docker 
```



### 4.2 镜像相关命令

```shell
docker search 关键词    #在docker hub中搜索镜像 

docker images    #查看所有镜像

docker pull centos:7    #下载nginx镜像---从docker hub上下载镜像 centos:latest最后一个版本

docker rmi 镜像名    #从本地删除一个已下载的镜像  -f强制删除  尽量先删除容器 再删镜像

docker rmi [-f] $(docker images -q)    #删除全部本地镜像 注意这要先删容器再删镜像
```



### 4.3 创建容器

```shell
docker run 
-d 后台运行，如果不加-d则退出时容器关闭。
-P 随机端口 
-p 指定端口映射 本地端口:容器端口 
-name=mycentos7 指定容器名称
-v 路径映射 本地路径:容器路径
docker run -itd --name=mycent centos:7 /bin/bash 

#退出
使用CTRL+D 或者 输入exit 退出容器

如果容器里有CMD启动或者ENTRYPOINT启动 执行程序，就不能用/bin/bash了，会把后面当成参数

直接执行：docker run –d –name=mycent contos:myjava
```



### 4.4 查看容器的错误日志

```shell
docker logs container 
```



### 4.5 查看容器

```shell
docker ps  #运行容器

docker ps -a   #所有容器
```

 

### 4.6 进入正在运行容器

```shell
1、docker attach 容器ID-------退出：ctrl+q+p (所有窗口显示一样的控制台)

2、使用nsenter进入docker

docerk inspect --help  这个命令中

docker inspect 容器id   查看容器详细信息

docker inspect -f{{.State.Pid}} 容器ID    获取进程PID

nsenter --target 3326 --mount --uts --ipc --net --pid 使用nsenter进入容器 

3、docker exec -it 容器id /bin/bash  进入容器----退出 ctrl +c 或者输入exit
```



### 4.7 查看容器和系统信息

```
docker version 版本信息

docker info docker系统信息
```



### 4.8关闭容器和删除容器

```
docker kill 容器ID 

docker start stop restart 容器id 再次启动 关闭容器

docker stop $(docker ps -a -q) 停止所有容器 

docker rm 容器id 删除

docker rm $(docker ps -a -q)  删除所有容器  -q只显示id
```

 

### 4.9 用于根据给定的Dockerfile和上下文构建Docker镜像

```
docker build -t nginx:v1 Dockerfile 
```



### 4.10 docker其它帮助命令

```
docker --help 查看更多命令

docker run --help查看 docker run 的参数
```

### 4.11 使用容器创建镜像

```shell
这种方法，先需要下载一个镜像，然后修改，然后提交，只是提交的差异部分。

docker commit -a "chenwei" -m "my description" CONTAINER myimage:v1

-a：提交镜像的作者

-m：提交时的文字说明

-c：使用dockerfile指令创建镜像

-p：在commit时将容器暂停

CONTAINER：容器id或者容器name

myimage：新的镜像的名称（REPOSITORY）

v1：TAG
```

### 4.12 镜像保存

```shell
docker save -o nginx.tar nginx:latest  

docker save nginx:latest > nginx.tar  
```

### 4.13 镜像导入

```shell
docker load -i nginx.tar

docker load < nginx.tar
```

 

### 4.14 容器导出

```shell
docker export -o nginx-test.tar nginx-test

其中-o表示输出到文件，nginx-test.tar为目标文件，

nginx-test是源容器name或容器id
```



### 4.15 容器导入

```shell
docker import nginx-test.tar nginx:imp

cat nginx-test.tar | docker import - nginx:imp
```

 

### 4.16 镜像导入导出与容器导入导出区别

```
镜像的导入是复制过程

容器的导入是将当前容器编程一个新的镜像

镜像的save是保存镜像的所有信息，包含历史

容器的export只导出当前信息
```

 

### 4.17 docker --help

```shell
[root@localhost automl_log]# docker --help
Usage: docker [OPTIONS] COMMAND
A self-sufficient runtime for containers
Options:
   --config string   Location of client config files (default "/root/.docker")
 -c, --context string   Name of the context to use to connect to the daemon (overrides DOCKER_HOST env var and default context set with "docker context use")
 -D, --debug       Enable debug mode
 -H, --host list     Daemon socket(s) to connect to
 -l, --log-level string  Set the logging level ("debug"|"info"|"warn"|"error"|"fatal") (default "info")
   --tls        Use TLS; implied by --tlsverify
   --tlscacert string  Trust certs signed only by this CA (default "/root/.docker/ca.pem")
   --tlscert string   Path to TLS certificate file (default "/root/.docker/cert.pem")
   --tlskey string   Path to TLS key file (default "/root/.docker/key.pem")
   --tlsverify     Use TLS and verify the remote
 -v, --version      Print version information and quit
Management Commands:
 builder   Manage builds
 config   Manage Docker configs
 container  Manage containers
 context   Manage contexts
 engine   Manage the docker engine
 image    Manage images
 network   Manage networks
 node    Manage Swarm nodes
 plugin   Manage plugins
 secret   Manage Docker secrets
 service   Manage services
 stack    Manage Docker stacks
 swarm    Manage Swarm
 system   Manage Docker
 trust    Manage trust on Docker images
 volume   Manage volumes
Commands:
 attach   Attach local standard input, output, and error streams to a running container
 build    Build an image from a Dockerfile
 commit   Create a new image from a container's changes
 cp     Copy files/folders between a container and the local filesystem
 create   Create a new container
 diff    Inspect changes to files or directories on a container's filesystem
 events   Get real time events from the server
 exec    Run a command in a running container
 export   Export a container's filesystem as a tar archive
 history   Show the history of an image
 images   List images
 import   Import the contents from a tarball to create a filesystem image
 info    Display system-wide information
 inspect   Return low-level information on Docker objects
 kill    Kill one or more running containers
 load    Load an image from a tar archive or STDIN
 login    Log in to a Docker registry
 logout   Log out from a Docker registry
 logs    Fetch the logs of a container
 pause    Pause all processes within one or more containers
 port    List port mappings or a specific mapping for the container
 ps     List containers
 pull    Pull an image or a repository from a registry
 push    Push an image or a repository to a registry
 rename   Rename a container
 restart   Restart one or more containers
 rm     Remove one or more containers
 rmi     Remove one or more images
 run     Run a command in a new container
 save    Save one or more images to a tar archive (streamed to STDOUT by default)
 search   Search the Docker Hub for images
 start    Start one or more stopped containers
 stats    Display a live stream of container(s) resource usage statistics
 stop    Stop one or more running containers
 tag     Create a tag TARGET_IMAGE that refers to SOURCE_IMAGE
 top     Display the running processes of a container
 unpause   Unpause all processes within one or more containers
 update   Update configuration of one or more containers
 version   Show the Docker version information
 wait    Block until one or more containers stop, then print their exit codes
```



## 5.dockerfile

```shell
#用于在Dockerfile写完后生成镜像。

docker build –t runoob/ubuntu:v1  #使用当前所在文件夹下的Dockerfile创建镜像

docker build github.com/creack/docker-firefox #使用url创建镜像

docker build –f /path/to/a/Dockerfile .      #通过-f指定Dockerfile的位置创建镜像

#测试，可以返回错误检查

docker build -t test/myapp .
```

 

docker build [OPTIONS] PATH | URL |-

参数说明：

· **--build-arg=[] :**设置镜像创建时的变量；

· **--cpu-shares :**设置 cpu 使用权重；

· **--cpu-period :**限制 CPU CFS周期；

· **--cpu-quota :**限制 CPU CFS配额；

· **--cpuset-cpus :**指定使用的CPU id；

· **--cpuset-mems :**指定使用的内存 id；

· **--disable-content-trust :**忽略校验，默认开启；

· **-f :**指定要使用的Dockerfile路径；

· **--force-rm :**设置镜像过程中删除中间容器；

· **--isolation :**使用容器隔离技术；

· **--label=[] :**设置镜像使用的元数据；

· **-m :**设置内存最大值；

· **--memory-swap :**设置Swap的最大值为内存+swap，"-1"表示不限swap；

· **--no-cache :**创建镜像的过程不使用缓存；

· **--pull :**尝试去更新镜像的新版本；

· **--quiet, -q :**安静模式，成功后只输出镜像 ID；

· **--rm :**设置镜像成功后删除中间容器；

· **--shm-size :**设置/dev/shm的大小，默认值是64M；

· **--ulimit :**Ulimit配置。

· **--tag, -t:** 镜像的名字及标签，通常 name:tag 或者 name 格式；可以在一次构建中为一个镜像设置多个标签。

· **--network:** 默认 default。在构建期间设置RUN指令的网络模式

 

 

### 5.1 主要组成部分

  dockerfile执行build命令时，从上倒下依次执行，命令的基本组成部分如下：

| 主要部分           | 代表性命令                                                   |
| ------------------ | ------------------------------------------------------------ |
| 基础镜像信息       | FROM                                                         |
| 维护者信息         | MAINTAINER Chenwei  MAINTAINER [chenwei@163.com](mailto:chenwei@163.com)  MAINTAINER Chenwei  <chenwei@163.com> |
| 镜像操作指令       | RUN、COPY、ADD、EXPOSE、WORKDIR、ONBUILD、USER、VOLUME、ENV等 |
| 容器启动时执行指令 | CMD、ENTRYPOINT                                              |

 

```shell
FROM 通过该指令从docker hub上或者本地存储库中拉取镜像作为基本镜像

RUN 执行语句。RUN （shell形式），RUN [“executable”, “param1”, “param2”]（exec形式）

CMD 类似RUN命令 用于运行程序，cmd在容器运行时，run在docker build时。

  CMD [“executable”,”param1”,”param2”]（exec形式，这是首选形式）

  CMD [“param1”,”param2”]（作为ENTRYPOINT的默认参数）

  CMD command param1 param2（shell形式）

​    一个Dockerfile中只能有一条CMD指令。如果列出多个CMD,则只有最后一个CMD生效。

​    如果docker run指定了其他命令，CMD 指定的默认命令将被忽略

COPY 复制指令，从上下文环境复制文件到容器制定目录，不解压

ADD 类似copy 但解压

ENTRYPOINT 在容器启动时通过执行 ENTRYPOINT ["/docker-entrypoint.sh"]

LABEL 该LABEL指令将元数据添加到镜像 LABEL a="ACME Incorporated"

ENV 置环境变量 ENV NODE_VERSION 7.2.0 $NODE_VERSION

ARG 与 ENV 作用一至。不过作用域不一样。ARG 设置的环境变量仅对 Dockerfile 内有效

VOLUME 在启动容器时忘记挂载数据卷，会自动挂载到匿名卷

EXPOSE 用于指定容器将要监听的端口。

WORKDIR 指定工作目录，类似执行cd命令，之后的命令都是是基于此工作目录。

USER 于指定执行后续命令的用户和用户组，这边只是切换后续命令执行的用户（用户和用户组必须提前已经存在）

HEALTHCHECK 用于指定某个程序或者指令来监控 docker 容器服务的运行状态。

ONBUILD 用于延迟构建命令的执行
```

 

### 5.2 完整demo

```dockerfile
# Dockerfile
# 基于的镜像
FROM openjdk:8-jdk-alpine
 
VOLUME /opt/tmp
 
ADD chapter-14-0.0.1-SNAPSHOT.jar app.jar
 
# -Djava.security.egd=file:/dev/./urandom 可解决tomcat可能启动慢的问题
# 具体可查看：https://www.cnblogs.com/mightyvincent/p/7685310.html
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
 
# 对外端口
EXPOSE 8080
```

 

## 6.Docker Compose

```
Compose 简介

Compose 是用于定义和运行多容器 Docker 应用程序的工具。通过 Compose，您可以使用 YML 文件来配置应用程序需要的所有服务。然后，使用一个命令，就可以从 YML 文件配置中创建并启动所有服务。
```

###  6.0 Docker-compose常用命令

```shell
docker-compose up -d nginx                     #构建建启动nignx容器
docker-compose exec nginx bash            #登录到nginx容器中
docker-compose down                              #删除所有nginx容器,镜像
docker-compose ps                                  #显示所有容器
docker-compose restart nginx                   #重新启动nginx容器
docker-compose run --no-deps --rm php-fpm php -v  #在php-fpm中不启动关联容器，并容器执行php -v 执行完成后删除容器
docker-compose build nginx                     #构建镜像     
docker-compose build --no-cache nginx   #不带缓存的构建
docker-compose logs  nginx                     #查看nginx的日志 
docker-compose logs -f nginx                   #查看nginx的实时日志
docker-compose config  -q                      #验证docker-compose.yml文件配置，正确时不输出，配置错误输出错误信息
docker-compose events --json nginx       #以json的形式输出nginx的docker日志
docker-compose pause nginx                 #暂停nignx容器
docker-compose unpause nginx             #恢复ningx容器
docker-compose rm nginx                       #删除容器（删除前必须关闭容器）
docker-compose stop nginx                    #停止nignx容器
docker-compose start nginx                    #启动nignx容器
```



### 6.1 Compose 使用的三个步骤：

```
1、使用 Dockerfile 定义应用程序的环境。
2、使用 docker-compose.yml 定义构成应用程序的服务，这样它们可以在隔离环境中一起运行。
3、最后，执行 docker-compose -d up 命令来启动并运行整个应用程序。 
```

#### 6.11 docker-compose.yml 的配置案例

如下（配置参数参考下文）：

```yaml
version: "3.7"
services:
  #从这开始就是服务 redis服务
  redis:
    image: redis  #构建基于镜像
    ports:  #暴露端口 冒号左边是主机的端口，右边是容器的端口，如果没有冒号是绑定容器的 6379到主机任意端口，容器启动时随机分配绑定主机端口号
      - "6379"
    networks: #指定网络 
      - frontend
    deploy:
      replicas: 2
      update_config:
        parallelism: 2  #一次要回滚容器数量，0全回滚 正常与启动的副本集一样
        delay: 10s  #延迟时间 10秒
      restart_policy:		#重启策略
        condition: on-failure  #失败
  #服务
  db:
    image: posgres:9.4
    volumes:  #指定挂载路径 冒号左边是本地路径   冒号右边是docker容器内路径
      - db-data:/var/lib/postgresql/data
    networks:
      - backend
    deploy:
      placement:
        constraints: [node.role == manager]
  #服务      
  vote:
    image: dockersamples/examplevotingapp_vote:before
    ports:  #端口映射 5000是主机端口    80是容器内端口
      - "5000:80"
    networks:
      - frontend
    depends_on:
      - redis
    deploy:
      replicas: 2
      update_config:
        parallelism: 2
      restart_policy:
        condition: on-failure
  #服务
  result:
    image: dockersamples/examplevotingapp_vote:before
    ports:
      - "5000:80"
    networks:
      - backend
    depends_on:
      - db
    deploy:
      replicas: 1
      update_config:
        parallelism: 2
        delay: 10s
      restart_policy:
        condition: on-failure
        
  worker:
    image: worker
    networks:
      - frontend
      - backend
    deploy:
      mode: replicated
      replicase: 1
      labels: [APP=VOTING]
      restart_policy:
        condition: on-failure
        delay: 10s
        max_attempts: 3
        window: 120s
      placement:
        constraints: [node.role == manager]
  visualizer:
    image: dockersamples/visualizer:stable
    ports:
      - "8080:8080"
    stop_grace_period: 1m30s
    volumes:
      - "/var/run/docer.sock:/var/run/docker.sock"
    deploy:
      placement:
        cpmstraints: [node.role == manager]

networks:
  frontend:
  backend:
volumnes:
  db-data: 
    
      
```



### 6.2 Compose 安装

#### Linux版本

```shell
Linux 上我们可以从 Github 上下载它的二进制包来使用，最新发行的版本地址：https://github.com/docker/compose/releases。
 运行以下命令以下载 Docker Compose 的当前稳定版本：
 $ sudo curl -L "https://github.com/docker/compose/releases/download/1.24.1/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
要安装其他版本的 Compose，请替换 1.24.1。
将可执行权限应用于二进制文件：
 $ sudo chmod +x /usr/local/bin/docker-compose
创建软链：
 $ sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose
测试是否安装成功：
$ docker-compose --version

cker-compose version 1.24.1, build 4667896b
注意： 对于 alpine，需要以下依赖包： py-pip，python-dev，libffi-dev，openssl-dev，gcc，libc-dev，和 make。
```

#### macOS版本

```shell
Mac 的 Docker 桌面版和 Docker Toolbox 已经包括 Compose 和其他 Docker 应用程序，因此 Mac 用户不需要单独安装 Compose。Docker 安装说明可以参阅 MacOS Docker 安装。
```

#### windows版本

```shell
Windows 的 Docker 桌面版和 Docker Toolbox 已经包括 Compose 和其他 Docker 应用程序，因此 Windows 用户不需要单独安装 Compose。Docker 安装说明可以参阅 Windows Docker 安装。
```



### 6.3 使用 Compose yaml详解

一份标准配置文件应该包含 version、services、networks 三大部分，其中最关键的就是 services 和 networks 两个部分，下面先来看 services 的书写规则

#### 1. image

```yaml
services:
  web:
    image: hello-world
```

 在 services 标签下的第二级标签是 web，这个名字是用户自己自定义，它就是服务名称。
 image 则是指定服务的镜像名称或镜像 ID。如果镜像在本地不存在，Compose 将会尝试拉取这个镜像。 

#### 2. build

服务除了可以基于指定的镜像，还可以基于一份 Dockerfile，在使用 up 启动之时执行构建任务，这个构建标签就是 build，它可以指定 Dockerfile 所在文件夹的路径。Compose 将会利用它自动构建这个镜像，然后使用这个镜像启动服务容器。

```yaml
services:
  web:
    build: /path/to/build/dir  
    #build: ./dir	#也可以是相对路径，只要上下文确定就可以读取到 Dockerfile。
```

 设定上下文根目录，然后以该目录为准指定 Dockerfile。

```yaml
build:
  context: ../
  dockerfile: path/of/Dockerfile
```

注意 build 都是一个目录，如果你要指定 Dockerfile 文件需要在 build 标签的子级标签中使用 dockerfile 标签指定，如上面的例子。

如果你同时指定了 image 和 build 两个标签，那么 Compose 会构建镜像并且把镜像命名为 image 后面的那个名字。

```yaml
services:
  web:
    build: ./dir
    image: webapp:tag
```

既然可以在 docker-compose.yml 中定义构建任务，那么一定少不了 arg 这个标签，就像 Dockerfile 中的 ARG 指令，它可以在构建过程中指定环境变量，但是在构建成功后取消，在 docker-compose.yml 文件中也支持这样的写法

```yaml
build:
  context: .
  args:
    buildno: 1
    password: secret
```

下面这种写法也是支持的，一般来说下面的写法更适合阅读。

```yaml
build:
  context: .
  args:
    - buildno=1
    - password=secret
```

与 ENV 不同的是，ARG 是允许空值的。例如：

```yaml
args:
  - buildno
  - password
```

这样构建过程可以向它们赋值。

注意：YAML 的布尔值（true, false, yes, no, on, off）必须要使用引号引起来（单引号、双引号均可），否则会当成字符串解析



#### 3. command

使用 command 可以覆盖容器启动后默认执行的命令。

```bash
command: bundle exec thin -p 3000
```

也可以写成类似 Dockerfile 中的格式：

```bash
command: [bundle, exec, thin, -p, 3000]
```

#### 4.container_name

前面说过 Compose 的容器名称格式是：<项目名称>*<服务名称>*<序号>
 虽然可以自定义项目名称、服务名称，但是如果你想完全控制容器的命名，可以使用这个标签指定：

```undefined
container_name: app
```

这样容器的名字就指定为 app 了。

#### 5.depends_on

在使用 Compose 时，最大的好处就是少打启动命令，但是一般项目容器启动的顺序是有要求的，如果直接从上到下启动容器，必然会因为容器依赖问题而启动失败。
 例如在没启动数据库容器的时候启动了应用容器，这时候应用容器会因为找不到数据库而退出，为了避免这种情况我们需要加入一个标签，就是 depends_on，这个标签解决了容器的依赖、启动先后的问题。
 例如下面容器会先启动 redis 和 db 两个服务，最后才启动 web 服务：

```bash
version: '2'
services:
  web:
    build: .
    depends_on:
      - db
      - redis
  redis:
    image: redis
  db:
    image: postgres
```

注意的是，默认情况下使用 docker-compose up web 这样的方式启动 web 服务时，也会启动 redis 和 db 两个服务，因为在配置文件中定义了依赖关系。

#### 6.dns

和 --dns 参数一样用途，格式如下：

```css
dns: 8.8.8.8
```

也可以是一个列表：

```css
dns:
  - 8.8.8.8
  - 9.9.9.9
```

此外 dns_search 的配置也类似：

```css
dns_search: example.com
dns_search:
  - dc1.example.com
  - dc2.example.com
```

#### 7. tmpfs

挂载临时目录到容器内部，与 run 的参数一样效果：

```undefined
tmpfs: /run
tmpfs:
  - /run
  - /tmp
```

#### 8. entrypoint

在 Dockerfile 中有一个指令叫做 ENTRYPOINT 指令，用于指定接入点，第四章有对比过与 CMD 的区别。
在 docker-compose.yml 中可以定义接入点，覆盖 Dockerfile 中的定义：

```jsx
entrypoint: /code/entrypoint.sh
```

格式和 Docker 类似，不过还可以写成这样：

```ruby
entrypoint:
    - php
    - -d
    - zend_extension=/usr/local/lib/php/extensions/no-debug-non-zts-20100525/xdebug.so
    - -d
    - memory_limit=-1
    - vendor/bin/phpunit
```

#### 9.env_file

还记得前面提到的 .env 文件吧，这个文件可以设置 Compose 的变量。而在 docker-compose.yml 中可以定义一个专门存放变量的文件。
 如果通过 docker-compose -f FILE 指定了配置文件，则 env_file 中路径会使用配置文件路径。

如果有变量名称与 environment 指令冲突，则以后者为准。格式如下：

```css
env_file: .env
```

或者根据 docker-compose.yml 设置多个：

```jsx
env_file:
  - ./common.env
  - ./apps/web.env
  - /opt/secrets.env
```

注意的是这里所说的环境变量是对宿主机的 Compose 而言的，如果在配置文件中有 build 操作，这些变量并不会进入构建过程中，如果要在构建中使用变量还是首选前面刚讲的 arg 标签。

#### 10. environment

与上面的 env_file 标签完全不同，反而和 arg 有几分类似，这个标签的作用是设置镜像变量，它可以保存变量到镜像里面，也就是说启动的容器也会包含这些变量设置，这是与 arg 最大的不同。
 一般 arg 标签的变量仅用在构建过程中。而 environment 和 Dockerfile 中的 ENV 指令一样会把变量一直保存在镜像、容器中，类似 docker run -e 的效果。

```bash
environment:
  RACK_ENV: development
  SHOW: 'true'
  SESSION_SECRET:

environment:
  - RACK_ENV=development
  - SHOW=true
  - SESSION_SECRET
```

#### 11. expose

这个标签与Dockerfile中的EXPOSE指令一样，用于指定暴露的端口，但是只是作为一种参考，实际上docker-compose.yml的端口映射还得ports这样的标签。

```bash
expose:
 - "3000"
 - "8000"
```

#### 12. external_links

在使用Docker过程中，我们会有许多单独使用docker run启动的容器，为了使Compose能够连接这些不在docker-compose.yml中定义的容器，我们需要一个特殊的标签，就是external_links，它可以让Compose项目里面的容器连接到那些项目配置外部的容器（前提是外部容器中必须至少有一个容器是连接到与项目内的服务的同一个网络里面）。
 格式如下：

```css
external_links:
 - redis_1
 - project_db_1:mysql
 - project_db_1:postgresql
```

#### 13. extra_hosts

添加主机名的标签，就是往/etc/hosts文件中添加一些记录，与Docker client的--add-host类似：

```bash
extra_hosts:
 - "somehost:162.242.195.82"
 - "otherhost:50.31.209.229"
```

启动之后查看容器内部hosts：

```css
162.242.195.82  somehost
50.31.209.229   otherhost
```

#### 14. labels

向容器添加元数据，和Dockerfile的LABEL指令一个意思，格式如下：

```csharp
labels:
  com.example.description: "Accounting webapp"
  com.example.department: "Finance"
  com.example.label-with-empty-value: ""
labels:
  - "com.example.description=Accounting webapp"
  - "com.example.department=Finance"
  - "com.example.label-with-empty-value"
```

#### 15. links

还记得上面的depends_on吧，那个标签解决的是启动顺序问题，这个标签解决的是容器连接问题，与Docker client的--link一样效果，会连接到其它服务中的容器。
 格式如下：

```css
links:
 - db
 - db:database
 - redis
```

使用的别名将会自动在服务容器中的/etc/hosts里创建。例如：

```css
172.12.2.186  db
172.12.2.186  database
172.12.2.187  redis
```

相应的环境变量也将被创建。

#### 16. logging

这个标签用于配置日志服务。格式如下：

```bash
logging:
  driver: syslog
  options:
    syslog-address: "tcp://192.168.0.42:123"
```

默认的driver是json-file。只有json-file和journald可以通过docker-compose logs显示日志，其他方式有其他日志查看方式，但目前Compose不支持。对于可选值可以使用options指定。
 有关更多这方面的信息可以阅读官方文档：
 [https://docs.docker.com/engine/admin/logging/overview/](https://link.jianshu.com?t=https://docs.docker.com/engine/admin/logging/overview/)

#### 17. pid

```bash
pid: "host"
```

将PID模式设置为主机PID模式，跟主机系统共享进程命名空间。容器使用这个标签将能够访问和操纵其他容器和宿主机的名称空间。

#### 18. ports

映射端口的标签。
 使用HOST:CONTAINER格式或者只是指定容器的端口，宿主机会随机映射端口。

```css
ports:
 - "3000"
 - "8000:8000"
 - "49100:22"
 - "127.0.0.1:8001:8001"
```

> 注意：当使用HOST:CONTAINER格式来映射端口时，如果你使用的容器端口小于60你可能会得到错误得结果，因为YAML将会解析xx:yy这种数字格式为60进制。所以建议采用字符串格式。

#### 19. security_opt

为每个容器覆盖默认的标签。简单说来就是管理全部服务的标签。比如设置全部服务的user标签值为USER。

```css
security_opt:
  - label:user:USER
  - label:role:ROLE
```

#### 20. stop_signal

设置另一个信号来停止容器。在默认情况下使用的是SIGTERM停止容器。设置另一个信号可以使用stop_signal标签。

```undefined
stop_signal: SIGUSR1
```

#### 21. volumes

挂载一个目录或者一个已存在的数据卷容器，可以直接使用 [HOST:CONTAINER] 这样的格式，或者使用 [HOST:CONTAINER:ro] 这样的格式，后者对于容器来说，数据卷是只读的，这样可以有效保护宿主机的文件系统。
 Compose的数据卷指定路径可以是相对路径，使用 . 或者 .. 来指定相对目录。
 数据卷的格式可以是下面多种形式：

```jsx
volumes:
  // 只是指定一个路径，Docker 会自动在创建一个数据卷（这个路径是容器内部的）。
  - /var/lib/mysql

  // 使用绝对路径挂载数据卷
  - /opt/data:/var/lib/mysql

  // 以 Compose 配置文件为中心的相对路径作为数据卷挂载到容器。
  - ./cache:/tmp/cache

  // 使用用户的相对路径（~/ 表示的目录是 /home/<用户目录>/ 或者 /root/）。
  - ~/configs:/etc/configs/:ro

  // 已经存在的命名的数据卷。
  - datavolume:/var/lib/mysql
```

如果你不使用宿主机的路径，你可以指定一个volume_driver。

```undefined
volume_driver: mydriver
```

#### 22. volumes_from

从其它容器或者服务挂载数据卷，可选的参数是 :ro或者 :rw，前者表示容器只读，后者表示容器对数据卷是可读可写的。默认情况下是可读可写的。

```css
volumes_from:
  - service_name
  - service_name:ro
  - container:container_name
  - container:container_name:rw
```

#### 23. cap_add, cap_drop

添加或删除容器的内核功能。详细信息在前面容器章节有讲解，此处不再赘述。

```undefined
cap_add:
  - ALL

cap_drop:
  - NET_ADMIN
  - SYS_ADMIN
```

#### 24. cgroup_parent

指定一个容器的父级cgroup。

```undefined
cgroup_parent: m-executor-abcd
```

#### 25. devices

设备映射列表。与Docker client的--device参数类似。

```bash
devices:
  - "/dev/ttyUSB0:/dev/ttyUSB0"
```

#### 26. extends

这个标签可以扩展另一个服务，扩展内容可以是来自在当前文件，也可以是来自其他文件，相同服务的情况下，后来者会有选择地覆盖原有配置。

```css
extends:
  file: common.yml
  service: webapp
```

用户可以在任何地方使用这个标签，只要标签内容包含file和service两个值就可以了。file的值可以是相对或者绝对路径，如果不指定file的值，那么Compose会读取当前YML文件的信息。
 更多的操作细节在后面的12.3.4小节有介绍。

#### 27. network_mode

网络模式，与Docker client的--net参数类似，只是相对多了一个service:[service name] 的格式。
 例如：



```bash
network_mode: "bridge"
network_mode: "host"
network_mode: "none"
network_mode: "service:[service name]"
network_mode: "container:[container name/id]"
```

可以指定使用服务或者容器的网络。

#### 28. networks

加入指定网络，格式如下：



```yaml
services:
  some-service:
    networks:
     - some-network
     - other-network
```

关于这个标签还有一个特别的子标签aliases，这是一个用来设置服务别名的标签，例如：



```yaml
services:
  some-service:
    networks:
      some-network:
        aliases:
         - alias1
         - alias3
      other-network:
        aliases:
         - alias2
```

相同的服务可以在不同的网络有不同的别名。

#### 29. 其它

还有这些标签：cpu_shares, cpu_quota, cpuset, domainname, hostname, ipc, mac_address, mem_limit, memswap_limit, privileged, read_only, restart, shm_size, stdin_open, tty, user, working_dir
 上面这些都是一个单值的标签，类似于使用docker run的效果。

```ruby
cpu_shares: 73
cpu_quota: 50000
cpuset: 0,1

user: postgresql
working_dir: /code

domainname: foo.com
hostname: foo
ipc: host
mac_address: 02:42:ac:11:65:43

mem_limit: 1000000000
memswap_limit: 2000000000
privileged: true

restart: always

read_only: true
shm_size: 64M
stdin_open: true
tty: true
```











 
