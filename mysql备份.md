## 数据的备份类型

```
数据的备份类型根据其自身的特性主要分为以下几组
完全备份、部分备份
完全备份指的是备份整个数据集( 即整个数据库 )
部分备份指的是备份部分数据集(例如: 只备份一个表)

而部分备份又分为以下两种:增量备份、差异备份
增量备份指的是备份自上一次备份以来(增量或完全)以来变化的数据; 特点: 节约空间、还原麻烦 
差异备份指的是备份自上一次完全备份以来变化的数据

特点: 浪费空间、还原比增量备份简单
```

  

## MySQL备份数据的方式

```
热备份指的是当数据库进行备份时, 数据库的读写操作均不是受影响
温备份指的是当数据库进行备份时, 数据库的读操作可以执行, 但是不能执行写操作 
冷备份指的是当数据库进行备份时, 数据库不能进行读写操作, 即数据库要下线
```

 MySQL中进行不同方式的备份还要考虑存储引擎是否支持

```
MyISAM	热备× 温备√ 冷备√
InnoDB	热备√ 温备√ 冷备√
```

| 备份方法   | 备份速度 | 恢复速度 | 便捷性                           | 功能 | 一般用于           |
| ---------- | -------- | -------- | -------------------------------- | ---- | ------------------ |
| cp         | 快       | 快       | 一般、灵活性低                   | 很弱 | 少量数据备份       |
| mysqldump  | 慢       | 慢       | 一般、可无视存储引擎的差异       | 一般 | 中小型数据量的备份 |
| lvm2快照   | 快       | 快       | 一般、支持几乎热备、速度快       | 一般 | 中小型数据量的备份 |
| xtrabackup | 较快     | 较快     | 实现innodb热备、对存储引擎有要求 | 强大 | 较大规模的备份     |

 

## 备份工具

这里我们列举出常用的几种备份工具 

| 工具                   | 作用                                                         |
| ---------------------- | ------------------------------------------------------------ |
| cp, tar 等归档复制工具 | 物理备份工具, 适用于所有的存储引擎, 冷备、完全备份、部分备份 |
| lvm2 snapshot          | 几乎热备, 借助文件系统管理工具进行备份                       |
| mysqlhotcopy           | 名不副实的的一个工具, 几乎冷备, 仅支持MyISAM存储引擎         |
| xtrabackup             | 一款非常强大的InnoDB/XtraDB热备工具, 支持完全备份、增量备份, 由`percona`提供 |
| mysqldump              | 逻辑备份工具, 适用于所有的存储引擎, 支持温备、完全备份、部分备份、对于InnoDB存储引擎支持热备 |

 

 

## 设计合适的备份策略

针对不同的场景下, 我们应该制定不同的备份策略对数据库进行备份, 一般情况下, 备份策略一般为以下三种

```
	 直接cp,tar复制数据库文件
     mysqldump+复制BIN LOGS
     lvm2快照+复制BIN LOGS
     xtrabackup
```

以上的几种解决方案分别针对于不同的场景

```
1. 如果数据量较小, 可以使用第一种方式, 直接复制数据库文件
2. 如果数据量还行, 可以使用第二种方式, 先使用mysqldump对数据库进行完全备份, 然后定期备份BINARY LOG达到增量备份的效果
3. 如果数据量一般, 而又不过分影响业务运行, 可以使用第三种方式, 使用lvm2的快照对数据文件进行备份, 而后定期备份BINARY LOG达到增量备份的效果
4. 如果数据量很大, 而又不过分影响业务运行, 可以使用第四种方式, 使用xtrabackup进行完全备份后, 定期使用xtrabackup进行增量备份或差异备份
```

 

## 3.1通过拷贝备份以及还原

```mysql
FLUSH TABLES WITH READ LOCK;   #向所有表施加读锁
```

过复制备份：

