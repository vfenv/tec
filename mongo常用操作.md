## 1.1   查询字段存在

```javascript
db.getCollection('annotation_model').find({"keypoints":{$exists:false}})
```

 

## 1.2   根据字段值不为空查询

```shell
db.annotation_model.find({"keypoints":{"$ne":null}})
```



## 1.3   字段不为空 并且存在

```shell
db.annotation_model.find({"keypoints":{"$ne":null, $exists:true}})
```

 

## 1.4   字段为null

```javascript
db.annotation_model.find({"test":null})
```

 

## 1.5   字段值in

```javascript
db.getCollection('annotation_model').find({keypoints:{$in:[null]}})
```

 

## 1.6   查询集合中keypoints为null的记录

```javascript
db.getCollection('annotation_model').find({"keypoints":{$in:[null]}})
db.getCollection('annotation_model').find({keypoints:[null]})
db.getCollection('annotation_model').find({"keypoints":{$in:[null], $exists:true}})
```

 

## 1.7   查找annotation_model这个collection的keypoints字段数组长度是3的

```javascript
db.annotation_model.find({keypoints:{$size:3}})
```



## 1.8   范围查找

这种查询比较慢，但是可以按照范围查询

```javascript
db.annotation_model.find({ $where: "this.keypoints.length>0" })
```



## 1.9   索引查找

```javascript
//这种查询比较快，通过字段的下标索引是0的 判断，如果有 就说明有
db.annotation_model.find({ "keypoints.0": {$exists:1} })
```

  

## 1.10  数组包含值

```javascript
db.user.find({state_arr:{$elemMatch:{$eq:"123"}}})
db.user.find({state_arr:{$elemMatch:{id:1}}})
```

 

```shell
#查找用户swk的订单

#1.获取swk的id
var user_id = db.users.findOne({username:"swk"})._id

#2.查找用户swk的订单
db.order.find({user_id:user_id})

```

  

## MongoDB数据类型

```text
字符串 - 这是用于存储数据的最常用的数据类型。MongoDB中的字符串必须为UTF-8。
 整型 - 此类型用于存储数值。 整数可以是32位或64位，具体取决于服务器。
 布尔类型 - 此类型用于存储布尔值(true / false)值。
 双精度浮点数 - 此类型用于存储浮点值。
 最小/最大键 - 此类型用于将值与最小和最大BSON元素进行比较。
 数组 - 此类型用于将数组或列表或多个值存储到一个键中。
 时间戳 - ctimestamp，当文档被修改或添加时，可以方便地进行录制。
 对象 - 此数据类型用于嵌入式文档。
 对象 - 此数据类型用于嵌入式文档。
 Null - 此类型用于存储Null值。
 符号 - 该数据类型与字符串相同; 但是，通常保留用于使用特定符号类型的语言。
 日期 - 此数据类型用于以UNIX时间格式存储当前日期或时间。您可以通过创建日期对象并将日，月，年的日期进行指定自己需要的日期时间。
 对象ID - 此数据类型用于存储文档的ID。
 二进制数据 - 此数据类型用于存储二进制数据。
 代码 - 此数据类型用于将JavaScript代码存储到文档中。
 正则表达式 - 此数据类型用于存储正则表达式。
```



在插入的文档中，如果不指定_id参数，那么MongoDB会成为文档分配一个唯一的ObjectID。

```text
ObjectID 长度为 12 字节，由几个 2-4 字节的链组成。每个链代表并指定文档身份的具体内容。以下的值构成了完整的 12 字节组合：
一个 4 字节的值，表示自 Unix 纪元以来的秒数
一个 3 字节的机器标识符
一个 2 字节的进程 ID
一个 3 字节的计数器，以随机值开始
```



要在单个查询中插入多个文档，可以在insert()命令中传递文档数组。如下所示：

