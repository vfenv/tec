# 一、logback介绍和配置详解

logback是Java的开源框架，性能比log4j要好。是springboot自带的日志框架。该框架主要有3个模块：

logback-core：核心代码块（不介绍）

log back-classic：实现了slf4j的api，加入该依赖可以实现log4j的api。

log back-access：访问模块与servlet容器集成提供通过http来访问日志的功能（也就是说不需要访问服务器，直接在网页上就可以访问日志文件）。

因为springboot已经引入了logback的依赖，所以不需要额外引入logback

## logback.xml格式详解

### 1.configuration

根节点<configuration>，包含下面三个属性：

​	1.scan: 当此属性设置为true时，配置文件如果发生改变，将会被重新加载，默认值为true。
　　　　

​	2.scanPeriod: 设置监测配置文件是否有修改的时间间隔，如果没有给出时间单位，默认单位是毫秒。当scan为true时，此属性生效。默认的时间间隔为1分钟。

​	3.debug: 当此属性设置为true时，将打印出logback内部日志信息，实时查看logback运行状态。默认值为false。

### 2.子节点contextName

用来设置上下文名称

### 3.子节点property

用来定义变量值，它有两个属性name和value，通过<property>定义的值会被插入到logger上下文中，可以使“${}”来使用变量。

name: 变量的名称
value: 的值时变量定义的值

### 4.子节点timestamp

获取时间戳字符串，他有两个属性key和datePattern

key: 标识此timestamp 的名字；

datePattern: 设置将当前时间（解析配置文件的时间）转换为字符串的模式，遵循java.txt.SimpleDateFormat的格式。

### 5.子节点appender

负责写日志的组件，它有两个必要属性name和class。name指定appender名称，class指定appender的全限定名

**appender class** 类主要有三种：**ConsoleAppender、FileAppender、RollingFileAppender**

#### 5.1.ConsoleAppender

把日志输出到控制台，有以下子节点：

  <encoder>：对日志进行格式化。

  <target>：字符串System.out(默认)或者System.err（区别不多说了）

#### 5.2.FileAppender

把日志添加到文件，有以下子节点：

  <file>：被写入的文件名，可以是相对目录，也可以是绝对目录，如果上级目录不存在会自动创建，没有默认值。

  <append>：如果是 true，日志被追加到文件结尾，如果是 false，清空现存文件，默认是true。

  <encoder>：对记录事件进行格式化。（具体参数稍后讲解 ）

  <prudent>：如果是 true，日志会被安全的写入文件，即使其他的FileAppender也在向此文件做写入操作，效率低，默认是 false。

#### 5.3.RollingFileAppender

滚动记录文件，先将日志记录到指定文件，当符合某个条件时，将日志记录到其他文件。有以下子节点：

  <file>：被写入的文件名，可以是相对目录，也可以是绝对目录，如果上级目录不存在会自动创建，没有默认值。

  <append>：如果是 true，日志被追加到文件结尾，如果是 false，清空现存文件，默认是true。

  <rollingPolicy>:当发生滚动时，决定RollingFileAppender的行为，涉及文件移动和重命名。属性class定义具体的滚动策略类class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy，是最受欢迎的滚动政策，例如按天或按月。TimeBasedRollingPolicy承担翻滚责任以及触发所述翻转的责任。TimeBasedRollingPolicy支持自动文件压缩。



有时您可能希望按日期归档文件，但同时限制每个日志文件的大小，特别是如果后处理工具对日志文件施加大小限制。为了满足这一要求，logback随附SizeAndTimeBasedRollingPolicy。

请注意除“％d”之外的“％i”转换标记。**％i和％d令牌都是强制性的。**每当当前日志文件在当前时间段结束之前达到maxFileSize时，它将以增加的索引存档，从0开始。

# 二、logback样例

