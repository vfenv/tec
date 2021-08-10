### 1、Nginx常用命令

```shell
#启动命令
nginx -c conf/nginx.conf
#测试配置文件是否正确
nginx -t
#重启
nginx -s reload

#查看CPU的内核情况，用于配置worker数量
cat /proc/cpuinfo | grep name | cut -f2 -d: | uniq -c
```

### 2、Nginx配置文件结构

1、全局块：配置影响nginx全局的指令。一般有运行nginx服务器的用户组，nginx进程pid存放路径，日志存放路径，配置文件引入，允许生成worker_process数等。

2、events块：配置影响nginx服务器与用户的网络连接处理。具体有每个进程的最大连接数，选取哪种事件驱动模型处理连接请求，是否允许同时接受多个网路连接，开启多个网络连接序列化等。

3、http块：主要是设定和一次HTTP请求处理有关的配置，可以嵌套多个server，配置代理，缓存，日志定义等绝大多数功能和第三方模块的配置。如文件引入，mime-type定义，日志自定义，是否使用sendfile传输文件，连接超时时间，单连接请求数等。

4、server块：配置虚拟主机的相关参数，一个http中可以有多个server。

5、location块：配置请求的路由，以及各种页面的处理情况。

```nginx
...              #全局块
events {         #events块，工作模式及并发量设置
   ...
}

http      #http块，主要是设定和一次HTTP请求处理有关的配置
{
    ...   #http全局块
    server        #server块，设定虚拟主机配置
    { 
        ...       #server全局块
        location [PATTERN]   #location块
        {
            ...
        }
        location [PATTERN] 
        {
            ...
        }
    }
    server
    {
      ...
    }
    ...     #http全局块
}

```



### 3、Nginx配置文件详解

```nginx
#user  nobody; #运行用户，可以不进行设置
worker_processes  2;  #启动进程数量,通常设置成和cpu的数量相等

#全局错误日志，这个设置可以放入全局块，http块，server块，级别依次为：debug|info|notice|warn|error|crit|alert|emerg
error_log  logs/error.log;
error_log  logs/error.log  notice;
error_log  logs/error.log  info;

#指定nginx进程运行文件存放位置
pid        logs/nginx.pid;

#工作模式及并发量设置
events {
    #epoll是多路复用IO(I/O Multiplexing)中的一种方式,仅用于linux2.6以上内核,可以大大提高nginx的性能
    use epoll; 
    #设置网路连接序列化，防止惊群现象发生，默认为on
    accept_mutex on;  
    #设置一个进程是否同时接受多个网络连接，默认为off
    multi_accept on;  
    #单个后台worker process进程的最大并发连接数。并发总数为worker_processes 和 worker_connections 的乘积，
    #设置了反向代理的情况下，并发总数会有所变化。
    worker_connections  1024;
}

#http请求处理块。主要是设定和一次HTTP请求处理有关的配置
http {
    #文件扩展名与文件类型映射表，设定mime类型,类型由mime.type文件定义
    include       mime.types;
    #默认文件类型，默认为text/plain
    default_type  application/octet-stream;

    #设定日志格式
    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';
    access_log  logs/access.log  main;
    
    #sendfile 指令指定 nginx 是否调用 sendfile 函数（zero copy 方式）来输出文件，
    #对于普通应用，必须设为 on,
    #如果用来进行下载等应用磁盘IO重负载应用，可设置为 off，
    #以平衡磁盘与网络I/O处理速度，降低系统的uptime.
    sendfile        on;
    #tcp_nopush     on;

    #连接超时时间，默认为75s，可以在http，server，location块。
    keepalive_timeout  65;
    #开启gzip压缩
    gzip  on;

   	#设定虚拟主机配置，一个HTTP模块可以设置多个虚拟主机
    server {
        #监听的端口
        listen       80;
		#监听地址
        server_name  localhost;

        #charset koi8-r;
        
		#设定本虚拟主机的访问日志
        access_log  logs/host.access.log  main;
		#默认请求    通用匹配/那个
        location / {
	    #网站根目录位置
            root   /home/html;
	    	#定义首页索引文件的名称
            index  index.html index.htm;
        }
        # 定义错误提示页面
        error_page  404              /404.html;
        # redirect server error pages to the static page /50x.html
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
        
        #禁止访问 .htxxx 文件    正则匹配
		location ~ /\.ht {
            deny  all;
        }
    }

}
```

### 4、Https配置

```nginx
# HTTPS 配置，主要是设定和一次HTTPS请求处理有关的配置主要多了个SSL，其他的和HTTP差不多
# HTTPS server
server {
    listen       443 ssl;
    server_name  localhost;

    ssl_certificate      cert.pem;
    ssl_certificate_key  cert.key;

    ssl_session_cache    shared:SSL:1m;
    ssl_session_timeout  5m;

    ssl_ciphers  HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers  on;

    location / {
        root   html;
        index  index.html index.htm;
    }
}
```



