## 1. python语言简介

- 编译性语言java等，需要编译成字节码文件

- 解释性语言pytho，不需要编译

一行一条命令，不能随便缩进，缩进会报错，python2.x不支持中文，2.x的最后版本是2.7

**应用的范围：**

科学计算、人工智能、web服务器端和大型网站后端、gui开发、游戏开发、移动设备、嵌入式设备、系统运维、大数据、云计算
不适用：性高要求高不用python

**数据类型：**

分为数字类型 和 非数字型

python中根据数字存储长度，分为 int 和  long

- 数字类型：
  - 整型 int
  - 浮点 float	
  - 布尔型 bool	
  - 复数型 complex 主要用于科学计算 例如 平面场问题 波动问题 电感电容等问题
- 非数字类型：
  - 字符串
  - 高级-列表
  - 高级-元祖
  - 高级-字典

```python
# 元组 tuple	("a","b","c")
# 整数 int	2
# 浮点数 float 	2.2
# 复数（不常用）	1+2f
# 字符串 str	'abc'
# 布尔 True False
# 空值 None
# 列表 list [1,2,3]
# 字典 dict {'a':1, 'b':2}
# 集合 set  {'1','2'}
```



## 2. 指定语言和编码

```python
#指定要用/usr/bin下面的python来运行脚本
#!/usr/bin/python
#指定语言
# -*- coding: utf-8 -*-

#指定语言的三种方式：
#第一种最常用
# -*- coding: utf-8 -*-
# coding: latin-1
# vim: set fileencoding=utf-8 :
```

## 3. 通用函数

