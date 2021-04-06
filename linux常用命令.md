### 1.运行命令输出日志文件
```shell
0 标准输入 1 标准输出 2标准错误>xxx.log 2>&1 &1理解为标准输出引用
java -jar a.jar >xxx.log 2>&1 &
java -jar a.jar &>xxx.log &
```
### 2.查找文件
```shell
find / -name "ab"
locate ab
whereis ab 二进制查找
which ab path中查找文件
```

### 3.查找进行的
```shell
使用ps aux|grep nginx 查找到nginx的进程id
ls –al /proc/进程id  可以查看运行文件的起始位置。 
```

### 4.查看文件相关内容
```shell
lsof |grep deleted
linux都是以文件打开，lsof从所有打开的文件中查找，包含deleted的。
lsof包括端口等，都是以文件打开，-i:端口号可以看端口的打开。
```

### 5.清空buff/cache缓存
```shell
echo 1 > /proc/sys/vm/drop_caches
echo 2 > /proc/sys/vm/drop_caches
echo 3 > /proc/sys/vm/drop_caches
```

### 6.列出所有块信息（硬盘）
```shell
lsblk：查看block device 也就是逻辑磁盘的大小

df：查看的是file system 也就是文件系统层的磁盘大小

fdisk：是一个创建和维护分区表的程序
fdisk -l #发现待分区的磁盘 /dev/sdb
fdisk /dev/sdb #对该磁盘进行分区，输入m并回车   然后输入n是new新建分区的意思，输入p，输入1个分区并回车，采用默认会将所有分给/dev/sdb1 
#输入w 保存，并回车，对刚才结果进行保存
#再次执行fdisk -l查看分区的结果，新建的分区就可以使用了。
mkfs -t ext3 /dev/sdb1
mount /dev/sdb1 /data
#此时就可以使用df查看挂载情况，发现 /data上挂载了/dev/sdb1
df -h
#注意 fdisk最大只能分2T的空间
```

lsblk和df不一致：
```shell
#如果是ext{2,3,4}文件系统的话，可以用resize2fs 命令来更新。
resize2fs /dev/vda2

#如果是xfs文件系统的话，用xfs_growfs更新
xfs_growfs /dev/vda2
```



```shell
#查看已有硬盘设备
ls /dev/sd*  #只有以下几块硬盘, 但是我们不重启可以让系统识别新添加的硬盘
/dev/sda  /dev/sda1  /dev/sda2
```



```shell
#让Linux重新扫描新接硬盘
echo '- - -' > /sys/class/scsi_host/host0/scan 
echo '- - -' > /sys/class/scsi_host/host1/scan 
echo '- - -' > /sys/class/scsi_host/host2/scan
```



```shell
ls /dev/sd*   #看！sdb识别出来了
/dev/sda  /dev/sda1  /dev/sda2  /dev/sdb
```



```shell
#对新扫描出来的磁盘进行分区
fdisk /dev/sdb
```



```shell
#修改磁盘分区表后无需重启，用partx通知内核读入新的分区 /dev/sdb   
partx -a /dev/sdb
#-a add
#-d delete
#-s show
```



```shell
#通知系统分区表的变化，使用fdisk或者其他命令创建一个新的分区，然后使用partprobe命令重新读取分区表。
#用于重读分区表，当出现删除文件后，仍然占用空间，可以使用partprobe不重启情况下重新读分区。
partprobe partprobe /dev/sdb
-d 不更新内核
-s 显示磁盘分区汇总信息
```



### 7.使用cat将内容追加或写入文件

```shell
追加：cat >>a.log<<EOF … EOF  cat <<EOF>>a.log … EOF
覆盖：cat >a.log<<EOF … EOF  cat <<EOF>a.log … EOF
```

### 8.查看最后重启时间
```shell
last reboot
who –b
```

### 9.挂载解挂
```shell
umount -l /test1/test1
mount -t nfs 192.168.1.103:/test/test /test1/test1
```

### 10.关机重启
```shell
reboot
shutdown –r 10/20:34 10分钟关机

halt
poweroff
shutdown –h now 立马关机
shutdown –h 10 10分钟后关机
```

