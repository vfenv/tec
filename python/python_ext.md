## 1. 监控进程发送邮件

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

## 2. 监控日志发送邮件

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
*/10 * * * * python  /usr/tomcat_log_error_analysis.py >> /usr/crontest.py.log 2>&1
```



## 3. 几个小算法概念

```python
水仙花数：
	153=1^3+5^3+3^3
	num%10  # 153%10=3
    num//10%10  # 153//10%10=15%10=5   
			    # 15//10%10=1
回文数
	1234321
	max(1,2,3)
    
闰年条件：能被4整除不能被100整除  或者  能被400整除
```

## 3. 安装图形easygui

```sh
# gui是图形用户接口
# 需要将easygui.py放在  python安装路径的lib里面   C:\Python27\Lib
$ pip install easygui
# https://sourceforge.net/projects/easygui/files/0.96/easygui-0.96.zip/download
```



## 4. 计算机进制转换和存储

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

## 5. turtle绘图

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

## 6. 歌词分析

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

## 7. 操作win32

需要一些配合使用的软件：

- memsearch.exe
- spy.exe
- PCHunter64.exe
- 控制窗体的显示和隐藏

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

## 8. 加载语音模块

1.更新pip
pip --version	查看pip版本
python -m pip install --upgrade pip	更新pip

2.安装python的PIL(图像处理库)
pip install Pillow

3.python应用导入PIL
import PIL.Image

安装语音模块
pip install speech

## 9. 递归

递归调用：一个函数，调用了自身，称为递归调用
递归函数：一个会调用自身的函数称为递归函数
凡是循环能干的事递归都能干
方式：

1. 写出临界条件
2. 找这一次和上一次的关系
3. 假设当前函数已经能用，调用自身计算上一次结果，再求出本次结果

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

## 10. 栈和队列

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

## 11. 递归 队列

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

## 12. 栈模拟遍历-深度遍历

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

## 13. 用队列处理文件循环  广度遍历

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



## 14. 循环文件并处理

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

## 15. 人开枪的例子

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

## 16. 提升与发送邮件

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

## 17. @property

```python
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

## 18. 运算符重载

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

## 19. 发短信的

```python
#文档中心有-短信通知文档下载里面有demo
#打开python的

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

## 20. 发邮件的

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

## 21. 银行自动提款demo

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

## 22.  图形界面(tkinter)

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

## 23.图形界面的其他组件

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

## 24. python操作CSV

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

## 25. 操作pdf

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

## 26. 播放音乐  修改windows的背景图片

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

## 27. 整蛊小程序

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

## 28. 键盘模拟

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

## 29. 语音控制

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

## 30. 鼠标模拟

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

## 31. 读取doc和docx

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

## 32. 读取xlsx xls文件

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

## 33. 操作PPT

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

## 34. 文件的封装

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

## 35. telnet

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



## 36. 破解密码

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

## 37. python爬虫

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

## 38. 网络概述

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

## 39. mysql

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

## 40. mysql常规操作

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

## 41. python操作mysql

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

## 42. mysql查询操作

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



## 43. NoSQL简介

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



## 44. mogodb 简介

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

## 45. mongo操作

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

## 46. mongo 添加文档

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

## 47. mongo 查询文档

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

## 48. mongo 更新文档

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

## 49. mongo 删除文档

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



## 50. redis安装

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

## 51. redis操作

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