### 5、Nginx的6种负载均衡策略

目前Nginx服务器的upstream模块支持6种方式的分配，其中4种是nginx自带的，如果是默认不用写负载策略，另外二种是三方提供的。

| 默认方式        | 轮询               |
| --------------- | ------------------ |
| 权重方式        | weight             |
| 依据ip分配方式  | ip_hash            |
| 最少连接方式    | least_conn         |
| 响应时间方式    | fair（第三方）     |
| 依据URL分配方式 | url_hash（第三方） |

#### 1.轮询

最基本的配置方法，它是upstream的默认策略,在upstream中什么都不用写，每个请求会按时间顺序逐一分配到不同的后端服务器。

有如下参数：

| fail_timeout | 与max_fails结合使用。                                        |
| ------------ | ------------------------------------------------------------ |
| max_fails    | 设置在fail_timeout参数设置的时间内最大失败次数，如果在这个时间内，所有针对该服务器的请求都失败了，那么认为该服务器会被认为是停机了。 |
| fail_time    | 服务器会被认为停机的时间长度,默认为10s。                     |
| backup       | 标记该服务器为备用服务器。当主服务器停止时，请求会被发送到它这里。 |
| down         | 标记服务器永久停机了。                                       |

在轮询中，如果服务器down掉了，会自动剔除该服务器。

缺省配置就是轮询策略。

此策略适合服务器配置相当，无状态且短平快的服务使用。

#### 2.权重

在轮询策略的基础上制定沦陷的几率。例如

```nginx
upstream foo {
    #在该例子中，weight参数用于指定轮询几率，weight的默认值为1,；weight的数值与访问比率成正比，比如weight=2被访问的几率为其他服务器的两倍。
    server localhost:8001 weight=2;
    server localhost:8002;
    server localhost:8003 backup;
    server localhost:8004 max_fails=3 fail_timeout=20s;
}
```

权重越高分配到需要处理的请求越多。

此策略可以与least_conn和ip_hash结合使用。

此策略比较适合服务器的硬件配置差别比较大的情况。

#### 3.ip_hash

负载均衡器按照客户端IP地址的分配方式，可以确保相同客户端的请求一直发送到相同的服务器。这样每个访客都固定访问一个后端服务器。

```nginx
upstream foo {
	ip_hash;
    server localhost:8001 weight=2;
    server localhost:8002;
    server localhost:8003;
    server localhost:8004 max_fails=3 fail_timeout=20s;
}
```

注意：

在nginx版本1.3.1之前，不能在ip_hash中使用权重（weight）。

ip_hash不能与backup同时使用。

此策略适合有状态服务，比如session。

当有服务器需要剔除，必须手动down掉。



#### 4.least_conn 最小连接

把请求转发给连接数较少的后端服务器。轮询算法是把请求平均的转发给各个后端，使它们的负载大致相同；
但是，有些请求占用的时间很长，会导致其所在的后端负载较高。这种情况下，least_conn这种方式就可以达到更好的负载均衡效果

```nginx
upstream foo {
	least_conn;
    server localhost:8001 weight=2;
    server localhost:8002;
    server localhost:8003 backup;
    server localhost:8004 max_fails=3 fail_timeout=20s;
}
```

注意：

此负载均衡策略适合请求处理时间长短不一造成服务器过载的情况。

#### 5.三方策略:fair-响应时间短的优先分配

按照服务器端的响应时间来分配请求，响应时间短的优先分配。

```nginx
#动态服务器组
upstream dynamic_zuoyu {
    server localhost:8080;  #tomcat 7.0
    server localhost:8081;  #tomcat 8.0
    server localhost:8082;  #tomcat 8.5
    server localhost:8083;  #tomcat 9.0
    fair;    #实现响应时间短的优先分配
}
```

#### 6.三方策略:url_hash

按访问url的hash结果来分配请求，使每个url定向到同一个后端服务器，要配合缓存命中来使用。同一个资源多次请求，可能会到达不同的服务器上，导致不必要的多次下载，缓存命中率不高，以及一些资源时间的浪费。而使用url_hash，可以使得同一个url（也就是同一个资源请求）会到达同一台服务器，一旦缓存住了资源，再此收到请求，就可以从缓存中读取。

```nginx
#动态服务器组
upstream dynamic_zuoyu {
    hash $request_uri;    #实现每个url定向到同一个后端服务器
    server localhost:8080;  #tomcat 7.0
    server localhost:8081;  #tomcat 8.0
    server localhost:8082;  #tomcat 8.5
    server localhost:8083;  #tomcat 9.0
}
```



### 6、Nginx多配置文件

