# pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>VfenvCloud</artifactId>
        <groupId>com.test.vfenv</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>service-test</artifactId>
    <version>1.0</version>

    <properties>
        <!-- harbor地址，docker镜像仓库地址 -->
        <docker.registry.url>192.168.1.101:80</docker.registry.url>
        <docker.registry.project>vfenv</docker.registry.project>
        <!-- 一台安装docker-ce的机器 -->
        <docker.host>http://192.168.1.101:2375</docker.host>
        <!-- 本地maven中配置的server的id -->
        <maven.serverId>vfenv-harbor</maven.serverId>
    </properties>

    <dependencies>
        <!-- my define -->
        <dependency>
            <groupId>com.test.vfenv</groupId>
            <artifactId>vfenvdb-spring-boot-starter</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
        
        <!-- cloud -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-sleuth</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-sleuth-zipkin</artifactId>
        </dependency>
        <!-- fastjson -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.49</version>
        </dependency>
		<!-- lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.16.10</version>
        </dependency>

        <!-- swagger -->
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>2.8.0</version>
        </dependency>
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-bean-validators</artifactId>
            <version>2.8.0</version>
        </dependency>
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
            <version>2.8.0</version>
        </dependency>
        <dependency>
            <groupId>com.github.xiaoymin</groupId>
            <artifactId>swagger-bootstrap-ui</artifactId>
            <version>1.9.1</version>
        </dependency>
		<!-- 配置文件提示 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
        <!-- REDIS -->
        <dependency>
            <groupId>com.test.vfenv</groupId>
            <artifactId>vfenvredis-spring-boot-starter</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
        <!-- tools -->
        <dependency>
            <groupId>com.test.vfenv</groupId>
            <artifactId>vfenv-common</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.test.vfenv</groupId>
            <artifactId>vfenvmongo-spring-boot-starter</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>1.7.25</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
        </dependency>
        <dependency>
            <groupId>commons-io</groupId>
            <artifactId>commons-io</artifactId>
            <version>2.5</version>
        </dependency>
    </dependencies>
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
                    <serverId>${maven.serverId}</serverId>
                    <registryUrl>${docker.registry.url}</registryUrl>
                    <dockerHost>${docker.host}</dockerHost>
                    <imageName>${docker.registry.url}/${docker.registry.project}/${project.artifactId}:${project.version}</imageName>
                    <!--dockerfile folder ***requirement***-->
                    <dockerDirectory>${basedir}/src/main/docker</dockerDirectory>
                    <resources>
                        <resource>
                            <targetPath>/</targetPath>
                            <directory>${project.build.directory}</directory>
                        </resource>
                    </resources>
                </configuration>
            </plugin>
        </plugins>
        <finalName>${project.artifactId}</finalName>
    </build>
</project>
```



# db的pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>VfenvCloud</artifactId>
        <groupId>com.com.test.vfenv</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>
    <artifactId>vfenvdb-spring-boot-starter</artifactId>

    <properties>
        <druid.version>1.1.10</druid.version>
        <mybatis-plus.version>3.2.0</mybatis-plus.version>
        <mysql.version>5.1.45</mysql.version>
        <sharding-sphere.version>4.1.1</sharding-sphere.version>
        <guava.version>25.1-jre</guava.version>
        <pagehelper.version>1.2.13</pagehelper.version>
    </properties>

    <dependencies>
        <!--mysql -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>${mysql.version}</version>
        </dependency>
        <!--guava-->
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
            <version>${guava.version}</version>
        </dependency>

        <!-- druid 官方 starter -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>${druid.version}</version>
        </dependency>
        <!--sharding-->
        <dependency>
            <groupId>org.apache.shardingsphere</groupId>
            <artifactId>sharding-jdbc-spring-boot-starter</artifactId>
            <version>${sharding-sphere.version}</version>
        </dependency>
        <!--
        <dependency>
            <groupId>org.apache.shardingsphere</groupId>
            <artifactId>sharding-jdbc-spring-namespace</artifactId>
            <version>${sharding-sphere.version}</version>
        </dependency>
        -->

        <!-- mybatis-plus -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>

        <!-- mybatis的分页插件 -->
        <dependency>
            <groupId>com.github.pagehelper</groupId>
            <artifactId>pagehelper-spring-boot-starter</artifactId>
            <version>${pagehelper.version}</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- 测试依赖 -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <scope>provided</scope>
        </dependency>
    </dependencies>

</project>
```



