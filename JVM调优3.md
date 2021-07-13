# 6.jmap,jstat,jinfo命令

## 6.1.jmap查看进程的堆使用情况

```shell
jmap -heap PID 查看堆配置参数及当前占用情况：
Heap Configuration:
   MinHeapFreeRatio         = 0 #空闲堆空间的最小百分比HeapFreeRatio=CurrentFreeHeapSize/CurrentTotalHeapSize
   								#HeapFreeRatio<MinHeapFreeRatio，则需要堆扩容，扩容的时机在每次垃圾回收之后
   MaxHeapFreeRatio         = 100 #空闲堆空间的最大百分比,如果HeapFreeRatio>MaxHeapFreeRatio，则需要堆缩容，缩容时机在每次垃圾回收之后
   MaxHeapSize              = 32210157568 (30718.0MB) #JVM 堆空间允许的最大值
   NewSize                  = 715653120 (682.5MB) #JVM 新生代堆空间的默认值。
   MaxNewSize               = 10736369664 (10239.0MB) #JVM 新生代堆空间允许的最大值。
   OldSize                  = 1431830528 (1365.5MB) #JVM 老年代堆空间的默认值。
   NewRatio                 = 2 #新生代（2个Survivor区和Eden区 ）与老年代（不包括永久区）的堆空间比值，表示新生代：老年代=1：2。
   SurvivorRatio            = 8 #两个Survivor区和Eden区的堆空间比值为 8，表示 S0 ： S1 ：Eden = 1：1：8。
   MetaspaceSize            = 21807104 (20.796875MB)	#JVM 元空间的默认值。
   CompressedClassSpaceSize = 1073741824 (1024.0MB)
   MaxMetaspaceSize         = 17592186044415 MB #JVM 元空间允许的最大值。
   G1HeapRegionSize         = 0 (0.0MB) #在使用 G1 垃圾回收算法时，JVM 会将Heap空间分隔为若干个Region，该参数用来指定每个Region空间的大小。

Heap Usage:
PS Young Generation
Eden Space:
   capacity = 3172466688 (3025.5MB)
   used     = 2563991152 (2445.2125091552734MB)
   free     = 608475536 (580.2874908447266MB)
   80.82011268072297% used
From Space:
   capacity = 29360128 (28.0MB)
   used     = 29123904 (27.77471923828125MB)
   free     = 236224 (0.22528076171875MB)
   99.19542585100446% used
To Space:
   capacity = 56623104 (54.0MB)
   used     = 0 (0.0MB)
   free     = 56623104 (54.0MB)
   0.0% used
PS Old Generation
   capacity = 2584739840 (2465.0MB)
   used     = 362400032 (345.6116027832031MB)
   free     = 2222339808 (2119.388397216797MB)
   14.020754676803373% used
46714 interned Strings occupying 5546896 bytes.

也许进程占用的总内存比较多，但我们在这里可以看到真正用到的并没有多少，很多都是"Free"。内存使用的堆积大多在老年代，内存池露始于此，所以要格外关心“Old Generation”。

jmap -histo PID 查看对内对象占用空间大小，有高到低排序
这里会生成一个类的统计报表，此表非常简单，如显示什么类有多少个实例，共占了多少字节等。

jmap -dump:live,format=b,file=heap.dmp PID 对存活的对象生成一个dmp文件用于分析，这个方法会FULLGC。

jmap -dump:format=b,file=heap.dmp PID 对存活的对象生成一个dmp文件用于分析，这个方法导出所有-但是要用启动进程的账号登录。

jmap -clstats pid：输出加载类信息
```

## 6.2.jstat查看JVM

