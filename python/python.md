# 1. tomcat运维

## 1.1 监控进程发送邮件

```python
from smtplib import SMTP_SSL
from email.mime.text import MIMEText
from email.header import Header
from os.path import getsize
from sys import exit
from re import compile, IGNORECASE
import sys, time
import os

#定义主机 帐号 密码 收件人 邮件主题
mail_info = {
    "from": "sender@sogoucloud.cn",
    "to": "reciver@sogoucloud.cn",
    "hostname": "smtp.exmail.qq.com",
    "username": "sender@sogoucloud.cn",
    "password": "123456",
    "mail_subject": "邮件标题",
    "mail_text": "邮件内容：tomcat挂了",
    "mail_encoding": "utf-8"
}

#发送邮件函数
def send_mail(error):
    #定义邮件的头部信息
    #连接SMTP服务器，然后发送信息
    smtp = SMTP_SSL(mail_info["hostname"])
    smtp.set_debuglevel(1)
    smtp.ehlo(mail_info["hostname"])
    smtp.login(mail_info["username"], mail_info["password"])
    msg = MIMEText(error, "plain", mail_info["mail_encoding"])
    msg["Subject"] = Header(mail_info["mail_subject"], mail_info["mail_encoding"])
    msg["from"] = mail_info["from"]
    msg["to"] = mail_info["to"]
    smtp.sendmail(mail_info["from"], mail_info["to"], msg.as_string())
    smtp.quit()
	
def isRunning(process_name):
    try:
        process = len(os.popen('ps aux | grep "' + process_name + '" | grep -v grep').readlines())
        if process >= 1:
            return True
        else:
		   return False
    except:
        print("Check process ERROR!!!")
        return False

#调用发送邮件函数发送邮件
if __name__ == '__main__':
	process_name = "tomcat_webapp"
	isrunning = isRunning(process_name)
	print(isrunning)
	if isrunning == False:
		send_mail("老铁!tomcat_webapp服务器挂了!")
```

添加crontab定时任务：

```shell
*/3 * * * * python  /usr/tools/qybd/cmd/sendEmail.py >> /usr/tools/qybd/cmd/tomcatlife.py.log 2>&1

#使用crontab -u root -l 命令查看当前运行的定时任务
```

## 1.2 监控日志发送邮件

```python
from smtplib import SMTP_SSL
from email.mime.text import MIMEText
from email.header import Header
from os.path import getsize
from sys import exit
from re import compile, IGNORECASE

#定义主机 帐号 密码 收件人 邮件主题
mail_info = {
    "from": "sender@vjsp.cn",
    "to": "reciver@vjsp.cn",
    "hostname": "smtp.exmail.qq.com",
    "username": "sender@vjsp.cn",
    "password": "123456",
    "mail_subject": "邮件主题",
    "mail_text": "邮件内容:服务器出现异常了,请及时处理!",
    "mail_encoding": "utf-8"
}

#定义tomcat日志文件位置
tomcat_log = '/opt/wms/tomcat_webapp/logs/catalina.out'

#该文件是用于记录上次读取日志文件的位置,执行脚本的用户要有创建该文件的权限
last_position_logfile = '/opt/wms/tomcat_webapp/logs/last_position.txt'

#匹配的错误信息关键字的正则表达式
pattern = compile(r'Exception|^\t+\bat\b',IGNORECASE)

#发送邮件函数
def send_mail(error):
    #定义邮件的头部信息
    #连接SMTP服务器，然后发送信息
    smtp = SMTP_SSL(mail_info["hostname"])
    smtp.set_debuglevel(1)
    smtp.ehlo(mail_info["hostname"])
    smtp.login(mail_info["username"], mail_info["password"])
    msg = MIMEText(error, "plain", mail_info["mail_encoding"])
    msg["Subject"] = Header(mail_info["mail_subject"], mail_info["mail_encoding"])
    msg["from"] = mail_info["from"]
    msg["to"] = mail_info["to"]
    smtp.sendmail(mail_info["from"], mail_info["to"], msg.as_string())
    smtp.quit()

#读取上一次日志文件的读取位置
def get_last_position(file):
    try:
        data = open(file,'r')
        last_position = data.readline()
        if last_position:
            last_position = int(last_position)
        else:
            last_position = 0
    except:
        last_position = 0
    return last_position

#写入本次日志文件的本次位置
def write_this_position(file,last_positon):
    try:
        data = open(file,'w')
        data.write(str(last_positon))
        data.write('\n' + "Don't Delete This File,It is Very important for Looking Tomcat Error Log !! \n")
        data.close()
    except:
        print "Can't Create File !" + file
        exit()

#分析文件找出异常的行
def analysis_log(file):
    error_list = []                                         #定义一个列表，用于存放错误信息.
    try:
        data = open(file,'r')
    except:
        exit()
    last_position = get_last_position(last_position_logfile) #得到上一次文件指针在日志文件中的位置
    this_postion = getsize(tomcat_log)                      #得到现在文件的大小，相当于得到了文件指针在末尾的位置
    if this_postion < last_position:                        #如果这次的位置 小于 上次的位置说明 日志文件轮换过了，那么就从头开始
        data.seek(0)
    elif this_postion == last_position:                     #如果这次的位置 等于 上次的位置 说明 还没有新的日志产生
        exit()
    elif this_postion > last_position:                      #如果是大于上一次的位置，就移动文件指针到上次的位置
        data.seek(last_position)

    for line in data:
        if pattern.search(line):
            error_list.append(line)
    write_this_position(last_position_logfile,data.tell())  #写入本次读取的位置
    data.close()

    return ''.join(error_list)                              #形成一个字符串

#调用发送邮件函数发送邮件
error_info = analysis_log(tomcat_log)
if error_info:
    send_mail(error_info)
```

添加crontab定时任务：

```sh
*/10 * * * * python  /usr/tools/qybd/cmd/tomcat_log_error_analysis.py >> /usr/tools/qybd/cmd/crontest.py.log 2>&1
```



# 2. python基础

## 2.1 指定语言和编码

```python
#!/usr/bin/python
# -*- coding: utf-8 -*-

#!/usr/bin/python 是用来说明脚本语言是python的，要用/usr/bin下面的python解释器，来解释和运行python脚本。
# -*- coding: utf-8 -*- 指定语言
```

指定语言的三种方式：

```python
# -*- coding: utf-8 -*-     第一种最常用
# coding: latin-1
# vim: set fileencoding=utf-8 :
```

## 2.2 linux部分命令

```shell
#a.txt中不区分大小写查找xxx，并且显示行号
grep -in xxx a.txt   
-i 不区分大小写
-v 取反 不匹配的
-n 显示行号

#echo 输出内容，一般与重定向连用：>> 追加  >新建
echo test > xxx  #新建了一个文件xxx，内容test
ls  >> xxx  #当前的目录结构追加到xxx中

#| :管道	左边写 右边读

shutdown 选项 时间
#-r 重启  -h 关机
shutdown -r now
shutdown 20:25
# 取消关机
shutdown -c 

# 连接
ln -s b/c.txt a.txt
ln -s /a/b/c.txt a1.txt
# 将c.txt软连接到a.txt

#查找
find -name "11" /

#查找python位置，添加软链接
which python
ls -lh /user/bin/python2.7
```

## 2.3 python语言的特点

- 编译性语言java等

- 解释性语言-python

一行一条命令，不能随便缩进，缩进会报错，python2.x不支持中文，2.x的最后版本是2.7

## 2.4 pycharm

https://www.jetbrains.com/pycharm/download/#section=windows

## 2.5 基础操作

运算符与表达式

```python
# 单行注释
""" 多行注释 """
''' 多行注释 '''
# +、 -、 *、 /、 **、 //、 %
# 加、减、乘、除、幂、除法取整、取模
```

变量与常量：

```python
变量定义：变量名=初始值 
初始值为了确定变量的类型，定义变量是为了存储数据，变量在使用前必须定义，否则会报错

删除变量 ：del 变量名
删除后变量无法引用

程序执行顺序从上到下 逐行解释执行（面向过程）
#查看变量a的首地址
print(id(a))

常量-程序运行期间不能改变的数据
a=18   #18就是常量
```

常用数字操作：

```python
# 整数
# 单个变量定义
num0=6
# 多个变量定义
num1=num2=num3=1
num4,num5=5,6     #num4=5    num5=6

# 浮点数
f1=1.1
f2=2.2
print(f1 + f2)

# 复数 实数部分+虚数部分

abs(-1) #绝对值
max(1,2,3,4) #返回最大值4
min(1,2,3,4) #返回最小值1
pow(2,5) #2的5次方
round(4.222,2) #四舍五入

# 导入math库
import math
math.ceil(18.1) #向上取整
math.floor(18.1) #向下取整
math.modf(22.3) #返回整数部分与小数部分的浮点数元组
math.sqrt(16)   #开平方

# 随机函数
import random
random.randint(0,100) #0-100之间随机取一个
random.choice([1,3,5,7,9]) #列表中随机选择一个，传入的是列表
random.choice(range(100))+1  #range(5)==[0,1,2,3,4...99]

import random
print(random.choice(range(10))+1)
print(random.randrange(1,100,2)) #1-100步长是2的随机数 最多是3个参数
print(random.randrange(10)) #0-9
print(random.random()) #0-1之间 包含0的一个随机小数
list=[1,3,5,6,7]  #就是一个列表
print(list)
#随机变一下顺序
random.shuffle(list)   #将列表的所有元素随机排序 意义
random.uniform(3,9)  #随机生成一个实数 小数或者整数 范围 3-9 包含二侧
```

运算符与表达式：

```python
表达式： 变量 常量  运算符  组成的式子
赋值运算符： 变量=表达式
算数运算符：
	+、-、*、/、**、//、%
	加、减、乘、除、幂、除法取整、取模
	a+=b;a-=b;.....a%=b;
判断：
	if:  elif:  else:  
	0, 0.0, '', None, False  # 这些都是假的，其他都是真

水仙花数：
	153=1^3+5^3+3^3
	num%10  # 153%10=3
    num//10%10  # 153//10%10=15%10=5   
			    # 15//10%10=1
回文数
	1234321
	max(1,2,3)

位运算符 把数字当成二进制计算
    & 按位与
    print(5 & 7)       #=5 都是1才是1
    & 按位或
    print(5 | 7)       #=7 1个是1就是1
    ^ 按位异或
    print(5 ^ 7)       #=2 2位相异为1 其他为0
    ~ 按位取反
    print(~5)       #=-6 每个二进制数据位取反
   
<<左移 各个二进制左移动若干位 左边舍弃  右边补0
>>右移 各个二进制右移动若干位 左边补0 右边舍弃

关系运算符:
	== >= <= != > <

关系运算表达式   
	表达式1  关系运算符  表达式2

关系运算符
	and or not

表达式优先级
	短路原则 and:一假后不计算   or:一真后不计算

字符串：单引号或者双引号引起来的就是字符串

字符串运算：
	拼接字符串 "123"+"456"
	字符串相乘  "123" * 3

通过索引下标查找字符 索引从0开始  
	a="123456"
	print(a[3]) #下表从0开始 4
字符串不可变 a[3]='a'会报错
截取字符串
	a="12345"
	b=a[2:3] #2包含  3不包含   下标从0开始
	b=a[2:]
	b=a[:4]
	b=a[3:len(a)-1]  #截取
	print("23" in a) #判断有没有
	print("xxx" not in a) #判断有没有
    
闰年条件：能被4整除不能被100整除  或者  能被400整除
print("""abc""")

```

### 2.5.1字符串操作

字符串不能修改

```python
eval(str) #将字符串当成有效的表达式来求值并返回
len(str) #返回字符串长度
str.lower() #小写
str.upper(str) #大写
str.swapcase() #大小写相互转换
'xx is a goo'.capitalize()  #首字母大写
'ab cd ef'.title()  #每个单词的首字母大写
'x'.center(40,'q') #填充字符串 40位，给定居中 两边填充q。先右后左
'1'.ljust(40,'0')  #左对齐  右补0  补齐40个0 默认不传fillchar 补空格
'1'.rjust(40,'0')  #右对齐  左补0  补齐40个0 默认不传fillchar 补空格
'x'.zfill(40)  #补0  补足40位 左补  右对齐
'abc abc abc'.count('abc')  #计数  待选参数3个 str,start,end 给定位置计数
'abcdefa'.find('a')  #返回第一次匹配下标，没有返回-1 可选 str,start,end
'abcdefa'.rfind('a')  #从右开始  返回第一次匹配下标，没有返回-1 可选 str,start,end
'abcdefa'.index('a')  #不存在会报错  更加严格，所以python 尽量用find()
'abcdefa'.rindex('a')
' abc'.lstrip()  #就是trim 待选可传 '***abc'.lstrip("*")
'abc   '.rstrip()
' abc   '.strip()
'bca'.split('c') #['b', 'a'] 分割成数组  二个参数的  第二d 是分割的个数 如果是2 分出来2个剩下的就不分了
'bca'.rsplit('c') #['b', 'a']
a='''
aaa
bbb
ccc
'''
a.splitlines([f]) #按照\r、\r\n、\n分隔返回,f默认false，不返回分隔符

#拼接
list1=['i','love','you']
str1=" ".join(list1)  #'i love you'

#min  max
min('abc')  #a
max('abc')  #c

#替换
'abbc'.replace('b','1')  #a11c 默认全替换
'abbc'.replace('b','1',1)  #a1bc 带参数只替换第一个

#创建一个映射表 这个意思是c-w h-i e-l n-l
#这种用的很少
maptab=str.maketrans('chen','will')
a='i am chen'
b=a.translate(maptab)   #i am wei

"abc".startswith('a',0,1)  #(str,start,end)
'abc'.endwith('c')  #以str结尾

#编码  解码--注意 解码要与编码一致，不一致会报错
#type("aa".encode())--<class 'bytes'>
data1='xxx'.encode()  #b'xxx'  
#二个参数 encoding="utf-8",errors='strict' 默认utf8 
data1.decode('utf-8')  #xxx  解码  文件操作的时候会用到。
#errors='ignore' 忽略错误
'xx'.isalpha()  #字符中至少一字符并且所有都是字母返回true
'xx'.isalnum()  #所有字符字母或数字
'xx'.isupper() #字符中至少一英文字母 所有英文字母全大写 返true
'xx'.islower()  #字符中至少一英文字母 所有英文字母全小写 返true
'xx'.istitle()  #标题化返回True 每个单词首字母大写
'xx'.isdigit() #只包含数字字符true
'xx'.isnumeric()  #只包含数字字符true
'xx'.isdecimal()  #判断十进制字符
'xx'.isspace()  #判断是不只包含空格 \t \n \r

a="12345"
b=a[2:3] #2包含  3不包含   下标从0开始
b=a[2:]
b=a[:4]
b=a[3:len(a)-1]  #截取

ord('a') #字符的ascii码  ord('a') 97
chr(97)  #ascii转字符
```

### 2.5.2 list 数组

list 数组 可以的数字 字符 ，下标从0开始 ，通过下标取对应值  pring(a[0])

```python
list1 = []
list1.append('1')
list1.append(2)
list1[0]='12'
len(list1)   # =3

# 列表乘法
list2 = list1 * 3
x=3 in list1  #成员判断

list3 = list2[2:5]  #list2[3:] list2[:4] 列表截取

# 二维数组
list11 = [[1,2,3],[4,5,6],[7,8,9],[10,11,12]]   #list11[1][2]

list1.append('1')
list1.extend([6,7,8])  #追加列表
list1.insert(2,'1')  #下标处添加一个元素 不覆盖原数据 原数据向后顺延

list1.pop()  #最后一个弹出
list1.pop(2)  #弹出指定下标的元素
list1[-1]    #就是取 list1的最后一个元素

list1.remove(1)  #移除列表中的某个元素  按照内容移除   匹配的第一个元素移除
list1.clear()  #清除

list1.index(3)  #找到第一个匹配值的下标，没找到报错

len(list1)  #长度
max(list1)  #列表最大值   有前提条件 里面元素必须是同类型 不然报错
min(list1)  #列表最小值   有前提条件 里面元素必须是同类型 不然报错

list1.count(1)   #1出现在list1中的次数
list1.reverse()  #把元素倒叙排序  直接改原
list1.sort()  #元素排序 升序

list1.copy()  #元素拷贝
list2=list((1,2,3,4)) #将元组转list

list2=list1
list1[1]=0  #发现list2  也跟着变了  用id(list1) id(list2) 去看地址，发现地址一样。
#其实list2=list1   是地址给定  是浅拷贝

栈区-系统自动分配 程序结束自动释放内存空间
堆区-程序员手动开辟  手动释放，实际高级程序已经实现自动释放
	我们的数据存放在堆区，他的地址0x100
	栈区存放一个list27-值是0x100
	          list28-值是0x100
```

### 2.5.3 元组

```python
a=()	# 一个元素的元组一定要加逗号
a=(1,)
a=(1,2,3,"good",True)
print(type(a))

print(a[0])  #取第一个
print(a[-1]) #取最后一个

# 元组不可变，一旦修改就报错a[0]=1会报错
# 元组内的list值可以变
a=(1,2,3,[1,2,4])
a[-1][0]='aa'

# 删除元祖
tuple6=(1,2,3)
del tuple6

t7=(1,2,3)
t8=(4,5,6)
print(t7+t8) #(1,2,3,4,5,6)
print(t7 * 3)  #(1,2,3,1,2,3,1,2,3)

# 判断元素在不在元组内
print(1 in t8)

# 元组截取： 元组名[开始:结束]  开始到结束前
print(t8[1:2])
print(t8[1:])
print(t8[:2])

# 二维元组 
tt=((1,2,3),(4,5,6),(7,8,9))
print(tt[1][1])

# 元组的方法
len(tt)  #元组元素个数
max(tt)  #最大
min(tt)  #最小

# 列表转元组
list1=[123]
tt=tuple(list1)

# 与list差不多  为什么要有个元组 它就是个不可变的列表  其实就是他安全，元组不能变，别人没法修改你的元组。无形当中增加了安全性。

# 元组遍历
for i in (1,2,3,4):
    print(i)
```

### 2.5.4 字典 dict

```python
#字典  dict 使用键-值去存储数据，查找速度极快
1、key必须唯一
2、key必须是不可变对象
3、字符串、整数都是不可变的，可以作为key
4、list是可变的 ，不能作为key 一般用字符串
#定义一个字典
dict1={"tom":60,'lilei':70}
#字典元素的访问  字典名[key]
dict1['tom']  #60 如果查找一个不存在的key 会报错
dict1.get('xxx')  #这种经常用 没有返回None 常用*********
#添加字典值
dict1['hanmeimei']=80
#修改字典值
dict1['lilei']=30
#删除
dict1.pop("tom")  
#遍历字典key
for key in dist1:
  print(key)
  print(dict1.get(key))
  
#遍历字典value
for value in dist1.values():
    print(value)
	
#遍历keyvalue   dict1.items() 拿出的是元组
for k,v in dict1.items():
    print(k,v)
	
#遍历  这种i打印的是个顺序  v是他的key
for i,v in enumerate(dict1):
    print(i,v)
```

- map快  list数据量大速度慢
- map浪费内存 list紧密排列不会浪费

### 2.5.5 set

set：类似dist 是一组key的集合 不存储value
本质：无序和无重复集合
创建set   需要一个list tuple 或者dict作为集合

```python
#重复数据在set是会被自动过滤
s1=set([1,2,3,4,5,3,4,5])
print(s1)  #{1,2,3,4} 自动过滤去重
s2=set((1,2,3,3,2,1));
print(s2)  #{1,2,3} 自动过滤去重
s3=set({1:"good",2:"nice"})
print(s2)  #{1,2} 自动过滤去重
#添加
s3.add(4)
s3.add([7,8,9])  #会报错，列表不能作为key key不能存可变对象
s3.add((7,8,9))  #可以  不会报错
s3.add({1:"a"})  #也会报错，字典也可变，不能作为key

#插入整个list tuple 字符串打碎插入
s3.update([6,7,8])  #这个没问题
s3.update((5,6))
s3.update("chenwei")  #'c','w','h'...

#删除  根据值直接删除
s3.remove(3)  

#无序没有下标，不能直接取某一个 可遍历拿
for i in s3:
    print(i)

#虽然有下标，但是没有实际意义，最主要是不能有重复元素
for index,data in enumerate(s3):
    print(index,data)

#set有二个概念  交集和并集
s8=set([1,2,3])
s9=set([2,3,4])
#求交集
s10=s8 & s9  #{2,3} 
#取并集
s11= s8 | s9  #{1,2,3,4}  

#list转set
list1=[1,1,2,3,4]
set1=set(list1)

#set转list
s3={1,2,3,4}
l3=list(s3)

#元组转set
t2=(1,2,3,3)
set2=set(t2)

#set转元组
set2={1,2,3}
t4=tuple(set2)

#字典和set转换用的少
```

### 2.5.6 迭代器

可迭代对象：可直接作用于for循环对象统称

Iterable 可迭代对象 isinstance()判断是否是可迭代对象
1、list tuple dict set str
2、就generator包括生成器和带yield的generator function

```python
from collection import Iterable
from collection import Iterator

print(isinstance([],Iterable))  #true
print(isinstance((),Iterable))  #true
print(isinstance({},Iterable))  #true
print(isinstance("",Iterable))  #true
print(isinstance((x for x in range(10)),Iterable))  #true
print(isinstance(1,Iterable))  #false

#迭代器 不但可以作用于for循环，还可以被next()函数不断调用并返回下一个值
#直到最后抛出一个stopIteration错误标识无法返回下一个值

#可迭代对象，可以被next函数调用，并不断返回下一个值的对象为迭代器(Iterator对象).
#可以使用isinstance 判断是不是可迭代对象
print(isinstance([],Iterator))  #false
print(isinstance((x for x in range(10)),Iterator))  #true
#只有range是true 是迭代器
l=(x for x in range(5))
print(next(l))  #0
print(next(l))	#1
print(next(l))	#2
print(next(l))	#3
print(next(l))	#4

l=(x for x in [2,33,45,66,78])  #也可以
print(next(l))  #0
print(next(l))	#1
print(next(l))	#2
print(next(l))	#3
print(next(l))	#4

# 转迭代器   list 字符串 元组 字典 都可以转
ll=[1,2,3,4,5]
a=iter(ll)
print(netx(a))

# 用处：
input("输入") 不能接回车  回车就结束了 怎么办，用迭代器

endstr="end"
str=""
for line in iter(input,endstr):
    str+= line + "\n"
print(str)
```



### 2.5.7 函数概述

某些功能反复使用，将功能封装成函数
当我们要使用功能时直接调用函数。
优点：
简化代码 增加复用
定义函数
格式：
def 函数名(参数列表):
    语句
	return 表达式

def:函数代码块    以def关键字开始
函数名：遵循标识符规则
参数列表：调用者给函数传入的信息，逗号分割
冒号：函数内容以冒号开始，并且缩进
语句：函数封装的功能
return：一般是用于结束函数的，还可以返回信息给调用者

注意return表达式 可以不写。不写相当于return None

```python
def myprint(a):
    print("chenwei ok!",a)
	return None

#函数定义完要调用，实参给形参赋值过程
#调用的死或坏即使没有参数 小括号也不能省略
myprint('test')
```

有返回值的函数：

```python
def add(a,b):
    return a+b
	
c=add(1,2)
```

值传递：传递不可变的
str tuple number

引用传递：传递可变类型
list dist set是可变的

引用传递和值传递是不一样的。
值传递不会改变原值
引用传递可以改变原对象内的值

关键字参数:让调用不关注参数顺序
c=add(b=1,a=3)

默认参数：调用函数如果没有传递参数则使用默认参数

```python
def myfunc(a=1,b=2):
    print(a,b)
```

如果有默认参数，必须将默认参数放在参数最后

```python
def myfunc(a,b=2):
    print(a,b)
```

不定长参数：能处理比定义时更多参数

```python
#这个*arr  会接收剩下的所有的参数
def func(name,*arr):
    print(name)
	for x in arr:
	    print(x)
func('a','b','c')

#加了星号(*)的变量存放所有未命名的变量参数，如果在函数调用时没有指定参数，它是一个空元组

def mysum(*xx)
    sum=0
	for x in xx:
	    sum+=x
	return sum

#还有一种传不定长参数方法,只接收按参数名传递 参数是个字典
def func2(**keyword):
    print(keyword)
    
func2(a=1,b=2)  #{'a': 1, 'b': 2}
# **代表键值对的参数字典 和* 代表的意义类似
```

```python
#下面这个可以接收任意参数
def func3(*args,**keyws):
    pass
```

匿名函数：匿名函数不使用def这样的关键字定义函数，使用lambda来创建匿名函数
特点：
1、lambda只是一个表达式，函数体比def简单
2、lambda的主体是一个表达式，而不是代码块，仅仅只能在lambda中封装简单逻辑
3、lambda函数有自己的命名空间，并且不能访问自有参数列表之外的或全局命名空间里的参数
4、虽然lambda是一个表达式看起来只能写一行，与c，c++内联不同
c，c++写内联为了增加速度
python没有效率