### 11.开机启动
```shell
方法1：
1、设置开机启动脚本
vi /start.sh
#!/bin/bash
/mysql/bin/mysql start
2、修改/etc/rc.local
/bin/bash /start.sh >a.log 2>&1

方法2：
vi /etc/init.d/test
#!/bin/bash
# chkconfig: 3 88 88
/bin/bash /start.sh >/dev/null 2>/dev/null

#chkconfig可以管理程序开机是否自启动、在哪些模式下启动，如果将3（2345）改为-，则代表所有模式都不开机启动。 
其后的88和88分别是系统启动时对每个服务启动和关闭的顺序。因为系统的服务是有依赖的，比如说sshd服务，需要网络服务，如果在网络服务还没有启动起来的时候就启动sshd服务，那肯定会导致sshd服务启动不起来，同样，在关机时候如果先关掉网络服务，sshd将会因为网络的中断而导致未知错误，因此要定义服务启动顺序。在自定义脚本时候，尽量将第一个数写大，第二个数写小，这样在保证所有其他服务开启后，启动我们自定义的脚本，关机时候我们自定义的服务先关掉后再关掉其他服务。


chmod +x /etc/init.d/test

chkconfig --add test
chkconfig --list test 
关闭
chkconfig test off
删除
chkconfig --del test
```

### 12.查询最近10天修改过的文件
```shell
BACKUPFILE=backup-$(date +%m-%d-%Y)
archive=${1:-$BACKUPFILE}
tar cvf - `find . -mtime -10 -type f -print` > $archive.tar
  # -mtime -10表示那些最近10*24小时内被修改的文件
  # -type f表示一般的文件
  # -print 在标准输出中打印完整的文件名
  
gzip $archive.tar
echo "Directory $PWD backed up in archive file \"$archive.tar.gz\"."
exit 0

# 上边代码,如果在发现太多的文件的时候,或者是如果文件
# 名包括空格的时候,将执行失败.

#=====================建议使用下面的方法======================

#  find . -mtime -1 -type f -print0 | xargs -0 tar rvf "$archive.tar"
#  -print0  在标准输出中打印完整的文件名,随后跟一个null字符,
#  对应了xargs命令中的‘-0’选项.
#  使用gnu版本的find.
#  find . -mtime -1 -type f -exec tar rvf "$archive.tar" '{}' \;
#  对于其他风格的UNIX便于移植,但是比较慢.
```

### 13.查询文件行数
```shell
wc file1 
20 200 2000 a.txt  #行数 字数 字节数
行数-l	 字数-w		字节数-c
wc -l file2 显示文件行数
```

### 14.单个文件拆成多个
```shell
split –l 100 coco.log –d –a 4 coco_

-i:指定每个小文件的行数
-d:按照数字命令小文件后缀
-a:指定数字后缀的位数
```

### 15.查找进程并关闭
```shell
ps -ef|grep python |awk '{print $2}' |xargs kill -9
awk ：文本分析工具
awk '{print $1,$4}' log.txt 每行按照空格或tab分割，输出分割文本的1、4项
awk -F,  '{print $1,$2}'  log.txt 使用逗号分割
awk -F '[  ,]'  '{print $1,$2}'  log.txt 使用多个分隔符进行分割，先使用空格分割，再使用逗号对分割结果二次分割。
awk  -f  cal.awk  log.txt
awk  '$1>2' log.txt  打印第一列大于2的行
awk  '$2  ~  /th/  {print $2,$4}'  log.txt 正则匹配 第二个分割包含th字符串 ~是模式开始 //中间是要匹配的内容
awk  '/re/'  log.txt 输出包含re的行
awk  'BEGIN{IGNORECASE=1}  /re/'  log.txt 忽略大小写 包含re的行

xargs ：
cat test.txt | xargs 多行输入单行输出	
cat test.txt | xargs  -n3 多行输入单行 3 个元素输出
ls *.jpg | xargs –n1 –I {} cp {} /data/images 把当前路径下所有图片文件复制到/data/images下
find .  -type  f  -name  "*.log"  -print0 |xargs  -0  rm  -f     -0是界定符
统计一个源代码目录中所有php文件行数
find .  –type f  -name "*.php" –print0 | xargs  -0  wc  -l
find . –type f –name "*.jpg" –print | xargs tar –czvf images.tar.gz
cat url-list.txt | xargs wget -c
```