```shell
jstat工具查看jvm的情况
#每2秒查看一下pid是12345的进程的gc情况
jstat -gc 12345 2000

#使用总查看gc情况
jstat -gcutil 12345 1s

S0C、S1C、S0U、S1U：Survivor 0/1区容量（Capacity）和使用量（Used）
EC、EU：Eden区容量和使用量
OC、OU：年老代容量和使用量
PC、PU：永久代容量和使用量
MC、MU：方法区容量和使用量
YGC、YGT：年轻代GC次数和GC耗时
FGC、FGCT：Full GC次数和Full GC耗时
GCT：GC总耗时

S0：年轻代中第一个survivor（幸存区）已使用的占当前容量百分比
S1：年轻代中第二个survivor（幸存区）已使用的占当前容量百分比
E：年轻代中Eden（伊甸园）已使用的占当前容量百分比
O：old代已使用的占当前容量百分比
P：perm代已使用的占当前容量百分比
M：方法区已使用的占当前容量百分比
CCS:压缩类空间已使用占用当前容量百分比

#类加载统计
jstat -class 123456 
Loaded:加载class的数量	Bytes：所占用空间大小	Unloaded：未加载数量	Bytes:未加载占用空间	Time：时间

#编译统计
jstat -compiler 123456

#堆内存统计
jstat -gccapacity 123456

#新生代垃圾回收统计
jstat -gcnew 123456

#新生代内存统计
jstat -gcnewcapacity 123456

#老年代垃圾回收统计
jstat -gcold 123456

#老年代内存统计
jstat -gcoldcapacity 123456

#元数据空间统计
jstat -gcmetacapacity 123456

# JVM编译方法统计
jstat -printcompilation 123456
Compiled：最近编译方法的数量	Size：最近编译方法的字节码数量	Type：最近编译方法的编译类型。Method：方法名标识。
```



## 6.3.jinfo查看指定PID的JVM信息

```shell
1）jinfo -flags pid 查询虚拟机运行参数信息。

2）jinfo -flag name pid，查询具体参数信息，如jinfo -flag UseSerialGC 42324，查看是否启用UseSerialGC

Non-default VM flags: -XX:CICompilerCount=18 -XX:ConcGCThreads=18 -XX:G1HeapRegionSize=16777216 -XX:InitialHeapSize=34359738368 -XX:InitiatingHeapOccupancyPercent=45 -XX:MarkStackSize=4194304 -XX:MaxGCPauseMillis=200 -XX:MaxHeapSize=34359738368 -XX:MaxNewSize=20602421248 -XX:MinHeapDeltaBytes=16777216 -XX:+UseFastUnorderedTimeStamps -XX:+UseG1GC
Command line:  -Xmx32g -Xms32g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:InitiatingHeapOccupancyPercent=45

参数解读
-XX:CICompilerCount=18        最大并行编译数
-XX:ConcGCThreads=18	并行标记的线程数
-XX:G1HeapRegionSize=16777216	分区大小单位KB   32G/2048
-XX:InitialHeapSize=34359738368   初始化堆大小 32G
-XX:InitiatingHeapOccupancyPercent=45	初始堆占用比例45%
-XX:MarkStackSize=4194304	标记堆栈大小4M
-XX:MaxGCPauseMillis=200	垃圾回收等待时间毫秒
-XX:MaxHeapSize=34359738368	最大堆32G
-XX:MaxNewSize=20602421248    新生代最大大小19G多-自动计算的
-XX:MinHeapDeltaBytes=16777216	扩展内存的最小值16M
-XX:+UseFastUnorderedTimeStamps 	使用快速无序时间戳
-XX:+UseG1GC	使用G1垃圾收集器

-XX:MinHeapDeltaBytes=524288
-XX:NewSize=20971520     设置年轻代的大小
-XX:OldSize=41943040
-XX:+UseCompressedClassPointers
-XX:+UseCompressedOops
-XX:+UseParallelGC  使用 Parallel收集器
```

# 7.常用工具简介