程序二种，数据密集  cpu计算
io密集，网络请求，文件读写。
python库多，方便，容易学习

格式：
lambda 参数1,参数2....参数n:表达式

```python
sum=lambda num1,num2:num1+num2
print(sum(1,2))
```

```python
a=10
b=10
print(id(a),id(b))  #二个地址是一样的
#原因是有常量区，常量存在常量区，原因是变量放的是地址
```

### 2.5.8 装饰器

装饰器：就是一个闭包 把一个函数当成参数 返回一个替代版的函数，本质上就是返回函数的函数

```python
# 简单装饰器：
def func1():
    print("chenwei is a good man")
def outer():
    print("*****")
	func1()
outer()  #这体现了装饰器的思想

def outAdept(func):
    def inner():
        print("******")
	    func()
	return inner;

# f是func1的加强版本	
f = outAdept(func1)
f()  #相当于执行了 inner() 没有修改func1 

# 复杂一点的装饰器
def say(age):
  print('aa is %d years old'%(age))

def outer(func):
    def inner(age):
	    pass
		if(age<0):
		    age=0
		func(age)
    return inner
say=outer(say)
say(-1)  #0
#如果say是别人的代码  要增强功能，用装饰器

#在def say(age):上 ，使用@将装饰器装饰函数
@outer 替代了 say=outer(say)

#通用装饰器
def outer(func):
    def inner(*args,**kwargs):
	    #在这添加要修饰的功能
	    func(*args,**kwargs)
	return inner
@outer
def say(name,age):
    print("name is %s,age is %d" %(name,age))
say('zhang',4)
#函数的参数实际是无限制的，但最好不要超过6-7个
```

### 2.5.9 偏函数

```python
print(int("1234",base=10)) #1234  base就是进制
print(int("1010",base=2))  #10 1010二进制10进制标识

def int2(str,base=2)
    return int(str, base)

print(int2("1011"))  #这就是偏函数，对默认值定义

#偏函数不需要我们自己写，引入就行了

import functools
#这个模块帮我们生成偏函数  固定int的参数base=2
int3=functools.partial(int,base=2)
```

### 2.5.10 变量的作用域

程序的变量不是所有位置都能使用，访问的权限取决于变量的位置
作用域:局部作用域、函数作用域、全局作用域、内建作用域

### 2.5.11 异常处理

异常处理：程序在运行过程中出现错误捕获和处理
处理客户错误输入或者网络传入，或者磁盘满等异常
当程序遇到问题，不让程序结束，越过程序，向下执行
try...except...else
格式：

```python
try:
    语句块
except 错误码 as e:
    语句
except 错误码 as e:
    语句2
except 错误码 as e:
    语句n
else:   #else可有可无  代码没有问题会执行
    语句e
```

如果当try内执行出现错误，会匹配第一个错误码，如果匹配上就执行对应语句
如果没有匹配的异常，错误将会被提交到上一层try语句，或者到程序最上层
如果没有出现错误，不会匹配任何异常，会执行else下的语句

BaseException	所有异常的基类
SystemExit	解释器请求退出
KeyboardInterrupt	用户中断执行(通常是输入^C)
Exception	常规错误的基类
StopIteration	迭代器没有更多的值
GeneratorExit	生成器(generator)发生异常来通知退出
StandardError	所有的内建标准异常的基类
ArithmeticError	所有数值计算错误的基类
FloatingPointError	浮点计算错误
OverflowError	数值运算超出最大限制
ZeroDivisionError	除(或取模)零 (所有数据类型)
AssertionError	断言语句失败
AttributeError	对象没有这个属性
EOFError	没有内建输入,到达EOF 标记
EnvironmentError	操作系统错误的基类
IOError	输入/输出操作失败
OSError	操作系统错误
WindowsError	系统调用失败
ImportError	导入模块/对象失败
LookupError	无效数据查询的基类
IndexError	序列中没有此索引(index)
KeyError	映射中没有这个键
MemoryError	内存溢出错误(对于Python 解释器不是致命的)
NameError	未声明/初始化对象 (没有属性)
UnboundLocalError	访问未初始化的本地变量
ReferenceError	弱引用(Weak reference)试图访问已经垃圾回收了的对象
RuntimeError	一般的运行时错误
NotImplementedError	尚未实现的方法
SyntaxError	Python 语法错误
IndentationError	缩进错误
TabError	Tab 和空格混用
SystemError	一般的解释器系统错误
TypeError	对类型无效的操作
ValueError	传入无效的参数
UnicodeError	Unicode 相关的错误
UnicodeDecodeError	Unicode 解码时的错误
UnicodeEncodeError	Unicode 编码时错误
UnicodeTranslateError	Unicode 转换时错误
Warning	警告的基类
DeprecationWarning	关于被弃用的特征的警告
FutureWarning	关于构造将来语义会有改变的警告
OverflowWarning	旧的关于自动提升为长整型(long)的警告
PendingDeprecationWarning	关于特性将会被废弃的警告
RuntimeWarning	可疑的运行时行为(runtime behavior)的警告
SyntaxWarning	可疑的语法的警告
UserWarning	用户代码生成的警告

错误:NameError...

```python
try:
    print(3/0)
except ZeroDivisionError as e:
    print("除数为0了")
else:
    print("代码没有问题")

#正常不使用任何错误类型,捕获所有的错误，只要有就处理
try:
    print(4/0)
except:
    print("程序出现了异常")
	
#使用except带着多种异常
try:
    print(5/0)
except (NameError,ZeroDivisionError):
    print('出现了a或b异常')

#特殊BaseException是所有异常的父类 如果捕获它后面就不执行了
#跨越多层调用
def func1(num):
    print(1/num)
def func2(num)
    func1(num)
def main():
    func2(2)
try:
    main()
except:
    print("跨越多层调用异常可以捕获")
#多层嵌套，里面的错了，外面捕获就可以了

#语句无论是否有错误 都执行finally之后代码
try...except...finally
try:
    语句1
except:
    异常处理
finally:
    关闭文件等

#断言 
def func(num, div):
    assert (div!=0), "div 不能为0"
	retur num/div
print(func(10,0))
```



## 2.6 常用函数

```python
# 打印
a=8.4 
b=4
c=a*b
print(c)

# 查看类型
# python不需要指定数据类型，会根据=右侧自动指定准确的数据类型
name="小明"  #字符串类型 str
age=15  #int 整数
weight=75.00  #float 浮点
gender=True  #bool True False 

'''
  数据类型：数字类型和非数字型
	整型 int
	浮点 float
	布尔型 bool
	复数型 complex 主要用于科学计算 例如 平面场问题 波动问题 电感电容等问题

  非数字
	字符串
	高级-列表
	高级-元祖
	高级-字典
'''	

# python中根据数字存储长度，分为 int 和  long

# 用type(a) 可以查看变量类型
5+True=6

# 字符拼接 和 字符串乘数字
a="张"
b="三"
c=a+b
c * 10 #张三张三张三张三张三张三张三张三张三张三 

# python 通过 input() 方法获取输入
# input()接的都是str
# int()接的都是int
# float() 接的都是float
qc=input()
qc=input("please input the size")
# 输入的类型都是str，但可通过int("123")  float("123.0") 转成数字

# 转字符串
str(123.33)

#判断是不是数字
a='a1b2'
if a[1]>='0' and a[1]<='9':   #这样就判断是数字了

#字符串比较 是按位比较的 从头比较 比较的是ascii码

```

## 2.7占位符

```python
# 变量格式化输出
%s 文本 
%d 整数 
%f 小数 
%% 百分号

name="chenwei"
print("hello %s nice to meet you!" %name)

name1="chenwei1"
print("hello %s,%s nice to meet you!" %(name,name1))

no=1
print("you no is %06d" %no)   # %06d ，6是最长位数，不到6位前面用0补位。

xx=1.0
xx1=2.0
print("%f元,%.2f元" %(xx,xx1))   # %.2f ，会四舍五入，比如2.3465=2.35

a="100"
print("%s%%" %a)  #输出 100%
```

## 2.8标识符

标识由 数字、字母、下划线组成，不能以数字开头，不能关键字作为标识符

```python
#查看所有关键字
import keyword      #导入到内存中
print(keyword.kwlist)
```

变量命名，小写下划线连接不同单词。

## 2.9条件判断

```python
# 判断语句 if elif else
if age>=18 :
    print("bigger than 17")
elif age<=14 :
    print("smaller than 14")
else:
    print("else")
```

## 3.0 python逻辑运算符

- and  并且
- or  或者
- not  非

使用条件判断+逻辑运算，实现石头剪刀布

```python
#-*- coding:utf-8 -*-
import random
player = int(input("您输入的是 1 2 3 石头 剪刀 布"))
computer = random.randint(1,3)  #电脑随机出拳
print("computer is %d"%computer)
if ((player==1 and computer==2 or (player==2 and computer==3) or (player==3 and computer==1))):
    print("你赢了")
elif player==computer:
    print("平了")
else:
    print("您输了")
```

## 3.1 while、for

```python
i=1
while 条件:
    #执行代码
    pass
	i++
# break 退出循环
# continue 结束本次循环 进行下次循环

l = [1, 2, 3, 4]
for i in l:
    print("输出的是:%d" % i)
i = 1

while True:
    print(str(i))
    i += 1
    if(i>10):
        break

# pass是python的占位语句  什么也不做的 保持结构
```

while 可以 和else连用，但是如果是break 跳出的循环 不会执行else语句

```python
a=1
while a<3:
    print("1")
	a+=1
else:
    print(2)
```

range() 函数  列表生成器,生成数列   

```python
range(100)  #0-99
range([start,] end [,step])  # 3个参数 起点 终点  步长  
range(2,200,2) #
```

```python
#同时遍历下标和元素
for index,m in enumerate([1,2,3,4]):
    print(index,m)
```



## 3.2 赋值运算符

```python
# =  += -= *= /= //= %=		2个符号之间挨在一起
c += a   # c = c+a
```

## 3.3 转义函数

```python
#\n换行  \t制表  \\ \' \"  #转义字符\
print("\"i\" am jim")  # \t  \n \\
print(r"\\\\tttt") #加了个r 没有转义字符了 就用这个
```

## 3.4 安装图形easygui

```sh
gui是图形用户接口
需要将easygui.py放在  python安装路径的lib里面
C:\Python27\Lib

pip install easygui
https://sourceforge.net/projects/easygui/files/0.96/easygui-0.96.zip/download
```

## 3.5 python应用范围

科学计算、人工智能、web服务器端和大型网站后端、gui开发、游戏开发、移动设备、嵌入式设备、系统运维、大数据、云计算
不适用：性高要求高不用python

配置path

```
C:\Python27;C:\Program Files\Python38\Scripts\;C:\Program Files\Python38\;
```

## 3.6 数据类型

```python
# 元组 tuple
("a","b","c")
# 整数 int
# 浮点数 float 
# 复数（不常用）
# 字符串 str
# 布尔 True False
# 空值 None
# 列表 list
# 字典 dict
# 集合 set
```

## 3.7计算机基础

```python
# 10进制转2进制，倒除法，余数逆序
10/2=5余0 5/2=2余1 2/2=1余0  1/2=0余1   最终结果1010

# 2进制转10进制
1010 2进制转10进制  10	当前位数字*2的位数次幂

# 8进制转2进制
65 -> 110101
1位变3位 
6       5
110     101 

# 16转2进制 1位转4位 转换时按10进制转
14
1010	0100

# 2进制转8进制 3位一取
110 101 -> -65
11 110 101 -> -365

# 2进制转16进制 4位一取
110101 -> -35
0011 0101

'''
最高位，1负数，0正数
4个字节，32个空间，每个字节占8位	00000000000000000000000000001010
源码：规定了数据，最高位(最左边)代表符号位，0是正数，1是负数。
反码：正数的反码就是他本身；负数的反码是其符号位不动，其余取反。
补码：反码+1

如1：00000000000000000000000000000001
1的原码==反码==补码  00000000000000000000000000000001
	
-1的源码：10000000000000000000000000000001
-1的反码：11111111111111111111111111111110
-1的补码：11111111111111111111111111111111

计算机不是以源码存放数据，计算机里放的是补码
1+（-1）
00000000000000000000000000000001
11111111111111111111111111111111
00000000000000000000000000000000
'''
```

## 3.8 turtle绘图

简单的绘图工具  提供了一个小海龟 理解一个小机器人

听的懂有限的命令，直线 左转 

默认远点是图片中间 0 0的位置 方向向右

```python
1、运动命令 
t=turtle.Pen()
t.forward(d) 向前移动d长度
t.backward(d) 向后移动d长度
t.left(30)  #向左转动30度
t.right(30) #向右转动30度
t.goto(x,y)  #移动到坐标为x,y的位置
t.speed(10)  #控制速度

2、笔画控制命令 
up() #笔画抬起不会绘图，只是路径变化 不画东西
down()  #笔画落下绘图
t.setheading(d)  #改变海龟的朝向
t.pensize(d)   #笔粗细
t.pencolor(colorstr)  #颜色 "red"
reset()  #清空窗口 重置turtle状态
clear()  #清空窗口  不重置状态
circle(r,e)  #绘制圆型  r半径  e是次数  如果是5  就是5边型

begin_fill()
fillcolor(colorstr)
end_fill()

3、其他命令
done()
undo()  #撤销
hideturtle  #隐藏海龟
showturtle  #显示海龟
screensize(400,400)  #控制什么尺寸

import turtle
turtle.done() #程序执行完停在那   出现一个画板  一直不退出
```

## 3.9 歌词分析

```python
geci="""[04:40.75][04:41.75]lalala
[04:42.75]我是卖报小行家
[04:43.75]不到天明去卖报"""
dist1=[]
list1=geci.splitlines()  #[04:40.75][04:41.75]lalala
for line in list1:
    for i in ln:
	    dist1[i]=ln[-1]

import time
time.sleep(1)  #1秒一次
```

## 4.0 文件读写

读文件,python内置了好多读写文件方法 和C是兼容的
文件的读写是不能直接通过程序控制，通过操作系统实现的
#读文件的过程 

1. 打开文件 

```python
open(path,flag[,encoding][,errors][,][,])
path：文件路径
flag：打开文件方式 
r  只读打开，文件描述符放在文件开头-常用
rb 以二进制只读打开,描述在开头-常用
r+  打开一个文件用于读写.描述在开头
w 打开一个文件 只用于写入,描述在开头,文件存在会覆盖,不存在新建-常用
wb 打开一个二进制文件 只用于写入,描述在开头,文件存在会覆盖,不存在新建-常用
w+ 打开一个文件用于读写
a 打开文件用于追加,文件存在追加,文件描述符在文件最后
a+ 打开一个文件用于读写追加
encoding：编码方式 utf-8 gbk
errors：错误处理-常用不处理ignore

p=r'D:\python\space1\shipin\a.txt'
f=open(p,'r',encoding="utf-8",errors="ignore")
```

2. 读文件内容

```python
读文件有多种方式
读取文件的全部内容
str1=f.read()  #全部读取，这种方式适合小文件 大文件不行
str2=f.read(10)  #读取10个字符内容  从文件里
str4=f.readline()  #一行一行读取
str5=f.readline(10)  #读取一行10个字符
list1=f.readlines()  #读取所有行返回列表
list2=f.readlines(25)  #读取给定数字所占的行数，根据行数 读取行数 返回在list里
#如果已经读完了，还想读，修改描述符的位置
f.seek(0)  #描述符换到0位置，可以重新读了
```

3. 关闭文件

```python
f.close()
```

```python
#with自动关文件  with很有用

with open(f,'r') as f2:
    print(f2.read())
    
#写文件
p=r'D:\python\space1\shipin\a.txt'
f=open(p,'w',encoding="utf-8",errors="ignore")
#追加
#f=open(p,'a',encoding="utf-8",errors="ignore")

#写文件是将信息写入缓冲区，不是直接写入文件
f.write('chenwei is a good person')
#真要写入文件要刷新缓冲区，是手动刷新，不是被动等待程序结束
f.flush()
#缓冲区满会自动刷新缓冲区
#遇到换行会刷新---这个没实现-看不出来

f.close()  #关闭文件的时候 会刷新缓冲区    
```

编码和解码,wb打开的 必须编码或者解码

```python
# 编码：
p=r'D:\python\space1\shipin\x.txt'
with open(p,"wb",encoding="utf-8") as f1:
    str="chenwei 123456"
    f1.write(str.encode("utf-8"))
# 解码
with open(p,"rb",encoding="utf-8") as f2:
    data=f2.read()
    print(data)
	newData=data.decode("utf-8") #解码
```

list set dict tuple的文件操作:

```python
import pickle #数据持久模块
#我们可以在多外文件中使用数据
#list会自动编码  已经不是字符串了
mylist=[1,2,3,4,5,'1','2','3','4','5']
p=r'D:\python\space1\shipin\x.txt'
f=open(p,"wb")
pickle.dump(mylist,f)
f.close()

#读出来
p=r'D:\python\space1\shipin\x.txt'
f=open(p,"rb")
templist=pickle.load(f)
print(templist)
f.close()

#list换成tuple dict set都行
```

## 4.1 os模块

包含了普遍的操作系统功能，也可以处理文件

```python
import os
print(os.name)  #获取操作系统name nt-window,posix-linux unix ,Mac OS X 
#print(os.uname())  #打印操作系统的详细信息，windows不支持
print(os.environ)  #获取操作系统的环境变量
print(os.environ.get("JAVA_HOME"))  #获取指定环境变量
print(os.curdir)  #获取当前目录
print(os.getcwd())  #获取当前工作目录，当前脚本所在的目录
print(os.listdir(path)) #获取path下的文件和目录
os.mkdir('muluming') #当前工作目录创建一个文件夹
os.mkdir(r'D:\python\space1\shipin\xxx.txt')
os.rmdir('muluming')  #删除
print(os.stat("xxx.txt")) #获取文件的属性
os.rename('xxx.txt','chenwei.txt')  #重命名
os.remove('chenwei.txt')  #删除文件

##
os.system("notepad")  #可以运行shell命令
os.system("write") #写字板
os.system("mspaint") #画板
os.system("msconfig") #系统设置
os.system("shutdown -s -t 500")  #关机
os.system("shutdown -a")  #取消关机
os.system("taskkill /f /im notepad.exe")

##有些存在os里  有些存在os.path里
#1查看当前的绝对路径 
os.path.abspath(".")

#拼接路径
p1=r'D:\python\space1\shipin'
p2='chenwei'
#参数2里开始不要有斜杠，后面有可以
print(os.path.join(p1,p2))

#拆分路径
p1=r'D:\python\space1\shipin'
print(os.path.split(p1)) #拆分出一个元组 path 和最后的文件名

#获取扩展名 元组最后一个元素
tuple1=os.path.splittext(p1)

#判断是目录
os.path.isdir(path) #true False

#判断文件是否是文件（存在）
os.path.isfile(path3)

#判断目录是否存在
os.path.exists(path4)

#获取文件大小 字节
print(os.path.getsize(p5))

#获取文件目录
print(os.path.dirname(path))

#获取文件名
print(os.path.basename(path))
```

## 4.2 操作win32

memsearch.exe
spy.exe
PCHunter64.exe
控制窗体的显示和隐藏

```sh
pip install pypiwin32  #安装模块
```

```python
import win32con
import win32gui
import time
#找出窗体的编号 类通过标题获取
#这二个通过spy.exe找到的
#类"TXGuiFoundation",标题"QQ"
QQWin=win32gui.FindWindow('类','标题')
#隐藏
win32gui.ShowWindow(QQWin,win32co.SW_HIDE)
#显示
win32gui.ShowWindow(QQWin,win32co.SW_SHOW)

while True:
    win32gui.ShowWindow(QQWin,win32co.SW_HIDE)
	time.sleep(2)
	win32gui.ShowWindow(QQWin,win32co.SW_SHOW)
	time.sleep(2)
```

控制窗体位置和大小

```python
import win32con
import win32gui
import time
import random
QQWin=win32gui.FindWindow('TXGuiFoundation','QQ')
#参数1要控制的窗体 
#参数2位置
#参数3 位置x
#参数4 位置y
#参数5 位置长度
#参数5 位置宽度
win32gui.SetWindowPos(QQWin,win32conn.HWND_TOPMOST,100,100,300,300,win32con.SWP_SHOWWINDOW)

while True:
    x=random.randrang(900)
	y=random.randrang(600)
	win32gui.SetWindowPos(QQWin,win32conn.HWND_TOPMOST,x,y,300,300,win32con.SWP_SHOWWINDOW)
```

window 后台运行

```sh
start /b python aaa.py
```

医院的叫号系统

语音合成  win10   win7报错是因为没有语音组件 去网上下个语音识别   ghost都去掉了语音这块了

```sh
pip install pypiwin32
```

```python
import win32com.client
dehua=win32com.client.Dispatch("SAPI.SPVOICE")
dehua.Speek('chenwei is a good man')
```

语音模块   ****要启动语音识别****

```python
from win32com.client import constants
import os
import win32com.client
import pythoncom

class SpeechRecognition:
    def __init__(self,wordsToAdd):
	    self.speaker=win32com.client.Dispatch("SAPI.SpVoice")
		self.listener=win32com.client.Dispatch("SAPI.SpShareRecognizer")
		self.context=self.listener.CreateRecoContext()
		self.grammar=self.context.CreateGrammar()
		self.grammar.DictationSetState(0)
		self.wordsRule=self.grammar.Rules.Add("wordsRule",constants.SRATopLevel+constants.SRADynamic,0)
		self.wordsRule.Clear()
		[self.wordsRule.InitialState.AddWordTransition(None,word) for word in wordsToAdd]
		self.grammar.Rules.Commit()
		self.grammar.CmdSetRuleState('wordsRule',1)
		self.grammar.Rules.Commit()
		self.eventHandler = ContextEvents(self.context)
		self.say("Started successfully")
	def say(self,phrase):
	    self.speaker.Speak(phrase)
		
class ContextEvents(win32com.client.getevents("SAPI.SpShareRecoContext")):
    def OnRecognition(self,StreamNumber,StreamPosition,RecognitionType,Result):
	    newResult=win32com.client.Dispatch(Result)
		print("说：",newResult.PhraseInfo.GetText())
		s=newResult.PhraseInfo.GetText()
		if s=='记事本':
		    os.system("notepad")
			
if __name__= '__main__':
    wordsToAdd=['关机','取消关机','记事本','图画板','写字板','设置','关闭记事本']
	speechReco=SpeechRecognition(wordsToAdd)
	while True:
	    pythoncom.PumpWaitingMessages()
```

内存修改

```python
import win32process  #进程
import win32conn  #系统
import win32gui #窗口
import win32api  #window api
import ctypes #c语言类型转换
#最高权限
PROCESS_ALL_ACCESS=(0x000F0000|0x00100000|0xFFF)

#找窗体
win=win32gui.FindWindow("MainWindow","植物大战僵尸")
#根据窗体找到进程号
hid,pid=win32process.GetWindowThreadProcessId(win)
#以最高权限打开进程
p = win32api.OpenProcess(PROCESS_ALL_ACCESS)

#c语言的 c_long 类型
data=ctypes.c_long()

#加载内核模块
md = ctypes.windll.LoadLibrary("c:\\Windows\\System32\\kernel32")
#读取内存   311944712为要修改的内存地址
md.ReadProcessMemory(int(p),311944712,ctypes.byref(data),4,None)
print("data=",data)

#修改内存值  4写4个字节的，None 错误信息
newData=ctypes.c_long(10000)
md.WriteProcessMemory(int(p),311944712,ctypes.byref(data),4,None)
```

PythonWin-tool-COM makepy utiTity
找到Microsoft Speech Object library[5.4]

## 4.3 加载语音模块

1.更新pip
pip --version	查看pip版本
python -m pip install --upgrade pip	更新pip

2.安装python的PIL(图像处理库)
pip install Pillow

3.python应用导入PIL
import PIL.Image

安装语音模块
pip install speech

## 4.4递归

递归调用：一个函数，调用了自身，成为递归调用
递归函数：一个会调用自身的函数称为递归函数
凡是循环能干的事递归都能干
方式：
1写出临界条件
2找这一次和上一次的关系
3假设当前函数已经能用，调用自身计算上一次结果，再求出本次结果

```python
def sum(n):
    sum=0
	for x in range(1,n+1):
	    sum+=x
	return sum

def sum11(n):
    x=0
    if n>0:
        x=n+sum11(n-1)
    return x

def sum11(n):
    if n==1:
	    return 1
	else:
        return n+sum11(n-1)
sum11(5)
```

## 4.5栈和队列

栈:
栈可以存数据-先进后出 或者说 后进先出
模拟栈结构

```python
stack=[]
#入栈
stack.append('a')
stack.append('b')
stack.append('c')
#出栈
c=stack.pop()
b=stack.pop()
a=stack.pop()
```

队列:
队列先进先出
队列是有结构的

```python
import collections
queue = collections.deque()
print(queue) #创建一个队列 deque([])
queue.append('a')
queue.append('b')
queue.append('c')
#出队列
a=queue.popleft()
b=queue.popleft()
c=queue.popleft()
print(a,b,c)
```

## 4.6递归 队列

