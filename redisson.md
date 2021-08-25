# redisson概述

Redisson是架设在[Redis](http://www.oschina.net/p/redis)基础上的一个Java驻内存数据网格（In-Memory Data Grid）。【[Redis官方推荐](http://www.redis.io/clients)】

Redisson在基于NIO的[Netty](http://netty.io/)框架上，充分的利用了Redis键值数据库提供的一系列优势，在Java实用工具包中常用接口的基础上，为使用者提供了一系列具有分布式特性的常用工具类。使得原本作为协调单机多线程并发程序的工具包获得了协调分布式多机多线程并发系统的能力，大大降低了设计和研发大规模分布式系统的难度。同时结合各富特色的分布式服务，更进一步简化了分布式环境中程序相互之间的协作。

```xml
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson</artifactId>
    <version>3.11.1</version>
</dependency>
```

# redisson的常规功能

1. 支持云托管服务模式（同时支持亚马逊云的ElastiCache Redis和微软云的Azure Redis Cache）:

- 自动发现主节点变化

2. 支持Redis集群模式（同时支持亚马逊云的ElastiCache Redis Cluster和微软云的Azure Redis Cache）:

- 自动发现主从节点
- 自动更新状态和组态拓扑
- 自动发现槽的变化

3. 支持Redis哨兵模式:

- 自动发现主、从和哨兵节点
- 自动更新状态和组态拓扑

4. 支持Redis主从模式

5. 支持Redis单节模式

6. 多节点模式均支持读写分离：从读主写，主读主写，主从混读主写

7. 所有对象和接口均支持异步操作

8. 自行管理的弹性异步连接池

9. 所有操作线程安全

10. 支持LUA脚本

11. 提供[分布式对象](https://github.com/redisson/redisson/wiki/6.-分布式对象)
    [通用对象桶（Object Bucket）](https://github.com/redisson/redisson/wiki/6.-分布式对象#61-通用对象桶object-bucket)、[二进制流（Binary Stream）](https://github.com/redisson/redisson/wiki/6.-分布式对象#62-二进制流binary-stream)、[地理空间对象桶（Geospatial Bucket）](https://github.com/redisson/redisson/wiki/6.-分布式对象#63-地理空间对象桶geospatial-bucket)、[BitSet](https://github.com/redisson/redisson/wiki/6.-分布式对象#64-bitset)、[原子整长形（AtomicLong）](https://github.com/redisson/redisson/wiki/6.-分布式对象#65-原子整长形atomiclong)、[原子双精度浮点数（AtomicDouble）](https://github.com/redisson/redisson/wiki/6.-分布式对象#66-原子双精度浮点数atomicdouble)、[话题（订阅分发）](https://github.com/redisson/redisson/wiki/6.-分布式对象#67-话题订阅分发)、 [布隆过滤器（Bloom Filter）](https://github.com/redisson/redisson/wiki/6.-分布式对象#68-布隆过滤器bloom-filter)和[基数估计算法（HyperLogLog）](https://github.com/redisson/redisson/wiki/6.-分布式对象#69-基数估计算法hyperloglog)

12. 提供[分布式集合](https://github.com/redisson/redisson/wiki/7.-分布式集合)
        [映射（Map）](https://github.com/redisson/redisson/wiki/7.-分布式集合#71-映射map)、[多值映射（Multimap）](https://github.com/redisson/redisson/wiki/7.-分布式集合#72-多值映射multimap)、[集（Set）](https://github.com/redisson/redisson/wiki/7.-分布式集合#73-集set)、[列表（List）](https://github.com/redisson/redisson/wiki/7.-分布式集合#77-列表list)、[有序集（SortedSet）](https://github.com/redisson/redisson/wiki/7.-分布式集合#74-有序集sortedset)、[计分排序集（ScoredSortedSet）](https://github.com/redisson/redisson/wiki/7.-分布式集合#75-计分排序集scoredsortedset)、[字典排序集（LexSortedSet）](https://github.com/redisson/redisson/wiki/7.-分布式集合#76-字典排序集lexsortedset)、[列队（Queue）](https://github.com/redisson/redisson/wiki/7.-分布式集合#78-列队queue)、[双端队列（Deque）](https://github.com/redisson/redisson/wiki/7.-分布式集合#79-双端队列deque)、[阻塞队列（Blocking Queue）](https://github.com/redisson/redisson/wiki/7.-分布式集合#710-阻塞队列blocking-queue)、[有界阻塞列队（Bounded Blocking Queue）](https://github.com/redisson/redisson/wiki/7.-分布式集合#711-有界阻塞列队bounded-blocking-queue)、[ 阻塞双端列队（Blocking Deque）](https://github.com/redisson/redisson/wiki/7.-分布式集合#712-阻塞双端列队blocking-deque)、[阻塞公平列队（Blocking Fair Queue）](https://github.com/redisson/redisson/wiki/7.-分布式集合#713-阻塞公平列队blocking-fair-queue)、[延迟列队（Delayed Queue）](https://github.com/redisson/redisson/wiki/7.-分布式集合#714-延迟列队delayed-queue)、[优先队列（Priority Queue）](https://github.com/redisson/redisson/wiki/7.-分布式集合#715-优先队列priority-queue)和[优先双端队列（Priority Deque）](https://github.com/redisson/redisson/wiki/7.-分布式集合#716-优先双端队列priority-deque)

13. 提供[分布式锁和同步器](https://github.com/redisson/redisson/wiki/8.-分布式锁和同步器)
    [可重入锁（Reentrant Lock）](https://github.com/redisson/redisson/wiki/8.-分布式锁和同步器#81-可重入锁reentrant-lock)、[公平锁（Fair Lock）](https://github.com/redisson/redisson/wiki/8.-分布式锁和同步器#82-公平锁fair-lock)、[联锁（MultiLock）](https://github.com/redisson/redisson/wiki/8.-分布式锁和同步器#83-联锁multilock)、[ 红锁（RedLock）](https://github.com/redisson/redisson/wiki/8.-分布式锁和同步器#84-红锁redlock)、[读写锁（ReadWriteLock）](https://github.com/redisson/redisson/wiki/8.-分布式锁和同步器#85-读写锁readwritelock)、[信号量（Semaphore）](https://github.com/redisson/redisson/wiki/8.-分布式锁和同步器#86-信号量semaphore)、[可过期性信号量（PermitExpirableSemaphore）](https://github.com/redisson/redisson/wiki/8.-分布式锁和同步器#87-可过期性信号量permitexpirablesemaphore)和[闭锁（CountDownLatch）](https://github.com/redisson/redisson/wiki/8.-分布式锁和同步器#88-闭锁countdownlatch)

14. 提供[分布式服务](https://github.com/redisson/redisson/wiki/9.-分布式服务)
    [分布式远程服务（Remote Service, RPC）](https://github.com/redisson/redisson/wiki/9.-分布式服务#91-分布式远程服务remote-service)、[分布式实时对象（Live Object）服务](https://github.com/redisson/redisson/wiki/9.-分布式服务#92-分布式实时对象live-object服务)、[分布式执行服务（Executor Service）](https://github.com/redisson/redisson/wiki/9.-分布式服务#93-分布式执行服务executor-service)、[分布式调度任务服务（Scheduler Service）](https://github.com/redisson/redisson/wiki/9.-分布式服务#94-分布式调度任务服务scheduler-service)和[分布式映射归纳服务（MapReduce）](https://github.com/redisson/redisson/wiki/9.-分布式服务#95-分布式映射归纳服务mapreduce)

15. [支持Spring框架](https://github.com/redisson/redisson/wiki/14.-第三方框架整合#141-spring框架整合)

16. 提供[Spring Cache](https://github.com/redisson/redisson/wiki/14.-第三方框架整合#142-spring-cache整合)集成

17. 提供[Hibernate Cache](https://github.com/redisson/redisson/wiki/14.-第三方框架整合#143-hibernate整合)集成

18. 提供[JCache](https://github.com/redisson/redisson/wiki/14.-第三方框架整合#144-java缓存标准规范jcache-api-jsr-107)实现

19. 提供[Tomcat Session Manager](https://github.com/redisson/redisson/wiki/14.-第三方框架整合#145-tomcat会话管理器tomcat-session-manager)

20. 提供[Spring Session](https://github.com/redisson/redisson/wiki/14.-第三方框架整合#146-spring-session会话管理器)集成

21. 支持[异步流方式](https://github.com/redisson/redisson/wiki/3.-程序接口调用方式#32-异步流执行方式)执行操作

22. 支持[Redis管道操作](https://github.com/redisson/redisson/wiki/10.-额外功能#103-命令的批量执行)（批量执行）

23. 支持安卓（Andriod）系统

24. 支持断线自动重连

25. 支持命令发送失败自动重试

26. 支持OSGi

27. 支持采用多种方式自动序列化和反序列化（[Jackson JSON](https://github.com/FasterXML/jackson), [Avro](http://avro.apache.org/), [Smile](http://wiki.fasterxml.com/SmileFormatSpec), [CBOR](http://cbor.io/), [MsgPack](http://msgpack.org/), [Kryo](https://github.com/EsotericSoftware/kryo), [FST](https://github.com/RuedigerMoeller/fast-serialization), [LZ4](https://github.com/jpountz/lz4-java), [Snappy](https://github.com/xerial/snappy-java)和JDK序列化）

28. 超过1000个单元测试

    

# 构造一个Redisson

```java
// 1. 构造Config对象 
// 单机redssion默认配置
Config config = new Config();
String prefix = "redis://";
Method method = ReflectionUtils.findMethod(RedisProperties.class, "isSsl");
if (method != null && (Boolean) ReflectionUtils.invokeMethod(method, redisProperties)) {
    prefix = "rediss://";
}
config.useSingleServer().setAddress(prefix + redisProperties.getHost() + ":" + redisProperties.getPort())
                    .setConnectTimeout(timeout).setDatabase(redisProperties.getDatabase())
                    .setPassword(redisProperties.getPassword());

// 2. 构造Redisson实例
RedissonClient redisson = Redisson.create(config);

// 3. 存储对象，获取需要的对象
RMap map = redisson.getMap("myMap");
// 获取一个重入锁
RLock lock = redisson.getLock("myLock");

RExecutorService executor = redisson.getExecutorService("myExecutorService");

// 或者其它30多中对象及服务 ...
```

# 几种常用redisson锁

```java
//获得一把锁
RLock lock = redisson.getLock("key");

lock.lock();//自带watchDog,不延期就自动释放了

//尝试加锁，如果没调用解锁方法10秒自动释放
lock.lock(10,TimeUnit.SENCONDS);

//尝试加锁，最多等待100秒，上锁后10秒自动解锁
boolean res = lock.tryLock(100,10,TimeUnit.SECONDS);
if(res){
    try{
        //...
    }finally{
        lock.unlock();
    }    
}

//异步
RLock lock = redisson.getLock("anyLock");
lock.lockAsync();
lock.lockAsync(10,TimeUnit.SECONDS);
Future<Boolean> res = lock.tryLockAsync(100,0,TimeUnit.SECONDS);

//Fair Lock  公平锁
//在提供了自动过期解锁功能的同时，保证了当多个Redisson客户端线程同时请求加锁时，优先分配给先发出请求的线程。
RLock fairlock = redisson.getFairLock("anyLock");
fairlock.lock();
fairlock.lock(10,TimeUnit.SECONDS);
boolean res = fairlock.tryLock(100,10,TimeUnit.SECONDS);
fairlock.unlock();

//MultiLock 联锁
RLock lock1 = redisson.getFairLock("lock1");
RLock lock2 = redisson.getFairLock("lock2");
RLock lock3 = redisson.getFairLock("lock3");

RedissonMultiLock lock = redisson.getMultiLock(lock1,lock2,lock3);
lock.lock(); //所有的锁都锁定成功才算成功
lock.unlock();

//RedLock 红锁 大部分节点上加锁成功就算成功
RLock lock1 = redisson.getFairLock("lock1");
RLock lock2 = redisson.getFairLock("lock2");
RLock lock3 = redisson.getFairLock("lock3");
RedissonRedLock lock = redisson.getRedLock(lock1,lock2,lock3);
lock.lock();
lock.unlock();

//读写锁 ReadWriteLock
//Redisson的分布式可重入读写锁RReadWriteLock Java对象实现了java.util.concurrent.locks.ReadWriteLock接口。同时还支持自动过期解锁。该对象允许同时有多个读取锁，但是最多只能有一个写入锁。
RReadWriteLock rwlock = redisson.getLock("anyLock");
rwlock.readLock().lock();
rwlock.writeLock().lock();

rwlock.readLock().lock(10,TimeUnit.SECONDS);
rwlock.writeLock().lock(10,TimeUnit.SECONDS);

rwlock.readLock().tryLock(100,10,TimeUnit.SECONDS);
rwlock.writeLock().tryLock(100,10,TimeUnit.SECONDS);

//信号量Semaphore
//Redisson的分布式信号量（Semaphore）Java对象RSemaphore采用了与java.util.concurrent.Semaphore相似的接口和用法。
RSemaphore semaphore = redisson.getSemaphore("semaphore");
semaphore.acquire();
semaphore.acquireAsync();
semaphore.acquire(23);
semaphore.tryAcquire();
semaphore.tryAcquireAsync();
semaphore.tryAcquire(23,TimeUnit.SECONDS);
semaphore.tryAcquireAsync(23,TimeUnit.SECONDS);
semaphore.release(10);
semaphore.release();
semaphore.releaseAsync();

//可过期性信号量PermitExpirableSemaphore
//Redisson的可过期性信号量（PermitExpirableSemaphore）实在RSemaphore对象的基础上，为每个信号增加了一个过期时间。每个信号可以通过独立的ID来辨识，释放时只能通过提交这个ID才能释放。
RPermitExpirableSemaphore semaphore = redisson.getPermitExpirableSemaphore("mys");
String permitId = semaphore.acquire();
String permitId = semaphore.acquire(2,TimeUnit.SECONDS);
semaphore.release(permitId);

//闭锁CountDownLatch
Redisson的分布式闭锁（CountDownLatch）Java对象RCountDownLatch采用了与java.util.concurrent.CountDownLatch相似的接口和用法。
```



# watchdog原理

首先watchdog的具体思路是 加锁时，默认加锁 30秒，每10秒钟检查一次，如果存在就重新设置 过期时间为30秒。

然后设置默认加锁时间的参数是 lockWatchdogTimeout（监控锁的看门狗超时，单位：毫秒）

官方文档描述如下

```
lockWatchdogTimeout（监控锁的看门狗超时，单位：毫秒）
默认值：30000
监控锁的看门狗超时时间单位为毫秒。该参数只适用于分布式锁的加锁请求中未明确使用leaseTimeout参数的情况。如果该看门狗未使用lockWatchdogTimeout去重新调整一个分布式锁的lockWatchdogTimeout超时，那么这个锁将变为失效状态。这个参数可以用来避免由Redisson客户端节点宕机或其他原因造成死锁的情况。
```

需要注意的是

1.watchDog 只有在未显示指定加锁时间时才会生效。（这点很重要）

2.lockWatchdogTimeout设定的时间不要太小 ，比如我之前设置的是 100毫秒，由于网络直接导致加锁完后，watchdog去延期时，这个key在redis中已经被删除了。



在调用lock方法时，会最终调用到tryAcquireAsync。详细解释如下：

```
如果指定了加锁时间，会直接去加锁
如果没指定加锁时间，会先进行加锁，并且默认时间就是localWatchdogTimeout的时间
这个是个异步操作，返回RFuture 类似netty的future
在RFuture的onComplate回调函数里判断如果没释放锁，就定时执行，让当前锁自动延期

```



1.要使 watchLog机制生效 ，lock时 不要设置 过期时间

2.watchlog的延时时间 可以由 lockWatchdogTimeout指定默认延时时间，但是不要设置太小。如100

3.watchdog 会每 lockWatchdogTimeout/3时间，去延时。

4.watchdog 通过 类似netty的 Future功能来实现异步延时

5.watchdog 最终还是通过 lua脚本来进行延时