```shell
mkdir /backup  #创建文件夹存放备份数据库文件
cp -a /var/lib/mysql/ /backup   #保留权限的拷贝源数据文件
service mysqld restart  #重启MySQL, 如果是编译安装的应该不能启动, 如果rpm安装则会重新初始化数据库
```

通过复制还原：

```shell
cp -a /backup/ /var/lib/mysql/   #将备份的数据文件拷贝回去
service mysqld restart  #重启MySQL
```

## 3.2通过mysqldump+复制binary log备份

```shell
我们通过mysqldump进行一次完全备份, 再修改表中的数据, 然后再通过binary log进行恢复 
#二进制日志需要在mysql配置文件中添加 log_bin=on 开启
```

 基本语法格式

```shell
 shell> mysqldump [options] db_name [tbl_name ...]   #恢复需要手动CRATE DATABASES
 shell> mysqldump [options] --databases db_name ...  #恢复不需要手动创建数据库
 shell> mysqldump [options] --all-databases      #恢复不需要手动创建数据库
 
  其他选项:
   -E, --events: 备份事件调度器
   -R, --routines: 备份存储过程和存储函数
   --triggers: 备份表的触发器; --skip-triggers 
   --master-date[=value]  
     1: 记录为CHANGE MASTER TO 语句、语句不被注释
     2: 记录为注释的CHANGE MASTER TO语句
     基于二进制还原只能全库还原
   --flush-logs: 日志滚动
     锁定表完成后执行日志滚动
```

 查看当前二进制文件的状态, 并记录下position的数字

```shell
mysql -uroot -p -e 'SHOW MASTER STATUS'
```

备份：

备份数据库到backup.sql文件中

```shell
mysqldump --all-databases --lock-all-tables > backup.sql
mysqldump –uroot --all-databases --lock-all-tables |gzip > backup`date +%Y%m%d`.gz
```

备份二进制文件：

```shell
SHOW MASTER STATUS;  #记下现在的position —> 106
cp /var/lib/mysql/mysql-bin.000003 /root #备份二进制文件
```

 

```shell
mysqldump -u用户名 -p密码 -h主机 数据库 a -w “sql条件” –lock-all-tables > 路径

mysqldump -hhostname -uusername -p dbname tbname>xxxx.sql

mysqldump -hhostname -uusername-p dbname tbname -w’id >= 1 and id<= 10000’–skip-lock-tables > xxxx.sql
或
mysqldump -hhostname -uusername -p dbname tbname --where=‘unit_id >= 1 and unit_id <= 10000’> ~/xxxx.sql
```



备份命令使用说明：

```shell
1. 导出整个数据库
	mysqldump -u 用户名 -p数据库名 > 导出的文件名
	mysqldump -u breezelark-p mydb > mydb.sql
2. 导出一个表（包括数据结构及数据）
    mysqldump -u 用户名 –p 数据库名 表名> 导出的文件名
    mysqldump -u lingxi -p mydb mytb> mytb.sql
3. 导出一个数据库结构（无数据只有结构）
    mysqldump -u lingxi -p -d --add-drop-table mydb >mydb.sql
    #-d 没有数据 
	#--add-drop-table 在每个create语句之前增加一个drop table
```

 

 还原：

```shell
mysql> SET sql_log_bin=OFF;		#暂时先将二进制日志关闭

mysql> source backup.sql  		#恢复数据，所需时间根据数据库时间大小而定

mysqlbinlog --start-position=106 --stop-position=191 mysql-bin.000003 | mysql employees 	#通过二进制日志增量恢复数据

mysql> SET sql_log_bin=ON;		#开启二进制日志
```

 

## 使用lvm2快照备份数据