应用于文件循环  爬虫

递归遍历目录

```python
import os
def getAllDir(path,sp=""):
    finallist=[]
    flist=os.listdir(path)
	sp += '   '
	for fileName in flist:
	    abspath=os.path.join(path,fileName)
	    if os.path.isdir(abspath)
		    print(sp+'目录：',fileName)
		    finallist.extend(getAllDir(abspath))
			
		else
		    print(sp+'文件：',fileName)
		    finallist.append(abspath)
    
	return finallist
getAllDir(r'd:\dir')
```

## 4.7栈模拟遍历-深度遍历

把文件想象成树，处理根，进栈，二级进栈，二级出栈一个，如果这个有子
子的进栈，然后子出栈，判断子的有没有子，有了进栈，
也就是从一个叶子深度到底。

```python
def getAllDirDeep(path,sp=""):
    stack=[]
	stack.append(path)
	while len(stack) != 0:
	    dirPath=stack.pop()
		flist=os.listdir(dirPath)
	    for fileName in flist:
		    abspath=os.path.join(path,fileName)
			if os.path.isdir(abspath)
		        print(sp+'目录：',fileName)
		        #finallist.extend(getAllDir(abspath))
			    stack.append(abspath)
		    else
		        print(sp+'文件：',fileName)
		        #finallist.append(abspath)
				
getAllDirDeep(r'd:\dir')
```

## 4.8用队列处理文件循环  广度遍历

就是利用队列 一层一层处理
先进队a然后进队bc处理b进队ed处理c进队fg

```python
import collections

print(queue) #创建一个队列 deque([])
#queue.append() queue.popleft()
def getAllDirGd(path,sp=""):
    queue = collections.deque()
	queue.append(path)
	while len(queue) != 0:
	    dirPath=queue.popleft()
		flist=os.listdir(dirPath)
	    for fileName in flist:
		    abspath=os.path.join(path,fileName)
			if os.path.isdir(abspath)
		        print(sp+'目录：',fileName)
		        #finallist.extend(getAllDir(abspath))
			    queue.append(abspath)
		    else
		        print(sp+'文件：',fileName)
		        #finallist.append(abspath)
				
getAllDirGd(r'd:\dir')
```

## 4.9 时间

```python
import time
c = time.time()  #获取当前时间戳 当前到-1970-01-01 浮点数
t = time.gmtime(c)  #格林威治时间元组
b = time.localtime(c)  #获取本地时间元组
#将本地时间转成时间戳
m=time.mktime(b)
#将时间元组转成字符串 Fri jul 28 14:00:00 2020
time.asctime(b)
time.ctime(c)#将时间戳转为字符串 Fri jul 28 14:00:00 2020
#"%Y-%m-%d %H:%M:%S"="%Y-%m-%d %X"
time.strftime("%Y-%m-%d %H:%M:%S")  #当前时间
q=time.strftime("%Y-%m-%d %H:%M:%S",b)  #时间元组转指定格式字符串

#时间字符串转时间元组
w=time.strptime(q,"%Y-%m-%d %X")

time.sleep(4)  #睡4秒

#unix返回全部运行时间  windows返回从第二次开始，都是以第一次调用函数的开始时间戳作为基数
time.clock()  #会返回当前程序的cpu执行时间
time.clock()  #连续2次  第二次就是我要的
#第一次相当于0，第二次就是到第一次时间
```

datetime
基于time进行了封装，提供了函数，提供接口更简单
模块里有几个类：
	datetime-常用 同时有时间和日期
	timedelta 主要用于计算时间跨度
	tzinfo  时区相关的
	time  时间
	date  日期

```python
import datetime
#当前时间2017-07-28 00:00:00.123456 类型是个类不是字符串  datetime.datetime
d1=datetime.datetime.now()  
#指定一个时间
d2=datetime.datetime(1999,02,02,23,59,59,123456)  
#时间转字符串
d3=d1.strftime("%y-%m-%d %X")
#将格式化字符串转datetime的对象类型
d4=datetime.datetime.strptime(d3,'%y-%m-%d %X')

#datetime类型可以进行加减
d5=datetime.datetime(1999,02,02,23,59,59,123456)  
d6=datetime.datetime.now()
#间隔天数
d7=d6-d5  #类型 timedelta  6140 days,
#间隔天数外秒数
print(d7.days)
```

time 和 datetime之间转换同构字符串

日历模块  calendar

```python
import calendar

#打印某年  某月的日历
print(calendar.month(2017,7))
#返回指定年的日历
print(calendar.calendar(2017))
#判断闰年 返回 True False
print(calendar.isleap(2000))
#返回(5,31) 返回某个月的weekday的第一天和月的天数
print(calendar.monthrange(2017,7))
#返回某个月以周为元素的列表
print(calendar.monthcalendar(2017,7))
```

## 5.0 模块

代码较少，写在一起看不出缺点，随着代码增多，代码难以维护
为了解决维护，我们把相似功能函数放在一个文件种，形成一个模块，一个py文件
就是一个模块
优点：
提高代码可维护
提高复用，可被多个地方引用
引用其他模块-内置模块、三方模块，自定义模块
可避免函数名和函数名冲突

```python
#引入标准模块-这个是标准库 针对黑屏终端
import sys
#黑屏里接收参数用的  列表[] 多个参数 命令后传入的 a 空格 b空格
#第一个参数是py程序路径，后面是参数
print(sys.argv)  

#打印的是查找所需模块的路径的列表
print(sys.path)

import time
import datetime
#这些模块引入就可以直接使用了
```

创建一个自定义模块

```python
a.py  这就是一个自定义模块
def sayGood():
    print(1)
def sayNice():
    print(2)
tt=100

#在另外一个文件里调用，会报错  
#格式：import module[,module2][,module3]
#import a 直接引入会报错-可忽略，实际可用
#一个模块只会被引入一次
import a  #a和当前方法py同路径
a.sayGood()
print(a.tt)

#第二种引入  from import  引入一部分
from a import sayGood,sayNice...
#a.sayGood() --会报错。直接调用引入部分
sayGood()
#程序内的同名函数会将引入覆盖
#第三种  73集
from...import *  #最好不要过多使用 内部方法容易被覆盖
```

模块的结构

```python
#__name__属性 前后各二个下划线
#模块本身就是一个可执行的.py文件
#import a 引入一个模块时，模块所有的非def内都被执行
#但是我们不想执行那些零散代码，这时用__name__控制哪些可被执行
#每一个模块都有一个__name__属性

#当它的值='__main__'表明该模块自身在执行

#__main__是程序入口
if __name__=="__main__":
    print('123')
else:
    print(__name__)  #a-----a.py

#正常的写代码

def main():
    pass
if __name__=="__main__":
    main()
```

## 5.1 循环文件并处理

```python
def work(abspath):
	with open(abspath,'r') as f:
        while True:
		    #abc@163.com----password
            lineinfo = f.readline()
			mailstr=lineinfo.split('---')[0]
			dirstr=mailstr.split('@')[1].split('.')[0]
			respath=r'd:\youjian'
			dirpath=os.path.join(respath,dirstr)
			if not os.path.exists(dirpath):
			    os.mkdir(dirpath)
			filepath=os.path.join(dirpath,'126.txt')
            with open(filepath,'a') as fw:
                fw.write(mailstr+'\n')
```

## 5.2 包

不同的人写的代码 模块同名怎么办
为了解决模块命名冲突，引入按目录组织木块的方法
引入包以后，只要顶层包不冲突，模块不会冲突
#同py在不同path下 通过包应用指定的
新建2个目录，但是目录不是包
目录只有包含一个叫做__init__.py的文件才被认作是一个包
目前 __init__.py里什么都不用写

```python
import a.test
import b.test
a.test.sayGood()
b.test.sayGood()
```

## 5.3 安装第三方模块

```sh
#Pillow 强大的处理图像的数据库
pip -V
pip install Pillow
#执行升级pip
pip install --upgrade pip
```

```python
from PIL import Image

im=Image.open("111.jpg")
print(im.format,im.size,im.mode)
im.thumbnail(150,100) #设置图片大小
#另存图片
im.save("temp.jpg","jpeg")
```

## 5.4 类的设计

类名：首字母大写，其他遵循驼峰原则
属性：见名知意，遵循驼峰
行为：见名知意，遵循驼峰

类不占用内存空间，类的实力化是占空间
格式：
class 类名(父类列表):
    pass
	#属性
	#行为

```python
#object 基类 超类，认为是所有类的父类
class Person(object):
    age=0
	name=""
	#注意 方法的参数必须以self当第一个参数
	#self 代表类的实例，或者说某个对象
	#写在类中的函数，第一个参数都是self
	def getName(self):
	    return name
	def getAge():
	    return age
	def run(self):
	    print('run')

#使用类来实例化对象
per = Person()
print(per)
per.name='张三'
per.getName()
print(per.age)
per.run()

#构造函数：
#__init__() 在使用类创建对象时自动调用
#注意：如果不显示的写构造函数，默认会自动加
#一个空的__init__(self)方法有一个参数self是不用传的

def __init__(self,name,age):
    print(name,age)
	self.name=name
	self.age=age
	#这里的self是创建的对象

#创建对象时传入的参数实际是给__init__传的
per1=Person('chenwei'35)
print(per1.name,per1.age)
per2=Person('xuefeng'34)
print(per2.name,per2.age)

#****如果用这个，上面的属性就没有了。
#__init__里面的 self.name  self.age 就变成了对象的属性
#以后就不用单独定义属性了
```

## 81 类设计2

```python
#self代表类的实例，而非类
#哪个对象调用方法，那么该方法是的self就代表那个对象
#self.__class__代表类名
class Person(object):
	def getName(self):
	    return name
	def getAge():
	    return age
	def run(self):
	    print('run-name is:',self.name)
		print(self.__class__)
	def __init__(self,name,age):
    print(name,age)
	self.name=name
	self.age=age
per1=Person('chenwei'35)
per1.run() #这里的self代表  per1
per2=Person('panpan'35)
per2.run() #这里的self代表  per2

#self 不是关键字，可以改名字，尽量不要改

per3=self.__class__('其他创建类',16)

#析构函数：
#__del__() #释放对象的时候调用的
def __del__():
    print('这里是析构函数')

per=Person('aa',12)
del per  #这里删除对象会调用对象的析构函数
#堆区的内存正常应该手动释放，python有自动回收机制
#我们在创建对象的时候，有一个引用计数器，引用一次+1 释放一次-1 =0自动释放了

#我们手动释放的话 用del
#del和程序结束都会执行触发析构函数
#对象释放以后 del就不能再访问了

#还有一种释放，在一个方法函数里创建一个对象
#执行函数结束以后 也会释放。

#析构函数很少写  知道__del__就行了
#适用范围，人里有条狗，删除人时自己删除狗的类
```

## 82 类的设计3

```python
#重写__repr__和__str__
#__repr__这个是给机器用的，看着跟str一样，实际是给黑屏敲对象返回的内容
#__str__类似toString()
#注意：在没有__str__并且有__repr__时
#有一种操作  str=repr
#所以以后写代码的时候写__str__(self)就可以了
class Person():
    def __init__(self,name,age):
        self.name=name
		self.age=age
	def run(self):
	    print(self.name)
	def __str__(self):
	    return self.name+str(self.age)
per=Person('aa',12)
print(per.name,per.age)
#想打印对象，也和打印属性一样
print(per) #正常是打印对象的内存地址
#__repr_和__str__ 类似java的toString()

#优点：当一个对象属性很多 并且都需要打印，简化代码
```

## 83 访问限制

```python
class Person():
    def __init__(self,name,__age):
        self.name=name
		self.__age=__age
	def setAge(self,age):
	    self.__age=age
	def getAge(self):
	    #外部不能访问私有变量 内部可以
	    return self.__age
	def run(self):
	    print(self.name)
	def __str__(self):
	    return self.name+str(self.age)
per=Person('aa',12)
#对象外部很轻松修改值 打印值
per.__age=10 
print(per.__age)
#如果我们想要让内部的属性不被外部直接访问
#哪个属性不想被访问，只要在属性前面加__就可以了
#例如__age
#在python中 如果在属性前加__ 则属性就变成了private的
#但是在python中没有绝对私有的
per.__age=10 #这样相当于给对象新加了一个属性
#再打印实际是打印的新的属性 和 私有的没关系
age=per.getAge()
print(age)
#私有属性通过共有方法进行赋值


per.__age #报错 因为私有的，python解释器把__变成了另一个名字
#_类名__属性-----   _Person__age
#我们正常可以用per._Person__age去访问，但是强烈不建议
#而且不同编辑器编译出的名字可能是不一样
#前后各二个下划线的 来叫私有，python中只任参数前面2个下划线的
#__XXX__这种在python中叫特殊变量，特殊变量可以直接访问

#在python还有一种 _xxx 这样的外部也是可以访问的，但是按照约定规则，当我们遇到这样变量
#它的意思是“虽然我可以访问，但是请把我当成私有的，不要直接访问我”

```

## 84 人开枪的例子

```python
#人 枪 弹夹
class BulletBox(object):
	def __init__(self,count):
		self.bulletCount=count
class Gun(object):
	def __init__(self,bulletBox):
		self.bulletBox=bulletBox
	def shoot(self):
		if self.bulletBox.bulletCount==0:
		    print('没有子弹了')
		else:
		    self.bulletBox.bulletCount -= 1
			print('剩余%d'%(self.bulletBox.bulletCount))
class Person(object):
	def __init__(self,gun):
		self.gun=gun
	def fire(self):
		self.gun.shoot()
	def fill(self,count):
		self.gun.bulletBox.bulletCount=count
		
#person.py gun.py bulletBox.py
from person import Person
from gun import Gun
from bulletBox import BulletBox

bulletBox = BulletBox(1)
gun = Gun(bulletBox)
per = Person(gun)
per.fire() #0发
per.fire() #没子弹了
per.fill(1) #装1发
per.fire() #开枪 剩0发
per.fire() #没子弹了
```

## 85 继承

有二个类，当A继承B的时候，我们就说A继承了B的所有属性和方法
object是所有类的父类，也可以称为超类
优点：
1继承简化了代码，减少冗余
2提高代码健壮性
3提高了安全性
4是多态的前提
缺点：
耦合和内聚是描述类与类的关系的
耦合越低，内聚越高代码越好。
但是继承是高耦合的

优点高于缺点

```python
#person.py
class Person(object):
    def __init__(self,namem,age):
	    self.name=name
		self.age=age
	def eat(self):
	    print('eat')
	def drink(self):
	    print('drink')
```

单继承实现

```python
#super(Student,self) 当前学生的父类
from person import Person
class Student(Person):
	def __init__(self,name,age,no):
	    super(Student,self).__init__(name,age)
		self.no = no

from person import Person
class Worker(Person):
	def __init__(self,name,age,workType,sal):
	    super(Student,self).__init__(name,age)
		self.workType = workType
		self.__sal=sal

#父类私有的属性 子类是不能直接访问的
#可以用继承来的set get来取
```

多继承

```python
class Father(object):
    def __init__(self,name,money):
	    self.name=name
		self.money=money
    def play(self):
	    print('play1')
class Mother(object):
    def __init__(self,name,hourse):
	    self.name=name
		self.hourse=hourse
    def play(self):
	    print('play2')

#从文件名中引入 类
from father import Father
from mother import Mother
class Child(Father,Mother):
    def __init__(self,name,money,hourse):
	    #多继承就只要注意这的写法就可以了
	    #super(Father,self).__init(name,money)
		Father.__init__(self,name,money)
		Mother.__init__(self,name,hourse)

######        
from child import Child
def main():
    c=Child('zhangsan',120,'white hourse')
    c.print(c.money,c.hourse)
	c.play() #打印的是play1
	#注意 父类方法名相同，默认调用括号中排前面的父类的方法

if __name__=='__main__':
    main()        
```

## 88 多态   

多态前提是多继承

```python
#动物   动物多种形态 猫狗猪牛
#最终目标 人可以喂任何动物

class Cat(object):
    def __init__(self,name):
	    self.name=name
	def eat(self):
	    print("cat eat")
class Mouse(object):
    def __init__(self,name):
	    self.name=name
	def eat(self):
	    print("cat eat")
===============
from cat import Cat
from mouse import Mouse

tom=Cat('tom')
jerry=Mouse('jerry')
#思考 添加100种动物 都有吃方法
#此时抽象出一个动物
==========
class Animal(object):
    def __init__(self,name):
	    self.name=name
	def eat(self):
	    print(self.name,"eat")
		
from animal import Animal
class Cat(Animal):
    def __init__(self,name):
		super(Cat,self).__init__(name)
		
from animal import Animal
class Mouse(Animal):
    def __init__(self,name):
	    super(Cat,self).__init__(name)

class Person(object):
    def __init__(self):
	def feedCat(self,cat):
	    print('给你食物')
        cat.eat()    
	def feedMouse(self,mouse):
	    print('给你食物')
        mouse.eat()


from person import Person
per = Person()
per.feedCat(tom)
per.feeMouse(jerry)

#如果要喂100种动物要写100个方法
#改造

class Person(object):
    def __init__(self):
	def feedAnimal(self,animal):
	    print('给你食物')
        animal.eat()    

per.feedAnimal(tom)
per.feedAnimal(jerry)

#人可以喂各种动物了。
#继承就是多态的基础了
#多态用的少  因为用不明白
```

## 89 对象属性与类属性

```python
class Person(object):
    name="person"  #这里叫做类属性
	def __init__(self,name):
	    self.name=name #这个是对象属性

	
#用类名来调用的叫类属性
print Person.name #person

#用对象打印的是对象属性
per=Person('tom')
print(per.name) #tom

#对象属性的优先级高于类属性

#可以动态给对象添加对象属性
per.age=10
#动态属性只是针对当前对象生效，其他类没有这个属性

del per.name #删除的只是对象属性
print(per.name) #类属性还是会打印

#尽量类属性和对象属性同名，因为对象会屏蔽类属性
```

## 90 提升与发送邮件

```python
#动态添加属性和方法
#创建一个空类
class Person(object):
    __slots__("name","age")
per=Person()
#动态添加属性 这体现了动态语言的特点  灵活
per.name='chenwei'
per.age=18
#动态添加方法
'''
def say(self):
  print("my name is"+self.name)
per.speak=say
per.speak()
#会报错，找不到self
'''
#####上面的添加不进去，自己添加不了自己 除非
#per.speak(per)  但是不支持这个
#需要用下面的方法
from types import MethodType

def say(self):
  print("my name is"+self.name)
  
per.speak = MethodType(say,per)  #相当于偏函数

#如果我们想要限制实例的属性怎么办
#比如只允许给对象添加 name,age,height,weight属性
#其他就不让添加 怎么办
#解决，定义类的时候，定义一个特殊的属性(__slots__) 可以限制动态添加的属性
__slots__("name","age")
#这样就限制了 只能添加 name age
```

## 91

```python
#@property
class Person(object):
    def __init__(self,age):
	    self.age=age
per=Person(18)

#属性对外直接暴露，不安全，没有数据过滤
print(per.age)
#之前是使用限制访问
#self.__age=age   需要自己写set get方法

class Person(object):
    def __init__(self,age):
	    self.__age=age
	'''
	def getAge(self):
	    return self.__age
	def setAge(self,age):
	    self.__age = age
	'''
	#想用per.age去访问私有属性
	#如果就想用点的方式，注释getset
	#这样就用到的@property
	
	#方法名受限制的变量去掉双下划线
	
	#get方法名加@
	@property
	def age(self):
	    return self.__age
	
	#set是@变量.setter
	@age.setter  #去掉下划线.setter
	def age(self,age):
		self.__age = age
	
per=Person(18)
#per.setAge(15)
#print(per.getAge())

per.age=100
print(per.age)
```

## 92 

```python
#运算符重载
print(1+2)
print(eval("1+2"))
print('1'+'2')
#不同的数据类型用加法会有不同的解释

class Person(object):
    def __init__(self,num):
	    self.num=num
	def __add__(self,other):
	    #self代表加号前面  
		#other 代表加号后面相加
		return Person(self.num+other.nm)
	def __str__(self):
	    return "num="+str(self.num)
		
per1=Person(1)
per2=Person(2)

#2个对象相加怎么解释
print(per1+per2)  #会报错
#我就想让他俩相加，怎么办  让加法重载就可以了
#想解释加法  重新__add__

#重载之后
print(per1+per2)  #3
#per1+per2相当于per1.__add__(per2)

互译无限 可以发送短信
注册会有apiid 和 apikey密码
```

## 93

```python
文档中心有-短信通知文档下载里面有demo
打开python的

import http.client
import urllib
host='106.ihuiyi.com'
sms_send_uri="/webservice/sms.php?method=Submit"
account="用户名"
password="password"

def send_sms(text,mobile):
	params=urllib.parse.urlencode({'account':account,"password":password,"content":text,"mobile":mobile,"format":"json"})
	headers={"Content-type":"application/x-www-form-urlencoded","Accept":"text/plain"}
	conn=http.client.HTTPConnection(host,port=80,timeout=30)
	conn.requst("POST",sms_send_uri,params,headers)
	response=conn.getresponse()
	response_str=response.read()
	conn.close()
	return response_str

if __name__=='__main__':
    mobile="18040105016"
	text="您的验证码是：1234。请不要泄露"
	print(send_sms(text,mobile))
```

## 94

```python
#注册163 打开 修改设置  POP3/SMTP/IMAP
#开启pop3和 imap
#左侧客户端授权密码  不要和登录邮箱密码相同

#发邮件要导入发送邮件的库
import smtplib
#邮件文本恋情下面这个
from email.mime.text import MIMEText
SMTPServer="smtp.163.com"
#发邮件的地址
Sender="chenwei@163.com"
#发送者邮箱的密码  不是登录密码  是授权密码
passwd='12345678900a'

#设置发送的内容
message="chenwei handsome"
#把字符串转成邮件文本
msg=MIMEText(message)

#标题
msg['Subject']='标题'
#发送者
msg['From']=sender

#创建（连接）SMTP邮件服务器
mailServer=smtplib.SMTP(SMTPServer,25)
#登录邮箱
mailServer.login(sender,passwd)

#发送邮件
#邮件内容转邮件形式字符串
mailServer.sendmail(sender,['chenwei@vjsp.cn','174737624@qq.com'],msg.as_string())

#退出邮箱
mailServer.quit()
#554错误:内容不合法 或者 密码错误
```

## 97 银行自动提款机系统

```python
#人    提款机    卡   
#行为：开户 查询 存款  改密 锁定 解锁 补卡 销户 退出

from admin_view import AdminView
from atm import ATM
import pickle
import os
def __main__():
	vw=AdminView()
	vw.printAdminView()
	if not vw.login():
	    return -1
	vw.funcView()
	while True:
	    #等待用户操作
		comm=input("请输入您的操作:")
		if comm=='1':
		    vm.openAcc()
		elif comm=='2':
			vm.search()
		elif comm=='3':
			pass
		elif comm=='4':
			pass
		elif comm=='5':
			pass
		elif comm=='6':
			pass
		elif comm=='7':
			pass
		elif comm=='8':
			pass
		elif comm=='9':
			pass
		elif comm=='0':
			pass
		else:
		    print('输入错误')
	
	def saveusers(allusers):
	    abspath=os.getcwd()
		filepath=os.path.join(abspath,'aa.txt')
		f=open(filepath,'wb')
		picle.dump(allusers)
		f.close()
	def getusers():
	    abspath=os.getcwd()
		filepath=os.path.join(abspath,'aa.txt')
		f=open(filepath,'rb')
		allusers=picle.load(f)
		f.close()
		return allusers
		
if __name=='__main__':
	main()

#界面类
import time
class AdminView(object):
	admin="1"
	password="1"
    def printAdminView(self):
		print("*****************************")
		print("***********欢迎登录**********")
		print("*****************************")
			
	def funcView(self):
		print("*****************************")
		print("开户(1) 查询(2)")
		print("*****************************")
	
	def login(self):
	    inputAdmin=input('请输入管理员账号')
		if inputAdmin!=self.admin:
		    print('账号错误')
			return -1
		inputpass=input('请输入管理员密码')
		if inputAdmin!=self.admin
			print('密码错误')
			return -1
		print('登录成功')
			time.sleep(3)
		    return 0

from card import Card
from atm import ATM
class ATM(object):
	def __init__(self,allusers);
	    self.allusers={}
		
    def openAcc(self):
	    name=input("用户名")
		idcard=input("身份证")
		#idcard=input("用户名")
		tel=input("电话")
		onePasswd=input('请输入密码：')
		money=input("存款金额")
		if not self.checkPasswd(onePasswd):
			print("密码输入错误，开户失败")
			return -1
		#所有用户信息全了
		cardId =self.randomCardId()
		
		card=Card(cardId,onePasswd,money)
		user=User(name,idCard,tel,card)
		
		self.allusers[cardStr]=user
		print("开户成功，牢记卡号")
		
    def search (self):
        cardNum=input('请输入卡号')
		if self.allusers.get(cardNum):
			print('卡号不存在')
			return -1
		#校验密码
		print('账号 %s 余额%d' %(user.card.cardId,user.card.money))
	def fetch (self):
        pass
	def save (self):
        pass
	def transfer (self):
        pass
	def repasswd (self):
        pass
	def lock (self):
        carNum=input('卡号')
		user=self.allusers.get(carNum)
		if not user:
			print('卡不存在')
			return -1
		if user.card.cardlock:
			print('卡已经锁定，');
			return -1
		user.card.cardlock=True
		print('锁定成功')
		
	def unlock (self):
		carNum=input('卡号')
		user=self.allusers.get(carNum)
        user.card.cardlock=False
	def newCard(self):
        pass
	def unRegist(self):
        pass
	def checkPasswd(sefl):
		return True
		
	def randomCardId(sefl):
		while True:
			str=''
			for i in range(6):
				ch=chr(random.randrange(ord('0'),ord('9')+1))
				str += ch
			if not self.allusers.get(str):
				return str
			

class User(object):
	def __init__(self,name,idcard,tel,password):
	    selft.name=name
		selft.idcard=idcard
		selft.tel=tel
		selft.password=password
	
class Card(object):
    def __init__(self,cardId,cardPasswd,cardMoney):
	    self.cardId=cardId
		self.cardPasswd=cardPasswd
		self.cardMoney=cardMoney
		self.cardlock
```