```python
# 常用的转义字符 \n \r \t \\ \' \"
# 使用r'\n' 字符串就不会转义了

a = 5
print(type(a)) # 查看对象的类型
print(id(a)) # 查看对象的内存地址
print(1+2) # 打印内容
input() # 获取输入
eval(str) # 可以将字符数转成有效的表达式（列表list，元组turple，字典dict，集合set）来求值或计算结果。
len(obj) # 返回对象（字符、列表、元组等）长度或项目个数
#python中没有字符，字符串也是数组，数组中每个元素是一个长度为1的字符串
abs(-1) #绝对值
all([]) # 用于判断给定的可迭代参数中所有元素是否都是True，0、空、False都是false，空数组或者元素返回True
any([]) # 判断列表或元祖中有一个为True结果就是True，否则False，空的返回False
ascii('\r') # 返回一个对象的字符串标识
bin(10) #输出一个数字的二进制 0b开头
bool(0) # 对象转bool
bytes([1,2,3,4], encoding='utf-8') #根据source不同返回不同byte数组
callable(a) # 判断是不是一个可调用对象
chr(10) # 查看10进制数字对应的ascii字符
@classmethod
complex(1,2) #1+2j #创建一个值为real + image * j的复数，或者转化一个字符串为复数
compile('print("123")', 'hello', 'exec') #将字符串编译成python可识别的代码
delattr(People, 'sex') #等同于 del People.sex
dict(a='1', b='2') #构造字典
dict(zip(['a','b'],[1,2])) #以字典映射的方式构造字典
dir() # 返回当前模块的属性列表，如果传递参数，返回传入对象的方法列表，字符串 数组 字典 元祖等
a,b = divmod(20,3) # 返回商和余数
for i in enumerate([1,2,34]): print(i) # enumerate获得一个枚举对象
exec("x+y") # 动态执行复杂代码 比eval强大

list(filter(lambda x:x!=2,[1,2,3,2,4,2])) #filter过滤
def is_oush(x):
    if x%2==0:
        return x
list(filter(is_oush,[1,2,3,2,4,2])) #filter过滤
float('2.2')

str.format() #格式化字符串
#通过位置
print ('{1},{0}'.format('chuhao',20))
print ('{},{}'.format('chuhao',20))
print ('{1},{0},{1}'.format('chuhao',20))
#通过关键字参数
print ('{name},{age}'.format(age=18,name='chuhao'))
#str()方法调用 对象的__str__(self):方法，可以自己定义
#通过映射 list
a_list = ['chuhao',20,'china']
print ('my name is {0[0]},from {0[2]},age is {0[1]}'.format(a_list))
#通过映射 dict
b_dict = {'name':'chuhao','age':20,'province':'shanxi'}
print ('my name is {name}, age is {age},from {province}'.format(**b_dict))

#填充与对齐
print ('{:>8}'.format('189'))	#     189
print ('{:0>8}'.format('189'))	#00000189
print ('{:a>8}'.format('189'))	#aaaaa189

#精度与类型f
#保留两位小数
print ('{:.2f}'.format(321.33345))	#321.33
#用来做金额的千位分隔符
print ('{:,}'.format(1234567890))	#1,234,567,890
#其他类型 主要就是进制了，b、d、o、x分别是二进制、十进制、八进制、十六进制。
print ('{:b}'.format(18)) #二进制 10010
print ('{:d}'.format(18)) #十进制 18
print ('{:o}'.format(18)) #八进制 22
print ('{:x}'.format(18)) #十六进制12


getattr(obj)  # 函数用于返回一个对象属性值
globals() # 函数会以字典类型返回当前位置的全部全局变量。
hasattr(object,name)  # 用于判断是否包含对应的属性
hash(object)	# 获取取一个对象（字符串或者数值等）的哈希值
hex(x) #将一个整数转换成十六进制字符串。
int([x[,radix]]) # 转整形 参数radix表示转换的基数（默认是10进制）
isinstance(1,int) # 函数来判断一个对象是否是一个已知的类型，类似 type()
print(type(A()) == A )  #type('')==str True
issubclass(C, A)  # 用于判断参数 class 是否是类型参数 classinfo 的子类

iter(o[, sentinel]) # 用来生成迭代器
lst = [1,2,3,4,5,6,7]
for i in iter(lst):
    print(i)      #输出1,2,3,4,5,6,7

lambda() # 是指一类无需定义标识符（函数名）的函数或子程序
#lambda匿名函数的格式：冒号前是参数，可以有多个，用逗号隔开，冒号右边的为表达式。
#其实lambda返回值是一个函数的地址，也就是函数对象。
f=lambda x,y:x+y
print(f(4,6))
#2.方法结合使用
from functools import reduce
foo=[2, 18, 9, 22, 17, 24, 8, 12, 27]
print(list(filter(lambda x:x%3==0,foo))) #筛选x%3==0 的元素
print(list(map(lambda x:x*2+10,foo)))    #遍历foo 每个元素乘2+10 再输出
print(reduce(lambda x,y:x+y,foo))        #返回每个元素相加的和


locals()  # 以字典类型返回当前位置的全部局部变量。

map(function,[]) # 通过把函数 f 依次作用在 list 的每个元素上，得到一个新的 list 并返回

max() # 返回给定参数的最大值，参数可以为序列。
#1.传入的多个参数的最大值
print(max(1,2,3,4))  #输出 4
#2.传入可迭代对象时，取其元素最大值
s='12345'
print(max(s))        #输出 5
#3.传入命名参数key，其为一个函数，用来指定取最大值的方法
s = [{'name': 'sumcet', 'age': 18},{'name': 'bbu', 'age': 11}]
a = max(s, key=lambda x: x['age'])
min() #与max类似，取小的

next()  # 返回迭代器的下一个项目 
# 首先获得Iterator对象:
it = iter([1, 2, 3, 4, 5])
# 循环:
while True:
    try:
        # 获得下一个值:
        x = next(it)
        print(x)
    except StopIteration:
        # 遇到StopIteration就退出循环
        break
        
a=iter('abcde')
print(next(a))    #输出 a

oct(12)   # 将一个整数转换成八进制字符串。

f=open('1.txt','r',encoding='utf-8')
print(f.read())

print(ord('a'))    #输出97   #它是chr() 函数（对于8位的ASCII字符串）或 unichr() 函数（对于Unicode对象）的配对函数
pow(100, 2) # 返回 xy（x的y次方） 的值 10000
@property #将方法变成属性，不可调用

range(start, stop[, step])
for i in range(5,-5,-1): #输出 [5, 4, 3, 2, 1, 0, -1, -2, -3, -4]
    print(i)

from functools import reduce
print(reduce(lambda x,y:x*y,lst))		# 运行过程为1*2*3*4*5*6=720
repr('\r') #\\r 函数将对象转化为供解释器读取的形式

b=list(reversed([1,2,3,4,5,6]))  #reversed 函数返回一个反转的迭代器

print ("round(-100.000056, 3) : ", round(-100.000056, 3)) #返回浮点数x的四舍五入值

setattr(People,'x',123)   #等同于 Peopel.x=123
print(People.x)  

a=set('www.baidu.com')  #输出   {'u', '.', 'm', 'c', 'w', 'd', 'i', 'a', 'o', 'b'}

# slice() 函数实现切片对象，主要用在切片操作函数里的参数传递
myslice=slice(5)      #设置一个 截取五个元素的切片
print(myslice)        #输出 slice(None, 5, None)
arr=list(range(10))
print(arr)            #输出 [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
print(arr[myslice])   #输出 [0, 1, 2, 3, 4]
print(arr[3:6])       #输出 [3, 4, 5]

'''
sorted() 函数对所有可迭代的对象进行排序操作。
sorted(iterable, key=None, reverse=False)
参数说明：
iterable -- 可迭代对象。
key -- 主要是用来进行比较的元素，只有一个参数，具体的函数的参数就是取自于可迭代对象中，指定可迭代对象中的一个元素来进行排序。
reverse -- 排序规则，reverse = True 降序 ， reverse = False 升序（默认）。
返回值
返回重新排序的列表。
'''
print(sorted([2,3,4,1,5,6]))          #输出 [1, 2, 3, 4, 5, 6]

#另一个区别在于list.sort() 方法只为 list 定义。而 sorted() 函数可以接收任何的 iterable。
print(sorted({1: 'D', 2: 'B', 3: 'B', 4: 'E', 5: 'A'}))      #输出  [1, 2, 3, 4, 5]

#利用key进行倒序排序
example_list = [5, 0, 6, 1, 2, 7, 3, 4]
result_list = sorted(example_list, key=lambda x: x*-1)
print(result_list)                   #输出  [7, 6, 5, 4, 3, 2, 1, 0]

#要进行反向排序，也通过传入第三个参数 reverse=True：
example_list = [5, 0, 6, 1, 2, 7, 3, 4]
result_list=sorted(example_list, reverse=True)
print(result_list)                   #输出 [7, 6, 5, 4, 3, 2, 1, 0]

#sorted 的应用，也可以通过 key 的值来进行数组/字典的排序，比如
array = [{"age":20,"name":"a"},{"age":25,"name":"b"},{"age":10,"name":"c"}]
array = sorted(array,key=lambda x:x["age"])
print(array)                         #输出 [{'age': 10, 'name': 'c'}, {'age': 20, 'name': 'a'}, {'age': 25, 'name': 'b'}]


@staticmethod #

# sum
print(sum([0,1,2]))              # 列表总和 3
print(sum((2,3,4),1))            # 元组计算总和后再加 1
print(sum([2,3,4,5,6],8))        # 列表计算总和后再加 2

a = list(range(1,11))
b = list(range(1,10))
c = sum([item for item in a if item in b])
print(c)                         #输出 45

#vars() 函数返回对象object的属性和属性值的字典对象。


#zip函数接受任意多个可迭代对象作为参数,将对象中对应的元素打包成一个tuple,然后返回一个可迭代的zip对象.
#这个可迭代对象可以使用循环的方式列出其元素
#若多个可迭代对象的长度不一致,则所返回的列表与长度最短的可迭代对象相同.

#1.用列表生成zip对象
x=[1,2,3]
y=[4,5,6]
z=[7,8,9]
h=['a','b','c','d']
zip1=zip(x,y,z)
print(zip1)
for i in zip1:
    print(i)

zip2=zip(x,y,h)
for i in zip2:
    print(i)

zip3=zip(h)
for i in zip3:
    print(i)

zip4=zip(*h*3)
for i in zip4:
    print(i)
print('==*=='*10)
#2.二维矩阵变换
l1=[[1,2,3],[4,5,6],[7,8,9]]
print(l1)
print([[j[i] for j in l1] for i in range(len(l1[0])) ])
zip5=zip(*l1)
for i in zip5:
    print(i)
```





