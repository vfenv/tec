## 5.3.ParNew收集器

```
ParNew收集器是Serial收集器的多线程版本，许多运行在Server模式下的虚拟机中首选的新生代收集器，除Serial外，只有它能与CMS收集器配合工作。
除了使用多条线程进行垃圾收集之外，其余行为包括Serial收集器可用的所有控制参数（例如：-XX:SurvivorRatio、 -XX:PretenureSizeThreshold、-XX:HandlePromotionFailure等）、收集算法、Stop The World、对象分配规则、回收策略等都与Serial收集器完全一样，实现上这两种收集器也共用了相当多的代码。

在JDK 1.5中使用CMS来收集老年代的时候，新生代只能选择ParNew或Serial收集器中的一个。ParNew收集器也是使用 -XX: +UseConcMarkSweepGC选项后的默认新生代收集器，也可以使用 -XX:+UseParNewGC选项来强制指定它。
ParNew收集器在单CPU的环境中绝对不会有比Serial收集器更好的效果，甚至由于存在线程交互的开销
当然，随着可以使用的CPU的数量的增加，它对于GC时系统资源的利用还是很有好处的。它默认开启的收集线程数与CPU的数量相同，在CPU非常多（譬如32个，现在CPU动辄就4核加超线程，服务器超过32个逻辑CPU的情况越来越多了）的环境下，可以使用-XX:ParallelGCThreads参数来限制垃圾收集的线程数

从ParNew收集器开始，后面还将会接触到几款并发和并行的收集器。在大家可能产生疑惑之前，有必要先解释两个名词：并发和并行。这两个名词都是并发编程中的概念，在谈论垃圾收集器的上下文语境中，他们可以解释为
并行（Parallel）：指多条垃圾收集线程并行工作，但此时用户线程仍然处于等待状态。 
并发（Concurrent）：指用户线程与垃圾收集线程同时执行（但不一定是并行的，可能会交替执行），用户程序继续运行，而垃圾收集程序运行于另一个CPU上。
```

## 5.4.Parallel Scavenge收集器

```
Parallel Scavenge收集器是一个新生代并行收集器，它也是使用复制算法的收集器，优势在于使用多线程去完成垃圾清理工作。
Parallel Scavenge收集器的目标则是达到一个可控制的吞吐量
所谓吞吐量就是CPU用于运行用户代码的时间与CPU总消耗时间的比值，即吞吐量 = 运行用户代码时间 /（运行用户代码时间 + 垃圾收集时间），虚拟机总共运行了100分钟，其中垃圾收集花掉1分钟，那吞吐量就是99%。
Parallel Scavenge收集器提供了两个参数用于精确控制吞吐量，分别是：
控制最大垃圾收集停顿时间的-XX:MaxGCPauseMillis参数，
直接设置吞吐量大小的-XX:GCTimeRatio参数 0-100。
参数-XX:+UseAdaptiveSizePolicy值得关注，这是一个开关参数，当这个参数打开之后，就不需要手工指定新生代的大小（-Xmn）、Eden与Survivor区的比例（-XX:SurvivorRatio）、晋升老年代对象年龄（-XX:PretenureSizeThreshold）等细节参数了，虚拟机会根据当前系统的运行情况收集性能监控信息，动态调整这些参数以提供最合适的停顿时间或者最大的吞吐量，这种调节方式称为GC自适应的调节策略（GC Ergonomics）。
只需要把基本的内存数据设置好（如-Xmx设置最大堆），然后使用MaxGCPauseMillis参数（更关注最大停顿时间）或GCTimeRatio（更关注吞吐量）参数给虚拟机设立一个优化目标，那具体细节参数的调节工作就由虚拟机完成了。

自适应调节策略也是Parallel Scavenge收集器与ParNew收集器的一个重要区别。
```



## 5.5.G1收集器