```
Java性能监控和故障诊断除了丰富的命令行工具（jps，jinfo，jstat，jmap，jstack，jcmd）外还提供了可视化的监控工具jconsole、jvisualvm和jmc

常用工三种工具：Jconsole，jProfile，VisualVM

Jconsole : jdk自带，功能简单，但是可以在系统有一定负荷的情况下使用。对垃圾回收算法有很详细的跟踪。详细说明参考这里

JProfiler：商业软件，需要付费。功能强大。

VisualVM：JDK自带，功能强大，与JProfiler类似,但是要自己额外安装一些插件。
```

查看jdk1.8默认垃圾收集器：

```shell
java -XX:+PrintCommandLineFlags -version
#Parallel Scavenge（新生代）+ Parallel Old（老年代）
查看堆内存情况
java -XX:+PrintGCDetails -version
```



# 8.Jconsole

```
输入命令jsonsole，打开客户端，选择一个进程，然后输入连接的用户名和密码。

点击连接，如果提示SSL连接密码，提示不安全继续。进入到jconsole的管理页面。
里面有内存、线程、类、VM概要、MBean等
在内存里可以查看
```



# 9.JProfiler

```
其中的allocation call tree，能够发现哪个函数占用了最大的内存，最后就能定位到发生泄漏或者内存使用过大的地方进行优化，譬如一个方法就占用了大量的CPU和内存这种是很有问题的。
申请临时Licence的地址为：https://www.ej-technologies.com/download/jprofiler/trial，通过邮箱申请后注意查看邮箱，将激活码在下图所示地方填写即可。
2. 在IDEA中安装JProfile插件
```



# 10.VisualVM

```
输入命令 jvisualvm
选择一个正在服务器中运行的进程。双击进去。
此时可以看到：
概述、监视、线程、抽样器和profile。
但是没有最重要的Visual GC，需要单独安装。
在插件中找到第二个，可用插件安装Visual GC 安装，这样就可就了，之后重启jvisualvm即可。
如果安不上可以手动安装：
https://visualvm.github.io/download.html
点击上面在Download，再在下载页点击Plugins Offline
然后找到Java VisualVM里找到当前服务器对应的版本，我本地是8U131，点击。
在打开页面种找到Tools-Visual GC
得到一个com-sun-tools-visualvm-modules-visualgc.nbm文件
然后在VISUALVM的菜单 工具-插件-已下载中点击添加插件，选择nbm文件，点击安装即可。
重新在左边打开这一个java进程就可以查看相应的内容VISUAL GC内容了。
如果输入命令jvisualvm 不能打开，考虑可能是xterm有问题，可以安装一个图形界面，用vnc登录到服务器运行命令。

export JAVA_HOME=/opt/annotation/jdk/jdk1.8.0_231
export PATH=$PATH:/opt/annotation/jdk/jdk1.8.0_231/bin
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
export DISPLAY=localhost:10.0

-Xmx   Java Heap最大值，默认值为物理内存的1/4，最佳设值应该视物理内存大小及计算机内其他内存开销而定
-Xms   Java Heap初始值，Server端JVM最好将-Xms和-Xmx设为相同值，开发测试机JVM可以保留默认值；
-Xmn   Java Heap Young区大小，不熟悉最好保留默认值；
-Xss   每个线程的Stack大小，不熟悉最好保留默认值；

当程序比较占内存时，可能会超出默认内存大小，导致full gc而导致cpu占用高。可以修改默认内存大小，保证内存够用，不频繁gc.

可以使用jmap -heap pid来查看下实时内存情况

一个机器192G，默认启动 堆在大小默认1/4 也就是48g 相当于启动一个jar给了48G，但是1.8有个规定，内存大于128G默认给30G
也就是说，默认最大堆在大小是32g左右。
可以自己设置最大、最小堆内存和年轻代的大小。
```

nohup java -Xmx4g -Xms4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:InitiatingHeapOccupancyPercent=45 -jar service-coco.jar --spring.profiles.active=test1  >coco.log 2>&1 &



nohup java -Xmx6g -Xms6g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:InitiatingHeapOccupancyPercent=45 -jar service-coco.jar --spring.profiles.active=prd1  >coco.log 2>&1 &