## 98  图形界面(tkinter)

tkinter是python提供一多个图形开发的库，8.0以后适应各大平台
而且 IDEA 也是用 tkinter写的

```python
import tkinter
#创建主窗口
win = tkinter.Tk()
#设置标题
win.title('我的窗口')
#设置大小，位置
win.geometry('400x400+200+0') #400x400  + left(200) +top(0)
 
#进入消息循环

#Label:标签控件  显示文本
#text 显示的文本
#bg 背景色
#fg 字体颜色
#wraplength 指定text多宽进行换行
#justify 换行后的对齐方式
#anchor 位置 n e s w center居中， ne se sw nw 默认center
label=tkinter.Label(win,text="我是一个标签" ,bg='pink',fg='red' ,font('黑体'，20),width=10,height=3,wraplength=10,justify="left",anchor="n")

#label挂在win上--显示出来
label.pack()

win.mainloop()
```

```python
import tkinter

def func():
	print('working')

def update():
	message=""
    if hobby1.get()==True:
	    print(1)
		message += 'woman\n'
	if hobby2.get()==True:
	    print(2)
		message += 'bear\n'
	if hobby3.get()==True:
	    print(3)
		message += 'money\n'
		
		
#创建主窗口
win = tkinter.Tk()
#设置标题
win.title('我的窗口')
#设置大小，位置
win.geometry('400x400+200+0') #400x400  + left(200) +top(0)

#command 执行的函数 直接写函数名  不用给参数
#width
#height
button=tkinter.Button(win,text="按钮1",command=func)
button.pack()  #显示按钮

button1=tkinter.Button(win,text="按钮2",command=lambda:print('内部函数按钮事件'))
button1.pack()  #显示按钮

button2=tkinter.Button(win,text="关闭",command=lambda:win.quit)
button2.pack()  #显示按钮

#创建一个变量绑定在控件身上
e=tkinter.Variable()
#输入控件  密码 show="*"
entry=tkinter.Entry(win,textvariable=e)
entry.pack()
#e就代表输入框这个对象
#设置初始值
e.set('123')
#取值
print(e.get())  #取值用变量
print(enty.get())  #也能取值

#文本控件，多行文本  height显示行数
text=tkinter.Text(win,width=30 height=4)
text.pack()
str='''afdsafsafd 
asdf adsf sdfa 
sadfsdaf sdf 
'''
#插入之前清空。。。
text.delete(0,0,tkinter.END)
#插入
text.insert(tkinter.INSERT,str)

#创建一个滚动条
scroll=tkinter.Scrollbar()
text1=tkinter.Text(win,width=30 height=4)
#side放到窗体的哪一侧， fill填充
scroll.pack(side=tkinter.RIGHT,fill=tkinter.Y)
text1.pack(side=tkinter.LEFT,fill=tkinter.Y)
#关联
scroll.config(command=text1.yview)
#文本动 滚动条也动
text1.config(yscrollcommand=scroll.set)
text1.pack()

hobby1=tkinter.BooleanVar()
hobby2=tkinter.BooleanVar()
hobby3=tkinter.BooleanVar()
check1=tkinter.Checkbutton(win,text="女人",variable=hobby1,command=update)
check2=tkinter.Checkbutton(win,text="啤酒",variable=hobby2,command=update)
check3=tkinter.Checkbutton(win,text="钱",variable=hobby3,command=update)
check1.pack()
check2.pack()
check3.pack()

#单选框要绑定变量 一组单选框要绑定同一个变量
r=tkinter.IntVar()  #int没bug  StringVar有bug 默认全选中了
#单选框 radiobutton  
radio1=tkinter.Radiobutton(win,text="男" value="1" variable=r,command=updateradio)
radio2=tkinter.Radiobutton(win,text="女" value="2" variable=r,command=updateradio)
radio1.pack()
radio2.pack()

win.mainloop()

def updateradio():
	print(r.get())
```

103 图形界面的其他组件

listbox列表框控件 可以包含一个或多个文本框，可单选多选

Demo：在listbox小窗口显示一个字符串--树形列表

```python
#创建一个listbox
import tkinter
		
#创建主窗口
win = tkinter.Tk()
#设置标题
win.title('我的窗口')
#设置大小，位置
win.geometry('400x400+200+0') #400x400  + left(200) +top(0)

lb=tkinter.Listbox(win,selectmode=tkinter.BROWSE)
lb.pack()
#在头上加
for item in ['a','b','c','a1','a2','a3']:
    lb.insert(tkinter.END,item)
#在头添加
lb.insert(tkinter.ACTIVE,'d')
#删除 二个参数  1为开始索引 2为结束索引  2可以不写，如果不写参数2 只删除1参数处的数据
lb.delete(1,3)
lb.delete(1)
#选中  和删除一样
lb.select_set(1,2)

#取消选中
lb.select_clear(2,3)
#获取列表中元素个数
print(lb.size())
#获取值
print(lb.get(2,4))
#获取当前索引项，不是元素
print(lb.curselection())
#判断一个选项是否被选中
print(lb.selection_include(1))

win.mainloop()

listboxk中

#创建一个listbox
import tkinter
		
#创建主窗口
win = tkinter.Tk()
#设置标题
win.title('我的窗口')
#设置大小，位置
win.geometry('400x400+200+0') #400x400  + left(200) +top(0)

#绑定变量
lbv=tkinter.StringVar()
#与上面的相似  但是不支持鼠标按下后选中位置
lb=tkinter.Listbox(win,selectmode=tkinter.SINGLE,listvariable=lbv)
lb.pack()
#在头上加
for item in ['a','b','c','a1','a2','a3']:
    lb.insert(tkinter.END,item)

#打印当前列表中的选项
print(lbv.get())
#设置选项  元组
lbv.set(("1","2","3"))

def myfunc(event):
	print(lb.curselection())  #返回下标
	print(lb.get(lb.curselection()))
	
	
#事件绑定不能用command Double-Button-1
#Double双击 Button按钮 1左键
lb.bind("<Double-Button-1>",myfunc)

win.mainloop()
```

```python
import tkinter
		
#创建主窗口
win = tkinter.Tk()
#设置标题
win.title('我的窗口')
#设置大小，位置
#win.geometry('400x400+200+0') #400x400  + left(200) +top(0)

#EXTENDED 可以使listbox支持shift和ctrl
lb=tkinter.Listbox(win,selectmode=tkinter.EXTENDED)
lb.pack()
#在头上加
for item in ['a','b','c','a1','a2','a3']:
    lb.insert(tkinter.END,item)
#滚动条 新的写法
sc=tkinter.Scrollbar(win)
sc.pack(side=tkinter.RIGHT, fill=tkinter.Y)

lb.configure(yscrollcommand=sc.set)
lb.pack(side=tkinter.LEFT,fill=tkinter.BOTH)
#额外给属性赋值
sc['command']=lb.yview

win.mainloop()

```

```python
import tkinter	
#创建主窗口
win = tkinter.Tk()
#设置标题
win.title('我的窗口')
#设置大小，位置
#win.geometry('400x400+200+0') #400x400  + left(200) +top(0)

#MULTIPLE 多选
lb=tkinter.Listbox(win,selectmode=tkinter.MUTIPLE)
lb.pack()
#在头上加
for item in ['a','b','c','a1','a2','a3']:
    lb.insert(tkinter.END,item)
win.mainloop()
```

scale

```python
import tkinter	
#创建主窗口
win = tkinter.Tk()
#设置标题
win.title('我的窗口')
#设置大小，位置
win.geometry('400x400+200+0') #400x400  + left(200) +top(0)

#Scale 供用户通过拖拽指示器 改变变量的值
#可以水平也可以竖直
#orient=tkinter.VERTICAL  竖直
#orient=tkinter.HORIZONTA 水平
#tickinterval=10,length=200
#length水平时宽度 竖直标识高度
#tickinterval 为值的 步长
scale=tkinter.Scale(win,from_=0,to=100,orient=tkinter.VERTICAL,tickinterval=10,length=200)
scale.pack()
#设置值
scale.set(20)

#获取值
print(scale.get())

def showNum():
	print(scale.get())
	
tkinter.Button(win,text="按钮",command=showNum).pack()

win.mainloop()
```

```python
#Spinbox   数值范围控件
v=tkinter.StringVar()
#更新
def update():
    print(v.get())
#increment 步长 默认为1
#用values最好不用from_ to联用  values不常用
#,values=(0,2,4,6,8)
sp=tkinter.Spinbox(win,from_=0,to=100,increment=1,textvariabl=v,command=update)
#需要绑定变量
print(v.get)
v.set(2)

#menu 顶层菜单
#菜单条
menubar = tkinter.Menu(win)

#这个是把menubar 配置到win上  就是桌面上面的menu上
win.config(menu=menubar)
#创建一个菜单选项
menu1=tkinter.Menu(menubar,tearoff=False)
#给菜单选项添加内容
def func():
	print('123')
from item in ['java','python','退出']:
    if item=='退出':
		#添加一个分割线
		menu1.add_separator()
	    menu1.add_command(label=item,command=lambda:win.quit)
	else:
		menu1.add_command(label=item,command=func)
#向菜单条上添加菜单选项
menubar.add_cascade(label="语言",menu=menu1)

menu2=tkinter.Menu(menubar,tearoff=False)
menu2.add_command(label='red')
menu2.add_command(label='blue')
menubar.add_cascade(label="颜色",menu=menu2)


import tkinter	
#创建主窗口
win = tkinter.Tk()
#设置标题
win.title('我的窗口')
#设置大小，位置
win.geometry('400x400+200+0') #400x400  + left(200) +top(0)

#这只是创建了一个menu 但是没绑定到任何容器上
menubar=tkinter.Menu(win)

menu1=tkinter.Menu(menubar,tearoff=False)
from item in ['java','python','退出']:
    if item=='退出':
		#添加一个分割线
		menu1.add_separator()
	    menu1.add_command(label=item,command=lambda:win.quit)
	else:
		menu1.add_command(label=item,command=func)
#向菜单条上添加菜单选项
menubar.add_cascade(label="语言",menu=menu1)

def showMenu(event):
	#在这个点显示出来
	menubar.post(event.x_root,event.y_root)

#win绑定事件 鼠标右键单击
win.bind("<Button-3>",showMenu)

win.mainloop()


```

```python
import tkinter	
from tkinter import ttk
#创建主窗口
win = tkinter.Tk()
#设置标题
win.title('我的窗口')
#设置大小，位置
win.geometry('400x400+200+0') #400x400  + left(200) +top(0)

cv=tkinter.StringVar()
#下拉框
com=ttk.Combobox(win,textvariable=cv)
com.pack()
#设置下拉数据
com['value']=('黑龙江','吉林','辽宁')
#设置默认值  通过下标设置默认值
com.current(0)
#获取值，绑定事件
def func(event):
	print(com.get())
	print(cv.get())
    print('123')
com.bind("<ComboboxSelected>",func)

win.mainloop()

=======frame  框架控件===
import tkinter	
#创建主窗口
win = tkinter.Tk()
#设置标题
win.title('我的窗口')
#设置大小，位置
win.geometry('400x400+200+0') #400x400  + left(200) +top(0)
#布局

frm = tkinter.Frame(win)
frm.pack()
#left
frm_l = tkinter.Frame(frm)
tkinter.Label(frm_l,text="左上"，bg="pink").pack(side=tkinter.TOP)
tkinter.Label(frm_l,text="左下"，bg="blue").pack(side=tkinter.TOP)

frm_l.pack(side = tkinter.LEFT) #放在窗口左侧
#
#right
frm_r = tkinter.Frame(frm)
tkinter.Label(frm_r,text="右上"，bg="pink").pack(side=tkinter.TOP)
tkinter.Label(frm_r,text="右下"，bg="blue").pack(side=tkinter.TOP)

frm_r.pack(side = tkinter.RIGHT) #放在窗口左侧
#

win.mainloop()
----------------------------------------------------------------------------
107
#表格数据  类似table
import tkinter	
from tkinter import ttk
#创建主窗口
win = tkinter.Tk()
#设置标题
win.title('我的窗口')
#设置大小，位置
win.geometry('600x400+200+0') #400x400  + left(200) +top(0)
#表格
tree = ttk.Treeview(win)
tree.pack()
#定义列
tree['columns'] = ('姓名','年龄','身高','体重')
#设置列  主要是设置列的宽度
tree.column('姓名',wind=100)
tree.column('年龄',wind=100)
tree.column('身高',wind=100)
tree.column('体重',wind=100)

#设置表头  表头上显示的是text
tree.heading('姓名',text="姓名-name")
tree.heading('年龄',text="姓名-name")
tree.heading('身高',text="姓名-name")
tree.heading('体重',text="姓名-name")

#添加数据   text-行标题（序号等）
tree.insert('',0,text="line1",values=('chenwei','35','180','86kg'))
tree.insert('',1,text="line2",values=('chenwei1','36','170','50kg'))

win.mainloop()


=================
#树状结构
import tkinter	
from tkinter import ttk
#创建主窗口
win = tkinter.Tk()
#设置标题
win.title('我的窗口')
#设置大小，位置
win.geometry('600x400+200+0') #400x400  + left(200) +top(0)
#树状结构
tree = ttk.Treeview(win)
tree.pack()

#添加一级树枝
treeF1=tree.insert('',0,'中国',text="显示的中国",values=('f1'))
treeF2=tree.insert('',1,'美国',text="显示的美国",values=('f2'))
treeF3=tree.insert('',2,'英国',text="显示的英国",values=('f3'))

#添加二级树枝
treeF1_1=tree.insert(treeF1,0,'黑龙江',text="中国-黑龙江",values=('f1_1'))
treeF1_2=tree.insert(treeF1,1,'吉林',text="中国-吉林",values=('f1_2'))
treeF1_3=tree.insert(treeF1,2,'辽宁',text="中国-辽宁",values=('f1_3'))

#无效添加下一级
win.mainloop()

----------------------------------------------------------------------------
108
#绝对布局
import tkinter	
from tkinter import ttk
#创建主窗口
win = tkinter.Tk()
#设置标题
win.title('我的窗口')
#设置大小，位置
win.geometry('600x400+200+0') #400x400  + left(200) +top(0)

label1=tkinter.Label(win,text='good',bg='blue')
label2=tkinter.Label(win,text='nice',bg='blue')
label3=tkinter.Label(win,text='cool',bg='blue')

#绝对布局  用 place替换了pack
#绝对布局有个特点 窗口大小对位置没有影响
label1.place(x=5,y=10)
label2.place(x=5,y=10)
label3.place(x=5,y=10)

win.mainloop()


===========
#相对布局
import tkinter	
from tkinter import ttk
#创建主窗口
win = tkinter.Tk()
#设置标题
win.title('我的窗口')
#设置大小，位置
win.geometry('600x400+200+0') #400x400  + left(200) +top(0)

label1=tkinter.Label(win,text='good',bg='blue')
label2=tkinter.Label(win,text='nice',bg='blue')
label3=tkinter.Label(win,text='cool',bg='blue')

#相对布局,受窗口影响
#tkinter.BOTH 撑满Y轴
label1.pack(fill=tkinter.Y,side=tkinter.LEFT)
label2.pack(fill=tkinter.X,side=tkinter.TOP)
label3.pack()

win.mainloop()

===========
#表格布局
import tkinter	
from tkinter import ttk
#创建主窗口
win = tkinter.Tk()
#设置标题
win.title('我的窗口')
#设置大小，位置
win.geometry('600x400+200+0') #400x400  + left(200) +top(0)

label1=tkinter.Label(win,text='good',bg='blue')
label2=tkinter.Label(win,text='nice',bg='blue')
label3=tkinter.Label(win,text='cool',bg='blue')
label4=tkinter.Label(win,text='handsome',bg='blue')

#表格布局
label1.grid(row=0,column=0)
label2.grid(row=0,column=1)
label3.grid(row=1,column=0)
label4.grid(row=1,column=1)

win.mainloop()
#布局一共就3种

====================
#事件
#鼠标点击事件
#<Button-i> i:1 2 3 左中右 鼠标
import tkinter	
#创建主窗口
win = tkinter.Tk()
#设置标题
win.title('我的窗口')
#设置大小，位置
win.geometry('600x400+200+0') #400x400  + left(200) +top(0)

#button绑定的事件函数要有一个event参数
#事件的属性
'''
char 键盘输入的和案件事件相关字符
keycode 从键盘输入的和按键事件相关的键的代码
keysym 从键盘输入的和案件事件相关的符号 -字符
num 案件数字，表明是按下哪个鼠标键
widget 除法这个事件的小构件对象
x和y 鼠标在小构件以像素为单位的位置
x_root 和 y_root 鼠标相对主屏幕左上位置（像素）
'''
def func(event):
    print(event.x,event.y)
button1=tkinter.Button(win,text='left button')
#<Doubel-Button-1>鼠标左键单击  Triple-Button-1鼠标三击
button1.bind('<Button-1>',fun)  #给控件绑定事件
button1.pack()

win.mainloop()
===================
#鼠标移动事件
#<B1-Motion>左键移动
#<B3-Motion> 右键移动
#<B2-Motion> 中轴移动
label1=tkinter.Label(win,text='ttt')
label1.pack()
def func(event):
    print(event.x,event.y)
label.bind('<B1-Motion>',func)
def func1(event):
    print(event.x,event.y)
label.bind('<ButtonReleased-1>',func1)

#<Bi-Motion> 鼠标移动
#<Button-i> 单击
#<Doubel-Button-1> 双击
#<ButtonReleased-i> 鼠标左键释放事件
#<Enter>  光标进入小构件
#<Key> 当单击一个键时事件发生
#<Leave> 光标离开小构件触发
#<Return> 单击Enter时，可以将键盘上任意键和一个事件绑定
#<Shift+A> 单击shift+A Alt Shift Control
#<Triple-Button-i> 三击鼠标

#进入构件事件
label=tkinter.Label(win,text='ttt')
label.pack()
def func(event):
    print(event.x,event.y)
#进入构件
label.bind('<Enter>',func)
def func1(event):
    print(event.x,event.y)
#离开构件事件
label.bind('<Leave>',func1)

#获取焦点，如果没有焦点 是没会响应键盘事件的
label.focus_set()
#取事件
def func2(event):
    print(event.char)
	print(event.keycode)
#响应所有事件
label.bind('<Key>',func2)

def func3(event):
    print(event.char)
	print(event.keycode)
#响应特殊案件的事件
#<Shift_L><Shift_R>  左右shift
#<F5><Return><BackSpace>
label.bind('<Shift_L>',func3)

#=======================指定按键 a
label.bind('a',func4)

def func5(event):
	print(event.keycode)
	print(event.char)
#=======================组合按键
label.bind('<Contrl-Alt>',func5)
----------------------------------------------------------------------------
109 树状目录
import tkinter
import os
from treeWindows import TreeWindows

win=tkinter.Tk()
win.title('title')
win.geometry('900x400+200+50')

treeWin = TreeWindows(win,r'd:\chenwei')

win.mainloop()

import tkinter
from tkinter import ttk
import os
class TreeWindows(tkinter.Frame)
	def __init__(self, master ,path):
		frame = tkinter.Frame(master)
		frame.pack()
		
		self.tree = ttk.Treeview(frame)
		self.tree.pack(side=tkinter.LEFT,fill=tkinter.Y)
		
	
		root=self.tree.insert("",'end',text=self.getLastPath(path),open=True)
		self.loadTree(root,path)
		
		#滚动条
		self.sy = tkinter.Scrollbar(frame)
		self.sy.pack(side=tkinter.RIGHT,fill=tkinter.Y)
		
		#配置  加上滚动条
		self.sy.config(command=self.tree.yview)
		self.tree.config(yscrollcommand=self.sy.set)
		
	
	def loadTree(self,parent,path):
		for fileName = os.listdir(parentPath)
			absPath=os.path.join(parentPaht,fileName)
			#插入树枝
			treey=self.tree.inser(parent,'end',text=self.getLastPath(absPath))
			if os.path.isdir(absPath):
				self.loadTree(treey,absPath)
			
			
			
	def getLastPath(self,path):
		pathlist=os.path.split(path)
		return pathList[-1]
		
	
----------------------------------------------------------------------------
110
import tkinter
import os
from treeWindows import TreeWindows
from infoWindows import InfoWindows

win=tkinter.Tk()
win.title('title')
win.geometry('900x400+200+50')

path=r"d:\dir"
infoWin = InfoWindows(win)

treeWin = TreeWindows(win,path,infoWin)


win.mainloop()


import tkinter
from tkinter import ttk
import os

#类 继承tkinter的frame  
class TreeWindows(tkinter.Frame)
	def __init__(self,master,path,otherWin):
		frame=tkinter.Frame(master)
		frame.grid(row=0, column=0)
		self.otherWin=otherWin
		frame.pack()
		
		self.tree = ttk.Treeview(frame)
		self.tree.pack(side=tkinter.LEFT,fill=tkinter.Y)
		self.cofig(command=self.tree.yview)
		self.tree.config(yscrollcommand=self.sy.set)
		
		#end往后添  
		#os.path.splittext(path) windows不生效
		root=self.tree.insert('','end',text=os.path.splittext(path),open=True,values=(path))
		self.loadTree(root,path)
		#添加滚动条
		self.sy = tkinter.Scrollbar(frame)
		self.sy.pack(side=tkinter.RIGHT,fille=tkinter.Y)
		
		#绑定事件
		self.tree.bind('<<TreeviewSelect>>',self.func)
		
		
	def func(self,event):
		self.v = event.widget.selection()
		for sv in self.v:
			file = self.tree.item(sv)['text']
		    print(file)
			self.otherWin.ev.set(file)
			apth=self.tree.item(sv)['values'][0]
			
		
	def loadTree(self,parent,parentPath):
		for filName = os.listdir(parentPath)
			absPath=os.path.join(parentPath, fileName)
			treey=self.tree.insert(parent,"end",text=self.getLastPath(absPath),values=(absPath))
			if os.path.isdir(absPath):
				self.loadTree(treey,absPath)
		
		
	def getLastPath(self,path):	
		pathList=os.path.split(path)
		return pathList.get(-1)


import tkinter
from tkinter import ttk
import os
class InfoWindows(tkinter.Frame):
	def __init__(self,master):
		frame = tkinter.Frame(master)
		frame.grid(row=0, column=1)
		
		#页面
		self.ev=tkiner.Variable()
		self.entry=tkiner.Entry(frame,textvariable=self.ev)
		self.entry.entry.pack()
		
		self.txt= tkinter.Text(frame)
		self.txt.pack()
```

## 111 python操作CSV

```python
#读csv文件
import csv

def readCsv(path):
	infoList=[]
	with open(path,'r') as f:
	    allFileInfo = csv.reader(f)
		print(allFileInfo) #读出的是个对象
		
		for row in allFileInfo:
			infoList.append(row)
			#print(row)
		return infoList
info=readCsv('d:\\a.csv')


#写cvs
import csv
def writeCsv(path,data):
	with open(path,"w") as f:
		writer = csv.write(f)
		for rowData in data:
			write.writerow(rowData)
path=r"d:\a.csv"
writeCsv(path,[["1","1","1"],["1","1","1"],["1","1","1"]])
```

## 112 操作pdf

