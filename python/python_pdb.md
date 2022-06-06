# 使用端点启动

```sh
$ python -m pdb aa.py
```

# 常用命令

```sh
#查看帮助
$ h/help

#查看所有断点
$ b/break

#在第五行添加断点
$ b 5
$ a.py 8

#查看断点处的代码
$ l/list

#查看第9行处的代码
$ l 9

#查看当前函数所有代码
$ ll

#设置条件断点 首先要执行b 看一下已有的所有断点编号，下面3就是断点编号，后面是条件
$ condition 3 1==1 

#清除所有断点
$ clear/cl <断点号> 

#重跑
$ restart/run

#进入方法 或者断点，setp into
$ s

#执行一步代码，不执行step into子函数
$ n

#执行后续所有代码，直到遇到断点：continue
$ c

# step out 运行到return处，执行到最后一行
$ return/r

#跳转到某行
$ j 7

#打印变量值
$ print val

#关闭/开启断点
$ disable/enable <断点号>

#查看调用堆栈
$ w/where/bt

#查看上一帧、下一帧堆栈
$ up/down

#查看当前函数的参数列表
$ a

#查看某对象源码，可以是方法，文件等
$ source func_name/py/...

#改变变量的值
$ !a=5 
$ pp a

```

