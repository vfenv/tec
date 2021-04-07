# 分布式mongodb部署

## 一、Mongo分片群集主要三个组件

1. Shard：分片服务器，用于存储整体数据的一部分，实际生产环境中一个shard可以由多台服务器组成一个Replica Set 承担，防止主机单点故障。

2. Config Server：配置服务器，存储了整个群集的元数据和配置信息（包括认证），其中包括chunk信息。从3.4开始，必须将配置服务器部署为副本集CSRS(Config Servers Replica Set)。

3. Mongos（Routers）：前端路由，客户端由此接入，且让整个群集看上去像单一数据库，前端应用可以透明使用。应用程序直连mongos即可。

   

## 二、系统环境

   1.Centos7.6、MongoDB4.4.0、关闭防火墙。

| **IP**        | **路由服务端口（mongos）** | **配置服务端口（config server）** | **分片1端口** | **分片二端口** |
| ------------- | -------------------------- | --------------------------------- | ------------- | -------------- |
| 192.168.1.100 | 27017                      | 27018                             | 27001         | 27002          |
| 192.168.1.101 | 27017                      | 27018                             | 27001         | 27002          |


   2.数据目录(一共使用了20个磁盘，共268T)

```shell
/data/mongodb
```




## 三、	服务器的安装与配置（3台服务器执行相同操作）



### 1.下载解压MongoDB

```shell
#下载地址：https://fastdl.mongodb.org/linux/mongodb-linux-x86_64-rhel70-4.4.0.tgz
#解压到/root/mongodb，设置环境变量：
#在/etc/profile文件添加如下内容
export PATH=$PATH:/root/mongodb/bin
#保存后执行：
source /etc/profile
```



### 2.创建路由、配置、分片等的相关目录

```shell
mkdir -p /usr/local/mongodb/conf
mkdir -p /data/mongodb/shard1
mkdir -p /data/mongodb/shard2
mkdir -p /usr/local/mongodb /log
```



### 3.配置服务器部署(config server)

1)	在/usr/local/mongodb/conf目录创建config.conf文件

```yaml
systemLog:
  destination: file
  path: "/usr/local/mongodb/log/config.log"
  logAppend: true
storage:
  journal:
    enabled: true
  dbPath: "/data/mongodb/config"
  engine: wiredTiger
  directoryPerDB: true
processManagement:
  fork: true
  pidFilePath: "/usr/local/mongodb/conf/server.pid"
net:
  bindIp: 0.0.0.0
  port: 27018
  compression:
    compressors: disabled
sharding:
  clusterRole: configsvr
replication:
  replSetName: configs
  oplogSizeMB: 50
#security:
#  keyFile: "/usr/local/mongodb/conf/keyfile"
```

2)	配置复制集

```shell
# 1.分别启动两台服务器
mongod -f /usr/local/mongodb/conf/config.conf

# 2.连接mongo,只需在任意一台机器执行即可：
mongo --host 192.168.1.100 --port 27018

# 3.切换数据库：
use admin

# 4.初始化复制集：
rs.initiate({_id:"configs",members:[{_id:0,host:"192.168.1.100:27018"},{_id:1,host:"192.168.1.101:27018"}]})
#其中_id:"configs"的configs是上面config.conf配置文件里的复制集名称，把两台服务器的配置服务组成复制集。

# 5.查看状态：
rs.status()
#等几十秒左右，执行上面的命令查看状态，三台机器的配置服务就已形成复制集，其中1台为PRIMARY，另外1台为SECONDARY。
```

### 4.分片服务部署shard server(2台服务器执行相同操作)

**i.在/root/mongodb/conf目录创建shard1.conf**

```yaml
systemLog:
  destination: file
  path: "/usr/local/mongodb/log/shard1.log"
  logAppend: true
storage:
  journal:
    enabled: true
  dbPath: "/data/mongodb/shard1"
  engine: wiredTiger
  wiredTiger:
    engineConfig:
      cacheSizeGB: 350
processManagement:
  fork: true
  pidFilePath: "/usr/local/mongodb/conf/shard1.pid"
net:
  bindIp: 0.0.0.0
  port: 28001
  compression:
    compressors: disabled
  maxIncomingConnections: 500
sharding:
  clusterRole: shardsvr
replication:
  replSetName: shard1
  oplogSizeMB: 50
#security:
#  keyFile: "/usr/local/mongodb/conf/keyfile"
```

**shard2.conf**

```yaml
systemLog:
  destination: file
  path: "/usr/local/mongodb/log/shard2.log"
  logAppend: true
storage:
  journal:
    enabled: true
  dbPath: "/data/mongodb/shard2"
  engine: wiredTiger
  wiredTiger:
    engineConfig:
      cacheSizeGB: 350
processManagement:
  fork: true
  pidFilePath: "/usr/local/mongodb/conf/shard2.pid"
net:
  bindIp: 0.0.0.0
  port: 28002
  compression:
    compressors: disabled
  maxIncomingConnections: 500
sharding:
  clusterRole: shardsvr
replication:
  replSetName: shard2
  oplogSizeMB: 50
#security:
#  keyFile: "/usr/local/mongodb/conf/keyfile"
```