```javascript
db.mycollection.insert([
  {
   _id: 101,
   title: 'MongoDB Guide', 
   description: 'MongoDB is no sql database',
   by: 'xhh tutorials',
   url: 'http://www.baidu.com',
   tags: ['mongodb', 'database', 'NoSQL'],
   likes: 100
  },
  {
   _id: 102,
   title: 'NoSQL Database', 
   description: "NoSQL database doesn't have tables",
   by: 'xhh tutorials',
   url: 'http://www.baidu.com',
   tags: ['mongodb', 'database', 'NoSQL'],
   likes: 210, 
   comments: [
     {
      user:'user1',
      message: 'My first comment',
      dateCreated: new Date(2017,11,10,2,35),
      like: 0 
     }
   ]
  },
  {
   _id: 104,
   title: 'Python Quick Guide', 
   description: "Python Quick start ",
   by: 'xhh tutorials',
   url: 'http://www.baidu.com',
   tags: ['Python', 'database', 'NoSQL'],
   likes: 30, 
   comments: [
     {
      user:'user1',
      message: 'My first comment',
      dateCreated: new Date(2018,11,10,2,35),
      like: 590 
     }
   ]
  }
])
```

```
要插入文档，也可以使用db.post.save(document)。如果不在文档中指定_id，那么save()方法将与insert()方法一样自动分配ID的值。如果指定_id，则将以save()方法的形式替换包含其_id文档的全部数据。
```

```shell
db.inventory.insertOne({ item: "canvas", qty: 100, tags: ["cotton"], size: { h: 28, w: 35.5, uom: "cm" } })
输出
{
    "acknowledged" : true,
    "insertedId" : ObjectId("5955220846be576f199feb55")
}
```



插入多个document-批处理：db.collection.insertMany

```shell
db.inventory.insertMany([
  { item: "journal", qty: 25, tags: ["blank", "red"], size: { h: 14, w: 21, uom: "cm" } },
  { item: "mat", qty: 85, tags: ["gray"], size: { h: 27.9, w: 35.5, uom: "cm" } },
  { item: "mousepad", qty: 25, tags: ["gel", "blue"], size: { h: 19, w: 22.85, uom: "cm" } }
])
输出
{
    "acknowledged" : true,
    "insertedIds" : [
        ObjectId("59552c1c46be576f199feb56"),
        ObjectId("59552c1c46be576f199feb57"),
        ObjectId("59552c1c46be576f199feb58")
    ]
}
```



查询 db.COLLECTION_NAME.find(document)

```shell
要以格式化的方式显示结果，可以使用pretty()方式。纯json返回
db.mycol.find().pretty()
返回
{
  "_id": 100,
  "title": "MongoDB Overview", 
  "description": "MongoDB is no sql database",
  "by": "yiibai tutorials",
  "url": "http://www.yiibai.com",
  "tags": ["mongodb", "database", "NoSQL"],
  "likes": "100"
}
```



### 1.10.1  操作 语法   示例   RDBMS等效语句

| **操作** | **语法**               | **示例**                                    | **RDBMS****等效语句** |
| -------- | ---------------------- | ------------------------------------------- | --------------------- |
| 相等     | {<key>:<value>}        | db.mycol.find({"by":"yiibai"}).pretty()     | where by = 'yiibai'   |
| 小于     | {<key>:{$lt:<value>}}  | db.mycol.find({"likes":{$lt:50}}).pretty()  | where likes < 50      |
| 小于等于 | {<key>:{$lte:<value>}} | db.mycol.find({"likes":{$lte:50}}).pretty() | where likes <= 50     |
| 大于     | {<key>:{$gt:<value>}}  | db.mycol.find({"likes":{$gt:50}}).pretty()  | where likes > 50      |
| 大于等于 | {<key>:{$gte:<value>}} | db.mycol.find({"likes":{$gte:50}}).pretty() | where likes >= 50     |
| 不等于   | {<key>:{$ne:<value>}}  | db.mycol.find({"likes":{$ne:50}}).pretty()  | where likes != 50     |

 

## 1.11  MongoDB中的AND操作符

在find()方法中，如果通过‘,’将它们分开传递多个键，则MongoDB将其视为AND条件。

```shell
db.mycol.find({$and:[{key1:value1}, {key2:value2}]}).pretty()
```

 

## 1.12  AND和OR联合使用

```shell
db.mycol.find({"col1": {$gt:10}, $or: [{"col2": "yiibai tutorials"},{"col3": "MongoDB Overview"}]}).pretty()
```

 

## 1.13  匹配嵌入/嵌套文档