```nginx
#导入外部服务器配置文件存放地址
include /usr/local/nginx/conf/vhosts/*.conf;

```

### 7、Nginx中location详解

#### 1.配置文件

```nginx
http {
    #集群的服务器
    #在http下添加 upstream upstream_name {} 来配置要映射的服务器。
    #其中的upstream_name大家可以指定为服务的域名或者项目的代号。
    upstream local_tomcat{
        server localhost:8080;
        server localhost:8081;
    }
    server {
        listen 80;
        server_name www.tomcat1.com;
        location ~ /upload/.*$ {
            root /www/resources/;
            expires 30d;
        }
        location / {		
            #指定集群的服务器
            #server下的location 我们将 / 下的全部请求转发到 http://upstream_name ，也就是我们上面配置的服务器列表中的某一台服务器上。
        	#具体是哪台服务器，nginx会根据配置的调度算法来确认。
            proxy_pass http://local_tomcat;

            #配置集群服务器故障转移
            #nginx与上游服务器(真实访问的服务器)超时时间 后端服务器连接的超时时间_发起握手等候响应超时时间
            proxy_connect_timeout 1s;
            #nginx发送给上游服务器(真实访问的服务器)超时时间
            proxy_send_timeout 1s;
            #nginx接受上游服务器(真实访问的服务器)超时时间
            proxy_read_timeout 1s;

            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header REMOTE-HOST $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }
    }
}
```

#### 2.location匹配规则

```nginx
location [=|~|~*|^~] /uri/ { … }
```

| 符号 | 含义                                                         |
| :--- | ------------------------------------------------------------ |
| `=`  | 开头表示精确匹配                                             |
| `^~` | 开头表示 uri 以某个常规字符串开头，理解为匹配 `url` 路径即可。<br />`nginx` 不对 `url` 做编码，因此请求为`/static/20%/aa`，可以被规则`^~ /static/ /aa`匹配到（注意是空格） |
| `~`  | 开头表示区分大小写的正则匹配                                 |
| `~`* | 开头表示不区分大小写的正则匹配                               |
| `/`  | 通用匹配，任何请求都会匹配到                                 |

多个 location 配置的情况下匹配顺序为

```nginx
#首先匹配 =    完全匹配的
#其次匹配 ^~    某个常规字符串开头
#其次是按文件中顺序的正则匹配    ~和~*开头的
#最后是交给 / 通用匹配    最后是通用匹配
#当有匹配成功时候，停止匹配，按当前匹配规则处理请求
location = / {
   #规则A
}
location = /login {
   #规则B
}
location ^~ /static/ {
   #规则C
}
location ~ \.(gif|jpg|png|js|css)$ {
   #规则D
}
location ~* \.png$ {
   #规则E
}
location / {
   #规则F
}
```

#### 3.location转发

常用的转发规则类型有多种，代理转发，根路径映射 、别名替换、重定向：proxy_pass、root、alias、rewrite

```nginx
#反向代理配置，用于代理请求，是否追加location取决于是否带URI
location /i/ {
	proxy_pass http://user.example.com;
}

#根路径配置，用于访问文件系统，在匹配到路径后，指向root配置的路径，并把我location追加到后面。
#path可以包含variable，除了$document_root 和$realpath_root
location /i/ {
    root /data/w3;		#/i/top.gif请求会返回`/data/w3/i/top.gif
}

#alias必须以/结束，否则会找不到文件，root可有可无。
#别名配置，用于访问文件系统，在匹配location后，指向alias配置的路径；如果想改变请求路径，则使用alias
location /i/ {
    alias /data/w3/images/;		#/i/top.gif请求，返回/data/w3/images/top.gif
}

#如果alias在regular expression的location中使用，那么regular expression应该包含capture，alias指向这些captures，比如
location ~ ^/users/(.+\.(?:gif|jpe?g|png))$ {
    alias /data/w3/images/$1;
}

当location匹配指令的最后一部分值
location /images/ {
    alias /data/w3/images/;
}
那么最好使用root指令替换
location /images/ {
    root /data/w3;
}


#rewrite 作用是地址重定向，语法：rewrite regex replacement[flag];
location /baidu/ {
    rewrite ^/(.*) http://www.baidu.com/ permanent;    #请求地址是 http://192.168.25.131:9003/baidu/开头的，都会跳转到百度
}
#rewrite 最后一项flag参数：
#last	本条规则匹配完成后继续向下匹配新的location URI规则
#break	本条规则匹配完成后终止，不在匹配任何规则
#redirect	返回302临时重定向
#permanent	返回301永久重定向