在resources下建立logback.xml文件，内容如下

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    <include resource="org/springframework/boot/logging/logback/console-appender.xml"/>

    <property name="LOG_HOME" value="./log"/>
    <property name="LOG_PREFIX" value="web-xxx"/>
    <!--
		各种占位符代表的含义

    %p:输出优先级，即DEBUG,INFO,WARN,ERROR,FATAL
    %r:输出自应用启动到输出该日志讯息所耗费的毫秒数
    %t:输出产生该日志事件的线程名
    %f:输出日志讯息所属的类别的类别名
    %c:输出日志讯息所属的类的全名
    %d:输出日志时间点的日期或时间，指定格式的方式： %d{yyyy-MM-dd HH:mm:ss}
    %l:输出日志事件的发生位置，即输出日志讯息的语句在他所在类别的第几行。
    %m:输出代码中指定的讯息，如log(message)中的message
    %n:输出一个换行符号
    -->
    
    <!--
    	Appender: 设置日志信息的去向,常用的有以下几个

    ch.qos.logback.core.ConsoleAppender (控制台)
    ch.qos.logback.core.rolling.RollingFileAppender (文件大小到达指定尺寸的时候产生一个新文件)
    ch.qos.logback.core.FileAppender (文件，不推荐使用)
    -->
    <!-- 控制台输出 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <!-- 包含1个 withJansi， 是否对不同级别的日志用颜色来区分 -->
        <withJansi>true</withJansi>
        <!-- 包含1个target，System.out 或者是System.err，默认为System.out -->
        <!--<target>System.out</target>-->
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <!--格式化输出：%d表示日期，%thread表示线程名，%-5level：级别从左显示5个字符宽度%msg：日志消息，%n是换行符-->
            <pattern>[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%thread] %highlight([%-5level] %logger{50} - %msg%n)</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    <!--滚动记录文件,先将日志记录到指定文件，当符合某个条件时，将日志记录到其他文件 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
	    <!--被写入的文件名，可以是相对目录，也可以是绝对目录，如果上级目录不存在会自动创建-->
    	<file>${LOG_HOME}/${LOG_PREFIX}-info.log</file>
	    
        <!--<encoder>：对记录事件进行格式化。-->
    	<encoder>
    		<!--格式化输出：%d表示日期,后面跟时间格式，默认%data{yyyy-MM-dd}，%thread表示线程名， %msg：日志消息，%n是换行符-->
    		<pattern>%date [%level] [%thread] %logger{60} [%file : %line] %msg%n</pattern>
    	</encoder>
        
        <!--设置滚动策略-->
        <!--TimeBasedRollingPolicy： 最常用的滚动策略，它根据时间来制定滚动策略-->
        <!--SizeAndTimeBasedRollingPolicy： 根据大小和事件来制定滚动策略-->        
    	<rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- 指定fileNamePattern，定义日志文件或者是压缩包存放的位置，如果存在file属性，日志输出到file文件中-->
            <fileNamePattern>${LOG_HOME}/logs/log-%d{yyyy-MM-dd}.%i.log</fileNamePattern>

            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">	
                <!--文件达到 最大10MB时会被压缩和切割 -->
                <maxFileSize>10MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
			<!-- 日志总保存量为1GB 超过就是删老的-->
            <totalSizeCap>1GB</totalSizeCap>
            <!--可选节点，控制保留的归档文件的最大时效，超出数量就删除旧文件。如果是30，则只保存最近30天的文件-->
            <maxHistory>30</maxHistory>
    	</rollingPolicy>
    </appender>
    
    <!--指定环境控制台打印sql-->
    <springProfile name="dev">
        <logger name="com.test.mapper" level="debug"/>
    </springProfile>
    
    <!--root是默认的logger 这里设定输出级别是info-->
    <root level="INFO">
	    <!--定义了两个appender，日志会通过往这两个appender里面写-->
    	<appender-ref ref="CONSOLE"/>
	    <appender-ref ref="FILE"/>
    </root>
</configuration>
```

其他策略：

```xml
		<rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <!-- 指定fileNamePattern，定义日志文件或者是压缩包存放的位置，如果存在file属性，日志输出到file文件中，fileNamePattern存放日志压缩文件的路径 -->
            <fileNamePattern>${file_root_app_history_dir}/%d{yyyy/MM,aux}/%d{yyyy-MM-dd}_log%i.log</fileNamePattern>
            <!-- 每个文件的大小 -->
            <maxFileSize>5MB</maxFileSize>
            <!-- 指定最大的历史，365天，对应fileNamePattern中的日期格式 -->
            <maxHistory>365</maxHistory>
            <!-- 日志文件的总大小,20GB -->
            <totalSizeCap>20GB</totalSizeCap>
        </rollingPolicy>
```



# 三、在application.xml配置文件中指定logback

```yaml
logging:
  config: classpath:logback-spring.xml
```

# 四、控制台增加日志显示

ide控制台日志输出了？因为我们上面的配置文件中还没有配置 console（控制台）：

在上面logback-spring.xml中添加一个appender,name为"CONSOLE":

```xml
<appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <!--此日志appender是为开发使用，只配置最底级别，控制台输出的日志级别是大于或等于此级别的日志信息-->
    <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
        <level>info</level>
    </filter>
    <encoder>
        <Pattern>${console_pattern}</Pattern>
        <!-- 设置字符集 -->
        <charset>UTF-8</charset>
    </encoder>
</appender>
```

```xml
将原来的
<property name="console_pattern" value="%-5level \t %d{yyyy-MM-dd HH:mm:ss.SSS} \t %msg \t [%thread] \t %logger{80}%n" />
（这个是黑白的，无感）   

替换为

<property name="console_pattern" value="[%highlight(%-5level)]%green([%date{yyyy-MM-dd HH:mm:ss}]) %boldMagenta([%thread-%logger{96}-%mdc{client}-%line line])  - %cyan(%msg%n)"/>
（这个是输出带彩色的格式，看起来酷炫一点，习惯一些）
```

最后在root中添加如下一行，使console配置起效

```xml
<appender-ref ref="CONSOLE" />
```

# 五、include子xml

根节点是include

```xml
<included>
    <!-- 定义logger -->
    <logger name="com.rain.test" level="debug" additivity="false">
        <appender-ref ref="console" />
        <appender-ref ref="file" />
    </logger>
</included>
```

