# StatefulSet和Headless Service简介

## 1.StatefulSet

StatefulSet是为了解决有状态服务问题，相对应的Deployment和ReplicaSet是无状态服务

其应用场景包括：

稳定的持久化存储：pod即使重新调度后还能访问相同的持久化数据，基于PVC来实现稳定的网络标志，即pod重新调度后PodName和HostName不变

基于Headless Service 也就是没有 Cluster IP的Service来实现。

有序部署，有序扩展，即Pod是有序的，部署或者扩展时要依据定义的顺序依次进行，从0到N-1，在下一个Pod运行之前所有之前的Pod必须都是Running和Ready状态，基于init containers来实现。

有序收缩，有序删除，从N-1到0

StatefulSet的Pod的DNS格式：

```shell
statefulSetName-{0..N-1}.serviceName.namespace.svc.cluster.local
#serviceName为Headless Service的名字
#0..N-1为Pod所在的序号，从0开始到N-1
#statefulSetName为StatefulSet的名字
#namespace为服务所在的namespace，Headless Service和StatefulSet必须在相同的namespace
#cluster.local为Cluster Domain
```

## 2.Headless Service

顾名思义，就是无头服务。

正常的service服务是为一组具有功能的容器提供一个统一的入口地址，实际访问到哪个pod由service实现。

但如果客户端想要和指定的Pod直接通信，不是随机选择pod进行通信，可以使用headlessService。

另外如果开发人员希望自己控制负载策略（ribbon），不使用service提供的默认负载的功能，或者应用程序希望知道属于同能服务的其它实例，可以使用headlessService。

Headless Service和普通的service的明显区别：**每个Pod都会有对应的DNS的域名**

例如使用下面的域名来访问具体的pod：

statefulSetName-0.serviceName.namespace.svc.cluster.local

在实际使用中，service的clusterIP设置成**None**就表明service是一个Headless Service。



## 3.StatefulSet和Headless Service结合

通过 StatefulSet，我们得到了一系列pod，每个pod的podName为statefulSetName-{0..N-1}，再通过Headless Service，我们可以通过pod名称来访问某个pod。

​	1.创建了一个名称叫eureka的StatefulSet，并且设置replicas =3，那么部署到k8s后，k8s会为我们生成三个名称依次为eureka-0，eureka-1，eureka-2的pod。

​	2.在namespace=mynamespace的命名空间下创建了一个名称为reg-service的service，并且关联了之前StatefulSet创建的pod，那么我们可以在集群内任意地方通过域名访问pod：

​		eureka-0.reg-service.mynamespace.svc.cluster.local

​		eureka-1.reg-service.mynamespace.svc.cluster.local

​		eureka-2.reg-service.mynamespace.svc.cluster.local



## 4.注册中心改造

首先明确部署eureka的关键点：需要让每个eureka注册到另外的eureka上。

也就是eureka.client.serviceUrl.defaultZone这个配置，是一组eureka的地址。

通过StatefulSet，我们可以定义eureka的名称，通过Headless Service绑定statefulSet创建的pod，使得我们又可以访问到每个eureka

```yaml
#通过pod的DNS->statefulSetName-{0..N-1}.serviceName.namespace.svc.cluster.local可以直接访问pod
#由于这三个pod在同一个命名空间内，可以省略后缀 .svc.cluster.local 
eureka.client.serviceUrl.defaultZone: http://regist-server-0.regist-server.test:8761/eureka/,http://regist-server-1.regist-server.test:8761/eureka/,http://regist-server-2.regist-server.test:8761/eureka/

#因为使用的域名，要关闭IP注册方式，直接使用默认的域名注册
eureka.instance.prefer-ip-address: false
```



```yaml
#StatefulSet.yaml中，增加环境变量配置，将pod的名称绑定到环境变量
         env:
          - name: MY_POD_NAME      
            valueFrom:
              fieldRef:
                fieldPath: metadata.name
```

```yaml
#在application.yaml读取 创建pod时使用的metadata.name 作为 hostname
eureka.instance.hostname: ${MY_POD_NAME}.regist-server.test
```

**完整配置**

**service.yaml**

```yaml
apiVersion: v1
kind: Service  #定义一个headless service
metadata:
  name: regist-server
  namespace: test
  labels:
    service: register-server  #service定义一个标签
spec:
  clusterIP: None  ##定义一个headless service，类型为ClusterIP，ClusterIP为none就是个headless service
  type: ClusterIP
  ports:
    - port: 8000  #集群内的其他容器组可通过 8000 端口访问 Service
      targetPort: http  #pod的端口 或者 name
      protocol: TCP  #协议类型TCP/UDP
      name: http  #端口的名字
    - port: 5018
      targetPort: http
      protocol: TCP
      name: debugg
  selector:
    service: register-server  #选择器 选择pod时指定
```

**StatefulSet.yaml**

