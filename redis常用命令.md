### 1 查看命令帮助
help @string
help @list
help @hash
help @set
help @zset

### 2 客户端登录

登录 redis-cli -p 5566 -a password
使用select 1 切换数据库
keys * 查看所有key

### 3 常规命令
检查key是否存在 EXISTS key
搜索某关键字 KSYS *4
返回一个Key所影响的vsl的类型 TYPE key

### 4 String
设置一个键的值 SET key value
获取一个建的值 GET key
删除键对 DEL key
同时获取多个 mget key1 key2

### 5 Hash
设置一个hash HMSET key valueKey value --<key,<valueKey,value>>
获取hash所有key&value HGETALL key
获取hash所有key HKEYS key
获取hash所有keu的vslue HVALS key
获取hash内键值对的长度 HLEN key
给一个hash的某个键值对赋值 HSET key valueKey value
当hash中valueKey不存在时赋值 HSETNX key valueKey value
hdel <hash-key> <key>

### 6 List
给list赋值 LPUSH listName value
按照索引取值 LINDEX listName 1
RPUSH
LPOP
RPOP
LINDEX
LRANGE


### 7 SET
SADD
SMEMBERS <SET-KEY>
SISMEMBER <SET-KEY> <VALUE>



### 8 zset
zadd <zset-key> <value> <key>
zrange 
zrangebyscore
zrem  <zset-key> <key>
