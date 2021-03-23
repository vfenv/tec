## 1.NFS服务器是什么

　　NFS server可以看作是一个FILE SERVER,它可以让你的PC通过网络将远端的NFS SERVER共享出来的档案MOUNT到自己的系统中，在CLIENT看来使用NFS的远端文件就象是在使用本地文件一样。

RPC（Remote Procedure Call）

　　NFS本身是没有提供信息传输的协议和功能的，但NFS却能让我们通过网络进行资料的分享，这是因为NFS使用了一些其它的传输协议。而这些传输协议用到这个RPC功能的。可以说NFS本身就是使用RPC的一个程序。或者说NFS也是一个RPC SERVER.所以只要用到NFS的地方都要启动RPC服务，不论是NFS SERVER或者NFS CLIENT。这样SERVER和CLIENT才能通过RPC来实现PROGRAM PORT的对应。可以这么理解RPC和NFS的关系：NFS是一个文件系统，而RPC是负责负责信息的传输。

## 2.ubuntu系统安装NFS

### 1.服务器端（192.168.94.54）

#### 1.1安装NFS服务：

执行以下命令安装NFS服务器，apt会自动安装nfs-common、rpcbind等13个软件包

```shell
sudo apt install nfs-kernel-server
```



#### 1.2编写配置文件：

编辑/etc/exports 文件：

```shell
sudo vi /etc/exports
#/etc/exports文件的内容如下：
/tmp *(rw,sync,no_subtree_check,no_root_squash)
/data *(rw,sync,no_subtree_check,no_root_squash)
/logs *(rw,sync,no_subtree_check,no_root_squash)

#针对101这个ip 挂载/data2/data这个文件夹
/data2/data 192.168.1.101(rw,sync,all_squash,no_subtree_check,anonuid=2000,anongid=2000)
```

```
Exports文件中可以设定的参数主要有以下这些：

(1) Ro 该主机对该共享目录有只读权限
(2) Rw 该主机对该共享目录有读写权限
(3) Root_squash 客户机用root用户访问该共享文件夹时，将root用户映射成匿名用户
(4) No_root_squash 客户机用root访问该共享文件夹时，不映射root用户
(5) All_squash 客户机上的任何用户访问该共享目录时都映射成匿名用户
(6) Anonuid 将客户机上的用户映射成指定的本地用户ID的用户
(7) Anongid 将客户机上的用户映射成属于指定的本地用户组ID
(8) Sync 资料同步写入到内存与硬盘中
(9) Async 资料会先暂存于内存中，而非直接写入硬盘
(10) Insecure 允许从这台机器过来的非授权访问
(11) subtree_check 如果共享/usr/bin之类的子目录时，强制NFS检查父目录的权限（默认）
(12) no_subtree_check 和上面相对，不检查父目录权限
(13) wdelay 如果多个用户要写入NFS目录，则归组写入（默认）
(14 )no_wdelay 如果多个用户要写入NFS目录，则立即写入，当使用async时，无需此设置。
(15) hide 在NFS共享目录中不共享其子目录
(16) no_hide 共享NFS目录的子目录
(17) secure NFS通过1024以下的安全TCP/IP端口发送
(18) insecure NFS通过1024以上的端口发送
	
	/ user01(rw) user02(rw,no_root_squash) 表示共享服务器上的根目录(/)只有user01和user02两台主机可以访问，且有读写权限；user01主机用root用户身份访问时，将客户机的root用户映射成服务器上的匿名用户(root_squash,该参数为缺省参数)，相当于在服务器使用nobody用户访问目录；user02主机用root用户身份访问该共享目录时，不映射root用户(no_root_squash),即相当于在服务器上用root身份访问该目录

　　/root/share/ 192.168.1.2(rw,insecure,sync,all_squash) 表示共享服务器上的/root/share/目录只有192.168.1.2主机可以访问，且有读写权限；此主机用任何身份访问时，将客户机的用户都映射成服务器上的匿名用户(all_squash),相当于在服务器上用nobody用户访问该目录（若客户机要在该共享目录上保存文件（即写操作），则服务器上的nobody用户对该目录必须有写的权限）

　　/home/ylw/ *.test.com (rw,insecure,sync,all_squash)* *表示共享**/home/ylw/**目录，*.test.com域中所有的主机都可以访问该目录，且有读写权限

　　/home/share/ .test.com (ro,sync,all_squash,anonuid=zh3,anongid=wa4) 表示共享目录/home/share/，*.test.com域中的所有主机都可以访问，但只有只读的权限，所有用户都映射成服务器上的uid为zh3、gid为wa4的用户
```

#### 1.3创建共享目录

```shell
#在服务器端创建/tmp /data和/logs共享目录
sudo mkdir -p /tmp
sudo mkdir -p /data
sudo mkdir -p /logs
```



#### 1.4重启nfs服务：

```shell
sudo service nfs-kernel-server restart
```