```yaml
apiVersion: apps/v1
kind: StatefulSet  #定义一个有状态的数集合，存放pod
metadata:
  name: register-server  #有状态数据集名
  namespace: test
  labels:
    service: register-server  #给当前的有状态数据集打标签
spec:  #状态集的详细设置
  replicas: 3  #副本集的数量
  serviceName: register-server  #指定service的名称
  selector:
    matchLabels:
      service: register-server  #匹配pod的标签
  template:  #指定pod模板
    metadata:
      labels:
        service: register-server  #pod的标签
      annotations:  #自定义注解
        service: register-server
    spec:  #pod的定义
      containers:  #容器
        - name: register-server  #容器名称
          image: codewjy/eureka:0.1.0  #镜像
          imagePullPolicy: Always  #拉取策略
          env:  #设置一个环境变量 名字是MY_POD_NAME 值是 metadata.name
            - name: MY_POD_NAME
              valueFrom:
                fieldRef:
                  fieldPath: metadata.name
            - name: SPRING_PROFILES_ACTIVE
              value: prod
          ports:  #端口
            - name: http  #端口名称
              containerPort: 8000  #开放端口
              protocol: TCP  #协议
          readinessProbe:  #健康监控探针
            httpGet:
              path: /actuator/health  #请求地址
              port: 8000  #暴露端口
              scheme: HTTP  #协议
            failureThreshold: 3  #失败阀值，重试次数
            initialDelaySeconds: 60  #延迟探测事件，启动60秒后开始监测健康情况
            periodSeconds: 10  #探针执行频率，10秒探测一次
            successThreshold: 1  #探测失败后，最小连续成功认为成功，默认是1
            timeoutSeconds: 10  #每次探测超时时间
          resources:  #资源配置
            limits:
              memory: 16Gi  #限制的内存大小16g
            requests:
              memory: 8Gi   #请求资源8g，希望被分配的资源大小
          volumeMounts:  #使用存储卷
          - mountPath: /logs
            name: data
      volumes:  #存储卷  #https://blog.csdn.net/u014042372/article/details/80582287
      - name: data  #定义了一个叫data的存储卷，使用的时候通过volumeMounts的name关联
        emptyDir: {} #不写默认就是这个
  podManagementPolicy: "Parallel"  #默认OrderedReady按照顺序管理启停pod，parallel为并发，pod的创建就不必等待，同时创建，同时删除
```

**application-prod.yml**

```yaml
server:
  port: 8000
eureka:
  datacenter: cloud
  environment: prod
  server:
    enable-self-preservation: false  #是否开启自我保护，客户端心跳检测15分钟内错误达到80%服务会保护，导致别人还认为是好用的服务 在K8S中需要关闭 不兼容驱逐重排
    wait-time-in-ms-when-sync-empty: 0   #在eureka服务器获取不到集群里对等服务器上的实例时，需要等待的时间，单机默认0
    shouldUseReadOnlyResponseCache: true #eureka是CAP理论种基于AP策略，为了保证强一致性关闭此切换CP 默认不关闭 false关闭
    eviction-interval-timer-in-ms: 5000 #驱逐间隔，即扫描失效服务的间隔时间 默认60s
    response-cache-update-interval-ms: 5000 #eureka server刷新readCacheMap的时间，注意，client读取的是readCacheMap，这个时间决定了多久会把readWriteCacheMap的缓存更新到readCacheMap上默认30s
    response-cache-auto-expiration-in-seconds: 5 #注册表信息改变时，缓存中注册表不失效时间，默认180秒
    renewal-percent-threshold: 0.85  #  指定每分钟需要收到的续约次数的阈值，默认值就是：0.85
  client:
    registerWithEureka: true  #是否把注册中心自己当客户端注册
    fetchRegistry: true  #是否拉取注册中心信息
    serviceUrl:
      #注册中心地址
      defaultZone: http://regist-server-0.regist-server.springcloud:8761/eureka,http://regist-server-1.regist-server.springcloud:8761/eureka,http://regist-server-2.regist-server.springcloud:8761/eureka
  instance:
    appname: ${spring.application.name}
    prefer-ip-address: false  #开启这个hostname失效
    hostname: ${MY_POD_NAME}.regist-server.springcloud
    instance-id: ${spring.application.name}:${MY_POD_NAME}.regist-server.springcloud:${MY_POD_NAME}.regist-server.springcloud:8761
    lease-renewal-interval-in-seconds: 10  #每隔几秒告诉eureka服务器我还存活，用于心跳检测
    lease-expiration-duration-in-seconds: 15 #如果心跳检测一直没有发送，10秒后会从eureka服务器中将此服务剔除
    metadata-map:
      management:
        context-path: /actuator
    health-check-url: http://${MY_POD_NAME}.regist-server.springcloud:8761/actuator/health
    status-page-url: http://${MY_POD_NAME}.regist-server.springcloud:8761/actuator/info
    home-page-url: http://${MY_POD_NAME}.regist-server.springcloud:8761/

logback:
  filename: register-server-prod
logging:
  config: classpath:logback.xml
```

**bootstrap.yml**

```yaml
spring:
  application:
    name: register-server
```

**logback.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration scan="true" scanPeriod="10 seconds">
    <springProperty scope="context" name="LOGBACK_FILE_NAME" source="logback.filename"/>
    <property name="LOG_HOME" value="/automl_log"/>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <withJansi>true</withJansi>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%thread] %highlight([%-5level] %logger{50} - %msg%n)</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    <appender name="ALL_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_HOME}/${LOGBACK_FILE_NAME}.log</file>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_HOME}/${LOGBACK_FILE_NAME}-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxHistory>30</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>10MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>
    </appender>
    <root level="info">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="ALL_FILE"/>
    </root>
</configuration>
```

**服务注册：**

将一般的微服务注册到eureka集群中，可以通过eureka的service来访问eureka，即：将eureka.client.serviceUrl.defaultZone设置成register-server.test.svc.cluster.local，使用了k8s的service负载均衡，将服务注册到任意一个活着的eureka上，然后eureka集群内部会做同步，最终注册到eureka集群内部所有eureka上