# mongo的pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>VfenvCloud</artifactId>
        <groupId>com.test.vfenv</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>vfenvmongo-spring-boot-starter</artifactId>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>provided</scope>
        </dependency>
        <!--mongodb -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>
        <!-- 测试依赖 -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.10</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>

</project>
```

# redis的pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <artifactId>VfenvCloud</artifactId>
        <groupId>com.test.vfenv</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <artifactId>vfenvredis-spring-boot-starter</artifactId>
    <description>redis-starter</description>
    <properties>
        <redssion.version>3.11.6</redssion.version>
    </properties>

    <dependencies>
        <!-- JSON  -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.49</version>
            <scope>provided</scope>
        </dependency>

        <!-- lombok  -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.10</version>
            <scope>provided</scope>
        </dependency>

        <!-- REDIS -->
        <dependency>
            <groupId>org.redisson</groupId>
            <artifactId>redisson</artifactId>
            <version>${redssion.version}</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>


        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-cache</artifactId>
        </dependency>

        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
        </dependency>

        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
        </dependency>

    </dependencies>

</project>
```



# application.yml

```yaml
spring:
  profiles:
    active: dev  #指定环境--spring.profiles.active=dev

management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: always
     
mybatis-plus:
  global-config:
    banner: false
    db-config:
      logic-delete-value: 1
      logic-not-delete-value: 0
  #扫描映射文件的路径
  mapper-locations: classpath*:com/test/**/mapper/*.xml
  #别名
  type-aliases-package: com.test.vfenv


```



# bootstrap.yml

```yaml
spring:
  application:
    name: service-test
  servlet:
    multipart:
      max-file-size: -1	#上传文件数量限制 -1不限制
      max-request-size: -1	#上传文件的请求的大小限制
```



# spring增加sharding-jdbc

