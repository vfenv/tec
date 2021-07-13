# 5.常用垃圾收集器

## 5.1.CMS垃圾收集器

```
CMS全称Concurrent mark sweep,是一款并发的，使用 标记-清除 算法的垃圾回收器，如果老年代使用CMS垃圾回收，需要使用-XX:+UseConcMarkSweepGC。
它可以与Serial收集器和Parallel New收集器搭配使用。
缺点：会产生内存碎片，其次cms算法实现比较复杂,当然实现的复杂可是也已经实现，不能算作缺点。
触发机制：
1.周期性Old GC
	ConcurrentMarkSweepThread每2秒判断是否需要触发Old GC，满足执行，不满足休眠2S
	触发条件：
		1、如果没有设置-XX:+UseCMSInitiatingOccupancyOnly，虚拟机会根据收集的数据决定是否触发（建议线上环境带上这个参数，不然会加大问题排查的难度）。
		2、老年代使用率达到阈值 CMSInitiatingOccupancyFraction，默认92%。
		3、永久代的使用率达到阈值 CMSInitiatingPermOccupancyFraction，默认92%，前提是开启 CMSClassUnloadingEnabled。
		4、新生代的晋升担保失败。
	晋升担保失败
		老年代是否有足够的空间来容纳全部的新生代对象或历史平均晋升到老年代的对象，如果不够的话，就提早进行一次老年代的回收，
		防止下次进行YGC的时候发生晋升失败。
		
	周期性OldGC过程：初始化标记-并发标记-预处理-重新标记-并发清理-调整堆大小-重置-闲置等待
	对象在标记过程中，根据标记情况，分成三类：
        白色对象，表示自身未被标记；
        灰色对象，表示自身被标记，但内部引用未被处理；
        黑色对象，表示自身被标记，内部引用都被处理；
        
    	1、InitialMarking（初始化标记，整个过程STW）
			该阶段单线程执行，主要分分为两步：
			1、标记GC Roots可达的老年代对象；
			2、遍历新生代对象，标记可达的老年代中对象；
			ps：为了加快处理速度，减少停顿时间，开启初始标记并行化，-XX:+CMSParallelInitialMarkEnabled，
			    同时调大并行标记的线程数，线程数不要超过cpu的核数。
		2、Marking（并发标记）
			该阶段GC线程和应用线程并发执行，遍历InitialMarking阶段标记出来的存活对象(老年代中被1标记的对象)，递归标记这些对象可达的对象。
			因为该阶段并发执行的，在运行期间可能发生新生代的对象晋升到老年代、或者是直接在老年代分配对象、或者更新老年代对象的引用关系等，对于这些对象，都			    是需要进行重新标记的，否则有些对象就会被遗漏，发生漏标的情况。
        	为了提高重新标记的效率，该阶段会把上述改变的对象所在的区域（Card）标识为Dirty，后续只需扫描这些Dirty Card的对象，避免扫描整个老年代。
        3、Precleaning（预清理阶段）
        	通过参数CMSPrecleaningEnabled选择关闭该阶段，默认启用，主要做两件事情：
        	1.处理新生代已经发现的引用，比如在并发阶段，在Eden区中分配了一个A对象，A对象引用了一个老年代对象B（这个B之前没有被标记），
        	  在这个阶段就会标记对象B为活跃对象。
			2.在并发标记阶段，如果老年代中有对象内部引用发生变化，会把所在的Card标记为Dirty
			 （其实这里并非使用CardTable，而是一个类似的数据结构，叫ModUnionTalble），通过扫描这些Table，
			  重新标记那些在并发标记阶段引用被更新的对象（晋升到老年代的对象、原本就在老年代的对象）
        4、AbortablePreclean（可终止的预清理）
			该阶段发生的前提是，新生代Eden区的内存使用量大于参数CMSScheduleRemarkEdenSizeThreshold 默认是2M，如果新生代的对象太少，
			就没有必要执行该阶段，直接执行重新标记阶段。
			为什么需要这个阶段，存在的价值是什么？
				因为CMS GC的终极目标是降低垃圾回收时的暂停时间，所以在该阶段要尽最大的努力去处理那些在并发阶段被应用线程更新的老年代对象，
				这样在暂停的重新标记阶段就可以少处理一些，暂停时间也会相应的降低。
			在该阶段，主要循环的做两件事：
				1.处理 From 和 To 区的对象，标记可达的老年代对象
				2.和上一个阶段一样，扫描处理Dirty Card中的对象
			当然了，这个逻辑不会一直循环下去，打断这个循环的条件有三个：
			1.可以设置最多循环的次数 CMSMaxAbortablePrecleanLoops，默认是0，意思没有循环次数的限制。
			2.如果执行这个逻辑的时间达到了阈值CMSMaxAbortablePrecleanTime，默认是5s，会退出循环。
			3.如果新生代Eden区的内存使用率达到了阈值CMSScheduleRemarkEdenPenetration，默认50%，会退出循环。
				（这个条件能够成立的前提是，在进行Precleaning时，Eden区的使用率小于十分之一）
			如果在循环退出之前，发生了一次YGC，对于后面的Remark阶段来说，大大减轻了扫描年轻代的负担，但是发生YGC并非人为控制，
				所以只能祈祷这5s内可以来一次YGC。
		5、FinalMarking（重新标记，第二次STW过程）
			该阶段并发执行，在之前的并行阶段（GC线程和应用线程同时执行，好比你妈在打扫房间，你还在扔纸屑），可能产生新的引用关系如下：
			1.老年代的新对象被GC Roots引用
			2.老年代的未标记对象被新生代对象引用
            3.老年代已标记的对象增加新引用指向老年代其它对象
            4.新生代对象指向老年代引用被删除
            5.也许还有其它情况..
         	上述对象中可能有一些已经在Precleaning阶段和AbortablePreclean阶段被处理过，但总存在没来得及处理的，所以还有进行如下的处理：
         	1.遍历新生代对象，重新标记
            2.根据GC Roots，重新标记
            3.遍历老年代的Dirty Card，重新标记，这里的Dirty Card大部分已经在clean阶段处理过
            在第一步骤中，需要遍历新生代的全部对象，如果新生代的使用率很高，需要遍历处理的对象也很多，这对于这个阶段的总耗时来说，
            是个灾难（因为可能大量的对象是暂时存活的，而且这些对象也可能引用大量的老年代对象，造成很多应该回收的老年代对象而没有被回收，
            遍历递归的次数也增加不少），如果在AbortablePreclean阶段中能够恰好的发生一次YGC，这样就可以避免扫描无效的对象。

			如果在AbortablePreclean阶段没来得及执行一次YGC，怎么办？
			CMS算法中提供了一个参数：CMSScavengeBeforeRemark，默认并没有开启，如果开启该参数，在执行该阶段之前，会强制触发一次YGC，
			可以减少新生代对象的遍历时间，回收的也更彻底一点。 另外，还可以开启并行收集：-XX:+CMSParallelRemarkEnabled。

			不过，这种参数有利有弊，利是降低了Remark阶段的停顿时间，弊的是在新生代对象很少的情况下也多了一次YGC，
				最可怜的是在AbortablePreclean阶段已经发生了一次YGC，然后在该阶段又傻傻的触发一次。
			所以利弊需要把握。
		6、并发清除 CMS concurrent sweep
			通过以上5个阶段的标记，老年代所有存活的对象已经被标记并且现在要通过Garbage Collector采用清扫的方式回收那些不能用的对象了。
			这个阶段主要是清除那些没有标记的对象并且回收空间；
		7、重置状态 等待下次CMS的触发。CMS concurrent reset。

	内存碎片问题
		CMS是基于标记-清除算法的，CMS只会删除无用对象，不会对内存做压缩，会造成内存碎片，这时候我们需要用到这个参数：
		-XX:CMSFullGCsBeforeCompaction=n
		意思是说在上一次CMS并发GC执行过后，到底还要再执行多少次full GC才会做压缩。
		默认是0，也就是在默认配置下每次CMS GC顶不住了而要转入full GC的时候都会做压缩。 
		如果把CMSFullGCsBeforeCompaction配置为10，就会让上面说的第一个条件变成每隔10次真正的full GC才做一次压缩。

2.主动Old GC
	这个主动Old GC的过程，触发条件比较苛刻：
    1.YGC过程发生Promotion Failed，进而对老年代进行回收
    	在进行Minor GC时，Survivor Space放不下，对象只能放入老年代，而此时老年代也放不下造成的，多数是由于老年带有足够的空闲空间，但是由于碎片较多，
    	新生代要转移到老年带的对象比较大,找不到一段连续区域存放这个对象导致的。
    2.比如执行了System.gc()，前提是没有参数ExplicitGCInvokesConcurrent
    3.其它情况...
	如果触发了主动Old GC，这时周期性Old GC正在执行，那么会夺过周期性Old GC的执行权（同一个时刻只能有一种在Old GC在运行），
	并记录 concurrent mode failure 或者 concurrent mode interrupted。
	主动GC开始时，需要判断本次GC是否要对老年代的空间进行Compact（因为长时间的周期性GC会造成大量的碎片空间），判断逻辑实现如下：
	在三种情况下会进行压缩：
	1.其中参数UseCMSCompactAtFullCollection(默认true)和 CMSFullGCsBeforeCompaction(默认0)，所以默认每次的主动GC都会对
	  老年代的内存空间进行压缩，就是把对象移动到内存的最左边。
    2.当然了，比如执行了System.gc()，前提是没有参数ExplicitGCInvokesConcurrent，也会进行压缩。
    3.如果新生代的晋升担保会失败。

	带压缩动作的算法，称为MSC，标记-清理-压缩，采用单线程，全暂停的方式进行垃圾收集，暂停时间很长很长...
	那不带压缩动作的算法是什么样的呢？
	不带压缩动作的执行逻辑叫Foreground Collect，整个过程相对周期性Old GC来说，少了Precleaning和AbortablePreclean两个阶段，其它过程都差不多。

	如果执行System.gc()，而且添加了参数ExplicitGCInvokesConcurrent，这时并不属于主动GC，它会推进周期性Old GC的进行，
		比如刚刚执行过一次，并不会等2s后检查条件，而是立马启动周期性Old GC。

过早提升与提升失败
在 Minor GC 过程中，Survivor Unused 可能不足以容纳 Eden 和另一个 Survivor 中的存活对象， 那么多余的将被移到老年代， 称为过早提升（Premature Promotion）,这会导致老年代中短期存活对象的增长， 可能会引发严重的性能问题。 再进一步，如果老年代满了， Minor GC 后会进行 Full GC， 这将导致遍历整个堆， 称为提升失败（Promotion Failure）。

早提升的原因
Survivor空间太小，容纳不下全部的运行时短生命周期的对象，如果是这个原因，可以尝试将Survivor调大，否则端生命周期的对象提升过快，导致老年代很快就被占满，从而引起频繁的full gc；
对象太大，Survivor和Eden没有足够大的空间来存放这些大对象。

提升失败原因
当提升的时候，发现老年代也没有足够的连续空间来容纳该对象。为什么是没有足够的连续空间而不是空闲空间呢？老年代容纳不下提升的对象有两种情况：
老年代空闲空间不够用了；
老年代虽然空闲空间很多，但是碎片太多，没有连续的空闲空间存放该对象。

解决方法

如果是因为内存碎片导致的大对象提升失败，cms需要进行空间整理压缩；
如果是因为提升过快导致的，说明Survivor 空闲空间不足，那么可以尝试调大 Survivor；
如果是因为老年代空间不够导致的，尝试将CMS触发的阈值调低。

CMS相关参数
-XX:+UseConcMarkSweepGC，默认false，老年代采用CMS收集器收集
-XX:+CMSScavengeBeforeRemark，默认false，The CMSScavengeBeforeRemark forces scavenge invocation from the CMS-remark phase (from within the VM thread as the CMS-remark operation is executed in the foreground collector).
-XX:+UseCMSCompactAtFullCollection，默认false，对老年代进行压缩，可以消除碎片，但是可能会带来性能消耗
-XX:CMSFullGCsBeforeCompaction=n，默认0，CMS进行n次full gc后进行一次压缩。如果n=0,每次full gc后都会进行碎片压缩。如果n=0,每次full gc后都会进行碎片压缩
–XX:+CMSIncrementalMode,默认false,并发收集递增进行，周期性把cpu资源让给正在运行的应用
–XX:+CMSIncrementalPacing,默认false,根据应用程序的行为自动调整每次执行的垃圾回收任务的数量
–XX:ParallelGCThreads=n,默认(ncpus <= 8) ? ncpus : 3 + ((ncpus * 5) / 8),并发回收线程数量
-XX:CMSIncrementalDutyCycleMin=n,默认0,每次增量回收垃圾的占总垃圾回收任务的最小比例
-XX:CMSIncrementalDutyCycle=n,默认10,每次增量回收垃圾的占总垃圾回收任务的比例
-XX:CMSInitiatingOccupancyFraction=n,jdk5默认是68%,jdk6默认92%,当老年代内存使用达到n%,开始回收。CMSInitiatingOccupancyFraction = (100 - MinHeapFreeRatio) + (CMSTriggerRatio * MinHeapFreeRatio / 100)
-XX:CMSMaxAbortablePrecleanTime=n,默认5000,在CMS的preclean阶段开始前，等待minor gc的最大时间。

总结
CMS收集器只收集老年代，其以吞吐量为代价换取收集速度。
CMS收集过程分为：初始标记、并发标记、预清理阶段、可终止预清理、重新标记和并发清理阶段。其中初始标记和重新标记是STW的。CMS大部分时间都花费在重新标记阶段，
可以让虚拟机先进行一次Young GC，减少停顿时间。CMS无法解决"浮动垃圾"问题。
由于CMS的收集线程和用户线程并发，可能在收集过程中出现"concurrent mode failure"，解决方法是让CMS尽早GC。
在一定次数的Full GC之后让CMS对内存做一次压缩，减少内存碎片，防止年轻代对象晋升到老年代时因为内存碎片问题导致晋升失败。
```