```python
#读pdf文件------------这个会用就行，会解析字符串就行了
#需要 cmd  pip list  找 pdf
#pip install pdfminer3K
import sys
import importlib
importlib.reload(sys)

from pdfminer.pdfparser import PDFParser,PDFDocument
from pdfminer.pdfinterp import PDFResourceManager,PDFPageInterpreter
from pdfminer.converter import PDFPageAggregator
from pdfminer.layout import LTTextBoxHorizontal,LAParams
from pdfminer.pdfinterp import PDFTextExtractionNotAllowed

def readPDF(path,toPath):
	f = open(path,"rb") #二进制打开
	#创建一个pdf文档分析器
	parser=PDFParser(f)
	#创建一个pdf文档
	pdfFile=PDFDocument()
	#连接分析器与文档对象
	parser.set_document(pdfFile)
	pdfFile.set_parser(parser)
	#提供初始化密码
	pdfFile.initialize()
	#检测文档是否提供txt检测
	if not pdfFile.is_extractalbe:
		raise PDFTextExtractionNotAllowed
	else:
		#解析数据
		#数据管理器
		manager = PDFResourceManager()
		#创建一个设备管理
		laparams=LAParams()
		device = PDFPageAggregator(manager,laparams=laparams)
		
		#解释器对象
		interpreter = PDFPageInterpreter(manager,device)
		
		#开始循环处理，每次处理一页
		for page in pdfFile.get_pages():
			interpreter.process_page(page)
			#拿到图层，需要循环处理图层
			layout = device.get_result()
			for x in layout:
				if (isinstance(x,LTTextBoxHorizontal)):
					with open(toPath,"a") as f:  #追加形式
						str = x.get_text()
						print str
						f.write(str+'\n')
	
path=r"d:\a.pdf"
toPath=r"d:\a.txt"
readPDF(path,toPath)
```

## 113 播放音乐  修改windows的背景图片

```python
#安装pygame 可以做播放器  也可以做小游戏
#pip install pygame 
import time 
import pygame

filePath=r"d:\1.mp3"
#初始化控件
pygame.mixer.init()
#加载音乐
track = pygame.mixer.music.load(filePath)
#播放
pygame.mixer.music.play()
#暂停
time.sleep(10)  #这是播放10秒钟
#pygame.mixer.music.pause()#暂停
#停止
pygame.mixer.music.stop()
```

```python
#修改windows的背景图片
#regedit 看下注册表-HKEY_CURRENT_USER->Control_pannel->desktop---WallPapper
#改了这个背景图的地址就变了
import win32api
import win32con
import win32gui

def setWallPaper(path):
	#打开注册表
	reg_key=win32api.RegOpenKeyEx(win32con.HKEY_CURRENT_USER,"Control Panel\\Desktop",0,win32con.KEY_SET_VALUE)
	#设置背景 2拉伸 0集中 6自适应 10填充
	win32api.RegSetValueEx(reg_key,"WallpaperStyle",0,win32con.REG_SZ,"2")
	#设置wallpaper
	#win32api.RegSetValueEx(reg_key,"Wallpaper")
	#修改并令立即生效
	win32gui.SystemParametersInfo(win32con.SPI_SETDESKWALLPAPER,path,win32con.SPIF_SENDWININICHANGE)
```

## 114 整蛊小程序

```python
import time 
import pygame
import win32api
import win32con
import win32gui

import threading   #多线程 start

def go(num):
	pygame.mixer.init()
	while True:
		for i in range(5):
			filePath=r"d:\res"+'\\'+str(i)+".mp3"
			track=pygame.mixer.music.load(filePath)
			pygame.mixer.music.play()
			time.sleep(10)
			pygame.mixer.music.stop();
			
def setWallPaper(path):
	reg_key=win32api.RegOpenKeyEx(win32con.HKEY_CURRENT_USER,"Control Panel\\Desktop",0,win32con.KEY_SET_VALUE)
	win32api.RegSetValueEx(reg_key,"WallpaperStyle",0,win32con.REG_SZ,"2")
	win32gui.SystemParametersInfo(win32con.SPI_SETDESKWALLPAPER,path,win32con.SPIF_SENDWININICHANGE)

th=threading.Thread(target=go,name="LoopThread")
th.start
while True:
		for i in range(9):
			filePath=r"d:\res"+'\\'+str(i)+".jpg"
			setWallPaper(filePath)
			time.sleep(5)
```

## 键盘模拟

```python
import win32con
import win32api
import time

#win32api.keybd_evemt(91,0,0,0)
#time.sleep(0.1)
#win32api.keybd_evemt(91,0,win32con.KEYEVENTF_KEYUP,0)
#win+d效果 跳来跳去
while True:
	win32api.keybd_evemt(91,0,0,0)
	time.sleep(0.1)
	win32api.keybd_evemt(77,0,0,0)
	time.sleep(0.1)
	win32api.keybd_evemt(77,0,win32con.KEYEVENTF_KEYUP,0)
	time.sleep(0.1)
	win32api.keybd_evemt(91,0,win32con.KEYEVENTF_KEYUP,0)
	time.sleep(3)
```

## 语音控制

```python
from win32com.client import constants
import os
import win32com.client
import pythoncom
import win32api
import win32con
import win32gui

class SpeechRecognition:
    def __init__(self,wordsToAdd):
	    self.speaker=win32com.client.Dispatch("SAPI.SpVoice")
		self.listener=win32com.client.Dispatch("SAPI.SpShareRecognizer")
		self.context=self.listener.CreateRecoContext()
		self.grammar=self.context.CreateGrammar()
		self.grammar.DictationSetState(0)
		self.wordsRule=self.grammar.Rules.Add("wordsRule",constants.SRATopLevel+constants.SRADynamic,0)
		self.wordsRule.Clear()
		[self.wordsRule.InitialState.AddWordTransition(None,word) for word in wordsToAdd]
		self.grammar.Rules.Commit()
		self.grammar.CmdSetRuleState('wordsRule',1)
		self.grammar.Rules.Commit()
		self.eventHandler = ContextEvents(self.context)
		self.say("Started successfully")
	def say(self,phrase):
	    self.speaker.Speak(phrase)
		
class ContextEvents(win32com.client.getevents("SAPI.SpShareRecoContext")):
    def OnRecognition(self,StreamNumber,StreamPosition,RecognitionType,Result):
	    newResult=win32com.client.Dispatch(Result)
		print("说：",newResult.PhraseInfo.GetText())
		s=newResult.PhraseInfo.GetText()
		if s=='上':
		    win32ap.keybd_event(38,0,0,0)
			time.sleep(0.1)
			win32api.keybd_event(38,0,win32con.KEYEVENTF_KEYUP,0)
		elif s=='下':
			win32ap.keybd_event(40,0,0,0)
			time.sleep(0.1)
			win32api.keybd_event(40,0,win32con.KEYEVENTF_KEYUP,0)
		elif s=='左':
			win32ap.keybd_event(37,0,0,0)
			time.sleep(0.1)
			win32api.keybd_event(37,0,win32con.KEYEVENTF_KEYUP,0)
		elif s=='右':
			win32ap.keybd_event(39,0,0,0)
			time.sleep(0.1)
			win32api.keybd_event(39,0,win32con.KEYEVENTF_KEYUP,0)
if __name__= '__main__':
    wordsToAdd=['上','下','左','右']
	speechReco=SpeechRecognition(wordsToAdd)
	while True:
	    pythoncom.PumpWaitingMessages()
```

## 鼠标模拟

```python
import win32con
import win32api
import time

#设置鼠标位置
win32api.SetCursorPos([20,40])
time.sleep(0.1)
#相当双击  左键按下  左键抬起 按下 抬起
win32api.mouse_event(win32con.MOUSEEVENTF_LEFTDOWN,0,0,0)
win32api.mouse_event(win32con.MOUSEEVENTF_LEFTUP,0,0,0)
win32api.mouse_event(win32con.MOUSEEVENTF_LEFTDOWN,0,0,0)
win32api.mouse_event(win32con.MOUSEEVENTF_LEFTUP,0,0,0)
```

## 115 读取doc和docx

```python
#三方库支持的不太好  用win32最稳定
import win32com
import win32com.client

def readWordFile(path):
	#调用系统word功能，可以处理doc和docx
	mw = win32com.client.Dispatch("Word.Application")
	#打开文件
	doc = mw.Documents.Open(path)
	for paragraphs im doc.Paragraphs:
		lin = paragraphs.Range.Text
		print(line)
	#关闭文件
	doc.Close()
	#退出word
	mw.Quit()
	
path=r"d:\1.docx"
readWordFile(path)
#===============

#读取doc和docx 写入其它文件 保存成txt
import win32com
import win32com.client

def readWordFile(path,toPath):
	#调用系统word功能，可以处理doc和docx
	mw = win32com.client.Dispatch("Word.Application")
	#打开文件
	doc = mw.Documents.Open(path)
	
	doc.SaveAs(toPath,2)  #2表示普通txt
	#关闭文件
	doc.Close()
	#退出word
	mw.Quit()
	
path=r"d:\1.docx"
toPath=r"d:\1.txt"
readWordFile(path,toPath)
#===============

#创建word文件
import win32com
import win32com.client
import os
def makeWordFile(fileName,name):
	#调用系统word功能，可以处理doc和docx
	mw = win32com.client.Dispatch("Word.Application")
	word.Visible=True
	#创建文档
	doc = word.Documents.Add()
	#文档吸入内容
	r=doc.Range(0,0)  #定位到文档头
	r.InsertAfter("亲爱的"+name+"\n")
	r.InsertAfter("欢迎\n")
	
	doc.SaveAs(path)
	
	doc.Close()
	#退出word
	mw.Quit()
	
names=['张三','李四','王五']
for name in names:
	path=os.pat.join(os.getcwd(),name)
	makeWordFile(path,name)
```

## 116 读取xlsx xls文件

```python
#pip openpyxl --> xlsx

#下面这个只能处理xlsx
from openpyxl.reader.excel import load_workbook

def readXlsxFile(path):
	file=load_workbook(filename=path)
	#所有sheet的名称
	print(file.get_sheet_names())
	sheets=file.get_sheet_names()
	sheet = file.get_sheet_by_name(sheets[0])
	#最大行数  最大列数  表名
	print(sheet.max_row)
	print(sheet.max_column)
	print(sheet.title)
	
	for lineNum in range(1,sheet.max_row+1):
		print(lineNum)
		lineList = []
		for columnNum in range(1,sheet.max_column+1):
			#取数据
			value=sheet.cell(row=lineNum,column=columnNum).value
			if value != None:
				lineList.append(value)
		print lineList	
	

path=r"d:\1.xlsx"


from openpyxl.reader.excel import load_workbook
def readXlsxFile(path):
	dic={} #用来存放所有数据
	file=load_workbook(filename=path)
	#所有sheet的名称
	print(file.get_sheet_names())
	sheets=file.get_sheet_names()
	for sheetName in sheets:
		sheetInfo=[]
		sheet = file.get_sheet_by_name(sheetName)
		#最大行数  最大列数  表名
		print(sheet.max_row)
		print(sheet.max_column)
		print(sheet.title)
		
		for lineNum in range(1,sheet.max_row+1):
			print(lineNum)
			lineList = []
			for columnNum in range(1,sheet.max_column+1):
				#取数据
				value=sheet.cell(row=lineNum,column=columnNum).value
				if value != None:
					lineList.append(value)
			print lineList	
			sheetInfo.append(lineList)
		dic[sheetName]=sheetInfo
	return dic
path=r"d:\1.xlsx"



#返回xls和xlsx的文件内容
#pip install xld
#pip install future
#pip install xlwt-future
#pip install pyexcel-io
#pip install ordereddict
#pip install pyexcel
#pip install pyexcel-xls
#有序字典
from collections import OrderedDict
#读取数据
from pyexcel_xls import get_data
def readXlsAndXlsxFile(path):
    dic=OrderedDict()
	#抓取数据
	xdata= get_data(path)
	for sheet in xdata:
		dic[sheet]=xdata[sheet]
	return dic

path=r"d:\1.xlsx"
dic=readXlsAndXlsxFile(path)
print(dic)
print(len(dic))

path1=r"d:\1.xls"
dic1=readXlsAndXlsxFile(path1)
print(dic1)
print(len(dic1))



#写入xls文件   目前只支持xls
#有序字典
from collections import OrderedDict
#读取数据
from pyexcel_xls import get_data
def makeExcelFile(path,data):
	dic=OrderedDict()
	for sheetName,sheetValue in data.items():
		d={}
		d[sheetName]=sheetValue
		dic.update(d)
	save_data(path,dic)

path=r"d:\b.xls"
makeExcelFile(path,{"表1":[[1,2,3],[4,5,6],[7,8,9]],"表2":[[11,12,13],[14,15,16],[17,18,19]]})
```

## 操作PPT

```python
#插入ppt
import win32com
import win32com.client

def makePPT(path):
	ppt = win32com.client.Dispatch("PowerPoint.Application")
	ppt.Visible=True
	
	pptFile = ppt.Presentations.Add()
	#创建页 Add(1,1) 第一个参数是页数-从1开始，第二个是类型 1 2 3 就是ppt新建的类型
	page1=pptFile.Slides.Add(1,1)
	t1=page1.Shapes[0].TextFrame.TextRange  #取下标为0的第一个框的文本的range
	t1.Text='chenwei0'
	t2=page1.Shapes[1].TextFrame.TextRange  #取下标为0的第一个框的文本的range
	t2.Text='chenwei1'
	
	#创建页
	page2=pptFile.Slides.Add(1,1)
	t3=page2.Shapes[0].TextFrame.TextRange  #取下标为0的第一个框的文本的range
	t3.Text='chenwei0'
	t4=page2.Shapes[1].TextFrame.TextRange  #取下标为0的第一个框的文本的range
	t4.Text='chenwei1'
	
	#保存
	pptFile.SaveAs(path)
	pptFile.Close()
	ppt.Quit()
	
path=r"d:\a.ppt"
makePPT(path)
```

## 文件的封装

```python
import csv

import sys
import importlib
importlib.reload(sys)
from pdfminer.pdfparser import PDFParser,PDFDocument
from pdfminer.pdfinterp import PDFResourceManager,PDFPageInterpreter
from pdfminer.converter import PDFPageAggregator
from pdfminer.layout import LTTextBoxHorizontal,LAParams
from pdfminer.pdfinterp import PDFTextExtractionNotAllowed
import win32com
import win32com.client

class DealFile(object):
	def readCsv(self,path):
		infoList=[]
		with open(path,'r') as f:
			allFileInfo = csv.reader(f)
			print(allFileInfo) #读出的是个对象
			
			for row in allFileInfo:
				infoList.append(row)
				#print(row)
			return infoList
	#info=readCsv('d:\\a.csv')
	
	
	def writeCsv(self,path,data):
		with open(path,"w") as f:
			writer = csv.write(f)
			for rowData in data:
				write.writerow(rowData)
	#path=r"d:\a.csv"
	#writeCsv(path,[["1","1","1"],["1","1","1"],["1","1","1"]])


	def readPDF(self,path,callback,toPath=""):
		f = open(path,"rb") #二进制打开
		#创建一个pdf文档分析器
		parser=PDFParser(f)
		#创建一个pdf文档
		pdfFile=PDFDocument()
		#连接分析器与文档对象
		parser.set_document(pdfFile)
		pdfFile.set_parser(parser)
		#提供初始化密码
		pdfFile.initialize()
		#检测文档是否提供txt检测
		if not pdfFile.is_extractalbe:
			raise PDFTextExtractionNotAllowed
		else:
			#解析数据
			#数据管理器
			manager = PDFResourceManager()
			#创建一个设备管理
			laparams=LAParams()
			device = PDFPageAggregator(manager,laparams=laparams)
			
			#解释器对象
			interpreter = PDFPageInterpreter(manager,device)
			
			#开始循环处理，每次处理一页
			for page in pdfFile.get_pages():
				interpreter.process_page(page)
				#拿到图层，需要循环处理图层
				layout = device.get_result()
				for x in layout:
					if (isinstance(x,LTTextBoxHorizontal)):
						if toPath!='':
							with open(toPath,"a") as f:  #追加形式
								str = x.get_text()
								print str
								f.write(str+'\n')
						else:
							str = x.get_text()
							if callback != None:
								callback(str)
							else:
								print(str)
	#path=r"d:\a.pdf"
	#toPath=r"d:\a.txt"
	#readPDF(path,toPath)
	


	def readWordFile(self,path,toPath):
		#调用系统word功能，可以处理doc和docx
		mw = win32com.client.Dispatch("Word.Application")
		#打开文件
		doc = mw.Documents.Open(path)
		
		doc.SaveAs(toPath,2)  #2表示普通txt
		#关闭文件
		doc.Close()
		#退出word
		mw.Quit()

main 
def func(str):
	print(str +"!")
#回掉函数	******************
readPDF(path,func)
```

## 119 python2 python3区别

```python
性能2比3高，3有极大的优化控件 效率追赶
3源码文件默认utf-8 是的变量名更广阔
去掉了<> 使用!=
加入as和with关键字 还有True False None
3.0 5/3=1.66667 5//3=1 2.0 5/3=1 5.0/3=1.6667
加入nonlocal语句
去掉了print语句 加入print()函数不断调用并返回下一个值
去掉了raw_input，加入了input()函数
新的super()，可以不再给super()传参数
2.0 1>"aa"会计算返回bool 3.0会报错，类型不一致 更严谨了
python2.0 8进制的数只需要加0 例如0654是428 3.0需要0o654=428
字符串和字节串：以8-bit字符串存储，p3是16位 unicode编码
数据类型p3去掉了long只有int类似p2的long
p3新增了bytes类型，对应p2的八位串   b=b'china'
面向对象 引入基类object
异常 所有异常从baseException 删除了stardardError
p2 try:except Exception,e: p3 try:except Exception as e:
p2 xrange() 改名 p3 range()
p2有个file类  p3没有，p2打开文件file和open  p3只有open了
	

```

## 120 mapreduce

```python
#高阶函数 map与reduce
#google最开始搞这个mapreduce  大数据设想
#haddop spark  storm-阿里Jstorm
#zookeper kafka 
#一堆数据 字符串数据
#要把这些字符串数据变成数字
#分布式系统中，让一堆电脑来干活
#zookeper管理分布式集群管理
#从字符串转成数字过程 是map
#数据处理之后变成整数的这个过程就是reduce
"1  3  6  8  9  4  2"
13  6  89  42
1368942

#python要实现mapreduce很简单。因为内置
#map
#原型   map(fn,lsd)
#参数1是函数  参数2是序列（列表）
#功能：将传入的函数依次作用在序列中的每一个元素，并把结果作为一个新的iterator返回

def chr2int(chr):
	return {"0":0,"1":1,"2":2,"3":3,"4":4,"5":5,"6":6,"7":7,"8":8,"9":9}[chr];
	

list1=["3","6","8","9","4","2"]

#map相当于chr2int("3")chr2int("6")...chr2int("2") 	
res=map(chr2int,list1)
#res是一个惰性列表，想要看必须转一下
print(list(res))
#打印的是 [3,6,8,9,4,2]

#将整数元素的序列 转为字符串型
#[1,2,3,4] - ['1','2','3','4']
l = map(str,[1,2,3,4])
print(list(l))

#reduce(fn,lsd)
#参数1函数  参数2列表
#功能和map完全不一样，一个函数作用在序列上，这个函数必须接收二个参数
#reduce把结果继续和序列的下一个元素累计运算
#reduce(f,[a,b,c,d])
#f(f(f(a,b),c),d)

from functools import reduce
#求一个序列的和
list2 = [1,2,3,4,5]
def mySum(a,b):
	return a+b
r=reduce(mySum,list2)
#计算出15
print("r=",r)


#将字符串转成字面量数字
def str2int(str):
	def fc(x,y):
		return x*10 + y
	def fs(chr):
		return {"0":0,"1":1,"2":2,"3":3,"4":4,"5":5,"6":6,"7":7,"8":8,"9":9}[chr];
	#return reduce(fc,map(fs,"1357"))
	#return reduce(fc,map(fs,list("1357")))
	return reduce(fc,map(fs,list(str)))
'''	
def myMap(func,inlist):
	rtlist=[]
	for item in inlist:
		rtlist.append(func(item))
'''
```

## 121 高阶函数

```python
#高级函数 filter(fn,lsd)
#参数1是函数  参数2是序列
#功能：用户过滤序列的
#把传入函数作用于每个元素，返回True保留 False去掉
list1=[1,2,3,4,5,6,7,8,9]
def func(num):
	if num%2==0:
		return True
	return False
list2=filter(func,list1)
print(list(list2))
====================
#高阶函数sorted  用来排序  默认升序
#冒泡，选择      快速排序 插入法排序  计数器排序
list1=[4,7,2,6,3]
list2=sorted(list1)
print(list1)
print(list2)

#按绝对值大小排序
#key接受的是函数，来实现排序规则
list3=[4,-7,2,6,-3]
list4=sorted(list1,key=abs)
print(list3)
print(list4)

#降序排列   reverse=True 先按照升序，再翻转
list5=[4,7,2,6,3]
list6=sorted(list5,reverse=True)
print(list5)
print(list6)


def myLen(str):
	return len(str)
	
list7=['c1','f3334','a11123','b532']
list8=sorted(list7,key=len)
print(list7)
print(list8)
```

## 122 单元测试

```python
#单元测试 用来对一个函数、一个类、或者一个模块来进行正确性校验的
#测试结果：1、单元测试通过，说明测试函数功能正常 2、不通过，说明有bug，要么测试条件不对

#函数单元测试
def mySum(x,y):
	return x+y
def mySub(x,y)
	return x-y
print(mySum(1,2))
#单元测试一般不在原文件上写
import unittest
from aaaa import mySum
from aaaa import mySub
class Test(unittext.TestCase):
	def setUp(self
		#连接数据库
		print("开始测试时自动调用")
	def tearDown(self):
		#关闭数据库
		print("结束测试时自动调用")
	def test_mySum(self):
		self.assertEqual(mySum(1,2),3,'加法错误')
	def test_mySub(self):
		self.assertEqual(mySub(2,1),1,'减法错误')
		
if __name__=="__main__":
	unittest.main()

#类单元测试
class Person(object):
	def __init__(self,name,age):
		self.name=name
		self.age=age
	
	def getAge(self,v):
		return self.age

              
from person import Person
per=Person("tom",20)
print(per.getAge())


import unittest
from person import Person
class Test(unittext.TestCase):
	def test_init(self):
		p=Person("hanmeimei",20)
		self.assertEqual(p.name,"hanmeimei",'属性不对')
	def test_getAge(self):
		p=Person("hanmeimei",20)
		self.assertEqual(p.getAge(),p.age,'getAge不对')

              
#文档测试
import doctest
#doctest 这个模块可以提取注释中的代码执行
#doctest 这个模块严格按python交互模式提取
#>>> 后面必须有一个空格
def mySum(x,y):
	'''
	get sum
	:param x:firstNum
	:param y:SencondNum
	:return :sum
	注意>>>后面必须有一个空格
	example:
	>>> print(mySum(1,2))
	3
	'''
	return x+y
	
print(mySum(1,2))
#函数内部 ''' ''' 可以写内部注释，主要是对函数备注

#进行文档测试,整篇文档进行文档测试
doctest.testmod()
```

## 125 telnet

```python
#telnet 远程登录标准协议
#控制面板-程序-启用或关闭windows功能，windows自带telnet功能
#win7 8 有个服务端，win10有服务端
telnet 10.0.142.219
telnet ip port 
输入用户名 密码 domain（IP）就可以登录了

#远程控制windows
import telnetlib
def telnetDoSomthing(ip,user,passwd,command):
	try
		#连接服务器
		telnet=telnetlib.Telnet(ip)
		#设置调试级别
		telnet.set_debuglevel(2)
		#读取信息
		rt=telnet.read_until("Login username:".encode("utf-8"))
		#写入用户名
		telnet.write((user+"\r\n").encode("utf-8"))
		#读取密码信息
		rt=telnet.read_until("Login password:".encode("utf-8"))
		#写入密码
		telnet.write((passwd+"\r\n").encode("utf-8"))
		#读取IP
		rt=telnet.read_until("Domain name:".encode("utf-8"))
		#写入IP
		telnet.write((ip+"\r\n").encode("utf-8"))
		
		#登录成功，写指令
		rt=telnet.read_until(">".encode("utf-8"))
		#写入IP
		telnet.write((command+"\r\n").encode("utf-8"))
		
		#上面命令执行成功 会继续读>
		#失败一般不是>
		rt=telnet.read_until(">".encode("utf-8"))
		
		#断开链接
		telnet.close()
		return True
	except:
		return False
		
if __name__=="__main__":
	ip="10.0.142.197"
	user="xumingbing"
	passwd="123456"
	command="tasklist"
	print(telnetDoSomthing(ip,user,passwd,command))
```



## 126 破解密码

```python
#破解密码 
#1234567890
#从N个元素中取出m个元素，叫排列，n=m全排列
#A33=3*2*1=6
#用代码生成排列
import itertools
mylist=list(itertools.permutations([1,2,3,4],3))
print(mylist)  #[(1,2,3),(1,2,4)...]
print(len(mylist))  #a43  4*3*2=24

#组合
import itertools
mylist=list(itertools.combinations([1,2,3,4],3))
print(mylist)  #[(1,2,3),(1,2,4),(1,3,4),(2,3,4)]
print(len(mylist))  #m!/n!(m-n)!  4


#排列组合  repeat=10就是取10位  0-9就是10位 10*10*10...
import itertools
mylist=list(itertools.product("0123456789abcdef...ABCDE...",repeat=4))
print(mylist)  #[(1,2,3),(1,2,4),(1,3,4),(2,3,4)]
print(len(mylist))  #m!/n!(m-n)!  4
```

