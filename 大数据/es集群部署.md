# 一．创建es用户

安装es不能使用root用户，需要额外创建一个es用户

```shell
adduser es;
passwd es;
elasticsearch
```

# 二．	安装es

1. 下载es的linux安装包
   https://www.elastic.co/cn/downloads/elasticsearch

   

2. 配置文件

   /home/es/elasticsearch/config/elasticsearch.yml

   ```yaml
   node.name: node1
   path.data: /mnt/data3/es-data
   path.logs: /mnt/data3/es-log
   network.host: 192.168.1.100
   cluster.initial_master_nodes: ["192.168.1.100"]
   #允许所有人开启跨域访问
   http.cors.enabled: true
   http.cors.allow-origin: "*"
   #action.auto_create_index: -vfenv*,+*
   discovery.zen.fd.ping_timeout: 1000s
   discovery.zen.fd.ping_retries: 10
   action.auto_create_index: -vfenv*,+*
   bootstrap.mlockall: true
   ```

   

3. 添加ik分词器

   ```shell
   a.下载
   https://github.com/medcl/elasticsearch-analysis-ik/releases
   
   b.解压放到plugin目录下
   cd /home/es/elasticsearch/plugins
   
   c.添加自定义词语
   	i.在/home/es/elasticsearch/plugins/ik/config目录下新增一个后缀为dic的文件
   	ii.需要自定义的词添加到dic文件中
   	iii.在/home/es/elasticsearch/plugins/ik/config/IKAnalyzer.cfg.xml文件中添加配置
   ```

   IKAnalyzer.cfg.xml

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <!DOCTYPE properties SYSTEM "http://java.sum.com/dtd/properties.dtd">
   <properties>
       <comment>IK Analyzer ext config</comment>
   	<!-- 用户可以在这里配置自己的扩展字典 -->
     	<entry key="ext_dict">test.dic</entry>
       <!-- 用户可以在这里配置自己的停止词字典 -->
     	<entry key="ext_stopwords"></entry>
       <!-- 用户可以在这里配置自己的远程扩展字典 -->
       <!--entry key="remote_ext_dict">test.dic</entry-->
       <!-- 用户可以在这里配置自己的远程停止词字典 -->
     	<!--entry key="remote_ext_stopwords"></entry-->
   </properties>
   
   ```

   

4. 启动es

   ```shell
   cd /home/es/elasticsearch/bin
   ./elasticsearch -d -p pid
   ```

   

5. 查看加载进来的插件

   ```shell
   cd /home/es/elasticsearch/bin
   ./elasticsearch-plugin list
   ```

   

# 三．	安装elasticsearch-head插件（谷歌浏览器安装）

插件管理中搜索添加即可



# 四．	安装kibana

Kibana是一个针对Elasticsearch的开源分析及可视化平台，用来搜索、查看交互存储在Elasticsearch索引中的数据。它操作简单，基于浏览器的用户界面可以快速创建仪表板（dashboard）实时显示Elasticsearch查询动态。

1. 下载
   https://www.elastic.co/cn/downloads/kibana

   

2. 配置文件

   /home/es/kibana/config/ kibana.yml

   ```yaml
   server.host: "192.168.1.100"
   elasticsearch.hosts: ["http://192.168.1.100:9200"]
   i18n.locale: "zh-CN"  #汉化
   ```

   

3. 启动

   ```shell
   cd /home/es/kibana/bin
   ./ kibana &
   ```

   

4. 停止：

   ```shell
   netstat -apn | grep 5601 	#查看端口对应进程，然后kill即可
   ```

   

# 五.Monstache配置与安装

monstache实现了mongo到es的数据实时同步。

因为monstache是基于mongodb的oplog实现同步，而开启oplog前提是配置mongo的复制集

开启复制集可参考：https://blog.csdn.net/jack_brandy/article/details/88887795



1.下载

```
https://rwynn.github.io/monstache-site/start/
```

2.配置文件

/home/es/monstache/config.toml

```ini
# connection settings
# 启用调试日志，这项要放在最上面，否则日志打印不到文件
verbose = true
# connect to MongoDB using the following URL
# mongodb的链接地址
mongo-url = "mongodb://admin:admin@192.168.1.100:27017"

# connect to the Elasticsearch REST API at the following node URLs

#es的链接地址
elasticsearch-urls = ["http://192.168.1.100:9200"]

#指定待同步的集合(存量)
direct-read-namespaces = ["vfenv_db.test_1","vfenv_db.test_2"]

#通过正则表达式指定需要监听的集合。此设置可以用来监控符合正则表达式的集合中数据的变化。
#aaa表示mongodb的数据库，bbb表示集合，表示要匹配的名字空间
#namespace-regex = '^aaa\.bbb$'      

#es用户（没有可不填）
#elasticsearch-user = "xxx"

#es密码（没有可不填）
#elasticsearch-password = "xxx"

#monstache最多开几个线程同步到es,默认为4
elasticsearch-max-conns = 4

#mongodb删除集合或库时是否同步删除es中的索引
dropped-collections = true
dropped-databases = true

#记录同步位点，便于下次从该位置同步
resume = true
#指定恢复策略。仅当resume为true时生效，默认为0-基于时间戳的变更流恢复
resume-strategy = 0

#生产环境记录日志必不可少，monstache默认是输出到标准输出的，这里指定它输出到指定的日志文件
[logs]
info = "/home/es/monstache/log/info.log"
warn = "/home/es/monstache/log/warn.log"
error = "/home/es/monstache/log/error.log"
trace = "/home/es/monstache/log/trace.log"

#设置日志切割参数，下面的配置意思是：每个日志文件超过500M会被切割，最大保存最近60个日志文件，会压缩历史日志
[log-rotate]
max-size = 500
max-age = 60
compress = true
#高可用模式下需要配置集群名称，集群名称一样的进程会自动加入一个集群内，要注意这是个集群是高可用的，而不是负载均衡的。（看到其他文档里说这个参数是es集群的名称，其实并不是，自定义值）

#cluster-name = 'HA-im'

#mapping定义mongodb数据到es的索引名称和type，namespace是库名.集合名
#这里需要注意一件事：最好是在es中创建好你要的索引结构，关闭es的自动创建索引功能，不然monstace会给mongodb中所有的集合都创建一个索引。
[[mapping]]
namespace = "vfenv_db.test_1"
index = "mongo_test"
#type = "test_fl_20200928"

[[mapping]]
namespace = "vfenv_db.test_2"
index = "mongo_test"
```



3.启动

```shell
./monstache -f config.toml &
```



4.停止

```shell
ps -ef | grep config.toml #查到对应进程kill即可
```

