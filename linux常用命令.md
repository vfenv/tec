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
lsblk：查看block device 也就是逻辑磁盘大小
df：查看的是file system 也就是文件系统层磁盘大小
fdisk：这个还没看
```

lsblk和df不一致：
```shell
如果是ext{2,3,4}文件系统的话，可以用resize2fs 命令来更新。
resize2fs /dev/vda2
如果是xfs文件系统的话，用xfs_growfs更新
xfs_growfs /dev/vda2
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

chmod +x /etc/init.d/test

chkconfig - -add test
chkconfig - -list test 
关闭
chkconfig test off
删除
chkconfig - -del test
```

### 12.查询最近10天修改过的文件
```shell
BACKUPFILE=backup-$(date +%m-%d-%Y)
archive=${1:-$BACKUPFILE}
tar cvf - `find . -mtime -10 -type f -print` > $archive.tar
#+ -mtime -10表示那些最近10*24小时内被修改的文件,
#+ -type f表示一般的文件,
#+ -print 在标准输出中打印完整的文件名
gzip $archive.tar
echo "Directory $PWD backed up in archive file \"$archive.tar.gz\"."
#上边代码,#+ 如果在发现太多的文件的时候,或者是如果文件
#名包括空格的时候,将执行失败.
#=====================建议使用下面的方法======================
#  find . -mtime -1 -type f -print0 | xargs -0 tar rvf "$archive.tar"
#-print0 在标准输出中打印完整的文件名,随后跟一个null字符,
#对应了xargs命令中的‘-0’选项.
#使用gnu版本的find.
#find . -mtime -1 -type f -exec tar rvf "$archive.tar" '{}' \;
#对于其他风格的UNIX便于移植,但是比较慢.
exit 0
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
find .  -type  f  -name  “*.log”  -print0 |xargs  -0  rm  -f     -0是界定符
统计一个源代码目录中所有php文件行数
find .  –type f  -name “*.php” –print0 | xargs  -0  wc  -l
find . –type f –name “*.jpg” –print | xargs tar –czvf images.tar.gz
cat url-list.txt | xargs wget -c
```

### 16.Linux常用监控工具
```shell
top： 看5分 10分 15分的负载情况。
 
htop：一个交互进程浏览器，完全替代top。但是需要额外安装。。。。。

iostat：iostat -d -k 2 只显示磁盘信息，每2秒一次刷新频率
  -d显示磁盘  -c 显示cpu
  –k以kb显示 
  2刷新频率 2是2秒，如果是2个数，1 2 这样连续2个数是1秒执行1次，连续2次

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