#将 http://192.168.25.131:9003/api/ 开头的地址，重定向到http://192.168.25.131:9003/****。也就是说，将中间的 /api 去掉
location /api/ {
    rewrite /api/(.*)  /$1 break;    #( ) --用于匹配括号之间的内容，通过$1、$2调用
    proxy_pass http://192.168.25.131:9003;
}
```



#### 4.proxy_pass的URI处理

Nginx的[官网](http://nginx.org/en/docs/http/ngx_http_proxy_module.html#proxy_pass)将proxy_pass分为两种类型：带URI和不带URI的

**带URI的：**

proxy_pass http://localhost:8080/

proxy_pass http://localhost:8080/abc

proxy_pass http://localhost:8080/abc/

**不带URI的：**

proxy_pass http://localhost:8080

- 不带URI的最终是要把location加上的

- 带URI的要把location替换掉。

```nginx
server {
    listen       80;
    server_name  localhost;
   	location /api1/ {
        proxy_pass http://localhost:8080;
    }
   	# http://localhost/api1/xxx -> http://localhost:8080/api1/xxx

   	location /api2/ {
        proxy_pass http://localhost:8080/;
    }
	# http://localhost/api2/xxx -> http://localhost:8080/xxx

   	location /api3 {
        proxy_pass http://localhost:8080;
    }
   	# http://localhost/api3/xxx -> http://localhost:8080/api3/xxx

   	location /api4 {
        proxy_pass http://localhost:8080/;
    }
   	# http://localhost/api4/xxx -> http://localhost:8080//xxx，请注意这里的双斜线。

   	location /api5/ {
        proxy_pass http://localhost:8080/haha;
    }
   	# http://localhost/api5/xxx -> http://localhost:8080/hahaxxx，请注意这里的haha和xxx之间没有斜杠。

   	location /api6/ {
        proxy_pass http://localhost:8080/haha/;
    }
   	# http://localhost/api6/xxx -> http://localhost:8080/haha/xxx

	location /api7 {
        proxy_pass http://localhost:8080/haha;
    }
   	# http://localhost/api7/xxx -> http://localhost:8080/haha/xxx

   	location /api8 {
        proxy_pass http://localhost:8080/haha/;
    }
	# http://localhost/api8/xxx -> http://localhost:8080/haha//xxx，请注意这里的双斜杠。
}
```



#### 4.location线上配置

实际使用中，至少有三个匹配规则定义，如下：

```nginx
# 直接匹配网站根，通过域名访问网站首页比较频繁，使用这个会加速处理，官网如是说。
# 这里是直接转发给后端应用服务器了，也可以是一个静态首页
# 第一个必选规则
location = / {    #精准匹配
    proxy_pass http://tomcat:8080/index
}


# 第二个必选规则是处理静态文件请求，这是 nginx 作为 http 服务器的强项
# 有两种配置模式，目录匹配或后缀匹配，任选其一或搭配使用
location ^~ /static/ {		#字符串匹配
    root /webroot/static/;
}
location ~* \.(gif|jpg|jpeg|png|css|js|ico)$ {		#正则匹配 ~ 或者 ~*开头
    root /webroot/res/;
}


# 第三个规则就是通用规则，用来转发动态请求到后端应用服务器
# 非静态文件请求就默认是动态请求，自己根据实际把握
# 毕竟目前的一些框架的流行，带.php、.jsp后缀的情况很少了
location / {			#通用匹配 任何请求都会匹配，但是优先级最低 其他匹配就不走这了
    proxy_pass http://tomcat:8080/
}


#rewrite 作用是地址重定向，语法：rewrite regex replacement[flag];
location /baidu/ {
    rewrite ^/(.*) http://www.baidu.com/ permanent;    #请求地址是 http://192.168.25.131:9003/baidu/开头的，都会跳转到百度
}
#rewrite 最后一项flag参数：
#last	本条规则匹配完成后继续向下匹配新的location URI规则
#break	本条规则匹配完成后终止，不在匹配任何规则
#redirect	返回302临时重定向
#permanent	返回301永久重定向


#将 http://192.168.25.131:9003/api/ 开头的地址，重定向到http://192.168.25.131:9003/****。也就是说，将中间的 /api 去掉
location /api/ {
    rewrite /api/(.*)  /$1 break;    #( ) --用于匹配括号之间的内容，通过$1、$2调用
    proxy_pass http://192.168.25.131:9003;
}
```



#### 5.location @name的用法

@用来定义一个命名location。主要用于内部重定向，不能用来处理正常的请求。其用法如下：

```nginx
location / {
    try_files $uri $uri/ @custom
}
location @custom {
    # ...do something
}
```

上例中，当尝试访问url找不到对应的文件就重定向到我们自定义的命名location（此处为custom）。

**值得注意的是，命名location中不能再嵌套其它的命名location**。







### 8、请求处理时间过长可以设置请求处理超时时间

在nginx 的配置文件 在 http,server,location 三个位置任意一个位置
加上

```nginx
proxy_read_timeout 240s;  #default: proxy_read_timeout 60
```












​	