## 3. pycharm下载地址

https://www.jetbrains.com/pycharm/download/#section=windows

## 4. 注释

```python
# 单行注释
"""多行注释"""
'''多行注释'''
```



## 5. 算数运算符

- 加	+    (2+3==5)
- 减    -    (3-1==2)
- 乘    *   (2* 3==6)
- 除    /  (5/2 == 2.5)
- 幂    **   (2**3 = 8)
- 除法取整    //    (3//2 == 1)
- 取模       %    (8/3 == 2)



## 6. 变量与常量

在 Python 中没有 **常量** 与 **变量** 之分。只有约定成俗的做法：

全大写字母的名称即为 常量：

```python
PI = 3.1415926
```

变量与常量都是用来在程序运行过程中，储存需要用到的值

变量在运行过程中会变化，用于存储临时的值。

常量在运行过程中不变，用于储存固定的值。

一般常量都是放在顶部，作为全局使用。

然而只是约定而已，Python 并没有语法上的强制要求，所以其实常量也可以变的，不过一般来说我们不会那么干。

赋值 

==注意==：=  不是等于，a = 1这条语句不是说 a 等于 1，而是在内存中分配了一块空间把1这个整数存储起来，然后又在内存中创建了一个名为a的变量，并且将a变量指向存储1的内存地址

在 Python 中，不用在声明时指定变量类型，甚至不需要去声明，直接使用即可。

但是偶尔我们也需要指定变量类型，那么可以先给变量赋一个初始值：

```python
a=5
b='abc'
```

- 在每个函数参数后添加冒号和数据类型
- 在函数后添加箭头（->）和数据类型以指定返回数据类型

```python
def add_numbers(num1: int, num2: int) -> int:
    return num1 + num2
print(add_numbers(3, 5)) # 8
```

Python的typing模块可以使数据类型注释更加冗长：

```python
from typing import Union

def square(arr: List[Union[int, float]]) -> List[Union[int, float]]:
    return [x ** 2 for x in arr]

print(squre([1, 2, 3])) # 1, 4, 9
```

删除变量 ：del 变量名

删除后变量无法引用

```python
#查看变量a的首地址，也就是内存地址
print(id(a))
```

## 7. 循环for/while

```python
while 条件:
    pass	# pass是python的占位语句  什么也不做的 保持结构

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

range()：列表生成器,生成数列，可以指定步长，不指定默认是1

```python
range(100)  #0-99
range([start,] end [,step])  # 3个参数 起点 终点  步长  
range(2,200,2)
```

```python
#同时遍历下标和元素
for index,m in enumerate([1,2,3,4]):
    print(index,m)
```



## 8.  数字操作

```python
# 整数
num0=6				# 单个变量定义
num1=num2=num3=1	# 多个变量定义
num4,num5=5,6     	# num4=5,num5=6