LVM快照简单来说就是将所快照源分区一个时间点所有文件的元数据进行保存，如果源文件没有改变，那么访问快照卷的相应文件则直接指向源分区的源文件，如果源文件发生改变，则快照卷中与之对应的文件不会发生改变。快照卷主要用于辅助备份文件。 这里只简单介绍，[点击查看详细介绍](http://www.360doc.com/content/13/0522/16/11801283_287305129.shtml)

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

创建逻辑卷：

```shell
#创建物理卷
pvcreate /dev/sdb1		

#创建卷组
vgcreate myvg /dev/sdb1

#创建逻辑卷
lvcreate -n mydata -L 5G myvg

#格式化
mkfs.ext4 /dev/mapper/myvg-mydata  

#创建文件夹
mkdir /lvm_data

#将逻辑卷   挂载到新创建的文件夹 /lvm_data
mount /dev/mapper/myvg-mydata /lvm_data  
```

 

```shell
vim /etc/my.cnf   #修改mysql配置文件的datadir如下
datadir=/lvm_data
```

```shell
service mysqld restart  #重启MySQL
```

 

创建快照卷并备份

```shell
mysql> FLUSH TABLES WITH READ LOCK;   #锁定所有表

lvcreate -L 1G -n mydata-snap -p r -s /dev/mapper/myvg-mydata   #创建快照卷

mysql> UNLOCK TABLES;  #解锁所有表
```

 

```shell
mkdir /lvm_snap  #创建文件夹
mount /dev/myvg/mydata-snap /lvm_snap/  #挂载snap

cd /lvm_snap/
ls
 
tar cf /tmp/mysqlback.tar   #打包文件到/tmp/mysqlback.tar
umount /lvm_snap/  #卸载snap
lvremove myvg mydata-snap  #删除snap
```

 
 恢复数据

```shell
rm -rf /lvm_data/
service mysqld start   #启动MySQL, 如果是编译安装的应该不能启动(需重新初始化), 如果rpm安装则会重新初始化数据库
cd /lvm_data/
rm -rf       #删除所有文件
tar xf /tmp/mysqlback.tar   #解压备份数据库到此文件夹 
ls  #查看当前的文件
 
```



## 使用Xtrabackup备份

为了更好地演示, 我们这次使用`mariadb-5.5`的版本, 使用`xtrabackup`使用InnoDB能够发挥其最大功效, 并且InnoDB的每一张表必须使用单独的表空间, 我们需要在配置文件中添加 `innodb_file_per_table = ON` 来开启

 

下载安装xtrabackup

```shell
#我们这里通过wget percona官方的rpm包进行安装
wget https://www.percona.com/downloads/XtraBackup/Percona-XtraBackup-2.3.4/binary/redhat/6/x86_64/percona-xtrabackup-2.3.4-1.el6.x86_64.rpm  
yum localinstall percona-xtrabackup-2.3.4-1.el6.x86_64.rpm  #需要EPEL源
```

 Xtrabackup是由percona提供的mysql数据库备份工具，据官方介绍，这也是世界上惟一一款开源的能够对innodb和xtradb数据库进行热备的工具。特点：

1. 备份过程快速、可靠；

2. 备份过程不会打断正在执行的事务；

3. 能够基于压缩等功能节约磁盘空间和流量；

4. 自动实现备份检验；

5. 还原速度快；

 

xtrabackup实现完全备份

我们这里使用xtrabackup的前端配置工具innobackupex来实现对数据库的完全备份

使用`innobackupex`备份时, 会调用`xtrabackup`备份所有的InnoDB表, 复制所有关于表结构定义的相关文件(.frm)、以及MyISAM、MERGE、CSV和ARCHIVE表的相关文件, 同时还会备份触发器和数据库配置文件信息相关的文件, 这些文件会被保存至一个以时间命名的目录.

备份过程:

```shell
mkdir /extrabackup  #创建备份目录
innobackupex --user=root /extrabackup/ #备份数据
###################提示complete表示成功

ls /extrabackup/  #看到备份目录
2016-04-27_07-30-48 
```

一般情况, 备份完成后, 数据不能用于恢复操作, 因为备份的数据中可能会包含尚未提交的事务或已经提交但尚未同步至数据文件中的事务。因此, 此时的数据文件仍不一致, 所以我们需要”准备”一个完全备份

```shell
innobackupex --apply-log /extrabackup/2016-04-27_07-30-48/ #指定备份文件的目录
#一般情况下下面三行结尾代表成功
#InnoDB: Starting shutdown...
#InnoDB: Shutdown completed; log sequence number 369661462
#160427 07:40:11 completed OK!
cd /extrabackup/2016-04-27_07-30-48/
ls -hl  #查看备份文件

```



恢复数据:

```shell
rm -rf /data/  #删除数据文件

#不用启动数据库也可以还原
innobackupex --copy-back /extrabackup/2016-04-27_07-30-48/  #恢复数据, 记清使用方法
```

 

```shell
#########我们这里是编译安装的mariadb所以需要做一些操作##########
killall mysqld
chown -R mysql:mysql ./ 
ll /data/    #数据恢复

service mysqld restart
```

 

增量备份:

```shell
innobackupex --incremental /extrabackup/ --incremental-basedir=/extrabackup/2016-04-27_07-30-48/
ls /extrabackup/2016-04-27_07-57-22/ #查看备份文件
```

BASEDIR指的是完全备份所在的目录，此命令执行结束后，`innobackupex`命令会在`/extrabackup`目录中创建一个新的以时间命名的目录以存放所有的增量备份数据。另外，在执行过增量备份之后再一次进行增量备份时，其`--incremental-basedir`应该指向上一次的增量备份所在的目录。

需要注意的是，增量备份仅能应用于InnoDB或XtraDB表，对于MyISAM表而言，执行增量备份时其实进行的是完全备份。



整理增量备份

```shell
innobackupex --apply-log --redo-only /extrabackup/2016-04-27_07-30-48/
innobackupex --apply-log --redo-only /extrabackup/2016-04-27_07-30-48/ --incremental-dir=/extrabackup/2016-04-27_07-5
 7-22/
```

 

恢复数据

```shell
rm -rf /data/  #删除数据
innobackupex --copy-back /extrabackup/2016-04-27_07-30-48/   #整理增量备份之后可以直接通过全量备份还原
chown -R mysql.mysql /data/
ls /data/ -l
```

```shell
其实我们还可以通过`Master-Slave Replication` 进行备份。
```



## mysql定时使用mysqldump备份

```shell
#创建一个备份目录-注意目录一定够大，不要备份几天就没空间了
mkdir –p /mysqlback
#添加一个脚本
vi /mysqlback/mysqlbackup.sh
#脚本内容
#!/bin/bash
#!/usr/bin/expect
mysqldump -uroot -p123456 --all-databases |gzip >/Data/backup`date +%Y%m%d`.gz
expect -c "
    spawn scp -r /Data/backup`date +%Y%m%d`.gz root@192.168.1.100:/data/backup/
    expect {
        \"*assword\" {set timeout 20; send \"password123456\r\"; exp_continue;}
    }
expect eof"

#添加定时任务
crontab -e
#添加
0 2 * * * sh /mysqlback/mysqlbackup.sh >/dev/null 2>&1 &
#如果 crontab不好用   使用
cron start #开启cron

#[linux]
chkconfig --list|grep cron

#Ubuntu 中系统没有了RH系统中的 chkconfig 命令,需要安装一个开机启动服务
#1.rcconf
sudo apt-get update
sudo apt-get install rcconf
#完成后在命令状态下输入：sudo rcconf 即可


#2.rcconf: 还可以使用 Ubuntu 自带的update-rc.d
#用法 
update-rc.dXXX stop/start
#例 
update-rc.d avahi-daemon stop

#sysv-rc-conf 设置开机启动
sudo apt-get install sysv-rc-conf
#把需要自启动的脚本放置的 /etc/init.d 目录下
sudo sysv-rc-conf xxxx on


```