```python
#暴力破解、概率破解
import itertools
import time
#mylist=list(itertools.product("0123456789abcdef...ABCDE...",repeat=4))
passwd=("".join(x) for x in itertools.product("0123456789",repeat=4)))
while True:
	try:
		time.sleep(0.5)#不停的话CPU受不了
		str=next(passwd)
		#在这就可以进行破解了
		print(str)
	except StopIteration as e:
		break

#正则表达式,如果不用正则，判断一个手机号很麻烦

def checkPhone(str):
	if str[0]!=1:
		return False
	elif len(str)!=11:
		return False
	for i in range(2,11):
		if i<"0" or i>"9":
			return False
	return True	
print(checkPhone("13069009401"))

#python1.5以后增加了re模块。提供了正则表达式模式
```

## 128 正则

```python
import re
#pip install 包管理工具
'''
re.match函数
原型：match(pattern,String,flags=0)
pattern-正则表达式
string-要匹配的正则表达式
flags-标志位，用于控制正则表达式的匹配方式
re.I 忽略大小写
re.L 做本地化识别 一般用不上
re.M 多行匹配，会影响^和$
re.S 使.匹配包括换行符在内的所有字符
re.U 根据Unicode字符集解析字符，影响\w \W \b \B
re.X 使我们更灵活的格式理解正则表达式

参数：
功能：尝试从字符串的起始位置匹配一个模式，如果不是其实位置，
匹配成功的话，返回None
'''
#www.baidu.com
re.match("www","www.baidu.com")  #返回一个span=(0,3)
re.match("www","awww.baidu.com") #None 从头匹配
re.match("www","wwW.baidu.com") #None
re.match("www","wwW.baidu.com",flags=re.I) #span=(0,3) match='wwW'
re.match("www","wwW.baidu.com",flags=re.I).span() #span=(0,3) match='wwW'
'''
re.search函数
原型：search(pattern,String,flags=0)
pattern-正则表达式
string-要匹配的正则表达式
flags-标志位，用于控制正则表达式的匹配方式
re.I 忽略大小写
re.L 做本地化识别 一般用不上
re.M 多行匹配，会影响^和$
re.S 使.匹配包括换行符在内的所有字符
re.U 根据Unicode字符集解析字符，影响\w \W \b \B
re.X 使我们更灵活的格式理解正则表达式

参数：
功能：扫描整个字符串，并返回第一个成功的匹配

'''
print(re.search('good',"a is good"))
'''
re.findall函数
原型：findall(pattern,String,flags=0)
pattern-正则表达式
string-要匹配的正则表达式
flags-标志位，用于控制正则表达式的匹配方式
re.I 忽略大小写
re.L 做本地化识别 一般用不上
re.M 多行匹配，会影响^和$
re.S 使.匹配包括换行符在内的所有字符
re.U 根据Unicode字符集解析字符，影响\w \W \b \B
re.X 使我们更灵活的格式理解正则表达式

参数：
功能：扫描整个字符串并返回结果列表
'''
print(re.findall('good',"a is good"))  #['good','good']
```

```python
'''
正则表达式的元字符
. 匹配除换行符以外的任意字符
[0123456789] []字符集合，表示匹配方括号中所包含的任意字符
[a-z] 匹配任意小写字母
[A-Z] 匹配任意大写字母
[0-9] 匹配任意数字
[0-9a-zA-Z] 匹配任意数字和字母
[0-9a-zA-Z_] 匹配任意数字、字母和下划线
[^sunck]  匹配除了 sunck这几个字符以外的所有字符，^称为脱字符 表示不匹配集合中的字符
[^0-9] 匹配所有的非数字字符
\d 匹配所有的数字
\D 匹配所有非数字字符
\w 匹配数字 字母 下划线
\W 匹配非数字 字母 下划线
\s 匹配任意的空白符  空格 换行 制表 换页 回车[ \f\n\r\t]
\S 匹配任意的非空白符  空格 换行 制表 换页 回车[^ \f\n\r\t]

re.findall('\d','adfads 12 3 bbadf',flags=re.I)
锚字符，边界字符
^ 行首匹配
$ 行尾匹配
\A 匹配字符串开始，\A只匹配整个字符串的开头
   在re.M模式下也不会匹配其它行行首
\Z 匹配字符串结束，\Z只匹配整个字符串结束
	在re.M模式下也不会匹配其它行结尾
\b 匹配一个单词的边界，也就是单子和空格间的位置
\B 匹配非单词边界



'''
print(re.search('^good$','xgoody'))
print(re.findall('good','good\ngood'),re.M)  #返回2个
print(re.findall('\Agood','good\ngood'),re.M) #只返回1个
print(re.findall('good','good\ngood'),re.M)  #返回2个
print(re.findall('good\Z','good\ngood'),re.M) #只返回1个
print(re.search(r'er\b','never')) #
print(re.search(r'er\b','nerve')) #

print(re.search(r'er\B','never')) #
print(re.search(r'er\B','nerve')) #
```

```python
'''
#x y z 假设都是普通字符，不是正则的原字符
(xyz) 匹配小括号内的xyz  作为一个整体去匹配
x? 匹配0个或者1个x
x* 匹配0或者任意多个x (.* 匹配0个或多个字符，换行符不算)
x+ 匹配至少1个x
x{n} 匹配确定的n个x n是一个非负整数
x{n,} 匹配至少n个x  贪婪匹配
x{n,m} 匹配至少n个x 至多m个x
x|y   x表示或 x或者y
'''
print(re.findall(r'(good)','good is good'))
print(re.findall(r'o?','good is good')) #非贪婪模式 尽可能少的匹配
print(re.findall(r'a*','aaabaa'))#贪婪匹配 尽可能多的去匹配
print(re.findall(r'a+','aaabaa'))#贪婪匹配 尽可能多的去匹配
print(re.findall(r'a{3}','aaaaabaa'))#
print(re.findall(r'a{3,5}','aaaaabaa'))#
print(re.findall(r'((s|S)unck)','aaaaabaa'))


str='abcxxxxxddd,abcxxxxxxxddd'
print(re.findall(r'^abc.*ddd$',str))

#特殊查找 
#*? +?  x? 最小匹配
#通常都是尽可能多的匹配，可以适用这种?
```

```python
'''
要从注释中找到要的内容
这个?要注意  加个问号就是非贪婪模式，最小匹配
不加问号就是贪婪模式，直到最后一个匹配内容
(?:x) 类似(xyz),但它不表示一个组
'''

/* part1 */ /* part2 */
print(re.findall(r'//*.*/*/',r'/* part1 */ /* part2 */'))
print(re.findall(r'//*.*?/*/',r'/* part1 */ /* part2 */'))

def checkPhone2(str):
	#13900887890
	pat=r"^1\d{10}$"
	res=re.match(pat,str)
	if res != None:
		return True
	else:
		return False
		
#qq mail phone user ip url
```

133 re模块深入

```python
import re
'''
字符串切割
'''
str='a is a'
print(str.split(' '))
print(re.split(' +',str))
'''
re.finditer函数
原型：finditer(pattern,String,flags=0)
pattern-正则表达式
string-要匹配的正则表达式
flags-标志位，用于控制正则表达式的匹配方式
re.I 忽略大小写
re.L 做本地化识别 一般用不上
re.M 多行匹配，会影响^和$
re.S 使.匹配包括换行符在内的所有字符
re.U 根据Unicode字符集解析字符，影响\w \W \b \B
re.X 使我们更灵活的格式理解正则表达式

参数：
功能：功能类似findall，但是返回的是一个迭代器
	这样就可以不导致内存耗尽死机，返回None
'''
str3="a is as ,b is xx ,c is afdsaf"
d=re.finditer(r'(is)',str3)
while True:
	try:
		l=next(d)
		print l
	except StopIteration as e:
		break

'''
字符串替换和修改
sub(pattern,repl,string,count=0,flags=0)
subn(pattern,repl,string,count=0,flags=0)
pattern 正则(规则)
repl 指定的用来替换的字符串
string 要处理的字符串
count=0 最多替换次数
flags=0

功能：目标字符串中找到规则匹配字符串，再指定字符串
	可以替换次数，不指定替换所有
区别：sub返回字符串，subn返回元组，里面有返回字符串和修改次数

'''
re.sub(r'(good)','nice','a is a good good good boy')
re.subn(r'(good)','nice','a is a good good good boy')
```

```python
'''
分组的概念：除了简单判断是否匹配之外
正则表达式还有提取子串的功能
用()表示分组，
'''
str="010-85330126"
m=re.match(r"(\d{3})-(\d{8})",str)

#m=re.match(r"(?P<first>\d{3})-(?P<last>\d{8})",str)
#给组取名字 (括号后面加?P<name>
#m.group("first")
print(m)
print(m.group(0)) #010-85330126  代表原始字符串
print(m.group(1)) #010 
print(m.group(2))  #85330126
print(m.groups()) #("010","85330126")

'''
编译 当我们使用正则表达式，re模块会干二件事
1、编译正则表达式，表达式本身不合法会报错
	re.compile(pattern,flags=0)
	
2、用编译后的正则去匹配对象

'''
#编译
pat=r"^1\d{10}$"
re_mobile=re.compile(pat)
re_mobile.match("13600000000")

'''
re.match()
re.search()
re.findall()
re.finditer()
re.split()
re.sub()
re.subn()
'''
re_QQ=re.compile(r"^[1-9]\d{5,9}$")
```

## python爬虫

```python
#python爬虫
'''
网络爬虫又称网络蜘蛛，网络蚂蚁，网络机器人
可自动化浏览网络中信息
爬虫组成：控制节点  爬虫节点  资源库构成

设计思路：
	确定爬取url
	通过http协议获取html页面
	提取有用的数据：数据存库，url继续遍历
	
	爬虫选择：
		php 对异步支持不好
		java 爬虫要经常修改代码
		c/c++ 除了汇编 学习成本太高
		python 有强大的爬虫Scrapy以及成熟高效的scrapy-redis分布式策略
	
	技术:
		urllib:HTTP请求处理，获取响应
		re、xpath、BeautifulSoup4、jsonpath、pyquery
		ajax
		Tesseract 机器图片系统，识别图片中的文本
		
	Scrapy框架:高性能（异步网络框架twisted），提取了数据存储 数据下载 提取规则等组件
	分布式策略：scrapy-redis 一个网页2个爬虫去爬
	
	爬虫与反爬虫与反反爬虫三角之争
		user-agent：模拟浏览器
		代理：代理一秒访问100次，换个ip代理去访问
		验证码：Tesseract
		动态数据加载：Selenium+PhantomJS(无界面的浏览器)，模拟真实浏览器加载js、ajax
		加密数据：
	爬虫分类
		通用网络爬虫：
			搜索引擎爬数据
			要访问的url存入队列，从队列提取url，解析DNS获取IP，通过ip下载html保存本地
			已经爬完存入已完成队列
			队列是广度遍历
			
		聚焦网络爬虫
		增量式网络爬虫
		深层网络爬虫
	
	怎么让网络爬虫爬到：
		1、百度的站长平台 去提交
		2、在其它网站里添加一个外链接（让百度自己去判断）
		3、搜索引擎一般和DNS服务合作，可以迅速收录
		
	爬虫要遵守Robots协议，robots.txt只是个协议，一般只要大网站才必须遵守
	搜索引擎排名：
		PageRank值：访问量
		竞价排名：谁钱多谁排名高
	缺点：
		只提供文本相关内容 不能提供多媒体
		提供结果千篇一律，不能针对不同人
		不能理解人的语义-所以出了聚焦检索--面向主题  面向需求
		
	URL：
		统一资源定位符，是互联网上的资源地址
		http https
	
	charles4.1.4 抓包工具
	抓包工具里最好用的 界面简洁
	正常不能抓https的
	手机的话 手机需要设置一个代理，代理换成手动，地址电脑的地址，小夜壶端口8888
	https的需要设置一下
	help-ssl proxying-install charles root certificate
	进入到证书安装页面，点击安装，当前用户，下一步，下一步，完成
	依次点击proxy-ssl proxying settings出现当前界面，点击add
	movie.douban.com  443 --ok--ok
	
'''
-------------
139
140
#1.urllib爬取网页
import urllib
#向指定的url地址发起请求，并返回服务器响应的数据（文件对象）
response = urllib.request.urlopen('http://www.baidu.com')

'''
#data = response.read().decode("utf-8")
data = response.read()  #返回字符串
print(type(data))  #<class'bytes>
print(data)
with open(r"d:\a.html","wb") as f:
	f.write(data)
'''
#方式二：
#data=response.readline()
#读取文件的全部内容
data=response.readlines()  #返回数组（bytes） 每行数据要处理也要decode('utf-8')
#response属性
print(response.info())
#返回状态码200 成功 304-说明已经有缓存 404  500 401 403
print(response.getcode())
if response.getcode()==200 or response.getcode()==304:
	return True

print(response.geturl()) #返回当前爬取地址
#解码
newUrl = urllib.request.unquote(url)
print(newUrl)
#编码
newUrl2 = urllib.request.unquote(newUrl)
print(newUrl2)
#######################################
#爬来的直接写入文件
#这个方法执行过程会产生缓存 如果一直不结束，缓存会越来越大，影响性能，要手动清除缓存
import url.request
urllib.request.urlretrieve("http://www.baidu.com",filename=r"d:\1.html")
urllib.request.urlcleanup()
#######################################
#模拟浏览器,防止反爬虫
import urllib.request
url="http://www.baidu.com"
headers={
	"User-Agent":"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36"
}
req = urllib.request.Request(url,headers=headers)
#这么写一般就绕过反爬虫了  加了个header
response=urllib.request.urlopen(req)
response.read().decode("utf-8")
print(data)

'''
上面不是成品
使用下面的agent，不同浏览器

header还有几个
Accept
X-Requested-With
Content-Type
'''
import urllib.request
import random
url="http://www.baidu.com"
agentList=[
	#百度去搜索user-agent大全
	"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36"
	,"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36"
]
agentStr=random.choice(agentList)
req = urllib.request.Request(url)
req.add_header("User-Agent",agentStr)
#这么写一般就绕过反爬虫了  加了个header
response=urllib.request.urlopen(req)
response.read().decode("utf-8")
print(data)
-------
141
'''
如果网页长时间未响应，系统判断超时，无法爬取
'''
import urllib.request

for i in range(1,100):
	try:
		request=urllib.urlopen("http://www.baidu.com",timeout=0.5)
		print(len(response.read().decode("utf-8")))
	except:
		print('请求超时，继续下一个爬取')
'''
http请求：进行客户端与服务端之间的消息传递时使用
常见
get 网址传参，不太安全
post 参数提交，数据量大，比较安全的数据传递
put 请求服务器存储一个资源，要指定存储位置
delete 请求服务器删除一个资源
opions  可以获取当前URL所支持的请求类型
head 请求获取对应的http报头信息

'''
#get请求
import urllib.request

url="http://www.baidu.com"
response = urllib.request.urlopen(url)
data=response.read().decode("utf-8")
print(data)
print(type(data))
#网络传输的都是字符串

#JsonView.exe  可以本地看json文件
--------
142
'''
json数据
保存数据的一种格式
可以保存本地json文件，也可以将json串进行传输
通常将json称为轻量级传输方式
json组成
{}对象-字典 []数组 ,分割 :键值对

'''
import json

jsonStr='{"name":"aa","age":18,"z":["a","b"]}'
jsonData=json.loads(jsonStr)
print(jsonData)
print(type(jsonData))
print(jsonData["name"])

jsonData2={"name":"aa","age":18,"z":["a","b"]}
jsonStr2=json.dumps(jsonData2)
print(jsonStr2)
print(type(jsonStr2))

#读取本地的json文件
path1=c"d:\1.json"
with open(path1,"rb") as f:
	data= json.load(f)
	print(data)
	#字典类型
	print(type(data))

#写本地json
path2=c"d:\2.json"
with open(path1,"w") as f:
	json.dump(jsonData2,f)
---------
143
'''
POST方式提交  爬虫
'''
#post方式提交 参数打包，单独传输，承载数据量大，安全
import urllib.request
import urllib.parse
url="http://www.baidu.com/form"
data={
	"username":"180401050106",
	"password":"chenwei"
}
#对要发送的数据进行打包
postData=urllib.parse.urlencode(data).encode("utf-8")
req = urllib.request.Request(url,data=postData)

req.add_header("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")

response = urllib.request.urlopen(req)
print(response.read().decode("utf-8"))

----------
144
'''
动态抓取ajax请求
'''
import urllib.request
import ssl

def ajaxUrl(url):
	headers={
	#百度去搜索user-agent大全
	"User-Agent":"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36"
	}
	req = urllib.request.Request(url,headers=headers)
	
	#使用ssl创建不需要验证的上下文
	context=ssl._create_unverified_context()

	response=urllib.request.urlopen(req,context=context)
	
	jsonStr=response.read().decode("utf-8")
	jsonData=json.loads(jsonStr)
	return jsonData
	
info = ajaxUrl("https://www.douban.com")
print(info)

for i in (1,11):
	url="https://www.douban.com?start="+str(i*20)+"&limit=20"
	info=ajaxUrl(url)
	print(len(info))
	
-------
145 146
#段子爬虫
import urllib.request
import re
def jokeCrawler(url):
	headers={
	"User-Agent":"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36"
	}
	req = urllib.request.Request(url,headers=headers)
	
	response=urllib.request.urlopen(req)
	
	HTML=response.read().decode("utf-8")
	
	pat=r'<div class="author clearfix">(.*?)<span class="stats-vote"><i class="number">'
	re_joke = re.compile(pat,re.S)
	divsList=re_joke.findall(HTML)
	
	#print(divsList)
	#print(len(divsList))
	dic={}
	for div in divsList:
		re_u=re.compile(r"<h2>(.*?)</h2>",re.S)
		username=re_u.findall(div)
		username=username[0]
		print(username)
		
		re_d=re.compile(r"<div class="content">\n<span>(.*?)</span>",re.S)
		duanzi=re_d.findall(div)
		duanzi=duanzi[0]
		print(duanzi)
		
		dic[username]=duanzi
		
	return dic

		
url="https://www.qiushibaike.com/text/page/1/"
info = jokeCrawler(url)
for k,v in info.items():
	print(k,v)
------------
147
#图片爬虫,爬虫容易 写正则难
import urllib.request
import re
import os
def imageCrauler(url,toPath):
	headers={
	"User-Agent":"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36"
	}
	req = urllib.request.Request(url,headers=headers)
	
	response=urllib.request.urlopen(req)
	
	HTML=response.read().decode("utf-8")
	#HTML=response.read() 写文件用这个
	#with open("d:\file.html","wb") as f:
	#	f.write(HTML)
	
	pat=r'<img (src)|(original)="//(.*?)" />'
	re_image = re.compile(pat,re.S)
	imagesList=re_image.findall(HTML)
	
	num=1
	for i in imagesList:
		path = os.path.join(toPath,str(num)+".jpg")
		num += 1
		urllib.request.urlretrieve("http://"+i,filename=path)
		

toPath=r"d:\img"		
url="https://www.qiushibaike.com/text/page/1/"
info = imageCrauler(url,toPath)
---------
148
#广度 深度遍历
import urllib.request
import ssl
import re
import os
from collections import deque

def writeFileBytes(htmlBytes,toPath):
	with open(toPath,"wb") as f:
		f.write(htmlBytes)
def writeFileStr(htmlBytes,toPath):
	with open(toPath,"w") as f:
		f.write(str(htmlBytes))

def getHtmlBytes(url):
	headers={"User-Agent":"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36"}
	req = urllib.request.Request(url,headers=headers)
	context = ssl._create_unverified_context()	
	response=urllib.request.urlopen(req,context=context)
	return response.read()

def qqCrawler(url,toPath):
	htmlBytes=getHtmlBytes(url)
	htmlStr=str(htmlBytes)
	pat=r"[1-9]\d{4,9}"
	re_qq=re.compile(pat)
	qqslist=re_qq.findall(htmlStr)
	qqslist=list(set(qqslist))  #去重
	f=open(toPath,"a")
	for qqStr in qqslist:
		f.write(qqStr+"\n")
	f.close()
	
	paturl=r"(((http|ftp|https)://)(([a-zA-Z0-9\._-]+\.[a-zA-Z]{2,6})|([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\&%_\./-~-]*)?)"
	re_url=re.compile(paturl)
	urlslist=re_url.findall(htmlStr)
	urlslist=list(set(urlslist))  #去重
	
	return urlslist
	
	#writeFileBytes(url,toPath)
	#getHtmlBytes(url,toPath)

url=r"https://www.douban.com/group/xxx"
toPath =r"d:\1.txt"
qqCrawler(url,toPath)

def center(url,toPath):
	#添加一个队列
	queue = deque()
	queue.appen(url)
	while len(queue) != 0:
		targetUrl=queue.popleft()
		urlslist=qqCrawler(targetUrl,toPath)
		for item in urlslist:
			tempUrl=item[0]
			queue.appen(tempUrl)
center(url,toPath)
```

## 151 网络概述

计算机之间 在互联网上 进行数据交互。

**七层架构**
物理层、数据链路层、网络层、传输层、会话层、表示层、应用层

**ipv4包头**

有20个字节  8位是1字节

4位版本-ip的操作版本 4或6
+4位首部长度
+8位服务类型
+16位总长度（字节数）
+16位标识+
3位标识
+13位片偏移
+8位生存时间
+8位协议
+16位首部检验和
+32位源ip地址
+32位目的ip地址

+选项----不足32位补0
+数据

```tex
TCP数据报
20字节的ipv4首部+20字节的TCP首部+TCP数据
16位源端口号
+16位目的端口号
+32位序号
+32位确认序号
+4位数据偏移
+6位保留-保留6个0
+1位LRC
+1位ACK
+1位PSH
+1位RST
+1位SYN
+1位FIN
（紧急标识，有意义的应答标识，推、重置连接标识，同步序列号标识，完成发送数据标识）
+16位窗口大小-目的机使用15位的域告诉源主机，它想收到的每个tpc数据段大小
+16位校验和-tcp也包括16位的错误检查域-校验和 域
+16位紧急指针-可选的16位指针，指向段内最后一个字节位置，只在urg标识设置时才生效
+选项-至少一字节的可变域标识哪个选线
+数据-域的大小是最大的MSS,MSS可以在源和目的机器之间协商
+填充-目的确保空间可预测、定时、规范大小，这个域加入额外的0保证TCP头是32位的整数倍
```

```tex
tcp/ip协议
ip协议 不保证到达 也不保证顺序
tcp是建立在ip协议之上。
tcp可保证数据包按照顺序到达
tcp会对数据号编号，如果丢包，自动重发
握手连接-确定连接走哪条路
ipv4是32位整数
ipv6是12位整数
三次握手：
	SYN、ISN以及目的的端口号
	SYN、ISN 以及确认信息（ACK）
	确认信息（ack）
断开一个TCP连接：
	服务器发一个 FIN，停止数据传输
	客户端发一个确认信息
	客户端发一个FIN
	服务器断开

ip包tcp包 客-服 客户端给服务器发包要连接
ip包tcp包 服-客 服务端给客户说好的
ip包tcp包 客-服 好的，我要开始说话了
经过以上tcp连接就建立了
ip包tcp包[说] 客-服 ip等
ip包tcp包[话，你] 客-服
ip包tcp包[收] 客-服

ip包tcp包[收到] 服-客
ip包tcp包[了] 服-客
ip包tcp包[！！！] 服-客

断开连接是服务端先断开的 

ip包tcp包 服-客 FIN 我不想聊天了
ip包tcp包 客-服 ACK 我知道了 
ip包tcp包 客-服 FIN 我早就不想聊了 
ip包tcp包 服-客 ACK 我知道了 咱俩断了

要理解发包和接包的过程
```

## 152 TCP 网络编程

```python
#客户端 创建TCP连接时，主动发起连接的
#服务端 接收客户端的连接
'''
服务器
socket()-bind()-listen()-accept()-read()-write()-close()
客户机
socket()-connect()-write()-read()-close()
要客户端连接 首先服务器必须开着的
'''
#socket 包含网络变成的所有东西
import socket
#1、创建一个socket
#第一个参数：指定协议-AF_INET：ipv4 AF_INET6：ipv6
#第二个参数SOCK_STREAM执行使用面向流的TCP协议
sk=socket.socket(socket.AF_INET,socket.SOCK_STREAM)
#建立连接 参数是一个元组，第一个参数是要连接IP地址，第二个元素是端口号
sk.connect(("www.sina.com.cn",80))
#
sk.send(b"GET / HTTP/1.1\r\nHost: www.sina.com.cn\r\nConnection: close\r\n\r\n")

data = []
while True:
	#每次接收1KB的数据
	tempData=sk.recv(1024)  #1024  1KB
	if tempData:
		data.append(tempData)
	else:
		break
dataStr = (b''.join(data)).decode("utf-8")
#断开连接
sk.close()
print(dataStr)

headers,HTML = dataStr.split('\r\n\r\n',1)
print(headers)
print(HTML)
```

## 154 socket通信