```yaml
server:
  port: 8080	#端口
spring:
  zipkin:
    base-url: http://192.168.1.100:9090
    sleuth:
      sampler:
        probability: 1.0
  sleuth:
    sampler:
      probability: 1
  redis:
  	#redis服务器配置
    host: 192.168.1.100
    port: 6379
    password: password
    timeout: 6000
    database: 1
    lettuce:
      pool:
        max-active: -1 # 连接池最大连接数（使用负值表示没有限制）,如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)
        max-idle: 8   # 连接池中的最大空闲连接 ，默认值也是8
        max-wait: -1 # # 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException
        min-idle: 2    # 连接池中的最小空闲连接 ，默认值也是0
      shutdown-timeout: 100ms
  cloud:
    discovery:
      client:
        health-indicator:
          enabled: true
  data:
    mongodb:
      host: 192.168.1.108
      database: my_mongo
      port: 27017
  rabbitmq:
    virtual-host: admin_vhost
    host: 192.168.1.100
    port: 5672
    username: admin
    password: admin_password
    listener:
      simple:
        concurrency: 1  #1个线程
        acknowledge-mode: MANUAL  #手动ack
        prefetch: 1  #从消息队列中获取的数量

  #sharding-jdbc配置开始
  shardingsphere:
    props:
      sql:
        show: false  #显示sql logic sql和actually sql
    datasource:
      names: ds0,ds1
      ds0:
        type: com.alibaba.druid.pool.DruidDataSource  #使用alibaba的druid数据源
        driver: driver-class-name=com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://192.168.1.100:3306/mydb?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false
        username: root
        password: 123456
        initial-size: 5
        min-idle: 5
        maxActive: 20
        maxWait: 60000
        timeBetweenEvictionRunsMillis: 60000
        minEvictableIdleTimeMillis: 300000
        validationQuery: SELECT 1
        testWhileIdle: true
        testOnBorrow: false
        testOnReturn: false
        poolPreparedStatements: true
        maxPoolPreparedStatementPerConnectionSize: 20
        filters: stat,wall,log4j2
        connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000
      ds1:
        type: com.alibaba.druid.pool.DruidDataSource
        driver: driver-class-name=com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://192.168.1.100:3306/mydb?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false
        username: root
        password: 123456
        initial-size: 5
        min-idle: 5
        maxActive: 20
        maxWait: 60000
        timeBetweenEvictionRunsMillis: 60000
        minEvictableIdleTimeMillis: 300000
        validationQuery: SELECT 1
        testWhileIdle: true
        testOnBorrow: false
        testOnReturn: false
        poolPreparedStatements: true
        maxPoolPreparedStatementPerConnectionSize: 20
        filters: stat,wall,log4j2
        connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000
    sharding:
      default-data-source-name: ds0
## mysql主从配置，主表保存数据，从表查询，目前没设置，所以暂时空
    masterslave:
      load-balance-algorithm-type: round_robin
      name: ms
      master-data-source-name: ds0
      slave-data-source-names:
        - ds1
        
feign:
  hystrix:
    enabled: true

hystrix:
  command:
    default:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 900000
eureka:
  instance:
    ip-address: 192.168.1.100
    prefer-ip-address: true  #开启这个hostname失效
    instance-id: ${spring.application.name}:${eureka.instance.ip-address}:${spring.application.instance_id:${server.port}}
    #instance-id: ${spring.application.name}:${docker.ipAddress}:${spring.application.instance_id:${server.port}}
    lease-renewal-interval-in-seconds: 30  #每隔几秒告诉eureka服务器我还存活，用于心跳检测
    lease-expiration-duration-in-seconds: 90 #如果心跳检测一直没有发送，10秒后会从eureka服务器中将此服务剔除
    metadata-map:
      management:
        context-path: /actuator
    health-check-url: http://${eureka.instance.ip-address}:${server.port}/actuator/health
    status-page-url: http://${eureka.instance.ip-address}:${server.port}/swagger-ui.html
    home-page-url: http://${eureka.instance.ip-address}:${server.port}/
  client:
    serviceUrl:
      defaultZone: http://192.168.1.100:8761/eureka #注册中心地址

logging:
  level:
    root: info

```



# 配置说明