#### 1.5常用命令工具：

在安装NFS服务器时，已包含常用的命令行工具，无需额外安装。

```shell
#显示已经mount到本机nfs目录的客户端机器。
sudo showmount -e localhost
```

```shell
#将配置文件中的目录全部重新export一次！无需重启服务。
sudo exportfs -rv
```

```shell
#查看NFS的运行状态
sudo nfsstat
```

```shell
#查看rpc执行信息，可以用于检测rpc运行情况
sudo rpcinfo
```

```shell
#查看网络端口，NFS默认是使用111端口。
sudo netstat -tu -4
```

### 2.**客户端（192.168.94.76）**

#### 2.1安装客户端工具：

在需要连接到NFS服务器的客户端机器上，需要执行以下命令，安装nfs-common软件包。apt会自动安装nfs-common、rpcbind等12个软件包

```shell
sudo apt install nfs-common
```

#### 2.2查看NFS服务器上的共享目录

显示指定的（192.168.94.54）NFS服务器上export出来的目录

```shell
sudo showmount -e 192.168.94.54
```

#### 2.3创建本地挂载目录

```shell
sudo mkdir -p /mnt/data
sudo mkdir -p /mnt/logs
```

#### 2.4挂载共享目录

将NFS服务器192.168.94.54上的目录，挂载到本地的/mnt/目录下

```shell
sudo mount -t nfs 192.168.94.54:/data /mnt/data
sudo mount -t nfs 192.168.94.54:/logs /mnt/logs
```

注：在没有安装nfs-common或者nfs-kernel-server软件包的机器上，直接执行showmount、exportfs、nfsstat、rpcinfo等命令时，系统会给出友好的提示，

比如直接showmount会提示需要执行sudo apt install nfs-common命令，比如直接rpcinfo会提示需要执行sudo apt install rpcbind命令。

## 3.centos6系统

### 1、安装nfs和rpcbind

检查自己的电脑是否已经默认安装了nfs和rpcbind：

\# rpm -aq | grep nfs

nfs-utils-lib-1.1.5-13.el6.x86_64

nfs-utils-1.2.3-75.el6_9.x86_64

\# rpm -aq | grep rpcbind 

rpcbind-0.2.0-13.el6_9.1.x86_64

这表示系统已经默认安装。如果没有安装也没事，可以采用下面的命令安装

\# yum install nfs-utils rpcbind 

### 2、配置nfs的配置文件和hosts文件

创建需要共享的目录：默认用/mnt

配置nfs的配置文件：

```shell
vim /etc/exports
```

在这个文件中添加需要输出的目录，如：

```shell
/usr/local/static *(rw)
# /usr/local/static：表示的是nfs服务器需要共享给其他客户端服务器的文件夹
# *：表示可以挂载服务器目录的客户端ip
# (rw)：表示该客户端对共享的文件具有读写权限
```

配置hosts文件：vim /etc/hosts

```shell
192.168.93.5 tomcat-01
192.168.93.7 tomcat-02
# 192.168.93.5：表示服务器本机的ip地址
# tomcat-01：表示服务器的机器名
```



### 3、启动nfs和rpcbind服务、检测服务状态、已经设置服务开机启动

```shell
#启动服务：
service rpcbind start
service nfs start 

#检查启动状态：
service rpcbind status 
service nfs status 
```



### 4、检测服务器的nfs状态

```shell
showmount -e localhost   #查看自己共享的服务 

#输出结果
Export list for hostname: 
/usr/local/static *

#注意：在执行这个命令的时候如果出现错误，说明DNS不能解析当前的服务器，那就是hosts文件没有配置。
```



### 5、客户端挂载NFS中共享的目录

客户端服务器也需要安装nfs 和 rpcbind 服务。

首先是启动nfs和rpcbind服务。

查询服务端共享的文件目录：

```shell
showmount -e 192.168.93.5

#输出结果
Export list for 192.168.93.5:
/usr/local/static *
```

创建挂载目录：

```shell
cd /mnt
mkdir static
```

挂载服务端的共享目录：

```shell
mount -t nfs -o nolock,nfsvers=3,vers=3 192.168.93.5:/usr/local/static /mnt/static
```

查看挂载的状态：

```shell
mount | grep nfs
#输出结果
sunrpc on /var/lib/nfs/rpc_pipefs type rpc_pipefs (rw)
nfsd on /proc/fs/nfsd type nfsd (rw)
192.168.93.5:/usr/local/static on /mnt/static type nfs (rw,nolock,nfsvers=3,vers=3,addr=192.168.93.5)
```

 

### 6、测试共享

服务器创建文件：

```shell
#cd /usr/local/static
touch test123
echo "123" > test123 
```

客户端查看文件：

```shell
cd /mnt/static
ll -h
#输出结果
total 4.0K	-rw-r--r--. 1 root root 4 Dec 26 10:06 test123
```

 