# 浮点数
f1=1.1
f2=2.2

# 复数（实数部分+虚数部分）

abs(-1) 		# 绝对值
max(1,2,3,4) 	# 返回最大值4
min(1,2,3,4) 	# 返回最小值1
pow(2,5) 		# 2的5次方 2**5
round(4.222,2) 	# 四舍五入

import math			# 导入math库
math.ceil(18.1)		# 向上取整
math.floor(18.1) 	# 向下取整
math.modf(22.3) 	# 返回整数部分与小数部分的浮点数元组
math.sqrt(16)   	# 开平方

import random
random.randint(0,100) 			# 0-100之间随机取一个
random.choice([1,3,5,7,9]) 		# 列表中随机选择一个，传入的是列表
random.choice(range(100))+1 	# range(5)==[0,1,2,3,4...99]

for i in range(5):print(i)		#0,1,2,3,4
for i in range(1,5):print(i)	#1,2,3,4
#指定步长
for i in range(1,5,2):print(i)  #1,3
    
import random
print(random.choice(range(10))+1)
print(random.randrange(1,100,2)) 	#1-100步长是2的随机数 最多是3个参数
print(random.randrange(10)) 		#0-9
print(random.random()) 				#0-1之间 包含0的一个随机小数

list=[1,3,5,6,7]  			#定义一个列表
random.shuffle(list)   		#将列表的所有元素随机排序 意义

random.uniform(3,9)  		#随机生成一个实数 小数或者整数 范围 3-9 包含二侧
```

## 9. 条件判断 if

```python
#0, 0.0, '', None, False 这些都是假，其他都是真
if a==1:
    pass
elif a==2:
    pass
else:
    pass
```

## 10. 位运算符 

把数字当成二进制计算:

```python
& 按位与
print(5 & 7)		#=5 都是1才是1
| 按位或
print(5 | 7)		#=7 1个是1就是1
^ 按位异或
print(5 ^ 7)		#=2 2位相异为1 其他为0
~ 按位取反
print(~5)			#=-6 每个二进制数据位取反

<< 左移 各个二进制左移动若干位 左边舍弃  右边补0
>> 右移 各个二进制右移动若干位 左边补0 右边舍弃
```

## 11. 关系运算符

```python
==	#等于
>=	#大于等于
<= 	#小于等于
!=	#不等于
> 	#大于
<	#小于

关系运算表达式：
	表达式1  关系运算符  表达式2
```

## 12. 逻辑运算符

- and  并且
- or  或者
- not  非

表达式优先级
	短路原则 and:一假后不计算   or:一真后不计算

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

## 12. 赋值运算符

如果二个运算符，运算符要挨在一起 +=、-= ......

- =  
- +=
- -= 
- *= 
- /= 
- //= 
- %= 

```python
c += a   # c = c+a
```



## 13. 字符串str

字符串：单引号或者双引号引起来的就是字符串，字符串不能修改。

```python
字符串运算：
	拼接字符串  "123"+"456"	#123456
	字符串相乘  "123" * 3	#123123123

通过索引下标查找字符 索引从0开始：
	a="123456"
	print(a[3]) #输出4  下标从0开始
字符串不可修改，如：a[3]='a' 会报错的。

截取字符串：
	a="123456"
	b=a[2:3]	# 3，下标从0开始，从下标2开始取到下标3，不包含下标3
	b=a[2:]		# 3456
	b=a[:4]		# 1234
	b=a[3:len(a)-1]			# 45	[3:5]
	print("23" in a)  		# 判断a是否包含'23'
	print("xxx" not in a)	# 判断a是否不包含'xx'

eval(str)		#将字符串当成有效的表达式来求值并返回
len(str) 		#返回字符串长度

str.lower()		#小写
str.upper(str)	#大写
str.swapcase()	#大小写相互转换

'capitalize'.capitalize()	#首字母大写
'ab cd ef'.title()			#每个单词的首字母大写

'x'.center(40,'q')			#填充字符串 40位，给定居中 两边填充q。先右后左
'1'.ljust(40,'0')			#左对齐  右补0  补齐40个0 默认不传fillchar 补空格
'1'.rjust(40,'0')			#右对齐  左补0  补齐40个0 默认不传fillchar 补空格
'x'.zfill(40)				#补0  补足40位 左补  右对齐

'abc abc abc'.count('abc')	#计数，待选参数3个，str,start,end，给定位置计数

'abcdefa'.find('a')			#返回第一次匹配下标，没有返回-1 可选 str,start,end
'abcdefa'.rfind('a')		#从右开始  返回第一次匹配下标，没有返回-1 可选 str,start,end

'abcdefa'.index('a')		#不存在会报错  更加严格，所以python 尽量用find()
'abcdefa'.rindex('a')

' abc'.lstrip()				#就是trim，待选可传
'***abc'.lstrip("*")		#abc 不仅仅可以strip空格，也可以strip其他指定字符
' abc '.rstrip()			#去掉右空格
' abc '.strip()				#去掉字符串左右空格