```python
import socket
#1、创建一个socket
#第一个参数：指定协议-AF_INET：ipv4 AF_INET6：ipv6
#第二个参数SOCK_STREAM执行使用面向流的TCP协议
server=socket.socket(socket.AF_INET,socket.SOCK_STREAM
#建立连接 参数是一个元组，第一个参数是要连接IP地址，第二个元素是端口号
#2、绑定监听IP和端口
server.bind(("127.0.0.1",8080))
#3、监听
server.listen(5)

#4、等待连接
#正常应该是等待连接后，新启用一个线程，将当前的clientSocket交给线程去处理
clientSocket,clientAddress=server.accept()
while True:
	data=clientSocket.recv(1024)
	print("收到",str(clientSocket),"的数据"+data.decode("utf-8"))
	clientSocket.send("你好帅 彼此彼此".encode("utf-8"))

################客户端################
import socket
client=socket.socket(socket.AF_INET,socket.SOCK_STREAM
#建立连接 参数是一个元组，第一个参数是要连接IP地址，第二个元素是端口号
client.connect(("127.0.0.1",8080))
#sk.send(b"GET / HTTP/1.1\r\nHost: www.sina.com.cn\r\nConnection: close\r\n\r\n")
count=0
while True
	data=input("请输入给服务器发送数据")
	client.send(data.encode("utf-8"))
	#每次接收1KB的数据
	info=client.recv(1024)  #1024  1KB
	print("服务器说",info.decode("utf-8"))

```

## 155 UDP

UDP编程
随时可以给服务区发消息，但是不能保证数据的到达，也不能保证数据的完整，不用建立连接，所以UDP的速度很快

TCP就建立可靠连接，并且通信双方都可以以流的方式发送数据
相对TCP，UDP是面向无连接协议
使用UDP协议时不需要建立连接，只需要知道对方IP地址和端口号
就可以直接发送数据包，但是能不能到达就不知道了
适合做广播。
虽然UDP传输数据不可靠，但是优点是和TCP进行比较：速度很快，对于安全要求不高的数据是可以用UDP的。

服务器
socket()-bind()-readfrom()-阻塞等待客户-sendto()-close()
客户机
socket()-bind()-sendto()-readfrom()-close()

1.冒充飞秋

```python
import socket
str="1_1bt4_10#32499#002481627512#0#0#0:1289671407:username:b:288:凯哥"
#SOCK_DGRAM
udp=socket.socket(socket.AF_INET,socket.SOCK_DGRAM)
udp.connect(("10.0.142.206",2425))
udp.send(str.encode("utf-8"))
```

2.客户端与服务端通信

```python
#server
import socket

udpServer=socket.socket(socket.AF_INET,socket.SOCK_DGRAM)
udpServer.bind(("127.0.0.1",8900))
while True:
	data,addr = udpServer.recvfrom(1024)
	print("客户端说：",data.decode("utf-8"))
	info=input("请输入数据")
	udpServer.sendto(info.encode("utf-8"),addr)
	
	
#client
import socket

client=socket.socket(socket.AF_INET,socket.SOCK_DGRAM)
while True:
	data=input("请输入数据")
	client.sendto(data.encode("utf-8"),("127.0.0.1",8900))
	info=client.recv(1024).decode("utf-8")
	print("服务器说:",info)
```

## 156 多任务原理

```python
'''
windows、max os x、linux、unix等都支持多任务

操作系统同时运行多个任务

早期的电脑都是单个CPU，1个核心只能处理一单任务
那一个核心如何实现多任务？
多个任务交替进行，cpu的调度速度非常快，感觉上是多个任务一起执行。
qq2us 程序2 执行2us 程序3执行2us 反复执行。

多核实际是多个单核cpu
如果5个任务，4核，如果任何多于核，还是在某个核上多个线程交替进行
由于任务数量多于cpu核心数量，所以操作系统也会自动把多个任务轮流调度到每个内核上

二个名词
并发：看上去一起执行，任务数多于cpu核心数
并行：真正一起执行，任务数小于等于cpu核心数

实现多任务的方式：
1、多进程模式：启动多个进程，每个进程执行一个任务
2、多线程模式：启动多个线程 常用
3、协程模式：真正开发用不上
4、多进程+多线程模式：不建议使用，容易写乱

多进程：
对于操作系统而言，一个任务就是一个进程
任务管理器中的每个 程序就是一个进程
进程是系统中程序执行和资源分配的基本单位
每个进程都有自己的数据段，代码段和堆栈段
'''
```



## 157 单任务

```python
from time import sleep

def run():
	while True:
		print("bbb")
if __name__ =="__main__":
	while True:
		print("aaa")
			sleep(1)
			
	run()  #这永远执行不到  这就是单任务
```



## 多进程multiprocessing

想同时执行二个while  需要2个线程

```python
'''
启动多线程
multiprocessing 库
windows不能用fork启动多线程
multiprocessing是跨平台多线程版本运行
提供了一个Process类来代表一个进程对象
'''
from multiprocessing import Process
from time import sleep
import os

# 主进程和子进程的进程号不一样，是二个不同的
def run():
    while True:
        sleep(2)
        print("run...")
        # 打印当前进程的id号
        print("子线程号：%s"%os.getpid())
        # 获取当前进程的父进程ID号
        print("父线程号：%s"%os.getppid())

def func():
    while True:
        sleep(2)
        print("func...")

if __name__ == "__main__":
    # 主进程或者父进程
    print("main thread is running")
    # 打印当前进程的id号
    print("主线程号：%s"%os.getpid())
    # 创建一个子进程
    # target说明进程执行的任务
    p = Process(target=run)
    # 还可以给进程传一些参数 参数是元组，要有个逗号，否则认为是字符串了
    # p=Process(target=run,args=("nice",))
    # 启动子进程
    p.start()
    p1 = Process(target=func)
    p1.start()
```



## 158 父子进程顺序

通常，主线程是不会等待子线程执行的，主线程会直接执行完毕，而子线程自己完成自己的执行。但是如果子线程执行join()方法，执行循序就变了。

join的作用：通过是把自己加入到主进程里，让主进程等自己

```python
p = Process(target=run)
p.start()
p1 = Process(target=func)
p1.start()
p.join()  # 这句是把把自己加入到主线程的堆栈中，让主线程等自己执行完再往下执行。
print("父结束") # 线程p执行完毕 就会走这，不会管p1是否执行完毕
```

## 全局变量在多进程不共享

```python
#全局变量在多个进程中不能共享。
from multiprocessing import Process

num=100 # 定义一个全局变量 num

def run():
	print("子开始")
	# 子进程可以读取到全局变量的值，修改只在子进程中进行，不会影响全局的num值
	global num
	num += 1
	print(num)
	print("子结束")

if __name__=="__main__":
	print('父开始')
	p=Process(target=run)
	p.start()
	p.join()
	print('父结束')

	print(num) #父进程可以读取到全局变量，但是子进程对全局的修改不生效
'''
父开始
子开始
101
子结束
父结束
100
'''
```

## 159 进程池 Pool

```python
from multiprocessing import Pool
import os, time, random

def run(name):
    print("子线程[%s]开始--pid:%s" % (name, os.getpid()))
    start = time.time()
    time.sleep(random.choice([1, 2, 3]))
    end = time.time()
    print("子线程[%s]结束--pid:%s--耗时%.2f" % (name, os.getpid(), end - start))

if __name__ == "__main__":
    print('父开始')
    # 进程池 表示同时执行的进程数
    # Pool默认大小是CPU核心数
    pool = Pool(4)  # 4可以不传
    for i in range(8):
        # 创建进程，放入线程池统一管理
		# 非阻塞模式 线程池中的几个线程一起执行，需要使用join加入到主线程
        pool.apply_async(run, args=(i,))
		# 阻塞模式 所有线程按加入线程池顺序执行，不用加入join也可以执行
        # pool.apply(run, args=(i,))

    # 如果要调用join之前，必须先调用close，调用close之后就不能再继续添加新的进程
    pool.close()
    pool.join()
    print('父结束')
```

## 普通文件拷贝

```python
import os,time

def copyFile(rPath,wPath):
	fr=open(rPath,"rb")
	fw=open(wPath,"wb")
	context=fr.read()
	fw.write(context)
	fr.close()
	fw.close()

path=r"d:\file"
toPath=r"d:\tofile"

fileList=os.listdir(path)
for fileName in fileList:
	copyFile(os.path.join(path,fileName),os.path.join(toPath,fileName))
```

## 160 多进程拷贝文件

```python
import os, time
from multiprocessing import Pool

def copyFile(rPath, wPath):
    fr = open(rPath, "rb")
    fw = open(wPath, "wb")
    context = fr.read()
    fw.write(context)
    fr.close()
    fw.close()

path = r"d:\file"
toPath = r"d:\tofile"

if __name__ == "__main__":
    fileList = os.listdir(path)
    start = time.time()
    pp = Pool(2)
    for fileName in fileList:
        pp.apply_async(copyFile, args=(os.path.join(path, fileName), os.path.join(toPath, fileName)))

    pp.close()
    pp.join()
    end = time.time()
    print("总耗时：%0.2f" % (end - start))
# 可能比单进程慢，因为创建是很耗时的
```

## 继承实现Process

```python
from multiprocessing import Process
import os, time
class ChenProcess(Process):
    def __init__(self, name):
        Process.__init__(self)
        self.name = name

    # run方法重写父类的run  调用start自动调用
    def run(self):
        print("子进程(%s--%s)启动" % (self.name, os.getpid()))
        time.sleep(3)
        print("子进程(%s--%s)结束" % (self.name, os.getpid()))

if __name__ == "__main__":
    print("父进程启动")
    # 创建子进程
    p = ChenProcess("sub thread")
    p.start()  # 自动调用p线程的run方法
    p.join()
    print("父进程结束")
```

## 161 使用Queue完成进程间通信

```python
from multiprocessing import Process, Queue
import os
import time

def write(q):
    print("启动写入的子进程%s" % os.getpid())
    for chr in ['A', 'B', 'C', 'D']:
        q.put(chr)
        time.sleep(1)
    print("结束写入的子进程%s" % os.getpid())

def read(q):
    print("启动读的子进程%s" % os.getpid())
    while True:
        value = q.get(True)
        print("value=", value)
    print("结束读的子进程%s" % os.getpid())

if __name__ == "__main__":
    print("父进程开始")
    q = Queue() # 创建队列
    pw = Process(target=write, args=(q,)) # 创建向队列写入的子进程
    pr = Process(target=read, args=(q,)) # 创建消费进程
    pw.start()
    pr.start()
    pw.join()
    # 强制结束，因为是用死循环q.get(True) 等待
    pr.terminate()  # pr进程里是个死循环，无法等待其结束，只能强行结束子进程
    print("父进程结束")
```

启动一个word.exe就是一个进程
但是一个进程里可以同时干多件事
就需要同时运行多个子任务，我们把进程里的这些“子任务”叫作线程

线程 通常叫作轻型的进程，线程是共享内存空间的并发执行的多任务
每个线程都共享一个进程的资源

进程-用户地址空间-线程1 线程2 线程3
线程是最小的执行单元，而进程是由至少一个线程组成。
调度进程和线程，完全是由操作系统决定，程序自己不可决定什么时候执行，执行多长

模块
1、_thread模块    低级模块-接近底层，c语言写的
2、threading模块  高级模块，对_thread进行了封装

## 162 单线程threading

线程不需要重新开进程，效率高

```python
import threading
import time

def run(num):
    print("子线程(%s)启动" % (threading.current_thread().name))
    # 实现线程功能
    print("打印", num)
    time.sleep(2)
    print("子线程(%s)结束" % (threading.current_thread().name))
if __name__ == "__main__":
    # 任何进程默认启动就会启动一个线程
    # 称为主线程，主线程可以启动新的子线程
    print("主线程(%s)启动" % (threading.current_thread().name))
    
    # 创建子线程
    t = threading.Thread(target=run, name="childName", args=(1,))
    
    t.start()
    # 等待线程结束  这是必须的，主线程必须等所有子线程结束
    t.join()
    print("主线程(%s)结束" % (threading.current_thread().name))
```

## 多线程全局变量问题

多线程和多线程种最大的区别就是变量问题，在多进程中，全局变量不共享。
多进程中同一个变量各自有一份进程有一个拷贝，互不影响。

多线程是共享全局变量的，所以任何一个变量都可以被任意一个线程修改。

线程之间共享数据最大的危险在于多个线程同时修改同一个变量，容易把内容改乱。

```python
import threading

num = 100
def run():
    global num
    for i in range(1000000):
        num = num + i
        num = num - i
if __name__ == "__main__":
    t = threading.Thread(target=run)
    t1 = threading.Thread(target=run)
    t.start()
    t1.start()
    # 等待线程结束  这是必须的，主线程必须等所有子线程结束
    t.join()
    t1.join()
    print("num=" + str(num))
# 按照逻辑看 最终应该是100,实际不是，是不规则的一个数
# 但是多线程，冲突，最后就乱了
```

## 163 线程锁解决并发threading.Lock

线程锁确保了这个代码只能一个线程执行

效率大大降低，阻止了多线程的并发执行，包含锁的某段代码实际上只能以单线程模式执行

由于可以存在多个锁，不同线程持有不同的锁

并试图获取其它的锁，这样锁来锁去，可能造成死锁，导致多个线程挂起

只能靠操作系统强制终止

```python
import threading
import time

lock = threading.Lock() # 锁也是一个对象
num = 100
def run():
    global num
    for i in range(1000000):
        lock.acquire() # 这加一个锁，多线程加同一把锁
        try:
            num = num + i
            num = num - i
        finally:
            lock.release() # 解锁
        '''
		with lock: 可以自动上锁 解锁
		with lock:
			num = num + n
			num = num - n
		'''
if __name__ == "__main__":
    t = threading.Thread(target=run)
    t1 = threading.Thread(target=run)
    t.start()
    t1.start()
    t.join()
    t1.join()
    print("num=" + str(num))
```

## 164-166 thread.local

为每个线程开辟一块空间进行数据存储，空间与空间是隔离的

```python
import threading
num = 0
local = threading.local()
def run(x, n):
    x = x + n
    x = x - n
def func(n):
    # 每个线程都有一个local.x，就是线程的局部变量
    local.x = num
    for i in range(10000):
        run(local.x, i)
    print("%s--%d" % (threading.current_thread().name, local.x+n))

if __name__ == "__main__":
    t = threading.Thread(target=func, args=(6,))
    t1 = threading.Thread(target=func, args=(9,))
    t.start()
    t1.start()
    t.join()
    t1.join()
    num += 1 # 修改全局变量并没有影响thread.local.x中的值
    print("num=" + str(num))
```

## 多线程socket通信

```python
'''
创建服务器端，监听8080端口
当有新的连接进入通过threading.Thread新创建一个线程去开启一个客户端处理程序
'''
import socket

server=socket.socket(socket.AF_INET,socket.SOCK_STREAM
server.bind(("127.0.0.1",8080))
server.listen(5)

def run(clientSocket):
	data=clientSocket.recv(1024)
	print("收到",str(clientSocket),"的数据"+data.decode("utf-8"))
	clientSocket.send("你好帅 彼此彼此".encode("utf-8"))

while True:
	clientSocket,clientAddress=server.accept()
	t=threading.Thread(target=run,args=(clientSocket,))
	t.start()
	
'''
创建连接的客户端
'''
import socket

client=socket.socket(socket.AF_INET,socket.SOCK_STREAM)
client.connect(("127.0.0.1",8080)) #建立连接，参数是一个ip、端口构成的元组

while True:
	data=input("请输入给服务器发送数据")
	client.send(data.encode("utf-8"))
	#每次接收1KB的数据
	info=client.recv(1024)  #1024  1KB
	print("服务器说",info.decode("utf-8"))
```

## 使用tkinter和多线程制作建议QQ聊天

```python
#server端
import tkinter
import socket
import threading
win = tkinter.Tk()
win.title('QQ服务器')
win.geometry('400x400+200+0') #400x400  + left(200) + top(0)
users={}
def run(clientSocket,clientAddress):
	#clientSocket.recv是一个阻塞方法，第一次传过来的就是一个用户名
	userName=clientSocket.recv(1024)
	users[userName.decode("utf-8")] = clientSocket  #用全局字典，记录用户名对应的client
	print(users)
	while True:
		rData=clientSocket.recv(1024) # 阻塞  接收要发给其它用户的信息
		dataStr=rData.decode("utf-8")
		infolist=dataStr.split(":") # chenwei:aaaa
		users[infolist[0]].send(infolist[1].encode("utf-8"))
def start():
	ipStr=eip.get() #获取eip控件的值
	portStr=eport.get() #获取port控件的值
	server=socket.socket(socket.AF_INET,socket.SOCK_STREAM)
	server.bind((ipStr,int(portStr)))
	server.listen(10)
	while True:
		clientSocket,clientAddress=server.accept()
		t=threading.Thread(target=run,args=(clientSocket,clientAddress))
		t.start()
def startServer():
	s=threading.Thread(target=start)
	s.start()
if __name__ == '__main__':
	labelIp=tkinter.Label(win,text="IP").grid(row=0,column=0)
	eip=tkinter.Variable()
	entryIp=tkinter.Entry(win,textvariable=eip).grid(row=0,column=1)
	labelPort=tkinter.Label(win,text="Port").grid(row=1,column=0)
	eport=tkinter.Variable()
	entryPort=tkinter.Entry(win,textvariable=eport).grid(row=1,column=1)
	button=tkinter.Button(win,text="启动",command=startServer).grid(row=2,column=0)
	text=tkinter.Text(win,width=30,height=10)
	text.grid(row=3,column=0)
	win.mainloop()
#############################

# QQ客户端
import tkinter
import socket
import threading
win = tkinter.Tk()
win.title('QQ服务器')
win.geometry('400x400+200+0')  # 400x400  + left(200) +top(0)
# 发送数据的时候要知道这个全局的client 进行发送
ck = None
def getInfo():
    while True:
        data = ck.recv(1024)
        text.insert(tkinter.INSERT, data.decode("utf-8"))
def connectServer():
    global ck
    ipStr = eip.get()
    port = eport.get()
    userStr = euser.get()
    client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client.connect((ipStr, int(port)))
    # 连接之后，先直接发送一个当前用户的用户名
    client.send(userStr.encode("utf-8"))
    ck = client
    # 等待接收数据
    t = threading.Thread(target=getInfo)
    t.start()
def run(ck, ca):
    info = ck.recv(1024)  # 1024  1KB
    print("服务器说", info.decode("utf-8"))
def sendMail():
    friend = efriend.get()
    sendStr = esend.get()
    sendStr = friend + ":" + sendStr
    ck.send(sendStr.encode("utf-8"))
labelUser = tkinter.Label(win, text="userName").grid(row=0, column=0)
euser = tkinter.Variable()
entryUser = tkinter.Entry(win, textvariable=euser).grid(row=0, column=1)
labelIp = tkinter.Label(win, text="ip").grid(row=1, column=0)
eip = tkinter.Variable()
entryIp = tkinter.Entry(win, textvariable=eip).grid(row=1, column=1)
labelPort = tkinter.Label(win, text="port").grid(row=2, column=0)
eport = tkinter.Variable()
entryPort = tkinter.Entry(win, textvariable=eport).grid(row=2, column=1)
button = tkinter.Button(win, text="连接", command=connectServer).grid(row=3, column=0)
text = tkinter.Text(win, width=30, height=10)
text.grid(row=4, column=0)
esend = tkinter.Variable()
entrySend = tkinter.Entry(win, textvariable=esend).grid(row=5, column=0)
efriend = tkinter.Variable()
entryFriend = tkinter.Entry(win, textvariable=efriend).grid(row=6, column=0)
button2 = tkinter.Button(win, text="发送", command=sendMail).grid(row=6, column=1)
win.mainloop()
```

## Semaphore信号量控制线程数量

```python
import threading, time
# 控制一次最多可以几个线程同时运行
sem = threading.Semaphore(2)
def run():
    with sem: #使用sem信号量
        for i in range(10):
            print("%s--%d" % (threading.current_thread().name, i))
            time.sleep(1)
if __name__ == "__main__":
    for i in range(5):
        thread = threading.Thread(target=run)
        thread.start()
```

## 并发控制Barrier

```python
# 凑够一定数量才能执行
import threading, time
barrier = threading.Barrier(4) # 控制一次最多可以几个线程同时运行
def run():
    print("%s--%d--start" % (threading.current_thread().name, i))
    time.sleep(1)
    '''
    线程到这停住，凑够4个往下走，不然等着
    '''
    barrier.wait()
    print("%s--%d--end" % (threading.current_thread().name, i))
if __name__ == "__main__":
    for i in range(5):
        threading.Thread(target=run).start()
```

## 定时线程

```python
import threading
def run():
    print("aabb")
# 线程创建出来并不直接执行，延迟5秒执行
t = threading.Timer(5, run)
t.start()
t.join()
print("父线程结束")
```

## 167 线程通信

线程之间通过事件进行通信

```python
import threading,time
def func():
	event = threading.Event()  #创建一个事件对象
	def run():
		for i in range(5):
			event.wait() #阻塞，等待事件的触发
			event.clear() #执行到这说明事件的阻塞被触发，重置事件，将事件重置初始状态
			print("ccww%d"%i)
	t = threading.Thread(target=run).start()
	return event

e = func()
for i in range(5):
	time.sleep(2)
	e.set() #触发事件,只有触发了事件，阻塞才往下走
```

## 生产者与消费者

一般用队列实现，多个线程产生数据存入队列，多个线程从队列中取数据使用

```python
import threading, queue, time, random

def product(i, q): # 生产者
    while True:
        num = random.randint(0, 10000)
        q.put(num)
        print("生产者%d生产了%d放入队列" % (i, num))
        time.sleep(3)
    q.task_done() # 任务完成
    
def customer(i, q): # 消费者
    while True:
        item = q.get()
        if item is None:
            break
        print("消费者%d消费了%d数据" % (i, item))
        time.sleep(2)
    q.task_done() # 任务完成
    
if __name__ == "__main__":
    q = queue.Queue()  # 消息队列
    for i in range(4): # 启动生产者
        threading.Thread(target=product, args=(i, q)).start()
    for i in range(3):  # 启动消费者
        threading.Thread(target=customer, args=(i, q)).start()
```

## 168 线程调度Condition

线程的先后执行 是不可控的，如果我们想让线程按顺序执行：
线程1   --》0 2 4 6 8 
线程2   --》1 3 5 7 9 
想这么打印  --》 0 1 2 3 4 5 6 7 8 9

```python
import threading,time

# 线程条件变量
cond = threading.Condition()
def run1():
    with cond:
        for i in range(0, 10, 2):
            print(threading.current_thread().name, i)
            time.sleep(1)
            cond.wait()
            cond.notify()
def run2():
    with cond:
        for i in range(1, 10, 2):
            print(threading.current_thread().name, i)
            time.sleep(1)
            cond.notify()
            cond.wait()
# 如果不使用cond执行的顺序是乱的，使用了Condition之后二个线程交替执行
threading.Thread(target=run1).start()
threading.Thread(target=run2).start()
```



## 多线程实现原理

多任务实现原理
	master-worker模式，m负责分配任务，w负责执行
	m一般是主进程，w是子进程
多进程：稳定性高-某子进程挂了也没事 创建进程代价大，同时运行进程有限	
多线程：主线程是master其它子线程是worker
	比多进程快一点点，window下比多进程要高
	缺点：任何一个线程挂掉都可能造成进程崩溃

计算密集：大量计算，消耗CPU。这种也可以用多任务，但是任务越多，切换越多，cpu效率降低，任务数等于cpu核心数
	比如计算圆周率，高清视频解码等。
IO密集：涉及到网络，IO密集任务，cpu消耗少，任务大部分事件都在等待IO操作完成。
	对于IO密集型，任务越多，cpu效率越高，但也有个限度，常见的web应用

## 169 协程

子程序/子函数：在所有语言中，都是层级调用，调用顺序是明确的：
A调B，在B执行过程中调用 C，C结束，B结束，A结束

```python
def A():
	B()
def B():
	C()
def C():
    pass
A()
```

多线程并发，多个线程同时执行，每个线程占用一个CPU。

多线程并行，多个线程一起执行，通过时间片来回切换。

协程：  python对协程的支持是通过generator，由yield修饰的函数就是一个协程。

与return不同：return直接终止程序并且返回需要的内容，return后面的内容永远不会被执行。

yield修饰的方法在调用的时候，不会直接执行，而是返回一个生成器，这个生成器是一个可迭代的对象，可以使用next和list迭代执行。

 当执行next( gen)时，相当于开始调用程序执行，从记录的位置执行到yield位置上，返回yield后面的内容，yield后可接也可以不接内容。

此时生成器gen对象记录它返回时的执行的位置，可以使用next（gen）继续让生成器执行。

send()方法也可以唤醒生成器，并且将yield后的值赋值给生成器内部赋值语句。。。

```python
#与线程相比，协成的执行效率极高，因为只有一个线程，也不存在同时写变量的冲突
#在协成中共享资源不加锁，只需要判断状态就行了
#实现的
def run():
	print(1)
	yield 10
	print(2)
	yield
#协成的最简单风格，控制函数的阶段执行，节约线程或者进程的切换
gen=run()
print(next(gen))
#1
#10
print(next(gen))
#2
#None
```