## 5.2.使用CMS垃圾回收器

```
jvm参数调优给出以下几条经验：
1：建议用64位操作系统，Linux下64位的jdk比32位jdk要慢一些，但是吃得内存更多，吞吐量更大。

2：XMX和XMS设置一样大，MaxPermSize和MinPermSize设置一样大，这样可以减轻伸缩堆大小带来的压力。

3：调试的时候设置一些打印参数，如-XX:+PrintClassHistogram -XX:+PrintGCDetails -XX:+PrintGCTimeStamps 
   -XX:+PrintHeapAtGC -Xloggc:log/gc.log，这样可以从gc.log里看出一些端倪出来。

4：系统停顿的时候可能是GC的问题也可能是程序的问题，多用jmap和jstack查看，或者killall -3 java，然后查看java控制台日志，能看出很多问题。
   有一次，网站突然很慢，jstack一看，原来是自己写的URLConnection连接太多没有释放，改一下程序就OK了。

5：仔细了解自己的应用，如果用了缓存，那么年老代应该大一些，缓存的HashMap不应该无限制长，建议采用LRU算法的Map做缓存，LRUMap的最大长度也要根据实际情况设定。

6：垃圾回收时promotion failed是个很头痛的问题，一般可能是两种原因产生，第一个原因是救助空间不够，救助空间里的对象还不应该被移动到年老代，
  但年轻代又有很多对象需要放入救助空间；第二个原因是年老代没有足够的空间接纳来自年轻代的对象；这两种情况都会转向Full GC，网站停顿时间较长。
  第一个原因我的最终解决办法是去掉救助空间，设置-XX:SurvivorRatio=65536 -XX:MaxTenuringThreshold=0即可，
  第二个原因我的解决办法是设置CMSInitiatingOccupancyFraction为某个值（假设70），这样年老代空间到70%时就开始执行CMS，年老代有足够的空间接纳来自年轻代的   对象。

7：不管怎样，永久代还是会逐渐变满，所以隔三差五重起java服务器是必要的，我每天都自动重起。

8：采用并发回收时，年轻代小一点，老年代要大一些，因为老年大用的是并发回收，即使时间长点也不会影响其他程序继续运行，网站不会停顿。

第一个JVM配置：
最小堆6000M最大堆6000，最小永久500M最大永久500M，删掉救助区，关闭虚拟机对class的垃圾回收功能，关闭system.gc，年轻代使用ParNew垃圾收集器，老年代使用CMS垃圾收集器，CMS开启压缩，执行fullgc 每次就进行压缩，开启对永久代的收集，开启并行收集器，内存的占用率达到90%开始GC，软引用对象引用后存活时间为0，打印出实例的数量以及空间大小，打印GC时的内存并且在程序结束时打印堆内存使用情况，每次GC时会打印程序启动后至GC发生的时间戳，每次GC时会分别打印回收前与回收后堆信息，将GC日志输出到指定位置

-Xms6000M -Xmx6000M -Xmn500M -XX:PermSize=500M -XX:MaxPermSize=500M -XX:SurvivorRatio=65536 -XX:MaxTenuringThreshold=0 -Xnoclassgc -XX:+DisableExplicitGC -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=0 -XX:+CMSClassUnloadingEnabled -XX:-CMSParallelRemarkEnabled -XX:CMSInitiatingOccupancyFraction=90 -XX:SoftRefLRUPolicyMSPerMB=0 -XX:+PrintClassHistogram -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintHeapAtGC -Xloggc:log/gc.log

说明一下， -XX:SurvivorRatio=65536 -XX:MaxTenuringThreshold=0就是去掉了救助空间；
-Xnoclassgc 禁用类垃圾回收，性能会高一点；
-XX:+DisableExplicitGC 禁止System.gc()，免得程序员误调用gc方法影响性能；
-XX:+UseParNewGC，对年轻代采用多线程并行回收，这样收得快；
带CMS参数的都是和并发回收相关的，不明白的可以上网搜索；
CMSInitiatingOccupancyFraction，这个参数设置有很大技巧，基本上满足(Xmx-Xmn)*(100-CMSInitiatingOccupancyFraction)/100>=Xmn就不会出现promotion failed。在我的应用中Xmx是6000，Xmn是500，那么Xmx-Xmn是5500兆，也就是年老代有5500兆，CMSInitiatingOccupancyFraction=90说明年老代到90%满的时候开始执行对年老代的并发垃圾回收（CMS），这时还剩10%的空间是5500*10%=550兆，所以即使Xmn（也就是年轻代共500兆）里所有对象都搬到年老代里，550兆的空间也足够了，所以只要满足上面的公式，就不会出现垃圾回收时的promotion failed；
SoftRefLRUPolicyMSPerMB这个参数我认为可能有点用，官方解释是softly reachable objects will remain alive for some amount of time after the last time they were referenced. The default value is one second of lifetime per free megabyte in the heap，我觉得没必要等1秒；

网上其他介绍JVM参数的也比较多，估计其中大部分是没有遇到promotion failed，或者访问量太小没有机会遇到，(Xmx-Xmn)*(100-CMSInitiatingOccupancyFraction)/100>=Xmn这个公式绝对是原创，真遇到promotion failed了，还得这么处理。

第二个JVM配置：
最小堆3000M最大堆3000,年轻代600M，最小永久500M最大永久500M，每个线程运行时栈的大小为256k，关闭system.gc，eden:s0:s1=1:4.5:4.5，年轻代使用ParNew垃圾收集器，老年代使用CMS垃圾收集器，CMS开启压缩，执行fullgc 每次就进行压缩，开启对永久代的收集，开启并行收集器，大内存分页大小128M，get和set方法转成本地代码，老年代的使用率达到阀值才会触发，内存的占用率达到70%开始GC，软引用对象引用后存活时间为0，打印出实例的数量以及空间大小，打印GC时的内存并且在程序结束时打印堆内存使用情况，每次GC时会打印程序启动后至GC发生的时间戳，每次GC时会分别打印回收前与回收后堆信息，将GC日志输出到指定位置

-Xmx3000M -Xms3000M -Xmn600M -XX:PermSize=500M -XX:MaxPermSize=500M -Xss256K -XX:+DisableExplicitGC -XX:SurvivorRatio=1 -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=0 -XX:+CMSClassUnloadingEnabled -XX:+CMSParallelRemarkEnabled -XX:LargePageSizeInBytes=128M -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:SoftRefLRUPolicyMSPerMB=0 -XX:+PrintClassHistogram -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintHeapAtGC -Xloggc:log/gc.log
注意了，解决停滞问题就是用UseParNewGC+CMS，解决promotion failed问题就是加大救助空间、加大年老代空间、设置CMSInitiatingOccupancyFraction。  
64位jdk似乎不能设置MaxTenuringThreshold，一旦设置MaxTenuringThreshold=0，CMS也有停滞。  
所以64位jdk参考下面这个设置，年老代涨得很慢，CMS执行频率变小，CMS没有停滞，也不会有promotion failed问题，内存回收得很干净。不过还是要灵活配置才行，只要理解了，总能找到适合自己应用的解决办法。 

第三个JVM配置：
最小堆4000M最大堆4000,年轻代600M，最小永久500M最大永久500M，每个线程运行时栈的大小为256k，关闭system.gc，eden:s0:s1=1:4.5:4.5，年轻代使用ParNew垃圾收集器，老年代使用CMS垃圾收集器，CMS开启压缩，执行fullgc 每次就进行压缩，开启对永久代的收集，开启并行收集器，大内存分页大小128M，get和set方法转成本地代码，老年代的使用率达到阀值才会触发，内存的占用率达到80%开始GC，软引用对象引用后存活时间为0，打印出实例的数量以及空间大小，打印GC时的内存并且在程序结束时打印堆内存使用情况，每次GC时会打印程序启动后至GC发生的时间戳，每次GC时会分别打印回收前与回收后堆信息，将GC日志输出到指定位置

-Xmx4000M -Xms4000M -Xmn600M -XX:PermSize=500M -XX:MaxPermSize=500M -Xss256K -XX:+DisableExplicitGC -XX:SurvivorRatio=1 -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=0 -XX:+CMSClassUnloadingEnabled -XX:+CMSParallelRemarkEnabled -XX:LargePageSizeInBytes=128M -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=80 -XX:SoftRefLRUPolicyMSPerMB=0 -XX:+PrintClassHistogram -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintHeapAtGC -Xloggc:log/gc.log
又有改进了，二的方法不太好，因为没有用到救助空间，所以年老代容易满，CMS执行会比较频繁。我改善了一下，还是用救助空间，但是把救助空间加大，这样也不会有promotion failed。
具体操作上，32位Linux和64位Linux好像不一样，64位系统似乎只要配置MaxTenuringThreshold参数，CMS还是有暂停。为了解决暂停问题和promotion failed问题，最后我设置-XX:SurvivorRatio=1 ，并把MaxTenuringThreshold去掉，这样即没有暂停又不会有promotoin failed，而且更重要的是，年老代和永久代上升非常慢（因为好多对象到不了年老代就被回收了），所以CMS执行频率非常低，好几个小时才执行一次，这样，服务器都不用重启了。
64位的配置，系统8G内存。

2和3的配置基本上很完善了，现在用3的配置，不用重启服务器了，只要程序没有内存泄漏，跑多久都没有问题
```