### 16.Linux常用监控工具
```shell
top： 看5分 10分 15分的负载情况。
  top - 当前时间 系统上线时间 当前登录用户数 系统负载（任务队列平均长度）
  tasks： 进程总数 正在运行进程数 休眠进程数 停止进程数 僵尸进程数
  %cpu： 用户空间占比 内核空间占比 改变过优先级的进程占比 空闲CPU占比 IO等待占比 硬中断占比 软中断占比
  KiB： mem 物理内存总量 空闲内存总量 已使用物理内存总量 内核缓存内存量
  Kib swap： 交换区总量 空闲交换区总量 使用的交换区总量
  PID(进程ID) USER(用户) PR(优先级) NI(Nice值) VIRT(虚拟内存) RES(常驻内存) SHR-S(共享内存) %CPI %MEM TIME+ COMMAND
  
htop：一个交互进程浏览器，完全替代top。但是需要额外安装。。。。。

iostat：iostat -d -k 2 只显示磁盘信息，每2秒一次刷新频率
  -d显示磁盘  -c 显示cpu
  –k以kb显示 
  2刷新频率 2是2秒，如果是2个数，1 2 这样连续2个数是1秒执行1次，连续2次

**iotop：查看磁盘的IO情况，写和读的速度**

ip a：查看网卡

iftop：用来监控硬盘的IO使用情况，具体到每个进程使用IO的情况流量和带宽监控工具，
	  可查看实时网络流量，监控TCP/IP连接等。
iftop 命令常用的参数如下，这里做个小规模总结：
-i 设定监测的网卡，如：# iftop -i eth1
-B 以 bytes 为单位显示流量 (默认是 bits)，如：# iftop -B
-n 使 host 信息默认直接都显示 IP，如：# iftop -n
-N 使端口信息默认直接都显示端口号，如: # iftop -N
-F 显示特定网段的进出流量，如 # iftop -F 10.10.1.0/24 或 # iftop -F 10.10.1.0/255.255.255.0
-h（ display this message ），帮助，显示参数信息
-p 使用这个参数后，中间的列表显示的本地主机信息，出现了本机以外的 IP 信息;
-b 使流量图形条默认就显示;

再来总结一下，进入 iftop 视图画面后的一些操作命令 (注意大小写)：
按 h切换是否显示帮助;
按 n切换显示本机的 IP 或主机名;
按 s切换是否显示本机的 host 信息;
按 d切换是否显示远端目标主机的 host 信息;
按 t切换显示格式为 2 行 /1 行 /只显示发送流量 /只显示接收流量;
按 N切换显示端口号或端口服务名称;
按 S切换是否显示本机的端口信息;
按 D切换是否显示远端目标主机的端口信息;
按 p切换是否显示端口信息;
按 P切换暂停 /继续显示;
按 b切换是否显示平均流量图形条;
按 T切换是否显示每个连接的总流量;
按 l打开屏幕过滤功能，输入要过滤的字符，比如 ip,按回车后，屏幕就只显示这个 IP 相关的流量信息;
按 L切换显示画面上边的刻度;刻度不同，流量图形条会有变化;
按 j或按k可以向上或向下滚动屏幕显示的连接记录;
按 1或2或3可以根据右侧显示的三列流量数据进行排序;
按<根据左边的本机名或 IP 排序;
按>根据远端目标主机的主机名或 IP 排序;
按o切换是否固定只显示当前的连接;
```

### 17.定时备份Mysql

```shell
#新建一个脚本： backup.sh

#!/bin/bash
#!/usr/bin/expect
mysqldump -uroot -p123456 --all-databases |gzip >/backup/backup`date +%Y%m%d`.gz
expect -c "
    spawn scp -r /backup/backup`date +%Y%m%d`.gz root@192.168.1.101:/buckup/mysqlback/
    expect {
        \"*assword\" {set timeout 20; send \"远程主机的密码\r\"; exp_continue;}
    }
expect eof"

#expect是一个自动化交互套件，主要应用于执行命令和程序时，系统以交互形式要求输入指定字符串，实现交互通信。
#expect自动交互流程：
#spawn启动指定进程---expect获取指定关键字---send向指定程序发送指定字符---执行完成退出.
#注意该脚本能够执行的前提是安装了expect
yum install -y expect

spawn               交互程序开始后面跟命令或者指定程序
expect              获取匹配信息匹配成功则执行expect后面的程序动作
send exp_send       用于发送指定的字符串信息
exp_continue        在expect中多次匹配就需要用到
send_user           用来打印输出 相当于shell中的echo
exit                退出expect脚本
eof                 expect执行结束 退出
set                 定义变量
puts                输出变量
set timeout         设置超时时间

crontab -e
*/5 * * * * sh /mysqlback/mysqlbackup.sh > /dev/null 2>&1 &
cron start
#chkconfig --add cron
```

### 18.查看某个文件夹下文件数量

```shell
find -type f | wc -l
```

### 19.查看某个文件夹下文件夹数量

```shell
find -type d | wc -l
#不计算隐藏文件夹数量
find . -type d ! -name ".*" | wc -l
```

### 20.设置代理

```shell
#修改/etc/profile
vi /etc/profile
export http_proxy=http://ipaddress:8080/
export https_proxy=http://ipaddress:8080/
export ftp_proxy=http://ipaddress:8080/

#不需要走代理的配置的 只是在host中配置的
export no_proxy= 'a.test.com,127.0.0.1,2.2.2.2'
```