整个嵌入式文档中的相等匹配需要精确匹配指定的<value>文档，包括字段顺序。
以下查询选择字段size等于{ h:14,w:21:uom:"cm"}的所有文档：

```shell
db.inventory.find( { size: { h: 14, w: 21, uom: "cm" } } )
```



## 1.14  查询嵌套字段

下示例选择在size字段中嵌套的字段uom等于“in”的所有文档：

```sh
db.inventory.find( { "size.uom": "in" } )
db.inventory.find( { "size.h": { $lt: 15}, "size.uom": "in", status: "D" })
```

  

## 1.15  批量更新

```shell
#function内部是JavaScript的语法
db.getCollection('image_model').find({"dataset_id":20}).forEach(function(item){
	var list = [];
    var temp=item.image_url;
	for (var i=0;i<temp.length;i++){
    	var url = temp[i].replace(/\/\//g,'/');
        list.push(url);
	}
	db.getCollection('image_model').update({"_id":item._id},{$set:{image_url:list}})
});
```

 

## 1.16  插入语法

```shell
db.[collectionName].insert({})
db.sample.insert({name:"mongo"})
```



## 1.17  删除所有

```shell
db.[collectionName].remove({})  #集合的本身和索引不会被删除
```

 

## 1.18  条件刪除

```shell
db.sample.remove({name:"c"})
```

 

## 1.19  insertOrUpdate

```shell
db.[collectionName].update({查询器},{修改器},true)
第三个参数设置为true，代表insertOrUpdate，即存在即更新，否则插入该数据 
```

 

## 1.20  更新所有OR一条

```shell
#默认情况下，当查询器查询出多条符合条件的数据时，默认修改第一条数据。那么如何实现批量修改？ 
#语法：
db.[collectionName].update({查询器},{修改器},false, true)
#即添加第四个参数，该参数为true，则批量更新，为false，则更新一条
```

 

## 1.21  $set修改器

$set修改器用来指定一个键值对,如果存在键就进行修改不存在则进行添加。

```javascript
// 修改器名称：$set
// 语法：
{$set:{field: value}}
// example:
{$set:{name:"Redis"}}
```

## 1.22  inc修改器

inc修改器只是使用与数字类型,他可以为指定的键对应的数字类型的数值进行加减操作.

```javascript
// 修改器名称：$inc
// 语法：
{ $inc : { field : value } }
// example：
{ $inc : { "count" : 1 } }
```



## 1.23  unset修改器

unset修改器用法很简单,就是删除指定的键值对。

```javascript
// 修改器名称：$unset
// 语法：
{ $unset: { field : 1} }
// example：
{ $unset : { "age" : 1 } } 
 
```

## 1.24  $push修改器

```
push修改器用法：

1.如果指定的键是数组增追加新的数值

2.如果指定的键不是数组则中断当前操作Cannot apply $push/$pushAll modifier to non-array

3.如果不存在指定的键则创建数组类型的键值对

4.此方法可添加重复数据
```

 

```javascript
// 修改器名称：$push
// 语法：{ $push : { field : value } }
// example：
{ $push : { language:"Oracle"}
```



## 1.25  `$pushAll`修改器 

`$pushAll`修改器用法和`$push`相似他可以批量添加数组数据 
 即可以添加整个数组，如下：

```javascript
// 修改器名称：$pushAll
// 语法：{ $pushAll : { field : array} }
// example：
{ $pushAll : { database:["Oracle","MySQL"]}
```



## 1.26  `$addToSet`修改器 

`$addToSet`修改器是如果目标数组存在此项则不操作,不存在此项则加进去，即不添加重复数据。

```javascript
// 修改器名称：$addToSet
// 语法：{ $addToSet: { field : value } }
// example:
{ $addToSet: { database:"Oracle"}
```



## 1.27  `$pop`修改器 

`$pop`修改器从指定数组删除一个值1删除最后一个数值,-1删除第一个数值。

```javascript
// 修改器名称：$pop
// 语法：{ $pop: { field : value } }
// example:
{ $pop: { database:1}
```



## 1.28  `$pull`修改器 

`$pull`修改器是删除一个被指定的数值。

```javascript
// 修改器名称：$pull
// 语法：{ $pull: { field : value } }
// example:
{ $pull: { database: "Oracle"}
```