'bca'.split('c')	#['b', 'a'] 分割成数组，二个参数的，第二参数是分割的个数，如果是2，分出来2个后，剩下的就不分了
'bca'.rsplit('c')	#['b', 'a']
a='''
aaa
bbb
ccc
'''
a.splitlines(False) #按\r、\r\n、\n分隔并返回数组,参数默认False，如果True分隔的数组包含分隔符

#拼接
list1 = ['i','love','you']
str1 = " ".join(list1)  #以空格拼接 'i love you'

#查找字符串中最大最小的字符
min('abc')  #a
max('abc')  #c

#替换
'abbc'.replace('b','1')  	#a11c 默认全替换
'abbc'.replace('b', '1', 1)  	#a1bc 带参数只替换第一个

#创建一个映射表 这个意思是c-w h-i e-l n-l
#这种用的很少
maptab=str.maketrans('chen','will')
a='i am chen'
b=a.translate(maptab)   #i am wei

"abc".startswith('a', 0, 1)		#(str,start,end)
'abc'.endwith('c')				#以str结尾

#编码  解码--注意 解码要与编码一致，不一致会报错
type("aa".encode())		#<class 'bytes'>
data1='xxx'.encode()	#b'xxx'  
#二个参数 encoding="utf-8",errors='strict' 默认utf8
data1.decode('utf-8')  #解码，文件操作的时候会用到
#errors='ignore' 忽略错误
'xx'.isalpha()  	#字符中至少一字符并且所有都是字母返回true
'xx'.isalnum()  	#所有字符字母或数字
'xx'.isupper() 		#字符中至少一英文字母 所有英文字母全大写 返true
'xx'.islower()  	#字符中至少一英文字母 所有英文字母全小写 返true
'xx'.istitle()  	#标题化返回True 每个单词首字母大写
'xx'.isdigit() 		#只包含数字字符true
'xx'.isnumeric()  	#只包含数字字符true
'xx'.isdecimal()  	#判断十进制字符
'xx'.isspace()  	#判断是不只包含空格 \t \n \r

ord('a') 	#字符的ascii码  ord('a') 97
chr(97)  	#ascii转字符
```

字符串中占位符

```python
'''
%s 文本 
%d 整数 
%f 小数 
%% 百分号
'''
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



## 14. list数组

list数组，可以存数字，字符，下标从0开始，通过下标取对应值list[0]

```python
list1 = []
list1.append('1')
list1.append(2)
list1[0] = '12'
len(list1)		#输出数字长度，3

# 列表乘法
list2 = [1, 2, 3] * 3

# 成员判断，常用这种方法判断x是否在list2中
x=1
x in list2

list3 = list2[2:5]  #list2[3:] list2[:4] 列表截取，和字符截取差不多

# 二维数组
list11 = [[1,2,3],[4,5,6],[7,8,9],[10,11,12]]   #list11[1][2]

list1.append('1')
list1.extend([6,7,8])	#追加列表
list1.insert(2,'1')		#下标处添加一个元素 不覆盖原数据 原数据向后顺延

list1.pop()  #最后一个弹出
list1.pop(2)  #弹出指定下标的元素
list1[-1]    #就是取 list1的最后一个元素

list1.remove(1)  #移除列表中的某个元素  按照内容移除   匹配的第一个元素移除
list1.clear()  #清除

list1.index(3)  #找到第一个匹配值的下标，没找到报错

len(list1)  #长度
max(list1)  #列表最大值   有前提条件 里面元素必须是同类型 不然报错
min(list1)  #列表最小值   有前提条件 里面元素必须是同类型 不然报错

list1.count(1)  #1出现在list1中的次数
list1.reverse()	#把元素倒叙排序  直接改原
list1.sort()  	#元素排序 升序
list1.sort(reverse=True)  #reverse=True倒叙，reverse=False默认的正序

list3 = list1.copy()  		#元素拷贝
list2 = list((1,2,3,4)) 	#将元组转list

list2=list1
list1[1] = 0  	#发现list2  也跟着变了  用id(list1) id(list2) 去看地址，地址是一样的
#list2=list1 是将list2的值指向list1指向的地址，是浅拷贝

栈区-系统自动分配 程序结束自动释放内存空间
堆区-程序员手动开辟  手动释放，实际高级程序已经实现自动释放
	我们的数据存放在堆区，他的地址0x100
	栈区存放一个list27-值是0x100
	          list28-值是0x100
```

## 15. 元组tuple

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
list1 = [123]
tt=tuple(list1)

# 与list差不多，为什么要有个元组？
# 它就是个不可变的列表，其实就是他安全，元组不能变，别人没法修改你的元组，无形当中增加了安全性

# 元组遍历
for i in (1,2,3,4):
    print(i)
```

## 16. 字典 dict

```python
#字典  dict 使用键-值去存储数据，查找速度极快
1、key必须唯一
2、key必须是不可变对象
3、字符串、整数都是不可变的，可以作为key
4、list是可变的，不能作为key，一般用字符串

#定义一个字典
dict1={"tom":60, 'lilei':70}

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