```shell
G1（garbage—first）是JVM中的一种垃圾回收器，适用于多核、大内存的服务端，garbage-first意思是总是优先回收价值最大的区域。
-XX:+UseG1GC #启用G1收集器
-Xmx32g #设置堆内存
-XX:MaxGCPauseMillis=200 #设置GC的最大暂停时间为200ms
-XX:G1HeapRegionSize=16M #设置reigon区域大小，1-32M之间，划分2048个region
-XX:G1NewSizePercent=5   #设置young代的堆空间占比，default：5%
-XX:NewRatio=2   #设置young与old的比率，default:2
-XX:SurvivorRatio=8 #设置Eden与Survivor的比率，default:8

java -Xmx4g -Xms4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:InitiatingHeapOccupancyPercent=45 -jar demo.jar


#为什么要使用G1？
G1的出现是为了替换CMS
低延迟：G1能充分利用硬件优势（多CPU、多核）来缩短Stop-The-World，很多情况下可以通过并发的方式让程序继续执行。
算法优化：CMS使用的mark-sweep标记清除算法会产生内存碎片，G1使用copying算法整理内存，复制的时候完成了堆压缩，不产生内存碎片，有利于程序长时间运行，分配大对象不会因为内存空间不足而提前触发下一次GC。
可调控GC：G1能建立可预测的停顿时间模型，设定在M毫秒的时间段内，垃圾回收时间不超过N毫秒。G1不是一个实时收集器，对gc停顿时间的设定并不绝对生效，只是保证高概率在停顿时间模型中完成gc，尽可能满足参数设定。

#G1为什么可以建立可预测的停顿时间模型？
G1有计划地避免在整个堆中进行全区域的垃圾收集，G1通过优先列表计算各个region里面的垃圾堆积价值（回收获得的空间以及回收所需时间），每次根据允许的收集时间，优先回收价值最大的Region（Garbage-First名称的来由）。G1使用region划分内存空间和设定优先级的回收方式，保证了有限时间内能高效率的回收垃圾，和程序的长时间运行。

#什么是region
Serial GC，Parallel GC，CMS GC将整个堆按年代划分，进行分代回收。G1依然是分代垃圾回收器，G1将堆划分为2048个region（Region的取值范围为1M~32M，且应为2的N次幂，所以Region的大小只能是1M、2M、4M、8M、16M、32M），如果没有显示指定，则G1会计算出一个合理的大小(堆大小/2048)，每个region从属不同的年代（注意：region并不固定属于某个年代，有时候属于young，有时候属于old，根据其保存对象来决定），每个年代都是一部分region的集合。
G1能管理的最大堆内存是2048*32M=64G。

#region的分类
每个Region可被标记成E、S、O、H，分别表示Eden、Survivor、Old、Humongous。其中E、S属于年轻代，O与H属于老年代。
H表示Humongous，从字面上就可以理解表示大的对象（下面简称H对象）。

#card（卡）和card table （卡表）
每个region由若干个Card(512byte，card是堆内存最小可用粒度)构成，一个对象通常会占用一个region的若干个card，GC就是对region的card进行处理。
region的所有card记录在Card Table(byte[])中，通过byte[]下标保存card的地址，每个card默认未被引用，当一个card被引用时，值设为0。

#Rset（Remembered Set）
每个Region都有一个Rset记录其他region对本region的所有引用（谁引用了我）。通过扫描本region的RSet，来确定对region内的对象进行引用的对象是否存活，进而确定region内对象的存活情况。
Rset底层是Hash table，Key是region的起始地址，Value是Card Table卡表，卡表下标是card卡的地址，存放值为0表示被引用。

G1通过一个增量式的完全标记并发算法，计算region的活跃度，得到准确的region引用信息，不进行整堆扫描（整堆扫描效率低）。
#写屏障(Write Barrier)
效果类似AOP的前后置通知（方法增强），eg：写前屏障pre-write barrier 。write barrier通过一定的性能开销来跟踪和记录对象及其引用，批处理更新到Rset中。
就是对一个对象引用进行写操作（即引用赋值）之前或之后附加执行的逻辑。
#并发优化线程(Concurrence Refinement Threads)
写后屏障会将跨region的引用更新加入缓冲区，并发优化线程永远活跃，一旦发现全局列表有记录存在，就开始并发处理。

#point-out
在CMS中，Rset记录老年代指向新生代的引用。Young GC扫描根时，只需扫描Rset，而不需要扫描整个老年代。
#point-in
G1因为region数量太多，有些不需要GC的分区引用会被扫描，point-out会造成大量的扫描浪费。G1使用point-in将当前分区的对象作为根来扫描，没有关联的分区不会被扫描，以此避免无效扫描。
	
#Humongous区
G1设置Humongous区域存储巨型对象（大小超过region50%以上），如果一个Humongous装不下，会寻找连续的Humongous区来存储，找不到能存放巨型对象的连续Humougous区域会强制Full GC。
	#注意：
	#巨型对象的复制会影响gc效率，并发标记期间非存活的巨型对象会被直接回收；
	#G1如果发现没有引用指向巨型对象，该对象不需等待到老年代收集周期，在年轻代收集周期中就会被回收。

#PLAB本地分配缓冲区
每个GC线程都有一个PLAB(Promotion Local Allocation Buffer) 。
在young gc中：
Eden到Survivor的晋升Promotion
Survivor到Old的晋升
MaxTenuringThreshold  来设定晋升年龄 default：15 

#Snapshot-At-The-Beginning(SATB)
SATB之前先了解下三色标记法：三色标记算法用来描述追踪式回收器，利用它可以推演回收器的正确性。 三色标记算法将对象分成三种类型：
	黑色:根对象，该对象与它的子对象都会被扫描
	灰色:对象本身被扫描,但还没扫描完该对象中的子对象
	白色:未被扫描对象，扫描完成所有对象之后，最终为白色的为不可达对象，即垃圾对象
CMS采用的是增量更新（Incremental update），只要在写屏障（write barrier）里发现要有一个白对象的引用被赋值到一个黑对象 的属性中，那就把这个白对象变成灰色的。即插入的时候记录下来。
G1中使用的是STAB（snapshot-at-the-beginning）并发标记阶段使用的增量式标记算法。并发标记是并发多线程的，但并发线程在同一时刻只扫描一个分区。SATB表示GC开始前对存活对象保存快照，并发标记时标记所有快照中当时的存活对象，标记过程中新分配的对象也会被标记为存活对象，删除的时候记录所有对象。

#G1的内存分配策略
1.G1默认使用TLAB(Thread Local Allocation Buffer)线程本地分配缓冲区，直到空间不足
2.Eden分配
	eden对象动到Survivor，当Survivor空间不够的时候会直接晋升到Old区。
	#查看相关的GC日志:-XX:+PrintGCDetails  -XX:+PrintGCTimeStamps
3.对象将进入老年代
	1,大对象直接进入老年代而不是分配到新生代。
		-XX:PretenureSizeThreshold
		将体积大于设定值的对象直接在老年代分配。防止Eden区及两个Survivor之间发生大量的内存复制。
	2,default：age>15 进入老年代
		-XX:MaxTenuringThreshold=15      设定晋升老年代的年龄阈值young到old的岁数,default:15
		对象从Eden复制到一个survivor，年龄为1，之后每一次Minor GC，age++
	
	注意：当有大批同龄对象占用空间超过Survivor的一半时，这批同龄对象可直接晋升，避免gc的大量复制操作出现。

#G1的gc方式
G1主要有作用于年轻代的Young gc，全堆扫描Full gc和混合收集Mix gc。
IHOP阈值：-XX:InitiatingHeapOccupancyPercent=45 设置IHOP阈值 default：45  heap中占用超过45%，触发mix gc

#--------------1.Young GC（未达到IHOP阈值）
Young GC 回收的是所有年轻代的Region，它在Eden空间耗尽时被触发。Eden空间的数据移动到新的Survivor空间中，如果Survivor空间不够，Eden空间的部分数据会直接晋升到老年代空间。旧Survivor区数据也移动到新的Survivor区中，也有部分数据晋升到老年代空间中。最终Eden空间的数据为空，GC停止工作，应用线程继续执行。

G1将堆分成多个Resion，每个Region不可能独立，其中存储的对象可能被其他任意region中对象引用，这些Region可能 Old或者Edon，这样一来YGC时就要扫描所有Region，包括Old和Eden的Region，这样相当于做了一个全堆扫描，降低YGC效率。

G1收集器使用RememberSet来避免全局扫描，每个Region创建时都对应一个Rset，每次Reference类型数据写操作时，都会产生一个Write Barrier暂时中断操作，然后检查要写入的引用指向的对象是否和该reference类型数据在不同的Region(检查老年代是否引用了新生代中的对象)，如果老年代引用了新生代，通过CardTable把相关引用信息记录到引用指向对象在所在Region的RSet中。当垃圾收集时，GCRoots的根节点的枚举范围加入RSet,可以保证非全局扫描也不会有遗漏。

#G1-Card Table和Remember Set
这两个数据结构是专门用来处理Old区到Young区的引用。
Young区到Old区的引用则不需要单独处理，因为Young区中的对象本身变化比较大，没必要浪费空间去记录下来。

RSet：全称Remembered Sets, 用来记录外部指向本Region的所有引用，每个Region维护一个RSet。
	 RSet本质上是一种哈希表，Key是Region的起始地址，Value是Card table(字节数组)，字节数组下标表示Card的空间地址，
	 当该地址空间被引用的时候会被标记为dirty_card。
	 RSet需要记录的东西应该是 xx Region的 xx Card。
Card:JVM将内存划分成了固定大小的Card（堆内存最小可用粒度）。这里可以类比物理内存上page的概念。
	 每个card默认的大小为512byte

每个Region被分成了若干个Card(一个对象通常会占用一个region的若干个card),region的所有card记录在card table<byte[]>中，通过byte[]下标保存card地址。

#Object young = new Object();
#old.p = young;
#java层面给老年代对象赋值年轻代对象之后，jvm底层会执行oop_store方法，实现位于oop.inline.hpp类中。
#c底层update_barrier_set，在赋值动作的前后，JVM插入一个pre-write barrier和post-write barrier，其中post-write barrier的最终动作如下：
1.找到该字段所在的位置(Card)，并设置为dirty_card
2.如果当前是应用线程，每个Java线程有一个dirty card queue，把该card插入队列
3.除了每个线程自带的dirty card queue，还有一个全局共享的queue
赋值动作到此结束，接下来的RSet更新操作交由多个ConcurrentG1RefineThread并发完成，每当全局队列集合超过一定阈值后，ConcurrentG1RefineThread会取出若干个队列，遍历每个队列中记录的card，并进行处理，位于G1RemSet::refine_card方法，大概实现逻辑如下：
1、根据card的地址，计算出card所在的Region
2、如果Region不存在，或者Region是Young区，或者该Region在回收集合中，则不进行处理
3、最终使用闭合函数G1UpdateRSOrPushRefOopClosure::do_oop_nv()的处理该card中的对象
其中_from是持有引用的对象所在的Region，to是引用对象所在的Region，通过add_reference方法加入到RSet中，更细节的实现在OtherRegionsTable::add_reference方法中。

Young GC的各个阶段：
	1：根扫描
		  静态和本地对象被扫描，如果有大量加载的类，可能会带来额外的等待时间。
	2：更新RS
		  并发优化线程处理dirty card队列，扫描有引用的region，更新RSet
	3：处理RS
		  检测从年轻代指向年老代的对象
	4：对象拷贝
		  拷贝存活的对象到survivor/old区域
	5：处理引用队列（对象地址变化了，引用值也要跟着变）
	      软引用，弱引用，虚引用处理

#--------------2.G1 Mix GC（达到IHOP阈值时,也就是达到默认的45%）
Mix GC进行正常的新生代垃圾收集和部分老年代region的收集。
它的GC步骤分2步：
1. 全局并发标记（global concurrent marking）

#初始标记（initial mark，stop-the-world）
G1对根进行标记，当达到IHOP阈值（default：45%）时，G1不会立即开始并发标记，而是等待并利用下一次年轻代收集的STW时间段完成初始标记，这种方式称为借道(Piggybacking)，这样减少了额外的单独的停顿时间。
#根区域扫描（root region scan）
在初始标记或新生代收集中被拷贝到survivor分区的对象，都需要被看做是根，survivor分区就是根分区，G1开始扫描survivor分区，所有被survivor分区所引用的对象都会被扫描到并将被标记。
#并发标记（Concurrent Marking）
识别高价值的老年代region，使用tract算法寻找所有存活对象，记录标记时引用发生改变的对象，这里主要使用了SATB（snapshot-at-the-beginning），标记前拍个快照，如果某个对象的引用发生变化，就通过pre-write barrier logs将该对象的旧值记录在一个SATB缓冲区中，如果这个缓冲区满了，就把它加到一个全局的列表中——G1会有并发标记的线程定期去处理这个全局列表。
	#-XX:ParallelGCThreads=8   设置stop-the-world工作线程数，通常和cpu数量一致（max=8），cpu大于8核时，设置为5/8
	#-XX:ConcGCThreads=10   设置并行标记的线程数为10,default:ParallelGCThreads的1/4。
#重新标记（Remark，stop-the-world）
标记那些在并发标记阶段发生变化的对象，帮助完成标记周期，stop-the-world。G1 GC 清空 SATB 缓冲区，跟踪未被访问的存活对象，并执行引用处理。标记会计算字节数并计入region，形成垃圾的价值（价值作为回收优先度的参考）。
#清除垃圾（Cleanup，stop-the-world）
在这个最后阶段，识别出所有空闲的分区、RSet梳理、将不用的类从metaspace中卸载、回收巨型对象等。识别出每个分区里存活的对象有个好处是遇到一个完全空闲的region时，可以立即清理region的Rset，同时这个region可以立刻被回收并加入到空闲队列中，而不需要再放入CSet等待混合收集阶段回收（此操作可能并发）；梳理RSet有助于发现无用的引用。

2. 拷贝存活对象（evacuation）
evacuation负责把一部分region里的存活对象拷贝到空region中，回收原region的空间。
部分region的选取（Collection set）取决于停顿时间模型，在一定范围内优先回收价值最高的region，以尽量保证程序运行开销。
	#-XX:MaxGCPauseMillis=200   设置GC的最大暂停时间为200ms
在复制存活对象过程中，会面临内存不足导致无法转移的问题，这个叫转移失败(Evacuation Failure)。转移失败是evacuation无法在堆空间中申请新的region（内存不足），G1被迫执行Full GC（stop-the-world，标记清除压缩算法），对整个堆扫描回收，效率极低。

在发生Minor GC之前，虚拟机会先检查老年代最大可用的连续空间是否大于新生代所有对象总空间，
	成立，确保Minor GC安全。
	不成立，是否允许担保失败？
	#HandlePromotionFailure   设置是否担保失败
		允许，检查老年代最大可用的连续空间是否大于历次晋升到老年代对象的平均大小
			大于，尽管Minor GC有风险，依然尝试进行一次Minor GC；
			小于，或者HandlePromotionFailure设置不担保失败，进行full gc。

#--------------3.触发Full GC
Full GC触发条件：
    从年轻代分区拷贝存活对象时，无法找到可用的空闲分区
    从老年代分区转移存活对象时，无法找到可用的空闲分区
    分配巨型对象时在老年代无法找到足够的连续分区
    并发处理完成之前，内存空间已经耗光（gc速度赶不上申请内存的速度）

通过to-space-exhausted、Evacuation Failure查看相关日志，做到尽量避免Full gc的出现（mix gc降级变成使用单线程的serial gc，效率低）。
#-XX:G1ReservePercent(默认10%)可以保留空间，来应对晋升模式下的异常情况，最大占用整堆50%。
#-XX:InitiatingHeapOccupancyPercent=35 减少IHOP值提前启动标记周期
#-XX:ConcGCThreads  增加并发线程数量
    
在某些情况下，G1触发了Full GC，这时G1会退化使用Serial收集器来完成垃圾的清理工作，它仅仅使用单线程来完成GC工作，GC暂停时间将达到秒级别的。整个应用处于假死状态，不能处理任何请求，我们的程序当然不希望看到这些。那么发生Full GC的情况有哪些呢？
1、并发模式失败
	G1启动标记周期，但在Mix GC之前，老年代就被填满，这时候G1会放弃标记周期。
	这种情形下，需要增加堆大小，或者调整周期（例如增加线程数-XX:ConcGCThreads等）。
2、晋升失败或者疏散失败
	G1在进行GC的时候没有足够的内存供存活对象或晋升对象使用，由此触发了Full GC。可以在日志中看到(to-space exhausted)或者（to-space overflow）。
	解决这种问题的方式是：
		a,增加 -XX:G1ReservePercent 选项的值（并相应增加总的堆大小），为“目标空间”增加预留内存量。
		b,通过减少 -XX:InitiatingHeapOccupancyPercent 提前启动标记周期。
		c,也可以通过增加 -XX:ConcGCThreads 选项的值来增加并行标记线程的数目。
3、巨型对象分配失败
	当巨型对象找不到合适的空间进行分配时，就会启动Full GC，来释放空间。这种情况下，应该避免分配大量的巨型对象，
	增加内存或者增大-XX:G1HeapRegionSize，使巨型对象不再是巨型对象。
	

G1的调优:

调优主要是为了避免Full gc，首先要学会打印gc日志
	-XX:+UseG1GC 
	-XX:+PrintGCDetails 
	-XX:+PrintGCDateStamps

#young gc调优
1.调整收集停顿时间
-XX:MaxGCPauseMillis=200 
减小数值会触发更多的young gc，带来一系列的问题，导致吞吐量受影响
尽可能根据项目实际生产环境来调试，刚开始建议尽量犯错，通过日志来调整，找到最合适的值。
#mix gc调优
1.调整IHOP阈值
-XX:InitiatingHeapOccupancyPercent default：45
参数调小，会提前(想对参数较大)触发mix gc周期，频繁进行并发收集会浪费CPU资源（gc没有垃圾可回收导致cpu做无用功）。
参数太高，导致转移空间不足，频繁发生Full gc。
2.调整并发线程数
-XX:ConcGCThreads 
mix gc周期过长，可增加标记线程的数量提高效率，注意线程数量不是越多越好
3.调整混合收集次数
-XX:G1MixedGCCountTarget=8 default：8 并发周期中，最多经历几次混合收集周期
参数调小，会增加每次混合收集的region数量，导致stop-the-world时间增加
4.调整垃圾分区的比例
-XX:G1MixedGCLiveThresholdPercent 存活对象对region占比

#注意不要设置新生代的大小，会覆盖暂停时间
#-XX:NewRatio=2 设置young与old的比率，default:2

调优一定要根据具体情况分析，没有万能的参数设置，生产环境中有了问题再去调整，学习过程中可以多犯错，久病良医。
```