## 1.29  `$pullAll`修改器 

`$pullAll`修改器是一次性删除多个指定的数值。

```javascript
// 修改器名称：$pullAll
// 语法：{ $pullAll: { field : array} }
// example:
{ $pullAll: { database: ["MySQL","MongoDB"]}
```



## 1.30  `$`数组定位符 

`$`数组定位器,如果数组有多个数值我们只想对其中一部分进行操作我们就要用到定位器(`$`)

```javascript
// 修改器名称：$
// 语法：{ $set: { array.$.field : value} }
// example:
{ $set: { database.$.com : "sun"}
```



## 1.31  `$addToSet`与`$each`结合完成批量数组更新

```shell
db.sample.update({name:"evers"},{$addToSet:{database:{$each:["JS","DB","DB" ]}}})
#`$each`会循环后面的数组把每一个数值进行`$addToSet`操作 
```

 

## 1.32  runCommand函数和findAndModify函数 

runCommand可以执行mongoDB中的特殊函数,findAndModify就是特殊函数之一,他的作用是返回update或remove后的文档。

```shell
runCommand({"findAndModify":"processes",
        query:{查询器},
        sort{排序},
         new:true
        update:{更新器},
        remove:true
       }).value
// example:
ps = db.runCommand({
               "findAndModify":"sample",
               "query":{"name":"evers"},
               "update":{"$set":{"email":"1221"}},
               "new":true 
}).value
```

这里有一段摘自MongoDB权威指南的findAndModify函数的介绍：

```shell
 findAndModify的调用方式和普通的更新略有不同，还有点慢，这是因为它要等待数据库的响应。
 这对于操作查询以及执行其他需要取值和赋值风格的原子性操作来说是十分方便的。
 findAndModify命令中每个键对应的值如下所示。
 
 findAndModify 字符窜，集合名。
 
 query 查询文档，用来检索文档的条件。
 
 sort 排序结果的条件。
 
 update 修改器文档，对所找到的文档执行的更新。
 
 remove 布尔类型，表示是否删除文档。
 
 new 布尔类型，表示返回的是更新前的文档还是更新后的文档。默认是更新前的文档。
 
 “update”和”remove”必须有一个，也只能有一个。要是匹配不到文档，这个命令会返回一个错误。
 
 这个命令有些限制。它一次只能处理一个文档，也不能执行upsert操作，只能更新已有文档。
 
 相比普通更新来说，findAndModify速度要慢一些。大概耗时相当于一次查找，一次更新和一次getLastError顺序执行所需的时间。
 
 db.runCommand(“findAndModify”:集合名,”query”:{查询条件},”upadte”:{修改器})
```

 

##  1.33 内存不够处理方式

```shell
1、创建索引
#不阻塞创建索引
db.xxxxx.createIndex({"name":1,"sex":1},{"background":1})

2、增加sort memory

#查询当前值
db.runCommand({ getParameter : 1, "internalQueryExecMaxBlockingSortBytes" : 1 } )
#33504432

#设置新值 2倍  或者可以设置10倍
db.adminCommand({setParameter:1, internalQueryExecMaxBlockingSortBytes:67108864})
```

##  1.34使用mongodb嵌套js处理

```javascript
var list = db.getCollection('dataset_model').find({dataset_id_mysql:{$in:['a','b','c']},deleted:false},{_id:1}).limit(2000);
var arrs = list.toArray();
var arrnew = [];
for(var i=0; i<arrs.length; i++){
	arrnew.push(arrs[i]._id);
}

var aa = db.getCollection('annotation_model').find({dataset_id:{$in:arrnew},deleted:false}).limit(1000000)

aa.forEach(function(e){
    var segmentation = e.segmentation;
    var ext_data = e.ext_data;
    var creator = e.creator;
    if(segmentation !=null && segmentation.length>0 && creator!='user1' && 'user2'!=creator && 'user3'!=creator){
        if(ext_data !=null &&ext_data.points!=null && (ext_data.points.first_point.position==null||ext_data.points.first_point.position=='') && ext_data.occupied==null  &&ext_data.weather ==null){
			print({"image_id":e.image_id});
            //print(e);
        }
    }
})
```

 

 

 

 

 

 