- map速度快，list数据量大速度慢
- map浪费内存，list紧密排列不会浪费

## 17. set无序不重复集合

set：类似dist 是一组key的集合，不存储value
本质：无序不重复集合
创建set，需要一个list、tuple或者dict作为集合

```python
#重复数据在set是会被自动过滤
s1=set([1,2,3,4,5,3,4,5])
print(s1)  #{1,2,3,4} 自动过滤去重

s2=set((1,2,3,3,2,1));
print(s2)  #{1,2,3} 自动过滤去重

s3=set({1:"good",2:"nice"})
print(s2)  #{1,2} 自动过滤去重

s3.add(4)		 #添加
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

补充：

```python
'''
描述
set() 函数创建一个无序不重复元素集，可进行关系测试，删除重复数据，还可以计算交集、差集、并集等。
语法
set 语法：
class set([iterable])
参数说明：
iterable -- 可迭代对象对象；
返回值
返回新的集合对象。
'''

a=set('www.baidu.com')
b=set('www.gogle.com')      #重复的元素被删除 元素唯一 可以用来去重
print(a)                    #输出   {'u', '.', 'm', 'c', 'w', 'd', 'i', 'a', 'o', 'b'}
print(b)                    #输出   {'.', 'm', 'e', 'c', 'w', 'o', 'l', 'g'}

print(a&b)                  #交集 {'m', 'c', 'w', '.', 'o'}
print(a|b)                  #并集 {'m', 'c', 'i', 'w', 'b', 'd', 'u', 'g', 'e', 'a', '.', 'o', 'l'}
print(a-b)                  #差集 {'i', 'b', 'd', 'u', 'a'}


#1.比较
se = {11, 22, 33}
be = {22, 55}
temp1 = se.difference(be)        #找到se中存在，be中不存在的集合，返回新值
print(temp1)        #{33, 11}
print(se)        #{33, 11, 22}

temp2 = se.difference_update(be) #找到se中存在，be中不存在的集合，覆盖掉se
print(temp2)        #None
print(se)           #{33, 11},


#2.删除
se = {11, 22, 33}
se.discard(11)
se.discard(44)  # 移除不存的元素不会报错
print(se)

se = {11, 22, 33}
se.remove(11)
#se.remove(44)  # 移除不存的元素会报错
print(se)

se = {11, 22, 33}  # 移除末尾元素并把移除的元素赋给新值
temp = se.pop()
print(temp)  # 33
print(se) # {11, 22}


#3.取交集
se = {11, 22, 33}
be = {22, 55}

temp1 = se.intersection(be)             #取交集，赋给新值
print(temp1)  # 22
print(se)  # {11, 22, 33}

temp2 = se.intersection_update(be)      #取交集并更新自己
print(temp2)  # None
print(se)  # 22


#4.判断
se = {11, 22, 33}
be = {22}

print(se.isdisjoint(be))        #False，判断是否不存在交集（有交集False，无交集True）
print(se.issubset(be))          #False，判断se是否是be的子集合
print(se.issuperset(be))        #True，判断se是否是be的父集合


#5.合并
se = {11, 22, 33}
be = {22}

temp1 = se.symmetric_difference(be)  # 合并不同项，并赋新值
print(temp1)    #{33, 11}
print(se)       #{33, 11, 22}

temp2 = se.symmetric_difference_update(be)  # 合并不同项，并更新自己
print(temp2)    #None
print(se)             #{33, 11}

#6.取并集

se = {11, 22, 33}
be = {22,44,55}

temp=se.union(be)   #取并集，并赋新值
print(se)       #{33, 11, 22}
print(temp)     #{33, 22, 55, 11, 44}


#7.更新
se = {11, 22, 33}
be = {22,44,55}

se.update(be)  # 把se和be合并，得出的值覆盖se
print(se)
se.update([66, 77])  # 可增加迭代项
print(se)


#8.集合的转换
se = set(range(4))
li = list(se)
tu = tuple(se)
st = str(se)
print(li,type(li))        #输出 [0, 1, 2, 3] <class 'list'>
print(tu,type(tu))        #输出 [0, 1, 2, 3] <class 'tuple'>
print(st,type(st))        #输出 [0, 1, 2, 3] <class 'str'>
```



## 18. 迭代器

迭代是 python 中访问集合元素的一种非常强大的一种方式。

迭代器是一个可以记住遍历位置的对象，因此不会像列表那样一次性全部生成，而是可以等到用的时候才生成，因此节省了大量的内存资源。

迭代器对象从集合中的第一个元素开始访问，直到所有的元素被访问完。

迭代器有两个方法：iter()和 next()方法。

iter(iterable)从可迭代对象中返回一个迭代器,iterable必须是能提供一个迭代器的对象

- iterable —> 可迭代的数据类型（比如列表、字典、元组以及集合类型等）

next(iterator) 从迭代器iterator中获取下一了记录,如果无法获取下一条记录,则触发stoptrerator异常

- 迭代器只能往前取值,不会后退



可迭代对象：

​	类似于list、tuple、str 等类型的数据可以使用for… in… 的循环遍历语法可以从其中依次拿到数据并进行使用，我们把这个过程称为遍历，也称迭代。python中可迭代的对象有list（列表）、tuple（元组）、dirt（字典）、str（字符串）set(集合)等。



**除了iter() 函数之外 ，我们还有其他方法生成迭代器：**

**第一种：**

​	for循环生成方法 —> 我们可以在函数中使用 for 循环， 并对每一个 for 循环的成员使用 yield() 函数 

​	每一个 for 循环成员放到一个迭代器对象中，不过只有被调用才会被放入。

```python
def test():
    for i in range(10):
        yield i 
