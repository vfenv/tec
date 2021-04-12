### 1.flask-mongoengine安装不上

```shell
#要先安装
pip install rednose
pip install nose
pip install coverage
#再次安装
pip install flask-mongoengine
#批量安装
pip install -r requirements.txt
```

#### 1.1.requirements.txt

```text
eventlet==0.24.1
opencv-python==4.4.0.44
flask==1.0.2
flask-cors==3.0.7
flask-login==0.4.1
flask-restplus==0.12.1
#flask-mongoengine==0.9.5
numpy
cython
scikit-image
requests
google_images_download==2.5.0
watchdog==0.8.3
pytest==3.9.3
pytest-ordering==0.6
imantics==0.1.9
flask-socketio==3.3.2
celery==4.2.2
Shapely==1.7.0
scipy
Pillow
matplotlib
keras==2.1.1
h5py
imgaug
IPython[all]
jupyter
```

### 2.后台运行python

```shell
nohup python models_v4.py >>console.log 2>&1 &
```



### 3.Windows下安装Py3.6之后list命令报错

windows安装python3.x后，pip list警告，DEPRECATION:The default format will switch to columns in future...

虽然不影响使用，但是总给人怪怪感觉

解决办法:

1.在C:\program DATA目录下，新建一个pip文件夹

2.进入刚才创建的pip文件夹新建一个pip.ini配置文件

3.添加以下信息：

[list]

format=columns

4.保存退出

5.重新执行pip list发现警告解决。



### 4.Python一键安装全部依赖包

生成安装配置文件：

```shell
pip freeze>requirements.txt
```

批量安装:

```shell
pip install -r requirements.txt
```



### 5.临时更换镜像

```shell
pip install eventlet0.24.1 -i https://mirrors.aliyun.com/pypi/simple/eventlet0.24.1
pip install flask-mongoengine==0.9.5
```



### 6.设置pip的数据源

查看pip的安装路径：

```shell
pip -v config list
```

找到第一个pip.ini，修改：

```ini
[list]
format = columns

[global]
trusted-host=mirrors.tools.huawei.com
index-url=http://mirrors.tools.huawei.com/pypi/simple/
```



### 7.安装过程缺少组件

```shell
#要先安装
pip install rednose
pip install nose
pip install coverage
```



### 8.python特殊函数def \_\_call__(self)

这个\__call__就是把一个类的实例转化为一个可调用对象，f可以被调用，所以f是一个可调用对象，所有的函数都是可调用对象

```python
>>>f=abs
>>>f.__name__
'abs'
>>>f(-123)
123

class Person(Object):
    def __init__(self, name, gender):
        self.name = name
        self.gender = gender
        
    def __call__(self,friend):
        print 'my name is %s' % self.name
        print 'my friend is %s' % friend

#我们可以直接调用实例
>>> p = Person('Bob', 'male')
>>> p('Tim')
my name is Bob
my friend is Tim

#这里我们并不知道p(‘Tim’) 里的p是一个函数还是一个类实例，所以在py中函数也是对象，对象和函数区别不大
```



























