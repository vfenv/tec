### 1、添加线程组

```
打开一个Jmeter的客户端
在Test Plan上单击右键
添加 - 线程（用户） - 线程组
Add-Threads(Users)-Thread Group

Number of Threads(users): 10		线程数量  设置的是10个
Ramp-Up Period(in seconds): 0		所有线程在指定时间内启动，0表示同时启动
Loop Count: 1						线程重复次数
```



### 2、添加http请求 

```
在线程组上右键单击
添加 - 取样器 - HTTP请求
协议：http/https
服务器名称或IP：www.vfenv.com
HTTP请求方式：GET 
路径：/gateway/service-test/api/data/12345
选中跟随重定向
选中使用KeepAlive

Add - Sampler - HTTP Request
protocol:
Server Name or IP:
Port Number:
GET
Path:
Content encoding:
Follow Redirects
Use KeepAlive
```



### 3、添加cookie

```text
右键Http Request
添加 - 配置元件 - HTTP Cookie管理器
Add - Config Element - HTTP Cookie Manager
```

添加自定义cookie:

| 名称   | 值     | 域            | 路径 |
| ------ | ------ | ------------- | ---- |
| userId | 123    | www.vfenv.com | /    |
| role   | member | www.vfenv.com | /    |

 

### 4、添加结果树和汇总报告

```
右键Http Request
添加 - 监听器 - 察看结果树
添加 - 监听器 - 汇总报告

Add - Listener - View Results Tree
Add - Listener - Summary Result

```



### 5、添加自定义jar

使用jmeter压测一个web接口的时候，踩了一个小坑，记录下来，要压测一个http接口，此接口有权限控制、需要签名（几个参数取md5），在jmeter提供的函数中未找到md5的函数，所以自己写了个java request来实现签名算法，详细代码如下：

```java
public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
  sampleResult = new SampleResult();
  sampleResult.setSampleLabel("sign");
  sampleResult.sampleStart();
  long timestamp = System.currentTimeMillis() / 1000;
  try {
    MessageDigest messageDigest = MessageDigest.getInstance("MD5");
    String source = username+password+timestamp;
    messageDigest.update(source.getBytes());
    String sign = StringUtil.toHexString(messageDigest.digest());
    sampleResult.setResponseCode("200");
    sampleResult.sampleEnd();
    sampleResult.setResponseData("" + timestamp + " " + sign, StringPool.UTF_8);
    sampleResult.setResponseCodeOK();
    sampleResult.setSuccessful(true);
  } catch (Exception e) {
    e.printStackTrace();
  }
  return sampleResult;
}
```

 

打包，放在jmeter lib/ext下，不用多说，在jmeter界面下编辑http请求（签名请求和http请求各占50%），完成之后放在linux机器上运行`sh jmeter.sh -n -t somename.jmx -l 1.jtl`，jmeter会把qps信息打印到控制台，如下：

```shell
    summary + 114726 in 00:00:10 = 11472.6/s Avg:     8 Min:     0 Max:   150 Err:     0 (0.00%) Active: 100 Started: 100 Finished: 0
    summary = 79522702 in 01:55:28 = 11478.8/s Avg:     8 Min:     0 Max:  1682 Err:     0 (0.00%)
    summary + 102670 in 00:00:10 = 10267.0/s Avg:     9 Min:     0 Max:  1202 Err:     0 (0.00%) Active: 100 Started: 100 Finished: 0
    summary = 79625372 in 01:55:38 = 11477.0/s Avg:     8 Min:     0 Max:  1682 Err:     0 (0.00%)
```



### 6、如何查看负载和性能瓶颈

服务器上执行**top**命令

可以看到系统负载`load average`情况，1分钟平均负载，5分钟平均负载，15分钟平均负载分别是`1.58, 0.50, 0.23`；

同时可以看到最耗费资源的进程为 `mysql`。

**load average**值的含义

```
1） 单核处理器
假设我们的系统是单CPU单内核的，把它比喻成是一条单向马路，把CPU任务比作汽车。当车不多的时候，load <1；当车占满整个马路的时候 load=1；当马路都站满了，而且马路外还堆满了汽车的时候，load>1
 2） 多核处理器
我们经常会发现服务器Load > 1但是运行仍然不错，那是因为服务器是多核处理器（Multi-core）。
假设我们服务器CPU是2核，那么将意味我们拥有2条马路，我们的Load = 2时，所有马路都跑满车辆
```



```shell
#注：查看cpu 核数命令： 
grep 'model name' /proc/cpuinfo | wc -l
```



什么样的Load average值要提高警惕

```shell
•	0.7 < load < 1: 此时是不错的状态，如果进来更多的汽车，你的马路仍然可以应付。
•	load = 1: 你的马路即将拥堵，而且没有更多的资源额外的任务，赶紧看看发生了什么吧。
•	load > 5: 非常严重拥堵，我们的马路非常繁忙，每辆车都无法很快的运行
```



三种Load值，应该看哪个

```
通常我们先看15分钟load，如果load很高，再看1分钟和5分钟负载，查看是否有下降趋势。
1分钟负载值 > 1，那么我们不用担心，但是如果15分钟负载都超过1，我们要赶紧看看发生了什么事情。
所以我们要根据实际情况查看这三个值。
```