result = test() 
print('for 循环，第一次 \'i\'的值为：', next(result))
print('for 循环，第二次 \'i\'的值为：', next(result))
print('for 循环，第三次 \'i\'的值为：', next(result))
print('for 循环，第四次 \'i\'的值为：', next(result))
```

**第二种：**

​	for 循环一行生成迭代器对象。

```python
result = (i for i in [1, 2, 3])        # 将 for 循环在非函数中 赋值 给一个变量， 这也是生成一个迭代器变量的方法  
 
print('for 循环，第一次 \'i\'的值为：', next(result))        # 使用 next 调用迭代器
print('for 循环，第二次 \'i\'的值为：', next(result))
print('for 循环，第三次 \'i\'的值为：', next(result))
```

使用 for 循环生成的迭代器，可以不使用 next() 函数 也可以执行，（依然可以通过 for 循环 获取迭代器的数据）不仅如此，当我们调取完迭代器中的数据之后，程序不会抛出异常，相比较与 next() 函数要友好的多。

```python
result = (i for i in [1, 2, 3])
for item in result:
    print(item)
```

```python
#可迭代对象
from collections import Iterable
from collections import Iterator
print(isinstance([],Iterable))  #true
print(isinstance((),Iterable))  #true
print(isinstance({},Iterable))  #true
print(isinstance("",Iterable))  #true
print(isinstance((x for x in range(10)),Iterable))  #true
print(isinstance(1,Iterable))  #false

#迭代器 不但可以作用于for循环，还可以被next(list)函数不断调用并返回下一个值
#直到最后抛出一个stopIteration错误标识无法返回下一个值

#可迭代对象，可以被next函数调用，并不断返回下一个值的对象为迭代器(Iterator对象)
print(isinstance([],Iterator))  #False
print(isinstance((x for x in range(10)),Iterator))  #True，只有range是true 是迭代器
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
print(next(a))
# 用处：
input("输入") #不能接回车  回车就结束了 怎么办，用迭代器
endstr="end"
str=""
for line in iter(input,endstr):
    str+= line + "\n"
print(str)
```



## 19. 函数概述

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

引用传递和值传递是不一样的：值传递不会改变原值，引用传递可以改变原对象内的值

**关键字参数：**让调用不关注参数顺序
c=add(b=1,a=3)

**默认参数：**调用函数如果没有传递参数则使用默认参数

```python
def myfunc(a=1,b=2):
    print(a,b)
```

如果有默认参数，必须将默认参数放在参数列表的最后

```python
def myfunc(a,b=2):
    print(a,b)
```

**不定长参数：**能处理比定义时更多参数

```python
# 这个*arr  会接收剩下的所有的参数，它可以是元祖，可以是list
def func(name, *arr):
    print(name)
	for x in arr:
	    print(x)
func('a','b','c')

# 加了星号(*)的变量存放所有未命名的变量参数，如果在函数调用时没有指定参数，它是一个空元组

def mysum(*xx)
    sum=0
	for x in xx:
	    sum+=x
	return sum

#还有一种传不定长参数方法,只接收按参数名传递 参数是个字典  **kwargs
def func2(**keyword):
    print(keyword)
    
func2(a=1,b=2)  #{'a': 1, 'b': 2}
# **代表键值对的参数字典 和* 代表的意义类似
```

```python
#下面这个可以接收任意参数
def func3(*args, **keyws):
    pass
```

**匿名函数：**匿名函数不使用def这样的关键字定义函数，使用lambda来创建匿名函数
特点：
1、lambda只是一个表达式，函数体比def简单
2、lambda的主体是一个表达式，而不是代码块，仅仅只能在lambda中封装简单逻辑
3、lambda函数有自己的命名空间，并且不能访问自有参数列表之外的或全局命名空间里的参数
4、虽然lambda是一个表达式看起来只能写一行，与c，c++内联不同
c，c++写内联为了增加速度，而python没有效率增加，只是单纯的简单

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

## 20. 装饰器

装饰器：就是一个闭包 把一个函数当成参数 返回一个替代版的函数，本质上就是返回函数的函数

在使用装饰器的时候，如果没有()就只是定义，如果有括号相当于执行

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

## 21. 偏函数

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

## 22. 变量的作用域

程序的变量不是所有位置都能使用，访问的权限取决于变量的位置

作用域：局部作用域、函数作用域、全局作用域、内建作用域

在全局定义的方法，在方法内如果想使用，先用global引入

```python
a='123'
def func():
    global a
    print(a)