端口分别是28001、28002，分别对应shard1.conf、shard2.conf。
还有数据存放目录、日志文件这几个地方都需要对应修改。
在2台机器的相同端口形成一个分片的复制集，由于2台机器都需要这2个文件，所以根据这4个配置文件分别启动分片服务：

```shell
mongod -f /usr/local/mongodb/conf/shard{1/2}.conf
```



**ii.将分片配置为复制集**

```shell
#连接mongo，只需在任意一台机器执行即可：
mongo --host 192.168.1.100 --port 28001 #这里以shard1为例，其他两个分片则再需对应连接到28002的端口进行操作即可

#切换数据库：
use admin

#初始化复制集：
rs.initiate({_id:"shard1",members:[{_id:0,host:"192.168.1.100:28001"},{_id:1,host:"192.168.1.101:28001"}]})

#以上是基于分片1来操作，同理，其他2个分片也要连到各自的端口来执行一遍上述的操作，让3个分片各自形成1主2从的复制集，注意端口及仲裁节点的问题即可，操作完成后3个分片都启动完成，并完成复制集模式。
```



### 5.路由服务部署mongos server(3台服务器执行相同操作)

**i.在/root/mongodb/conf目录创建mongos.conf，内容如下：**

```yaml
systemLog:
  destination: file
  path: "/usr/local/mongodb/log/mongos.log"
  logAppend: true
  quiet: true
processManagement:
  fork: true
  pidFilePath: "/usr/local/mongodb/conf/mongos.pid"
net:
  bindIp: 0.0.0.0
  port: 27017
  compression:
    compressors: disabled
  maxIncomingConnections: 1000
sharding:
  configDB: configs/192.168.1.101:27018,192.168.1.100:27018
#security:
#  keyFile: "/usr/local/mongodb/conf/keyfile"
```



**ii.启动mongos**

```shell
#分别在两台服务器启动：
mongos -f /root/mongodb/conf/mongos.conf

```

**iii	启动分片功能**

```shell
#连接mongo：
mongo --host 192.168.1.100 --port 27017
#切换数据库：
use admin
#添加分片，只需在一台机器执行即可：        
sh.addShard("shard1/192.168.1.100:28001,192.168.1.101:28001")
sh.addShard("shard2/192.168.1.100:28002,192.168.1.101:28002")
#查看集群状态：
sh.status()
```



### 6.添加用户

(参考https://www.cnblogs.com/swordfall/p/10841418.html)

```shell
i	创建超级用户（root）
use admin
db.createUser({user:"admin",pwd:"admin",roles:[{role:"root",db:"admin"}]})
ii	创建所有数据库管理用户
db.createUser({ user: "useradmin", pwd: "useradmin", roles: [{ role: "userAdminAnyDatabase", db: "admin"}]})
iii	创建单个数据库用户
db.createUser({user:"aita",pwd:"aita",roles:[{role:"readWrite",db:"aita_db"}]})
```



### 7.重启服务，开启权限认证

```shell
#i.停止服务configserver,mongos server,shard server（每个节点都需要执行）

#ii.生成keyfile,并拷贝到其他节点的同路径下
openssl rand -base64 745 > /usr/local/mongodb/conf/keyfile
chmod 400 /usr/local/mongodb/conf/keyfile

#iii.开启认证（每个节点都需要执行）
修改/usr/local/mongodb/conf路径下的所有.conf配置文件，在末尾添加：
security:
keyFile: "/usr/local/mongodb/conf/keyfile"

#iv.开启服务（每个节点都需要执行）
cd /usr/local/mongodb/conf
mongod -f config.conf
mongod -f shard1.conf
mongod -f shard2.conf
mongos -f mongos.conf
```



### 8.Demo:

```shell
#i.实现分片功能

#设置分片chunk大小        
use config
db.setting.save({"_id":"chunksize","value":1}) # 设置块大小为1M是方便实验，不然需要插入海量数据

#ii.模拟写入数据 
use calon
for(i=1;i<=50000;i++){db.user.insert({"id":i,"name":"jack"+i})} #模拟往calon数据库的user表写入5万数据

#iii.启用数据库分片
sh.enableSharding("calon")

#iv.创建索引，对表进行分片   
db.user.createIndex({"id":1}) # 以"id"作为索引
sh.shardCollection(calon.user",{"id":1}) # 根据"id"对user表进行分片
sh.status() # 查看分片情况
```

## 四．附件

1.配置详解

https://www.cnblogs.com/shuiche/p/6074615.html

2.磁盘合并说明

https://blog.csdn.net/walk_persuit/article/details/45037613
