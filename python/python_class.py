from functools import partial
from abc import abstractmethod,ABCMeta
class Task(metaclass=ABCMeta):
    def __init__(self):
        print("parent init")
    cout=0 # 这是一个个类变量
    mmap = {} #定义了一个类变量
    @abstractmethod  #抽象方法，必须在子类中重写
    def do(self):  #类函数，只是比普通函数多一个self参数，有self就是类方法，如果有其他参数接到self后面
        raise NotImplementedError("must be implement")
    @staticmethod  #静态方法，可以直接被访问，没有self，cls这样特殊参数
    def alert():
        print('静态方法，通过 类名.方法名 直接可以调用')


class TaskImpl(Task):
    '''
        doc：类中三个单引号包含的内容就是文档
    '''
    def __init__(self, a): #也是类方法，第一个参数是self
        super(TaskImpl,self).__init__() #
        self.a = a  #在init中定义的是实例属性，么个实例独有的
    def do(self):
        super().mmap['a']=1
        print("类内使用 self.属性 调用实例变量--> {}".format(self.a))
    @classmethod  #定义类方法，使用 类名.方法名
    def build(cls):  # 类方法有一个默认参数cls，代表当前类对象，python把类本身绑定到cls上
        #类方法中是不能调用实例变量的，但是可以调用类变量
        print("类调用类变量，cls代表的是类本身")
        task = cls('test')
        return task
    #安全方法
    def _safe(self):
        self._aq = '安全方法和安全属性可以在任何位置访问'
        self.__pri()
    #私有方法，只能在内部通过self或者cls调用
    def __pri(self):
        print("私有方法不能通过类名和实例名调用，只能self.__pri或者cls.__pri")
    @property #把方法当属性使用，它可以和属性一起使用，隐藏属性，不能修改属性值
    def property_method(self):
        return 15

#无参装饰器
def no_args_zhuangshiqi(func):
    def _no_args_zhuangshiqi():
        print("before call func")
        func()
        print("after call func")
    return _no_args_zhuangshiqi #返回内部包装的函数
@no_args_zhuangshiqi #无参装饰器
def no_args_func(): #调用它的时候，先调用no_args_zhuangshiqi
    print("my func call")

#装饰器 修饰带参数的方法
def my_zhuangshiqi(func):
    def _my_zhuangshiqi(a,b):
        print("before call func")
        ret = func(a,b)
        print("after call func")
        return ret
    return _my_zhuangshiqi #返回内部包装的函数
@my_zhuangshiqi #装饰器 修饰带参数的方法
def myfunc(a,b): #调用它的时候，先调用my_zhuangshiqi
    print("my func call params is a={}, b={}".format(a,b))

#装饰器本身带参数
def zhuangshiqi_with_args(args):
    def _zhuangshiqi_with_args(func):
        def __zhuangshiqi_with_args():
            print("before call func")
            func()
            print("after call func")
        return __zhuangshiqi_with_args
    return _zhuangshiqi_with_args #返回内部包装的函数
#装饰器本身带参数
@zhuangshiqi_with_args('param')
def myfunc_args():
    print("myfunc_args call")

if __name__ == '__main__':
    task = TaskImpl.build()
    task.do()
    task.alert()
    task.mmap['c']=5 #直接调用引用对象 的 方法，去修改，此时改的是类变量内的值
    print("在类内类外都可以调用类变量--> {}".format(task.mmap)) #task.mmap={'c':5}这个是增加实例属性
    print("在类内类外都可以调用类变量--> {}".format(TaskImpl.mmap))
    print("类外使用 实例名.属性 调用实例变量--> {}".format(task.a))
    print(task.cout) #查找一个变量，先从实例变量里查找，找不到去类变量里查找
    task.cout=1 #如果用实例对象修改属性，相当于修改了实例对象中的count属性
    print(task.cout) #查找变量，从实例变量中找到了cout=1，返回1
    TaskImpl.cout = 2 #这改的是类变量中的值，不会影响实例变量中的task的值，不建议修改类变量的值，会影响到所有类
    #del task.cout #可以删除自己给实例对象增加的属性
    print(task.cout) #这打印的是 1
    task1 = TaskImpl.build() #新new了一个对象，此时，类变量已经被改成2
    print(task1.cout) #这打印的是2，打印的是类变量，因为没有给task1添加实例变量，找不到只能找类变量
    print(task.__dict__) #查看类里的变量
    task._safe()
    int2 = partial(int,base=10) #偏函数 返回当前函数的拷贝，预制变量的值
    print(int2('150'), int('150',base=10))
    print(task.__doc__)
    print("property method call without() --> {}".format(task.property_method))
    #多态
    # 魔法方法：如果在类中定义了私有变量__slots__=["name","age"]
    # 如果定义了__slots__属性，那么不能修改list中没有的属性，不然报错，没有__slots__时可以直接添加其他属性
    # slots不常用
    no_args_func()
    myfunc('1','2') #装饰器简单应用
    myfunc_args()