```



## 23. 异常处理

程序在运行过程中出现错误，需要捕获和处理

常见如：处理客户错误输入或者网络传入，或者磁盘满等异常

当程序遇到问题，不让程序结束，越过错误的程序，向下执行，并打印错误信息或者做其他处理。
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
finally:
    pass
```

如果当try内执行出现错误，会匹配第一个错误码，如果匹配上就执行对应语句

如果没有匹配的异常，错误将会被提交到上一层try语句，或者到程序最上层

如果没有出现错误，不会匹配任何异常，会执行else下的语句

- BaseException	所有异常的基类
- SystemExit	解释器请求退出
- KeyboardInterrupt	用户中断执行(通常是输入^C)
- Exception	常规错误的基类
- StopIteration	迭代器没有更多的值
- GeneratorExit	生成器(generator)发生异常来通知退出
- StandardError	所有的内建标准异常的基类
- ArithmeticError	所有数值计算错误的基类
- FloatingPointError	浮点计算错误
- OverflowError	数值运算超出最大限制
- ZeroDivisionError	除(或取模)零 (所有数据类型)
- AssertionError	断言语句失败
- AttributeError	对象没有这个属性
- EOFError	没有内建输入,到达EOF 标记
- EnvironmentError	操作系统错误的基类
- IOError	输入/输出操作失败
- OSError	操作系统错误
- WindowsError	系统调用失败
- ImportError	导入模块/对象失败
- LookupError	无效数据查询的基类
- IndexError	序列中没有此索引(index)
- KeyError	映射中没有这个键
- MemoryError	内存溢出错误(对于Python 解释器不是致命的)
- NameError	未声明/初始化对象 (没有属性)
- UnboundLocalError	访问未初始化的本地变量
- ReferenceError	弱引用(Weak reference)试图访问已经垃圾回收了的对象
- RuntimeError	一般的运行时错误
- NotImplementedError	尚未实现的方法
- SyntaxError	Python 语法错误
- IndentationError	缩进错误
- TabError	Tab 和空格混用
- SystemError	一般的解释器系统错误
- TypeError	对类型无效的操作
- ValueError	传入无效的参数
- UnicodeError	Unicode 相关的错误
- UnicodeDecodeError	Unicode 解码时的错误
- UnicodeEncodeError	Unicode 编码时错误
- UnicodeTranslateError	Unicode 转换时错误
- Warning	警告的基类
- DeprecationWarning	关于被弃用的特征的警告
- FutureWarning	关于构造将来语义会有改变的警告
- OverflowWarning	旧的关于自动提升为长整型(long)的警告
- PendingDeprecationWarning	关于特性将会被废弃的警告
- RuntimeWarning	可疑的运行时行为(runtime behavior)的警告
- SyntaxWarning	可疑的语法的警告
- UserWarning	用户代码生成的警告

```python
try:
    print(3/0)
except ZeroDivisionError as e:
    print("除数为0了")
else:
    print("代码没有问题")

# 正常不使用任何错误类型,捕获所有的错误，只要有就处理
try:
    print(4/0)
except:
    print("程序出现了异常")
	
# 使用except捕获多种异常
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
    print("跨越多层调用异常可以捕获")   #多层嵌套，里面的错了，外面捕获就可以了

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

## 24. 标识符

标识符  由数字、字母、下划线组成，不能以数字开头，不能关键字作为标识符

```python
#查看所有关键字
import keyword
print(keyword.kwlist)
```

变量命名，小写下划线连接不同单词



## 25. 文件读写

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

## 26. os模块

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



## 27. 时间time

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



## 28. 模块

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



## 29. 包

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

## 30. 安装第三方模块

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

## 31. 类的设计

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

## 32. 类设计2

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

## 33. 类的设计3

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

## 34. 访问限制

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

## 35. 继承

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

## 36. 多态   

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

## 37. 对象属性与类属性

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

## 38. python2 python3区别

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

## 33. mapreduce

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

## 34. 高阶函数

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

## 35. 单元测试

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

## 36. 正则

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

## 37. re模块深入

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

## 38. TCP 网络编程

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

## 39. socket通信

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

## 40. UDP

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

## 41. 多任务原理

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



## 42. 单任务

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



## 43. 多进程multiprocessing

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



## 44. 父子进程顺序

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

## 45. 全局变量在多进程不共享

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

## 46. 进程池 Pool

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

## 47. 普通文件拷贝

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

## 48. 多进程拷贝文件

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

## 49. 继承实现Process

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

## 50. 使用Queue完成进程间通信

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

## 51. 单线程threading

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

## 52. 多线程全局变量问题

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

## 53. 线程锁解决并发threading.Lock

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

## 54. thread.local

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

## 55. 多线程socket通信

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

## 56. 使用tkinter和多线程制作建议QQ聊天

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

## 57. Semaphore信号量控制线程数量

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

## 58. 并发控制Barrier

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

## 59. 定时线程

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

## 60. 线程通信

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

## 61. 生产者与消费者

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

## 62. 线程调度Condition

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



## 63. 多线程实现原理

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

## 64. 协程

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
```













