```python
#使用list封装它，变成一个可迭代的，返回内容是所有yield后的内容组成在list
def run():
	print(1)
	yield 10 #把yield看做是一个return
	print(2)
	yield
	print(21)

g = run()
for item in list(g):
	print(item)
'''
1
2
10
None
'''
```

二个协程方法，通过内部方法调用完成多任务

```python
import time
def a(fun):
	fun.__next__();
	time.sleep(1)
	print("---a")
	yield

def b():
    time.sleep(1)
	print("b---")
	yield

while True:
	b1 = b();
	a1 = a(b1);
	next(a1);

```

多任务，使用死循环调用

```python
import time
def a():
   while True:
      time.sleep(1)
      print("---1")
      yield

def b():
   while True:
      time.sleep(1)
      print("---2")
      yield

if __name__ == '__main__':
   t1 = a()
   t2 = b()
   while True:
      next(t1)
      next(t2)
'''
---1
---2
---1
...
'''
```

## 170 mysql

关系型数据库：sqlite 比较小适合移动端、sqlserver 收费、mysql 大型、oracle 大型 收费
 nosql数据库：

- mogodb、redis（免费，开源的 稳定性差一点）

- 亚信 内存数据库

- 分布式内存数据库

```tex
mysql简介
持久化存储，优化读写，保证数据有效性
数据库分类：文档型 sqlite等就是一个文件
			服务型	数据存储在物理文件中，需要tcp ip协议连接，进行数据库读写操作
范式：
	1 列不可拆分
	2 唯一标识
	3 引用主键
	每个范式，都是在前一个范式的基础上建立的
	
E-R模型 当前的物理数据库都就按照E-R模型涉及的
	E 表示entry实体
	R 表示relationship关系
	一个实体转换为数据库中一个表
	关系 1对1 1对多 多对多
	关系转换位数据库中的一个列：在关系型数据库中一行就是一个对象
	
主要操作
	数据库操作 创建 删除
	表的操作 创建 修改 删除
	数据操作 包括增加 修改 删除 查询 简称CRUD
	
数据完整性：强制校验 not null
数据类型 int,decimal 
		 char,varchar,text
		 datetime,bit(布尔)
约束：主键，非空not null，唯一unique，默认default，外键foreign key
逻辑删除表内容
```

## 177 mysql常规操作

```tex
管理员身份cmd
启动：net start 服务名称
停止：net stop 服务名称
windows连接服务器：
mysql -uroot -p123456
退出：quit或exit

查看版本:
select version();
select now();

远程连接：
mysql -h ip地址 -u 用户名 -p(输入密码)

常用命令：
create database 数据库名 charset=utf8;
drop database 数据库名;
use 数据库名;
select database();
show tables;
create table 表名(字段 类型)
drop table 表名;

desc 表名 -查看表结构
show create table 表名 -查看建表语句

rename table 原表名 to 新表名-重命名表
alter table 表名 add|change|drop column 类型

插入数据：
insert into 表名 values(...)
insert into 表名(...) values(...)
insert into 表名(...) values(...),(...)

delete from 表名
update 表名 set column='a' where 1=1
select * from 表名

查询：
select * FROM 表名
as作为别名,多列逗号分割
消除重复行：
select distinct a,b from table
查询条件:
	where 1=1
比较运算符：
	>、>=、<、<=、=、!=、<>
逻辑运算符
	and、or、not
模糊查询
	%、_
	多个，一个
范围查询
	between and 连续范围内包括边界值
	in 表示在一个非连续范围内
空判断
	is null、is not null
	null和""是不同的
优先级
	小括号、not、比较运算符、逻辑运算符
聚合
	sum() max() min() count() avg()
分组
	group by 列1,列2...having 列1,列2 聚合
	
排序
	order by desc asc
分页
	limit start,count
	limit 5,10
关联
	foreign key(classid) refrences tb_class(id)
	外键如果不存在 插入不进去。级联查询的一种
	
	inner join on
	left join
	right join
```

## 178 python操作mysql

```sh
#安装mysql
pip install PyMySQL
```

```python
import pymysql

#参数1-数据库ip 参数2-用户名 参数3-密码 参数4-数据库名称
db = pymysql.connect("localhost","root","123456","dbname")
#创建一个cursor对象
cursor = db.cursor()
#要执行的sql语句
sql = "select version()"
#执行sql
cursor.execute(sql)
#获取返回信息
data = cursor.fetchone()
print(data)
cursor.close()
db.close()
#开启远程用户  mysql-user表 localhost换成%
```

```python
import pymysql

db = pymysql.connect("localhost","root","123456","dbname")
cursor = db.cursor()

#创建表
cursor.execute("drop table if exists bandcard")
sql = "create table bandcard(id int auto_increment primary key,money int not null)"
cursor.execute(sql)

#插入数据
sql1 = "insert into bandcard values (0,100)"
try:
	cursor.execute(sql1)
	db.commit()  #提交事物
except:
	db.rollback()  #回滚事物

#更新数据
sql2 = "update bandcard set money=10000 where id=1"
try:
	cursor.execute(sql2)
	db.commit()  #提交事物
except:
	db.rollback()  #回滚事物

#删除数据
sql3 = "delete from bandcard where id=1"
try:
	cursor.execute(sql3)
	db.commit()  #提交事物
except:
	db.rollback()  #回滚事物
    
cursor.close()
db.close()
```

## 179 mysql查询操作

- fetchone()	查询单个结果，结果是一个对象

- fetchall()    接收所有的返回行

rowcount：是一个只读属性，返回execute()方法影响的行数

```python
import pymysql

db = pymysql.connect("localhost","root","123456","dbname")
cursor = db.cursor()
sql = "select * from bandcard"
try:
	cursor.execute(sql)
	reslist = cursor.fetchall()
	for row in reslist:
		print("%d--%d"%(row[0],row[1]))
except:
	pass

cursor.close()
db.close()
```

封装成类

```python
import pymysql

class SunckSql():
	def __init__(self,host,user,passwd,dbName):
		self.host=host
		self.user=user
		self.passwd=passwd
		self.dbName=dbName
	
	def connect(self):
		self.db=pymysql.connect(self.host,self.user,self.passwd,self.dbName)
		self.cursor = self.db.cursor()
	
	def close(self):
		self.cursor.close()
		self.db.close()
		
	def get_one(self,sql)
		res = None
		try:
			self.connect()
			self.cursor.execute(sql)
			res=self.cursor.fetchone()
			self.close()
		except:
			print("失败")
		return res
		
	def get_all(self,sql):
		res = ()
		try:
			self.connect()
			self.cursor.execute(sql)
			res=self.cursor.fetchall()
			self.close()
		except:
			print("失败")
		return res
	
	def insert(self,sql):
		return self.__edit(self,sql)
		
	def update(self,sql):
		return self.__edit(self,sql)
		
	def delete(self,sql):
		return self.__edit(self,sql)
	
	def __edit(self,sql)
		count = 0
		try:
			self.connect()
			count=self.cursor.execute(sql3)
			self.db.commit()  #提交事物
		except:
			self.db.rollback()  #回滚事物
		return count

##########    
from sunckSql import SunckSql

s = SunckSql("127.0.0.1","root","123456","dbname")
reslist = s.get_all("select * from user_inf")
for row in reslist:
	print("%d--%d"%(row[0],row[1]))
```



## 181 NoSQL简介

NoSQL Not Only SQL 指的是非关系型的数据库。

随着访问量的上升，网站的数据库性能出现了问题，于是nosql被提了出来

优点：高扩展、分布式、低成本、架构灵活、半结构化数据、没有复杂的关系。

缺点：没有标准化、有限的查询功能、最终一致是不直观的程序

其他NoSQL：

- 列存储代表：hbase、cassandra、Hypertable

​		特点：按照列存储数据，最大特点是方便存储结构化和半结构化数据

​		方便做数据压缩，对某一列或几列查询有非常大的IO优势

- 文档存储：mongodb、couchdb

​		特点：文档存储一般用类似json格式存储，存储的内容是文档类型的

​		这样也就有机会对某些字段简历索引，关系数据库的某些功能

- key-value存储：tokyo cabinet/tyrant、Berketey DB、MemcacheDB、redis

  特点：可以通过key快速检索value，一般来说存储不管value格式

- 图存储：Neo4j、FlockDB

  特点：使用传统的关系数据库来解决的话性能地下，而且设计使用不方便

- 对象存储：db4o、Versant

  特点：类似面向对象语言的语法操作数据库，通过对象的方式存储数据

- xml数据库：Berkeley DB XML、BaseX



## mogodb 简介

```python
mongoDB是C++编写的，基于分布式文件存储的开源数据库系统
在高负载的情况下添加更多的节点，可保证服务器性能。
mongoDB将数据存储位一个文档，数据结构由键值(key=value)对组成，其文档类似于JSON对象
字段的值可以包含其它文档，数组以及文档数组

特点：
 1 提供文档存储，思路将原来行概念换成更灵活的文档模型，一条记录可以表示非常复杂的层次关系
 2 支持丰富查询表达式，查询执行使用JSON形式标记。
 3 非常容易扩展，多台机器分割数据，还可以平衡集群的数据和负载，自动重排文档
 4 语言支持的多
 5 功能丰富，包括索引，存js，聚合，固定集合，文件存储
 6 方便的管理，除了启动数据库服务器，几乎没有必要管理操作，集群只需要知道有新节点，就自动集成和配置新节点

mogodb和sql术语对应
'''
sql术语		mogodb术语		解释说明
database  	database		数据库
table	  	collection		数据库表/集合
row			document		数据记录行/文档
column		field			数据字段/域
index		index			索引
table joins					表连接，mongodb不支持
primary key	primary key   主键，mongodb自动将_id字段设置为主键

mongodb中一行数据就是一个json对象，多行是一个collection
一个mdb可以建立多个数据库
mdb的单个实例可容纳多个独立数据库，每一个都有自己的集合和权限，不同的数据库也放在不同文件中
mdb数据库名：全部小写，最多64，不得含有特殊字符，不能空串
mdb有保留数据库名，可直接使用：
	admin：root数据库，权限最高的
	local：用于不会被复制，仅限本地数据
	config：有集群的时候，配置集群使用的 单台的用不上

文档：核心概念，核心单元，文档看成一行
多个键及其关联放在一起就是文档。mdb使用了BJSON存储数据
BJSON理解在JSON上添加了json没有的数据类型

注意：
	文档中键值对是有序的
	文档中的值不仅是字符串，还可以是其它类型
	区分类型和大小写
	不能有重复键
	文档键是字符串
文档键命名：
	不能含有\0（空字符），这个字符用来表示键的结尾
	和$有特别的意义，只有在特定环境下才能使用
	不要以下划线开头
集合：
	集合就是表，集合中存放的文档结构可以不同，一般相同的
	当第一个文档插入，集合就被创建
	集合不能以system开头，system是保留前缀
	
mdb类型：
String			字符串
Integer			整形
Boolean			布尔
Double			双精度浮点
Min/Max keys	将一个值与BSON元素的最低值和最高值相对比
Arrays			数组
Timestamp		时间戳
Object			内嵌文档
Null
Symbol			符号，基本等同字符串，一般采用特殊符号类型的语言
Date			日期
Object ID			对象ID
Binary Data			二进制
Code				存储代码js
Regular Expression 存储正则
'''

进入安装目录
cd c:\program files\mongondb\Server\3.4\bin

启动服务：
mongod.exe --dbpath=d:\data\db 
```

## 182-187 mongo操作

```python
mongodb基本操作
一、操作数据库
	1、创建数据库
		use 数据库名称
		如果数据库不存在则创建数据库，否则切换到指定数据库
		use mydb
		如果刚创建的数据库不在列表内，
		如果要显示它，我们可以向新建数据库插入一些数据		
		( db.student.insert({name:"tom",age:18}) )
	2、查看所有数据库 
		show dbs
	3、查看当前正在使用的数据库
		db
		db.getName()
	4、断开连接 
		exit
	5、查看命令api 
		help
	6、删除数据库
		use 数据库名
		db.dropDatabase()
二、操作集合
	1、查看数据库下哪些集合
		show collections
	2、创建集合
		db.createCollection("集合名称")
		db.集合名称.insert(document)
		前者创建空集合，后者不仅创建而且插入一个文档
	3、删除当前数据库中集合
		db.集合名称.drop()
		db.class.drop()删除当前数据库下的class这个集合

三、操作文档
	1、插入文档
		a、使用insert方法插入文档
			db.集合名.insert(document)
			插入一个：db.student.insert({name:"tom",age:18})
			插入多个：db.student.insert([{name:"tom",age:18},{name:"tom1",age:18}])
			
		b、使用save方法插入文档
			db.集合名.save(document)
			如果不指定_id字段，save方法类似insert
			如果指定了_id字段,则更新_id字段的数据
			db.student.save({_id:ObjectId("3333344444"),name:"tom",age:18})
	
	2、文档更新
		a、update()方法用于更新已存在的文档
			db.集合.update(
				<query>,
				<update>,
				{
					upset:<boolean>,
					multi:<boolean>,
					writeConcern:<document>
				}
			)
			query:update 的查询条件，类似于查询的where
			update：update对象和一些更新的操作符($set,$inc)等,$set直接更新,$inc原来基础上累加
			upset:可选的，如果不存在update的记录，是否当新数据插入，true插入，false不插入，默认false
			multi:可选的，默认false，如果只更新找到的第一条记录，如果参数true，全部更新
			writeConcern：抛出以上的级别，可选，一般不写了
			
			#直接更新
			db.student.update({name:"lilei"},{$set:{age:25}})
			
			#在原来基础上累加
			db.student.update({name:"lilei"},{$inc:{age:25}})
			
			#改单个  改全部
			db.student.update({name:"poi"},{$inc:{age:40}},{multi:true}) #默认false不全改  multi改成true全改
			
		b、save()方法通过传入的文档替换已有文档
			db.集合名.save(
				document,
				{
					writeConcern:<document>
				}
			)
			document:文档数据
			writeConcern：可选，抛出异常级别

	3、文档删除
		在执行remove()函数前，先执行find()命令来判断
		执行条件是否存在 是一个良好的习惯
		db.集合名.remove(
			query,
			{
				justOne:<boolean>,
				writeConcern:<document>
			}
		)
		query:删除文档的条件
		justOne：可选，true或1只删除第一条找到还是所有
		writeConcern：抛出异常的级别，可选，一般不写了
		
		use mydb
		db.student.remove({name:"tom"})
		db.student.remove({name:"tom"},{justOne:true})
		
	4、文档查询
		a、find()方法 查询所有
			db.集合名.find()
			db.student.find()
		b、find() 查询指定列
			db.集合名.find(
				query,
				{
					<key>:1,
					<key>:1
				}
			)
			#只显示gender=1的 name和age列
			db.student.find({gender:1},{name:1,age:1})
			
			query：查询条件
			key：要显示的字段 1表示显示
			没有查询条件
				db.student.find({},{name:1,age:1})
		c、pretty()方法以格式化方式显示文档
			db.sudent.find().pretty()
		d、findOne()方法查询匹配结果的第一条数据
			db.student.findOne({gender:0})
			
	5、查询条件操作符
		a、大于 $gt
			db.集合名.find({<key>:{$gt:<value>}})
			db.student.find({age:{$gt:2}})
		b、大于等于 $gte
			db.集合名.find({<key>:{$gte:<value>}})
		c、小于 $lt
			db.集合名.find({<key>:{$lt:<value>}})
		d、小于等于 $lte
			db.集合名.find({<key>:{$lte:<value>}})
		e、大于等于和小于等于 - $gte 和$lte
			db.集合名.find({<key>:{$gte:<value>,$lte:<value>}})
		f、等于 -:
			db.集合名.find({<key>:<value>})
		g、使用 _id进行查询
			db.集合名.find({"_id":ObjectId("ID值")})
			db.student.find({"_id":ObjectId("1111122223")})
		h、查询某个结果集的数据条数
			db.student.find().count()
		i、查询某个字段的值当中是否包含令一个值
			db.student.find({name:/lie/})
		j、查询某个字段是否以另外一个值开头
			db.student.find({name:/^lie/})
	6、条件查询and 和 or
		a、and条件的使用
			db.集合名.find({条件1,条件2...})
			db.student.find({gender:0,age:{$gt16}})
		b、or条件的使用
			db.集合名.find(
				{
				  $or:[{条件1},{条件2}...]
				}
			)
			db.student.find({$or:[{age:17},{age:{$gt:20}}]})
		c、or and 联合使用
			db.集合名.find(
				{
					条件1,
					条件2,
					$or:[{条件3},{条件4}...]
				}
			)
	7、limit、skip
		a、limit()：读取指定数量的数据记录
			db.集合名.find().limit(2)  
		b、skip():跳过指定数量的数据
			db.集合名.find().skip(3)
		c、skip与limit联合使用
			通常使用这种方式实现分页功能
			db.集合名.find().skip(5).limit(5)
			db.student.find().skip(5).limit(5)
	
	8、排序
		语法：db.集合名.find().sort({<key>:1|-1})
			1升序 -1降序
			db.student.find().sort({age:1})
```

## mongo 添加文档

```python
'''
#安装mongodb的支持包
pip install pymongo
'''
from pymongo import MongoClient

#连接服务器
conn = MongoClient("localhost","27017")
#连接数据库
db = conn.mydb
#获取集合
collection = db.student
#添加文档
#collection.insert({name:"tom",age:18})
collection.insert([{name:"jerry",age:18},{name:"tom",age:19}])
#断开
conn.close()
```

## mongo 查询文档

```python
import pymongo  #降序需要使用
from pymongo import MongoClient
from bson.objectid import ObjectId #用于ID查询

#连接服务器
conn = MongoClient("localhost","27017")
#连接数据库
db = conn.mydb
#获取集合
collection = db.student
#查询部分文档
res = collection.find({"age":{"$gt":20}})
for row in res:
	print(row)
	print(type(row))
#查询所有文档
res = collection.find()
for row in res:
	print(row)
	print(type(row))
#统计查询
res = collection.find().count()
print(res)
#根据ID查询
res = collection.find({"_id":ObjectId("123456主键ID")})
print(res)
print(res[0])
#结果排序
res = collection.find().sort("age")#升序
for row in res:
	print(row)
res = collection.find().sort("age",pymongo.DESCENDING)#降序
for row in res:
	print(row)
#分页 越过3条拿5条
res = collection.find().skip(3).limit(5)
for row in res:
	print(row)

#断开
conn.close()
```

## mongo 更新文档

```python
from pymongo import MongoClient

#连接服务器
conn = MongoClient("localhost","27017")
#连接数据库
db = conn.mydb
#获取集合
collection = db.student
#更新文档
#collection.insert({name:"tom",age:18})
collection.update({"name":"lilei"},{"$set":{"age":25}})
#断开
conn.close()
```

## mongo 删除文档

```python
from pymongo import MongoClient

#连接服务器
conn = MongoClient("localhost","27017")
#连接数据库
db = conn.mydb
#获取集合
collection = db.student
#删除文档
collection.remove({"name":"lilei"}) #不写条件全部删除
#断开
conn.close()
```



## 188-191 redis安装

redis安装 redis-windows-master-2.8，修改redis.window.conf配置文件

```sh
maxheap 1024000000
#密码
requirepass 123456
#主机IP
bind 127.0.0.1
#端口号
port 6379
#数据存放
dbfilename dump.rdb
```

 启动redis：

```sh
#进入到安装目录下执行命令启动redis：
redis-server.exe redis.windows.conf
```

客户端登录：

```sh
redis-cli.exe
> ping   #会提示 NOAUTH
> auth "123456"
> ping   #PONG
> set name suck  #往redis存了一个数据 name就是key suck就是value
```

redis是key-value数据，键的类型是字符串。
值的类型分为五种：

- 字符串String
- 哈希hash
- 列表list
- 集合set
- 有序集合zset

```python
一、字符串String
	String是redis最基本的类型，最大能存储512MB的数据
	String类型是二进制安全的，即可以存储任何数据，比如数字，图片、序列化的对象等
	1设置
		a设置键值
			set key value
			set a 123
		b设置键值以及失效事件，秒为单位
			setex key senconds value
			setex b 10 456
		c设置多个键值
			mset key value[key value...]
			mset a 12 b 34
	2获取
		a根据键获取值，如果不存在返回None(null 0 nil)
			get key
		b根据多个键获取多个值
			mget key[key1...]
			mget a b c
	3运算
		要求：值是数据类型字符串
		a key对应的值加1
			incr key
		b key对应的值减1
			decr key
		c key对应的值加整数
			incrby key 2
		c key对应的值减整数
			decrby key 3
	4其它
		a 追加值
			append key value
			append a ! 返回长度
		b 获取值长度
			strlen key
二、键
	1查找键值
		key pattern
		key * 匹配所有的键
	2判断键存在 有1 无0
		exists key
		exists a
	3查看键对应的value类型
		type key
		type a
	4删除键以及值
		del key[key1...]
	5设置过期事件 秒为单位
		expire key seconds
		expire a 5
	6查看有效时间，以秒为单位
		ttl key   #-1是一直不过期
		
三、哈希hash
	概述：hash用于存储对象
	{name:"tom",age:18}
	1设置
		a设置单个值
		hset key field value
		a设置多个值
		hmset key field value[field value...]
	2获取
		a获取一个属性的值
			hget key field
			hget p1 name
		b获取多个属性的值
			hmget key field[field...]
			
		c获取所有属性和值
			hgetall key
		d获取所有的属性
			hkeys p1
		e获取所有值
			hvals key
		f返回包含数据的个数
			hlen key
	3其它
		a判断属性是否存在
			hexists key field #1有 0没有
		b删除属性以及值
			hdel key field[field...]
		c返回值的字符串长度
			hstrlen key field
四、列表list（类似队列）
	概述：列表的元素类型为string，按照插入顺序排序，在列表的头部或尾部添加元素
	1设置
		a在头部插入
			lpush key value[value...]
		b在尾部插入
			rpush key value[value...]
		c在一个元素的前或者后插入新元素
			linsert key before|after pivot value
			pivot是里面的一个元素
		d设置指定索引的元素值
			lset key index value
			注意：index从0开始
				索引值可以是复数，表示从偏移量的尾部开始，如果-1表示最后一个元素
	2获取
		a移除并返回key对应的list的第一个元素
			lpop key
		b移除并返回key对应的list的最后一个元素
			rpop key
		c返回存储在key的列表中的指定范围的元素
			lrange key start end
			lrange s1 0 -1
			注意：start end 都是0开始
				偏移量可以是负数
	3其它
		a裁剪列表，改为原集合的一个子集
			ltrim key start end
		b返回存储在key里的list的长度
			llen key
		c返回列表中索引对应的值
			llen key index
			lindex key 1
五、集合set
		概述：无序集合，元素类型为string类型，元素具有唯一性，不重复
		1设置
			a添加元素
			sadd key member[member...]
			sadd d2 2 3 5 6
		2获取
			a返回key集合中所有元素
				smember key
			b返回集合元素个数
				scard key
		3其它
			a求多个集合的交集
				sinter key[key...]
				sinter d1 d2
			b求多个集合的差集
				sdiff key[key...]
				sdiff d1 d2
			c求多个集合的合集
				sunion key[key...]
				sunion d1 d2
			d判断元素是否在集合中,存在返回1 不存在返回0
				sismember key member
				sismember d1 2
六、有序集合zset
	概述：有序集合，元素类型为string，元素具有唯一性，不能重复
	每个元素都会关联一个double类型的score(表示权重)，
		通过权重的大小进行排序，元素的score可以相同
	1设置
		a添加
			zadd key score member[score member...]
			zdd z1 1 a 5 b 3 c 2 d 4 e
	2获取
		a返回指定范围内元素
			zrange key start end
			zrange z1 0 -1
		b返回元素个数
			zcard z1
		c返回有序集合key中，score在min和max之间的元素个数
			zcount key min max
			zcount z1 2 4
		d返回有序集合key中，成员member的score值
			zscroe z1 c
```

## 192 redis操作

```sh
#安装redis
pip install redis
```

```python
import redis
# 连接redis
r=redis.StrictRedis(host="localhost",port=6379,password="123456")
# 方法1：根据数据类型的不同，调用不同的方法
# 写
r.set("p1","good")
# 读
x=r.get("p1")
print(x)

# 方法2：使用pipeline批量提交，缓冲多条命令，然后依次执行，减少服务器-客户端之间的TCP数据包
pipe=r.pipeline()
pipe.set("p2","good")
pipe.set("p3","good")
pipe.set("p4","good")
pipe.execute()
```

**redis作用：**
客户端-服务器-redis-mysql
先去redis里找数据，没有去mysql找。
redis主要去做缓存，常用数据缓存

```python
#redis封装
import redis
class SunckRedis():
	def __init__(self,passwd,host="localhost",port=6379):
		self.__redis = StrictRedis(host=host,port=port,password=passwd)
	def set(self, key, value):
		self.__redis.set(key,value)
	def get(self,key):
		if self.__redis.exists(key):
			return slef.__redis.get(key)
		else:
			return ""
```