```yaml
spring:
  shardingsphere:
    orchestration:
      name: '#治理实例名称'
      overwrite: '#本地配置是否覆盖注册中心配置。如果可覆盖，每次启动都以本地配置为准'
      registry:
        digest: '#连接注册中心的权限令牌。缺省为不需要权限验证'
        max-retries: '#连接失败后的最大重试次数，默认3次'
        namespace: '#注册中心的命名空间'
        operation-timeout-milliseconds: '#操作超时的毫秒数，默认500毫秒'
        props: '#配置中心其它属性'
        retry-interval-milliseconds: '#重试间隔毫秒数，默认500毫秒'
        server-lists: '#连接注册中心服务器的列表。包括IP地址和端口号。多个地址用逗号分隔。如: host1:2181,host2:2181'
        time-to-live-seconds: '#临时节点存活秒数，默认60秒'
        type: '#配置中心类型。如：zookeeper'
    encrypt:
      encryptors:
        <encryptor-name>:
          props:
            <property-name>: '#属性配置, 注意：使用AES加密器，需要配置AES加密器的KEY属性：aes.key.value'
          type: '#加解密器类型，可自定义或选择内置类型：MD5/AES '
      tables:
        <table-name>:
          columns:
            <logic-column-name>:
              assistedQueryColumn: '#辅助查询字段，针对ShardingQueryAssistedEncryptor类型的加解密器进行辅助查询'
              cipherColumn: '#存储密文的字段'
              encryptor: '#加密器名字'
              plainColumn: '#存储明文的字段'
    datasource:
      <data-source-name>:
        driver-class-name: '#数据库驱动类名'
        password: '#数据库密码'
        type: '#数据库连接池类名称'
        url: '#数据库url连接'
        username: '#数据库用户名'
        xxx: '#数据库连接池的其它属性'
      names: '#数据源名称，多数据源以逗号分隔'
    props:
      check:
        table:
          metadata:
            enabled: '#是否在启动时检查分表元数据一致性，默认值: false'
      executor:
        size: '#工作线程数量，默认值: CPU核数'
      sql:
        show: '#是否开启SQL显示，默认值: false'
    sharding:
      binding-tables:
        - '#绑定表规则列表'
        - '#绑定表规则列表'
      binding-tables[x]: '#绑定表规则列表'
      broadcast-tables:
        - '#广播表规则列表'
        - '#广播表规则列表'
      broadcast-tables[x]: '#广播表规则列表'
      default-data-source-name: '#未配置分片规则的表将通过默认数据源定位'
      default-database-strategy:
        xxx: '#默认数据库分片策略，同分库策略'
      default-key-generator:
        props:
          <property-name>: '#自增列值生成器属性配置, 比如SNOWFLAKE算法的worker.id与max.tolerate.time.difference.milliseconds'
        type: '#默认自增列值生成器类型，缺省将使用org.apache.shardingsphere.core.keygen.generator.impl.SnowflakeKeyGenerator。可使用用户自定义的列值生成器或选择内置类型：SNOWFLAKE/UUID/LEAF_SEGMENT'
      default-table-strategy:
        xxx: '#默认表分片策略，同分表策略'
      master-slave-rules:
        <master-slave-data-source-name>:
          load-balance-algorithm-class-name: '#从库负载均衡算法类名称。该类需实现MasterSlaveLoadBalanceAlgorithm接口且提供无参数构造器'
          load-balance-algorithm-type: '#从库负载均衡算法类型，可选值：ROUND_ROBIN，RANDOM。若`load-balance-algorithm-class-name`存在则忽略该配置'
          master-data-source-name: '#主库数据源名称'
          slave-data-source-names:
            - '#从库数据源名称列表'
            - '#从库数据源名称列表'
          slave-data-source-names[x]: '#从库数据源名称列表'
      tables:
        <logic-table-name>:
          actual-data-nodes: '#由数据源名 + 表名组成，以小数点分隔。多个表以逗号分隔，支持inline表达式。缺省表示使用已知数据源与逻辑表名称生成数据节点。用于广播表（即每个库中都需要一个同样的表用于关联查询，多为字典表）或只分库不分表且所有库的表结构完全一致的情况'
          database-strategy:
            complex:
              algorithm-class-name: '#复合分片算法类名称。该类需实现ComplexKeysShardingAlgorithm接口并提供无参数的构造器'
              sharding-columns: '#分片列名称，多个列以逗号分隔'
            hint:
              algorithm-class-name: '#Hint分片算法类名称。该类需实现HintShardingAlgorithm接口并提供无参数的构造器'
            inline:
              algorithm-expression: '#分片算法行表达式，需符合groovy语法'
              sharding-column: '#分片列名称'
            standard:
              precise-algorithm-class-name: '#精确分片算法类名称，用于=和IN。该类需实现PreciseShardingAlgorithm接口并提供无参数的构造器'
              range-algorithm-class-name: '#范围分片算法类名称，用于BETWEEN，可选。该类需实现RangeShardingAlgorithm接口并提供无参数的构造器'
              sharding-column: '#分片列名称'
          key-generator:
            column: '#自增列名称，缺省表示不使用自增主键生成器'
            props:
              <property-name>: '#属性配置, 注意：使用SNOWFLAKE算法，需要配置worker.id与max.tolerate.time.difference.milliseconds属性'
            type: '#自增列值生成器类型，缺省表示使用默认自增列值生成器。可使用用户自定义的列值生成器或选择内置类型：SNOWFLAKE/UUID/LEAF_SEGMENT'
          table-strategy:
            xxx: '#省略'
```

