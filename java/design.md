# 一、设计模式的六大原则

## 1、单一职责-类/方法只做一个事

Single Responsibility Principle

指的是一个类或者一个方法只做一件事。如果一个类承担的职责过多，就等于把这些职责耦合在一起，

一个职责的变化就可能抑制或者削弱这个类完成其他职责的能力。

例如餐厅服务员负责把订单给厨师去做，而不是服务员又要订单又要炒菜。



## 2、开闭原则-最重要

Open Close Principle

开闭原则就是说对扩展开放，对修改关闭。在程序需要进行拓展的时候，不能去修改原有的代码，实现一个热插拔的效果。

所以一句话概括就是：为了使程序的扩展性好，易于维护和升级。

想要达到这样的效果，我们需要使用接口和抽象类。

也就是一个类实现一个接口，然后如果要新增功能，再写一个类去实现这个接口，扩展新的方法

所有几大原则都是为了实现开闭原则的



## 3、里氏代换原则-尽量不重写父方法

里氏代换原则(Liskov Substitution Principle LSP)面向对象设计的基本原则之一。 

所有基类出现的地方都可以用派生类替换而不会程序产生错误。

子类可以扩展父类的功能，但不能改变父类原有的功能。

也就是说子类尽量不要重写父类的功能，否则就不能在任意地方替换了，例如父类pay方法付款100，子类改造之后变成付款200

LSP是继承复用的基石，只有当衍生类可以替换掉基类，软件单位的功能不受到影响时，基类才能真正被复用，而衍生类也能够在基类的基础上增加新的行为。

里氏代换原则是对“开-闭”原则的补充。

里氏替换原则告诉我们，继承实际上让两个类耦合性增强了， **在适当的情况下，可 以通过聚合，组合，依赖 来解决问题。**



## 4、接口隔离原则-用小接口不用大接口

Interface Segregation Principle

类不应该依赖不需要的接口，知道越少越好。

应当为客户端提供尽可能小的单独的接口，而不是提供大的总的接口。

使用多个隔离的接口，比使用单个接口要好。

降低类之间的耦合度。



## 5、依赖倒置原则-依赖接口而非实现类

Dependence Inversion Principle

指的是高级模块不应该依赖低级模块，而是依赖抽象。

抽象不能依赖细节，细节要依赖抽象。

依赖关系实际就是一种使用关系，无论是作为返回值，还是作为参数传入给方法，但是我们不希望依赖具体的类，

而是希望依赖具体类的接口或者抽象类，这样，在我们改变类的方法或者属性时，就不用修改原来的类，而是新增类，也符合开闭

这个是开闭原则的基础，具体内容：针对接口编程，依赖于抽象而不依赖于具体。



## 6、迪米特法则-局部变量不要使用陌生类

Demeter Principle ：最少知道原则

一个对象应该对其他对象保持最少的了解，因为类与类关系越密切，耦合度越大

一个类对自己依赖的类知道的 越少越好。

也就是对于被依赖的类，不管多么复杂，都尽量将逻辑封装在类的内部。对外仅提供public 方法，不对外泄露任何信息



==迪米特法则还有个更简单的定义：只与直接的朋友通信==

只要两个对象之间有耦合关系， 我们就说这两个对象之间是朋友关系

耦合的方式很多种：依赖，关联，组合，聚合    等。

其中，我们称**出现在**==成员变量，方法参数，方法返回值==中的类为**直接的朋友**，而出现在==局部变量中的类不是直接的朋友==

==也就是说，陌生的类最好不要以局部变量的形式出现在类的内部==



**迪米特法则注意事项和细节**

迪米特法则的核心是 ==降低类之间的耦合==

但是注意：由于每个类都少了不必要的依赖，因此迪米特法则只是要求降低类间 ( 对象间 ) 耦合关系， 并不要求完全没有依赖关系



## 7、合成复用原则-用合成/聚合替代继承

Composite Reuse Principle

原则是尽量使用合成/聚合的方式，而不是使用继承。

也就是尽量使用成员变量，这样的耦合性比较低，如果想使用一个类的某些方法，使用继承，那这二个类就耦合到一起了



# 二、设计模式的分类

总体来说设计模式分为三大类：

**创建型模式，共五种：**

- 工厂方法模式、抽象工厂模式、单例模式、建造者模式、原型模式。

**结构型模式，共七种：**

- 适配器模式、装饰器模式、代理模式、外观模式、桥接模式、组合模式、享元模式。

**行为型模式，共十一种：**

- 策略模式、模板方法模式、观察者模式、迭代子模式、责任链模式、命令模式、备忘录模式、

- 状态模式、访问者模式、中介者模式、解释器模式。

**其实还有两类：**

- 并发型模式、线程池模式。



# 三、Java的23中设计模式

## ----------五种创建模式----------

## 1、工厂方法模式（Factory Method）

   工厂方法模式分为三种：普通工厂模式、多工厂方法模式、静态工厂方法模式

### 1.1、普通工厂模式

就是建立一个工厂类，对实现了同一接口的一些类进行实例的创建

抽象产品接口：定义通用产品

产品类：实现抽象产品接口

简单工厂类：提供一个统一的获取产品实例的方法，根据入参返回的不同的产品

```java
// 产品接口
public interface Sender {
	public void send();
}
// 产品实现类1
public class MailSender implements Sender {
	@Override
	public void send() {
		System.out.println("MailSender");
	}
}
// 产品实现类2
public class SmsSender implements Sender {
	@Override
	public void send() {
		System.out.println("SmsSender");
	}
}
// 简单工厂类
public class SendFactory {
	public Sender produce(String type) {
        //通过传入类型，判断不同的字符串返回不同的分支，从而返回创建的对象，确定是一旦参数传递错误就不会返回正确对象
		if ("mail".equals(type)) {
			return new MailSender();
		} else if ("sms".equals(type)) {
			return new SmsSender();
		} else {
			System.out.println("请输入正确的类型!");
			return null;
		}
	}
}
// 客户端
public class Test {
	public static void main(String[] args) {
        // 简单工厂
		SendFactory factory = new SendFactory();
        // 通过产品工厂生产产品
		Sender sender = factory.produce("mail");
        // 调用产品的方法
		sender.send();
	}
}
```

​      

### 1.2、多工厂方法模式

多工厂方法模式是对普通工厂方法模式的改进，在普通工厂方法模式中，如果传递的字符串出错，则不能正确创建对象

而多个工厂方法模式是在工厂类中提供多个生产方法，分别创建不同的产品

抽象产品接口：产品接口

产品类：实际要创建的产品

简单工厂类：提供N个创建产品的方法，需要调用指定的方法  创建  指定的产品

```java
public interface Sender {
	public void send();
}
public class MailSender implements Sender {
	@Override
	public void send() {
		System.out.println("MailSender");
	}
}
public class SmsSender implements Sender {
	@Override
	public void send() {
		System.out.println("SmsSender");
	}
}
public class SendFactory {
	public Sender produceMail() {
		return new MailSender();
	}
	public Sender produceSms() {
		return new SmsSender();
	}
}
public class Test {
	public static void main(String[] args) {
		SendFactory factory = new SendFactory();
        // 通过工厂类生产不同的产品
		Sender sms = factory.produceSms();
		sms.send();
        Sender mail = factory.produceMail();
		mail.send();
	}
}
```



### 1.3、静态工厂方法模式

将上面的多个工厂方法模式里的方法置为静态的，不需要创建实例，直接调用即可

抽象产品接口：定义通用产品

产品类：实现抽象产品接口，多个实现类 各自是各自的产品

简单工厂类：提供N个的获取产品实例的静态方法，只需要调用指定的方法获取指定的产品实例

```java
public interface Sender {
	public void send();
}
public class MailSender implements Sender {
	@Override
	public void send() {
		System.out.println("MailSender");
	}
}
public class SmsSender implements Sender {
	@Override
	public void send() {
		System.out.println("SmsSender");
	}
}
// 工厂方法中的方法是静态的 不需要new工厂，直接获取即可
public class SendFactory {
	public static Sender produceMail() {
		return new MailSender();
	}
	public static Sender produceSms() {
		return new SmsSender();
	}
}
public class Test {
	public static void main(String[] args) {
		Sender sender = SendFactory.produceSms();
		sender.send();
        Sender mail = SendFactory.produceMail();
		mail.send();
	}
}
```

总体来说，工厂模式适合：凡是出现了大量的产品需要创建，并且具有共同的接口时，可以通过工厂方法模式进行创建。

以上的三种模式中：

第一种如果传入的字符串有误，不能正确创建对象。

第二种创建个工厂类，调用不同方法返回不同的类即可。

第三种相对于第二种，不需要实例化工厂类，直接使用工厂类返回产品实例即可。

所以，大多数情况下，我们会选用第三种，静态工厂方法模式



## 2、抽象工厂模式（Abstract Factory）

工厂方法模式有一个问题就是，类的创建依赖工厂类，也就是说，想要创建新的产品，必须对工厂类进行修改，这违背了开闭原则

所以，从设计角度考虑，有一定的问题，如何解决？

抽象工厂模式可以解决简单工厂的问题，创建多个工厂类，每个工厂生产不同产品

这样一旦需要增加新的产品，直接增加新的工厂类和新的产品就可以了，不需要修改其他产品的代码



将工厂类抽象，再有新的产品不需要改原来的工厂，只需新加工厂类即可

```java
// 抽象产品接口
public interface Sender {
	public void Send();
}
// 抽象工厂接口
public interface Provider {
	public Sender produce();
}
//产品1
public class MailSender implements Sender {
	@Override
	public void Send() {
		System.out.println("mail sender");
	}
}
//加入原来只有一种产品，那么要生产这种产品，只需要新建一个工厂类实例即可，这个工厂负责生产产品1
public class SendMailFactory implements Provider {  
    @Override  
    public Sender produce(){  
        return new MailSender();  
    }
}

//此时，要生产新的产品2，产品2与产品1几乎类似的功能，那么只需要产品2也实现产品接口，创建一个新的产品2类
public class SmsSender implements Sender {
	@Override
	public void Send() {
		System.out.println("sms sender");
	}
}
//新写一个产品工厂，它也实现这个工厂接口，直接创建产品2并返回即可
public class SendSmsFactory implements Provider{
	@Override
	public Sender produce() {
		return new SmsSender();
	}
}

public class Test {
	public static void main(String[] args) {
		Provider provider = new SendMailFactory();
		Sender sender = provider.produce();
		sender.Send();
		Provider provider2=new SendSmsFactory();
		Sender sender2=provider2.produce();
		sender2.Send();
	}
}

```



## 3、单例模式（Singleton）

单例对象（Singleton）是一种常用的设计模式。在Java应用中，单例对象能保证在一个JVM中，该对象只有一个实例存在。

这样的模式有几个好处：

1、某些类创建比较频繁，对于一些大型的对象，这是一笔很大的系统开销。

2、省去了new操作符，降低了系统内存的使用频率，减轻GC压力。

3、有些类如交易所的核心交易引擎，控制着交易流程，如果该类可以创建多个的话，系统完全乱了。

​	  所以只有使用单例模式，才能保证核心交易服务器独立控制整个流程。

​	  再比如一个军队出现了多个司令员同时指挥，肯定会乱成一团，这些场景都需要单例，只返回同一个实例对象。

### 3.1 懒汉模式

```java
/**
 * 最简单的单例模式
 * 懒汉式 需要加锁，双重null判断才线程安全
 */
public class Singleton1 {
	/**
	 * 懒汉模式 静态私有 类变量
	 */
	private static Singleton1 singleton=null;
	
	/**
	 * 构造方法私有
	 */
	private Singleton1(){}
	
	/**
	 * 静态公有方法 - 获取单例的实例
	 * 使用双重null判断，保持多线程安全
	 * 不要在方法上加synchronized，因为synchronized锁的是this，也就是当前类的实例，这样效率很低
	 */
	public static Singleton1 getInstance(){
		if(singleton==null){
            synchronized (singleton) {
                if(singleton==null){
					singleton=new Singleton1();
                }
            }
		}
		return singleton;
	}
}
```

### 3.2 饿汉模式

不管要不要，我在变量里都会创建一个实例，不安全

```java
/**
 * 饿汉模式线程不安全
 * 虚拟机会将指令重排，初始化时，先初始化一个变量空间 singleton = null
 * 然后每个方法会执行一个new，这样中间的内容可能就不一样了 可能发生覆盖
 */
public class Singleton1 {
	/**
	 * 饿汉模式 持有一个静态私有final的成员变量，线程不安全
	 */
	private static final Singleton1 singleton=new Singleton();
	
    /**
	 * 构造方法私有
	 */
	private Singleton1(){}
	
    /**
	 * 饿汉模式 只将上面对象返回
	 * */
	public static Singleton getInstance(){
		return singleton;
	}
}
```

### 3.3 静态内部类单例

```java
//这种更安全
public class Singleton3 {	
    /**构造方法私有**/
    private Singleton3(){}
    
	/**
	 * 此处使用一个私有的静态内部类来维护单例
	 * 可以使用 volatile 防止指令重排
	 */  
    private static class SingletonFactory {
        private static Singleton3 instance = new Singleton3();  
    }
    
    /* 获取实例 */  
    public static final Singleton3 getInstancenew() {  
        return SingletonFactory.instance;
    }
}
```

通过枚举创建单例模式：

```java
public enum  EnumSingleton {
    INSTANCE;
    public EnumSingleton getInstance(){
        return INSTANCE;
    }
}
```

### 3.4 通过类和枚举

```java
public class User {
    //私有化构造函数
    private User(){}
    //定义一个静态枚举类
    static enum SingletonEnum{
        //创建一个枚举对象，该对象天生为单例
        INSTANCE;
        private User user;
        //私有化枚举的构造函数
        private SingletonEnum(){
            user=new User();
        }
        public User getInstnce(){
            return user;
        }
    }
    //对外暴露一个获取User对象的静态方法
    public static User getInstance(){
        return SingletonEnum.INSTANCE.getInstnce();
    }
}
// User.getInstance()
```

通过单例模式的学习告诉我们：

   1、单例模式理解起来简单，但是具体实现起来还是有一定的难度。

   2、synchronized关键字锁定的是对象，在用的时候，一定要在恰当的地方使用

   （注意需要使用锁的对象和过程，可能有的时候并不是整个对象及整个过程都需要锁）



## 4、建造者模式（Builder）

### 4.1 标准建造者

工厂类模式提供的是创建单个简单类的模式，不关注细节

而建造者模式则是用来创建复杂的产品对象，它的各个部分子对象用一定的算法构成，由于需求变化，这个对象的各个部分经常面临着剧烈的变化，但是它们组合在一起的算法相对稳定。建造者模式就是将变化和不变分离。

建造者包括以下角色：

- 抽象建造者 Builder：声明一个接口，提供设置产品各个部件的抽象方法

- 实际建造者 ConcreteBuilder：内置一个产品类，并且在声明产品时就已经初始化，提供修改产品各个部件的方法，并提供获取产品的方法

- 产品类：具体产品对象（拥有多个部件，并提供设置部件的方法）

- 导演类：又叫指导类，这部分是不稳定的，内置一个抽象建造者，创建导演类时传入实际建造者

  然后由实际建造者设置不同部分构件，并返回构建后的对象。
  
  导演类和产品类不发生依赖关系，与之直接交互的是建造者。所以，建造者要提供修改产品构件的方法，供导演类调用。

```java
// 产品类   要提供修改产品构件的方法
public class Product {
	String part1; // 轮子
	String part2; // 外壳
	String part3; // 发动机
	public String getPart1() {
		return part1;
	}
	public void setPart1(String part1) {
		this.part1 = part1;
	}
	public String getPart2() {
		return part2;
	}
	public void setPart2(String part2) {
		this.part2 = part2;
	}
	public String getPart3() {
		return part3;
	}
	public void setPart3(String part3) {
		this.part3 = part3;
	}
}

// 抽象建造者    必须提供获取产品的方法，因为建造者就是要创建产品
public abstract class Builder {
	// 生产车轮
	public abstract void setPart1(String arg1);
	// 生产车外壳
	public abstract void setPart2(String arg1);
	// 生产发动机
	public abstract void setPart3(String arg1);
	// 提供获取产品的方法
	public abstract Product getProduct();
}

// 实际建造者
// 实际建造者必须持有一个产品属性，并且有获取产品的方法
public class ConcreteBuilder extends Builder {
	private Product product = new Product();
    @Override
	public Product getProduct() {
		return product;
	}
	@Override
	public void setPart1(String arg1) {
		product.setPart1(arg1);
	}
	@Override
	public void setPart2(String arg1) {
		product.setPart2(arg1);
	}
	@Override
	public void setPart3(String arg1) {
		product.setPart3(arg1);
	}
}

// 导演类
// 需要持有一个建造者类属性，在创建导演类时，传入实际的建造者
// 提供获取产品的方法，方法内部使用实际建造者创建产品
public class Director {
	private Builder concretebuilder;
    // 构造方法中也可以传递builder
    public Director(Builder builder) {
    	this.concretebuilder = builder;
    }
	public Product getcar1() {
		concretebuilder.setPart1("固特异轮胎");
		concretebuilder.setPart2("红色金属光泽");
		concretebuilder.setPart3("V8发动机");
		return concretebuilder.getProduct();
	}
	public Product getcar2() {
		concretebuilder.setPart1("米其林轮胎");
		concretebuilder.setPart2("黑色蜡烛光");
		concretebuilder.setPart3("V12发动机");
		return concretebuilder.getProduct();
	}
}

/**
 * 使用建造者模式的好处：
 * 1.使用建造者模式可以使客户端不必知道产品内部组成的细节。 
 * 2.具体的建造者类之间是相互独立的，对系统的扩展非常有利。
 * 3.由于具体的建造者是独立的，因此可以对建造过程逐步细化，而不对其他的模块产生任何影响。
 * 
 * 使用建造者模式的场合：
 * 1.创建一些复杂的对象时，这些对象的内部组成构件间的建造顺序是稳定的，但是对象的内部组成构件面临着复杂的变化。
 * 2.要创建的复杂对象的算法，独立于该对象的组成部分，也独立于组成部分的装配方法时。
 */
public class Test {
	public static void main(String[] args) {
		Builder builder = new ConcreteBuilder();
		Director director = new Director(builder);
		// 指挥者负责流程把控,调用建造者，使用指定的流程创造产品
		Product product1 = director.getcar1();
		product1.showProduct();
		// 指挥者负责流程把控
		Product product2 = director.getcar2();
		product2.showProduct();
	}
}
```

**点菜的例子：**

```java
// 产品类
public class Food {
    String majorIngredient; // 主料
    String minorIngredient; // 辅料
    String salt; // 盐
    String oil; // 油

    public String getMajorIngredient() {
        return majorIngredient;
    }

    public void setMajorIngredient(String majorIngredient) {
        this.majorIngredient = majorIngredient;
    }

    public String getMinorIngredient() {
        return minorIngredient;
    }

    public void setMinorIngredient(String minorIngredient) {
        this.minorIngredient = minorIngredient;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getOil() {
        return oil;
    }

    public void setOil(String oil) {
        this.oil = oil;
    }

    @Override
    public String toString() {
        return "Food{" +
                "majorIngredient='" + majorIngredient + '\'' +
                ", minorIngredient='" + minorIngredient + '\'' +
                ", salt='" + salt + '\'' +
                ", oil='" + oil + '\'' +
                '}';
    }
}

/**
 * 抽象建造者
 */
public abstract class FoodBuilder {
    abstract void buildMajorIngredient(String majorIngredient);
    abstract void buildMinorIngredient(String minorIngredient);
    abstract void buildSalt(String salt);
    abstract void buildOil(String oil);
	// 提供获取产品的方法
    abstract Food makeFood();
}

/**
 * 实际建造者
 */
public class FoodConcreteBuilder extends FoodBuilder {
    private Food food = new Food();

    @Override
    void buildMajorIngredient(String majorIngredient) {
        food.setMajorIngredient(majorIngredient);
    }

    @Override
    void buildMinorIngredient(String minorIngredient) {
        food.setMinorIngredient(minorIngredient);
    }

    @Override
    void buildSalt(String salt) {
        food.setSalt(salt);
    }

    @Override
    void buildOil(String oil) {
        food.setOil(oil);
    }

    @Override
    Food makeFood() {
        return food;
    }
}

/**
 * 导演类（指导类），也就是服务员
 */
public class Waiter {
    FoodBuilder builder; // 这个builder就是厨师

    /**
     * 构造时传入实际建造者
     */
    public Waiter(FoodBuilder builder) {
        this.builder = builder;
    }
    
	// 可以理解这个方法就是服务员点菜
    public Food makeFood(String majorIngredient, String minorIngredient,
                         String salt, String oil) {
        this.builder.buildMajorIngredient(majorIngredient);
        this.builder.buildMinorIngredient(minorIngredient);
        this.builder.buildOil(oil);
        this.builder.buildSalt(salt);
        return this.builder.makeFood();
    }
}

// 客户端类，也就是客户点菜
public class Test {
    public static void main(String[] args) {
        FoodBuilder builder = new FoodConcreteBuilder();
        Waiter w = new Waiter(builder);
        Food food = w.makeFood("芹菜", "葱姜蒜", "少盐", "少油");
        System.out.println(food);
    }
}

```

### 4.2 链式调用

产品类和建造者类放在一起，产品构造方法使用建造者的属性构建。

建造者的属性和产品的属性需要保持一致。

建造者类可以建造产品的各个部分，但是不是直接作用于产品。而且建造部件后返回建造者本身，return this

最终的build的方法返回new Foor(this)，也就是使用建造者创建Food。

```java
// 将产品类和建造者类放在同一个类中
public class Food {
    String majorIngredient; // 主料
    String minorIngredient; // 辅料
    String salt;
    String oil;

    public Food(FoodBuilder builder) {
        this.majorIngredient = builder.majorIngredient;
        this.minorIngredient = builder.minorIngredient;
        this.oil = builder.oil;
        this.salt = builder.salt;
    }

    public static class FoodBuilder {
        String majorIngredient; // 主料
        String minorIngredient; // 辅料
        String salt;
        String oil;

        public FoodBuilder buildMajorIngredient(String majorIngredient) {
            this.majorIngredient = majorIngredient;
            return this;
        }

        public FoodBuilder buildMinorIngredient(String minorIngredient) {
            this.minorIngredient = minorIngredient;
            return this;
        }

        public FoodBuilder buildSalt(String salt) {
            this.salt = salt;
            return this;
        }

        public FoodBuilder buildOil(String oil) {
            this.oil = oil;
            return this;
        }

        public Food build() {
            return new Food(this);
        }
    }

    @Override
    public String toString() {
        return "Food{" +
                "majorIngredient='" + majorIngredient + '\'' +
                ", minorIngredient='" + minorIngredient + '\'' +
                ", salt='" + salt + '\'' +
                ", oil='" + oil + '\'' +
                '}';
    }
}

//测试类
public class Test {
    public static void main(String[] args) {
        Food food = new Food.FoodBuilder()
                .buildMajorIngredient("土豆")
                .buildMinorIngredient("大蒜")
                .buildOil("多油")
                .buildSalt("微重点口")
                .build();
        System.out.println(food);

    }
}
```

**建造者和工厂模型对比：**

- 粒度不同，建造者适合创建复杂对象，比如汽车很多零部件，当生产汽车零部件可能选择工厂模式

- 调用顺序不同，产生不同的结果



## 5、原型模式（Prototype）

原型模式的角色： 客户（端）角色、抽象原型角色、具体原型角色

原型模式（Prototype Pattern）是用于创建重复的对象，同时又能保证性能。

这种类型的设计模式属于创建型模式，它提供了一种创建对象的最佳方式。

这种模式是实现了一个原型接口，该接口用于创建当前对象的克隆。

当直接创建对象的代价比较大时，会采用这种模式。



**实现方法：**

在 JAVA 中实现 Cloneable 接口，重写 clone()，此处clone方法可以改成任意的名称，因为Cloneable是个空接口，

你可以任意定义实现类的方法名，如cloneA或者cloneB，因为此处的重点是super.clone()，

super.clone() 调用的是Object的clone()方法，而在Object类中，clone()是native本地方法，底层实现的。



- 浅复制：将一个对象复制后，基本数据类型的变量都会重新创建，而引用类型，指向的还是原对象所指向的。

- 深复制：将一个对象复制后，不论是基本数据类型还有引用类型，都是重新创建。

  简单来说，就是深复制进行了完全彻底的复制，而浅复制不彻底，只复制基本数据类型。

### 5.1 简单原型

```java
/**
 * 抽象原型角色
 */
public interface Prototype {
	public Object clone();
}

/**
 * 具体原型角色1
 */
public class ConcretePrototype1 implements Prototype {
	public Prototype clone() {
		Prototype prototype = new ConcretePrototype1();
		return prototype;
	}
}

/**
 * 具体原型角色2
 */
public class ConcretePrototype2 implements Prototype {
	public Prototype clone() {
		Prototype prototype = new ConcretePrototype2();
		return prototype;
	}
}
/**
 *  客户角色
 */
public class Client {
	/**
	 * 持有需要使用的原型接口对象
	 */
	private Prototype prototype;

	/**
	 * 构造方法，传入需要使用的原型接口对象
	 */
	public Client(Prototype prototype) {
		this.prototype = prototype;
	}
	// 需要创建原型接口的对象
	public void operation(Prototype example) {
		Prototype copyPrototype = (Prototype) prototype.clone();
	}
}

```

### 5.2 带属性的原型模式

```java
/**
 * 抽象原型角色
 * 登记原型模式
 */
public interface Prototype {
	public Object clone();
	public String getName();
	public void setName(String name);
}
/**
 * 具体原型角色
 */
public class ConcretePrototype1 implements Prototype {
	private String name;
    
	public Prototype clone() {
		// 最简单的克隆，新建一个自身对象，由于没有属性就不再复制值了
		Prototype prototype = new ConcretePrototype1();
		prototype.setName(this.name);
		return prototype;
	}

	public String toString() {
		return "Now in Prototype1 , name = " + this.name;
	}
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}

/**
 * 具体原型角色
 */
public class ConcretePrototype2 implements Prototype {
	private String name;
	public Prototype clone() {
		// 最简单的克隆，新建一个自身对象，由于没有属性就不再复制值了
		Prototype prototype = new ConcretePrototype2();
		prototype.setName(this.name);
		return prototype;
	}

	public String toString(){
        return "Now in Prototype2 , name = " + this.name;
    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}

/**
 * 原型管理器
 */
public class PrototypeManager {
	/**
	 * 用来记录原型的编号和原型实例的对应关系
	 */
	private static Map<String, Prototype> map = new HashMap<String, Prototype>();

	/**
	 * 私有化构造方法，避免外部创建实例
	 */
	private PrototypeManager() {
	}

	/**
	 * 向原型管理器里面添加或是修改某个原型注册
	 * @param prototypeId 原型编号
	 * @param prototype 原型实例
	 */
	public synchronized static void setPrototype(String prototypeId,
			Prototype prototype) {
		map.put(prototypeId, prototype);
	}

	/**
	 * 从原型管理器里面删除某个原型注册
	 * @param prototypeId 原型编号
	 */
	public synchronized static void removePrototype(String prototypeId) {
		map.remove(prototypeId);
	}

	/**
	 * 获取某个原型编号对应的原型实例
	 * 
	 * @param prototypeId 原型编号
	 * @return 原型编号对应的原型实例
	 * @throws Exception 如果原型编号对应的实例不存在，则抛出异常
	 */
	public synchronized static Prototype getPrototype(String prototypeId)
			throws Exception {
		Prototype prototype = map.get(prototypeId);
		if (prototype == null) {
			throw new Exception("您希望获取的原型还没有注册或已被销毁");
		}
		return prototype;
	}
}
/**
 * 登记原型模式
 * 具体的客户角色
 * 作为原型模式的第二种形式，它多了一个原型管理器(PrototypeManager)角色， 
 * 该角色的作用是：创建具体原型类的对象，并记录每一个被创建的对象。
 */
public class Client {
	public static void main(String[] args) {
		try {
			Prototype p1 = new ConcretePrototype1();
			PrototypeManager.setPrototype("p1", p1);
			// 获取原型来创建对象
			Prototype p3 = (Prototype) PrototypeManager.getPrototype("p1").clone();
			p3.setName("张三");
			System.out.println("第一个实例：" + p3);
			// 有人动态的切换了实现
			Prototype p2 = new ConcretePrototype2();
			PrototypeManager.setPrototype("p1", p2);
			// 重新获取原型来创建对象
			Prototype p4 = (Prototype) PrototypeManager.getPrototype("p1").clone();
			p4.setName("李四");
			System.out.println("第二个实例：" + p4);
			// 有人注销了这个原型
			PrototypeManager.removePrototype("p1");
			// 再次获取原型来创建对象
			Prototype p5 = (Prototype) PrototypeManager.getPrototype("p1").clone();
			p5.setName("王五");
			System.out.println("第三个实例：" + p5);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

```

### 5.3 深浅复制的例子

```java
/**
 * 原型模式 深浅复制的例子
 * 属于对象的创建模式，通过给出一个原型对象的类型，
 * 用复制原型对象创建出更多同类对象，就不需要关心实例本身，只需要实现克隆方法，无需new
 *
 * 需要三个角色：客户角色、抽象原型、具体原型
 *
 * 浅克隆
 * 只负责克隆按值传递的数据（比如基本数据类型、String类型），而不复制它所引用的对象，
 * 换言之，所有的对其他对象的引用都仍然指向原来的对象
 * 
 * 深克隆
 * 除了浅度克隆要克隆的值外，还负责克隆引用类型的数据。
 * 那些引用其他对象的变量将指向被复制过的新对象，而不再是原有的那些被引用的对象。
 * 
 * 换言之，深度克隆把要复制的对象所引用的对象都复制了一遍，而这种对被引用到的对象的复制叫做间接复制
 *
 * 在Java语言里深度克隆一个对象，常常可以先使对象实现Serializable接口，然后把对象（实际上只是对象的拷贝）
 * 写到一个流里（序列化），再从流里读回来（反序列化），便可以重建对象
 */
public class Prototype implements Cloneable, Serializable {
    private static final long serialVersionUID = 1L;
    private int value = 0;
    private String str = "abc";
    private List<String> list = new ArrayList<String>() {{
        add("a");
        add("b");
        add("c");
    }};

    /* 浅复制 */
    public Object clone() throws CloneNotSupportedException {
        Prototype proto = (Prototype) super.clone();
        return proto;
    }

    /* 深复制 */
    public Object deepClone() throws IOException, ClassNotFoundException {
        /* 写入当前对象的二进制流 */
        ByteArrayOutputStream bos = new ByteArrayOutputStream(); // 新建二进制输出流
        ObjectOutputStream oos = new ObjectOutputStream(bos); // 用对象输出流包装二进制流
        oos.writeObject(this); // 向输出流写入对象，实际写到字节输出流中

        /* 读出二进制流产生的新对象 */
        byte[] btyes = bos.toByteArray(); // 从二进制输出流中读取字节数组
        ByteArrayInputStream bis = new ByteArrayInputStream(btyes); // 使用字节组数创建字节输入流
        ObjectInputStream ois = new ObjectInputStream(bis); // 使用对象输入流包装字节输入流
        return ois.readObject(); // 从输入对象流中读取对象
    }

    public static void main(String[] args) throws Exception {
        Prototype prototype = new Prototype();
        Prototype pnew = (Prototype) prototype.clone(); // 浅复制
        Prototype pdeep = (Prototype) prototype.deepClone(); // 深拷贝

        System.out.println(pnew.value == prototype.value); // true 数字比较
        System.out.println(pnew.str == prototype.str); // true 字符串比较
        System.out.println(pnew.list == prototype.list); // true 引用类型比较

        System.out.println(pdeep.value == prototype.value); // true 数字比较
        System.out.println(pdeep.str == prototype.str); // false 字符串比较
        System.out.println(pdeep.list == prototype.list); // false 引用类型比较
    }
}
```



## ----------七种 结构模型----------

## 6、适配器模式（Adapter）

适配器模式将某个类的接口转换成客户端期望的另一个接口表示，目的是消除由于接口不匹配所造成的类的兼容性问题

比如中国的插头要插入到欧洲的插座上，因为不同的尺寸是插不上的，需要接一个转换器才能插上

适配器模式三种分类：类适配器模式、对象适配器模式、接口适配器模式

只有适配器类（adapter）需要实现接口，适配者类（原类）不需做任何操作

### 6.1 类适配

通过继承实现类适配，用适配器类实现目标接口并继承待适配类，这样待适配类就拥有了接口的方法

```java
/**
 * 需要适配的类：适配者类
 */
public class Source {
	public void method1() {
		System.out.println("this is original method!");
	}
}
/**
 *	目标接口：客户期待的接口
 */
public interface Targetable {
	/* 与原类中的方法相同 */
	public void method1();
	/* 新类的方法 */
	public void method2();
}
/**
 *	适配器类
 */
public class Adapter extends Source implements Targetable {  
    @Override  
    public void method2() {  
        System.out.println("this is the targetable method!");  
    }  
}
/**
 * Adapter是Source的子类，同时实现接口，最终将Adapter适配成接口类型
 * 这样Targetable接口的实现类就具有了Source类的功能
 * 即适配器模式使得原本由于接口不兼容而不能一起工作的那些类可以在一起工作
 * 简单来说，就是一个类Source想具备接口的方法，但是又不想改造Source
 * 此时，写一个适配器类，继承Source，并实现目标接口
 * 那么，适配器类Adapter既有了Souce方法，又实现了目标接口方法
 */
public class Test {
	public static void main(String[] args) {
		Targetable target = new Adapter();
		target.method1(); // 调用的是source的方法
		target.method2(); // 调用的是adapter自己的方法
	}
}
```

### 6.2 对象适配：

对象适配就是用组合关系替代继承关系，适配器类实现目标接口，并内置待适配的类，在适配器的构造方法传入待适配的类

```java
/**
 * 待适配类
 */
public class Source {
	public void method1() {
		System.out.println("this is original method!");
	}
}
/**
 * 目标接口
 */
public interface Targetable {
	/* 与原类中的方法相同 */
	public void method1();
	/* 新类的方法 */
	public void method2();
}
/**
 * 对象适配器
 * 将要适配的对象传入构造器，调用传入的对象进行执行方法
 * 对象适配器，只持有原类的对象，不用继承原类
 */
public class Wrapper implements Targetable {  
	private Source source;
	public Wrapper(Source source){
		this.source=source;
	}
	@Override
	public void method1() {
		// 与装饰模式很像，但是细看，实际适配器，是在适配目标接口方法里调用待适配对象的方法。
        // 本质还是本方法拥有接口的方法。
		source.method1();
	}  
	@Override
    public void method2() {  
        System.out.println("this is the targetable method!");  
    }
}
/**
 * 对象适配器，只持有类对象，不继承
 */
public class Test {
	public static void main(String[] args) {
		Source source = new Source();
		Targetable target = new Wrapper(source);
		target.method1();
		target.method2();
	}
}
```

### 6.3 接口适配：

接口适配：用一个抽象类（类也可以）实现接口的所有方法，所有方法都是空的，不实现真正的方法

那么该抽象类的子类可有选择的覆盖父类的某些方法来实现需求

适用不使用一个接口所有的方法的情况

```java
/**
 * 目标接口，客户希望实现的方法
 */
public interface Sourceable {
	public void method1();  
    public void method2();
}
/**
 * 适配接口实现
 * 实现接口，所有方法都是空实现
 */
public class Wrapper2 implements Sourceable{
	@Override
	public void method1() {
	}
	@Override
	public void method2() {
	}
}
/**
 * 目标接口部分适配：只需要继承适配器类，并重写关注的部分
 */
public class Source1 extends Wrapper2 {
	public void method1(){  
        System.out.println("method1");  
    }  
}
/**
 * 目标接口部分适配：只需要继承适配器类，并重写关注的部分
 */
public class Source2 extends Wrapper2 {
	public void method2(){  
        System.out.println("method2");  
    }  
}
/**
 * 接口适配器：
 * 接口Sourceable中有多个方法，我们要使用接口，就要实现接口的所有方法方法，但是这显然是比较浪费的
 * 所以，我们写一个抽象类Wrapper2，他实现Sourceable接口 的 所有方法
 * 实际使用时，我们只需要和Wrapper2打交道，重写自己关注的方法
 */
public class Test {
	public static void main(String[] args) {  
        Sourceable source1 = new Source1();
        Sourceable source2 = new Source2();
        source1.method1();
        source1.method2();
        source2.method1();
        source2.method2();
    }  
}
```



## 7、装饰模式（Decorator）

顾名思义，装饰模式就是给一个对象增加一些新的功能，而且是动态的，要求装饰对象和被装饰对象实现同一个接口

核心在于增强功能，装饰者类和待装饰类都实现同一接口，并且装饰器类内置待装饰类作为成员变量，在初始化装饰器时传入

因为实现同一接口，在实现接口的方法时，直接调用待装饰类的方法，并且增强，也就是前后增加一些装饰内容

装饰器模式的应用场景：

1、需要扩展一个类的功能（调用接口方法前后加功能）

2、动态的为一个对象增加功能，而且还能动态撤销（继承不能做到这一点，继承的功能是静态的，不能动态增减）

缺点：产生过多相似的对象，不易排错

```java
/**
 * 装饰模式接口
 * 装饰类和被装饰类都实现本接口，同时装饰类持有被装饰类
 */
public interface Sourceable {
	public void method();  
}

/**
 * 被装饰类
 * 实现了Sourceable 接口
 * 想要动态的为 本类添加装饰方法
 */
public class Source implements Sourceable {
	@Override
	public void method() {
		 System.out.println("source method!");  
	}
}

/**
 * 装饰类，也实现了Sourceable接口
 * 动态的传入被装饰的类作为构造函数入参
 */
public class Decorator implements Sourceable{
	private Sourceable source;
	public Decorator(Sourceable source){
        this.source = source;  
    }
	
	@Override
	public void method() {
		System.out.println("before decorator!");
        source.method();
        System.out.println("after decorator!");
	}
}

/**
 * 装饰类和被装饰类 都 继承同一个接口，
 * 然后装饰类把被装饰类作为一个参数传入  加一些装饰。
 * 
 * 装饰者模式是在不改变接口，加入新的责任，或者说新的功能
 * 适配器模式 是将一个接口改为另一个接口
 * 代理模式关注点在于控制对象访问，其原型对象对于客户来说不可知。
 */
public class Test {
	public static void main(String[] args) {  
        Sourceable source = new Source();  
        Sourceable obj = new Decorator(source);  
        obj.method();  
    }
}
```

-

## 8、代理模式（Proxy）

分为静态代理和动态代理

代理角色：代理角色、被代理角色、抽象接口

静态代理 与 装饰模式类似，但也有不一样的地方

代理类和被代理类都实现接口，但是代理类，将被代理类作为内部变量，初始化时创建

在实现接口时，代理类加入自己的业务逻辑，然后再执行被代理类相关业务逻辑，达到代理目的

它与装饰模式区别是，装饰模式需要初始化装饰器时指定被装饰的类

而代理则可以隐藏细节，不必关注原来的被代理角色的情况。

代理模式的应用场景：

如果已有的方法在使用的时候需要对原有的方法改进，此时有两种办法：

- 修改原有的方法来适应。但这样违反了“对扩展开放，对修改关闭”的原则

- 就是采用一个代理类调用原有的方法，且对产生的结果进行控制，这种方法就是代理模式

使用代理模式，可以将功能划分的更加清晰，有助于后期维护

### 8.1 静态代理

```java
/**
 * 抽象接口
 */
public interface Sourceable {
	public void method();  
}

/**
 * 实现类
 */
public class Source  implements Sourceable {
	@Override
	public void method() {
		System.out.println("Source");
	}
}

/**
 * 代理类
 * 与装饰模式类似，但是也有区别：
 * 代理模式，代理类在编译时就已经指定了要代理的对象，可以隐藏实际对象的具体信息
 * 而装饰模式，是在创建装饰器时动态传入的被装饰类
 * 调用代理类时，并不知道实际是哪个类在干活
 */
public class Proxy implements Sourceable{
	private Source source;
	public Proxy(){
        this.source = new Source();
    }
	
	@Override
	public void method() {
		before();
        source.method();
        atfer();
	}
	
	private void atfer() {
        System.out.println("after proxy!");
    }
    private void before() {
        System.out.println("before proxy!");
    }
}

/**
 * 代理模式隐藏了被代理的主类：Source.java类，在使用过程是看不到的
 */
public class Test {
	public static void main(String[] args) {  
        Sourceable source = new Proxy();  
        source.method();  
    }  
}
```

### 8.2 动态代理（jdk）

```java
/**
 * 抽象接口
 */
public interface Sourceable {
	public void method();  
}
/**
 * 要被代理的原类
 */
public class Source  implements Sourceable {
	@Override
	public void method() {
		System.out.println("invocation handler!!!");
	}
}
/**
 * 动态代理
 * 使用jdk实现动态代理，jdk是通过接口实现的
 */
public class DynaProxy implements InvocationHandler{
	private Object object; // 在初始化时，传入要被代理的对象
	public DynaProxy(Object object){  
        this.object=object;
    }  

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		System.out.println("dyna proxy begin");
		Object obj=method.invoke(object, args);
		System.out.println("dyna proxy end");
		return obj;
	}  
}
/**
 * 客户端，使用
 */
public class Test {
    public static void main(String[] args) {
        // 原类
        Sourceable source = new Source();
        // 动态代理类，初始化时要传入要被代理的类
        DynaProxy proxy = new DynaProxy(source);
        // 底层会动态创建一个新类，这个新类是接口的实现
        // 通过Proxy代理反射获取实例对象
        Sourceable instance = (Sourceable) Proxy.newProxyInstance(
                source.getClass().getClassLoader(),
                new Class[]{Sourceable.class},
                proxy);
        // 调用代理类的方法
        instance.method();
    }
}
```

### 8.4 动态代理（cglib）

```java
/**
 * 抽象接口
 */
public interface Sourceable {
	public void method();  
}

/**
 * 原方法
 */
public class Source implements Sourceable {
    @Override
    public void method() {
        System.out.println("cglib test!!!");
    }
}

/**
 * 通过spring的cglib来获取动态代理
 */
public class CglibProxy implements MethodInterceptor {
	@Override
	public Object intercept(Object o, Method method, Object[] args,
			MethodProxy methodProxy) throws Throwable {
		System.out.println("++++++before " + methodProxy.getSuperName() + "++++++");
		System.out.println(method.getName());
		Object p = methodProxy.invokeSuper(o, args);
		System.out.println("++++++after " + methodProxy.getSuperName() + "++++++");
		return p;
	}
}

/**
 * 客户端类
 * cglib通过增强类实现动态代理
 */
public class Test {
	public static void main(String[] args) {
		// 创建代理
		CglibProxy proxy = new CglibProxy();
		// 增强类
		Enhancer enhancer = new Enhancer(); 
		// 设置被代理类
		enhancer.setSuperclass(Source.class); 
		// 设置代理类
		enhancer.setCallback(proxy);
		// 返回包含真实对象的代理类
		Sourceable o = (Sourceable)enhancer.create();  
		// 调用代理类的方法，实际是调用cglibproxy的intercept方法
		o.method();
	}
}
```



## 9、外观模式（Facade）

外观模式（Facade Pattern）隐藏系统的复杂性，并向客户端提供了一个客户端可以访问系统的接口。

这种类型的设计模式属于结构型模式，它向现有的系统添加一个接口，来隐藏系统的复杂性。

这种模式涉及到一个单一的类，该类提供了客户端请求的简化方法和对现有系统类方法的委托调用。

1、去医院看病，可能要去挂号、门诊、划价、取药，很复杂，如果有提供接待人员，只让接待人员来处理，就很方便

2、JAVA 的三层开发模式

```java
public class Cpu{
	public void startup(){  
        System.out.println("cpu startup!");  
    }  
      
    public void shutdown(){  
        System.out.println("cpu shutdown!");  
    }
}
public class Memory{
	public void startup(){  
        System.out.println("Memory startup!");  
    }  
      
    public void shutdown(){  
        System.out.println("Memory shutdown!");  
    }
}
public class Disk{
	public void startup(){  
        System.out.println("Disk startup!");  
    }  
      
    public void shutdown(){  
        System.out.println("Disk shutdown!");  
    }
}
/**
 * 外观模式的包装类
 * 通过外观的包装，使应用程序只能看到外观包装类，而不必看到具体细节
 * 这样，无疑会降低应用的复杂度，提高程序的可维护性
 */
public class Computer{
	private Cpu cpu;
    private Memory memory;
    private Disk disk;

    public Computer(){
        cpu = new Cpu();
        memory = new Memory();
        disk = new Disk();
    }
    public void startup(){
        System.out.println("开机!");
        cpu.startup();
        memory.startup();
        disk.startup();
        System.out.println("开机完成!");
    }
    public void shutdown(){
        System.out.println("关机!");
        cpu.shutdown();
        memory.shutdown();
        disk.shutdown();
        System.out.println("关机完成!");
    }
}
/**
 * 客户端类
 * 如果我们没有Computer类，那么，Cpu、Memory、Disk他们之间将会相互持有实例，产生关系
 * 这样会造成严重的依赖，修改一个类，可能会带来其他类的修改，这不是我们想要看到的
 * 有了Computer类，他们之间的关系被放在了Computer类里，这样就起到了解耦的作用，这就是外观模式
 * 其实就是应用程序只能看到外观模式封装类，而看不到具体细节处理类
 */
public class Client {
	public static void main(String[] args) {  
        Computer computer = new Computer();  
        computer.startup();  
        computer.shutdown();  
    }  
}
```



## 10、桥接模式（Bridge）

桥接模式定义：将抽象部分与它的实现部分分离，使它们都可以独立地变化

它是一种对象结构型模式，又称为柄体(Handle and Body)模式或接口(Interface)模式

```java
// 定义一个接口
public interface Source {
	public void getDataSouce();  
}
// 接口实现1
public class SourceSub1 implements Source{
	@Override
	public void getDataSouce() {
		System.out.println("SourceSub1.getDataSouce");
	}
}
// 接口实现2
public class SourceSub2 implements Source{
	@Override
	public void getDataSouce() {
		System.out.println("SourceSub2.getDataSouce");
	}
}
// 定义一个抽象的桥，持有接口，可以通过set方法设置接口的具体实现
public abstract class Bridge {
    private Source source;
	
    //桥内方法
    public void method() {
        source.getDataSouce();
    }

    public Source getSource() {
        return source;
    }
    
    public void setSource(Source source) {
        this.source = source;
    }
}
// 抽象类的实现 重写桥的方法，调用接口的具体方法
public class MyBridge extends Bridge{
	@Override
	public void method() {
		System.out.println(123);
		getSource().getDataSouce();
		System.out.println(123);
	}
}

public class BridgeTest {
	public static void main(String[] args) {
        // 定义一个桥的实例
		Bridge bridge = new MyBridge();
		// 为桥设置具体接口的实例化
		Source source1 = new SourceSub1();
		bridge.setSource(source1);
        // 调用桥的具体方法
		bridge.method();

		// 为桥设置具体接口的实例化
		Source source2 = new SourceSub2();
		bridge.setSource(source2);
        // 调用桥的具体方法
		bridge.method();
	}
}
```

咖啡例子：

```java
// 抽象一个桥：咖啡，构造方法 传入 实际添加剂
public abstract class Coffee {
    // 咖啡添加剂
    ICoffeeAdditives additives;

    public Coffee(ICoffeeAdditives additives) {
        this.additives = additives;
    }
    // 结账
    public abstract void orderCoffee(int count);
}
// 添加剂接口
public interface ICoffeeAdditives {
    String addSomething();
}
// 添加剂-冰
public class Ice implements ICoffeeAdditives {
    @Override
    public String addSomething() {
        return "加冰";
    }
}
// 添加剂-牛奶
public class Milk implements ICoffeeAdditives {
    @Override
    public String addSomething() {
        return "加奶";
    }
}
// 大杯咖啡
public class LargeCoffee extends Coffee {
    public LargeCoffee(ICoffeeAdditives additives) {
        super(additives);
    }

    @Override
    public void orderCoffee(int count) {
        System.out.println("大杯" + additives.addSomething()
                + "咖啡" + count + "杯");
    }
}
// 中杯咖啡
public class MidCoffee extends Coffee{
    public MidCoffee(ICoffeeAdditives additives) {
        super(additives);
    }

    @Override
    public void orderCoffee(int count) {
        System.out.println("中杯" + additives.addSomething()
                + "咖啡" + count + "杯");
    }
}
// 客户端类
public class Client {
    public static void main(String[] args) {
        Coffee coffee = new LargeCoffee(new Milk());
        coffee.orderCoffee(2);

        Coffee iceCoffee = new MidCoffee(new Ice());
        iceCoffee.orderCoffee(1);
    }
}
```



## 11、组合模式（Composite）

组合模式有时又叫 “部分-整体模式” ，在处理类似树形结构的问题时比较方便

抽象节点接口（子节点和叶子都可以继承该接口），规范方法，提供：新增、删除、遍历的方法

子节点，也是个容器，存入List<节点接口>

实现新增、删除、遍历、其中遍历不仅要打印自己，还要循环所有子，调用子节点的foreach方法

叶子节点，无List，只需实现foreach打印自己就可以，新增和删除空方法即可

```java
/**
 * 抽象接口
 * 抽象和规范子部件
 */
public abstract class Component {
    String name;

    public Component(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract void add(Component c);

    public abstract void remove(Component c);

    public abstract void foreach();
}

/**
 * 根节点，有子节点
 */
public class Composite extends Component {
    private List<Component> child = new ArrayList<Component>();

    public Composite(String name) {
        super(name);
    }

    @Override
    public void add(Component c) {
        child.add(c);
    }

    @Override
    public void remove(Component c) {
        child.remove(c);
    }

    @Override
    public void foreach() {
        System.out.println("节点名:\t" + name);
        for (Component c : child) {
            c.foreach();
        }
    }
}

/**
 * 叶子节点，无子节点
 */
public class Leaf extends Component {
    public Leaf(String name) {
        super(name);
    }

    @Override
    public void add(Component c) {
    }

    @Override
    public void remove(Component c) {
    }

    @Override
    public void foreach() {
        System.out.println("叶子节点名称-->" + this.name);
    }
}

/**
 * 组合模式
 * 组合(Composite)模式的其它翻译名称也很多，比如合成模式、树模式等等
 * 将对象以树形结构组织起来，以达成 “部分－整体” 的层次结构
 * 使得客户端对单个对象和组合对象的使用具有一致性
 *
 * 组合模式三部分构成：
 * 1)抽象构件角色Component：它为组合中的对象声明接口，也可以为共有接口实现缺省行为。
 * 2)树叶构件角色Leaf：在组合中表示叶节点对象——没有子节点，实现抽象构件角色声明的接口。
 * 3)树枝构件角色Composite：在组合中表示分支节点对象——有子节点，实现抽象构件角色声明的接口；存储子部件。
 *
 * 抽象abstract Component，Composite 继承 Component
 * Leaf与Component可以2个类，也可以一个类，一个类冗余，但是能保持一致性
 */
public class Test {
	public static void main(String[] args) {
		Component root = new Composite("根节点");
		Component child = new Composite("一级子节点child");
		Component child_1 = new Leaf("一级子节点child之子节点一");
		Component child_2 = new Leaf("一级子节点child之子节点二");
		child.add(child_1);
		child.add(child_2);
		Component child2 = new Composite("一级子节点child2");
		root.add(child);
		root.add(child2);
		root.foreach();
	}
}
```



## 12、享元模式（Flyweight）

当对象数量太多时，将导致运行带价过高，带来性能下降等问题。享元模式正式为解决这一类问题而诞生的

享元模式把一个对象的状态分成内部状态和外部状态，内部状态即是不变的，外部状态是变化的

然后通过共享不变的部分，达到减少对象数量并节约内存的目的

- 内部状态：封装在元对象内部，以成员变量存在，初始化元对象构造方法传入
- 外部状态：调用方法的时候传入，也就是方法的参数

享元的核心是享元工厂，所以元对象都存储在享元工厂的缓存中。这个缓存是map、vector、list等。

存储的条件是内部状态，也就是作为成员变量的值，因为这个值是不变的。

```java
/**
 * 抽象享元
 */
public interface IFlyweight {
	//id为外部状态，不共享，调用时传入外部状态
    public void setId(int id);
}

/**
 * 具体享元
 */
public class FlyweightObj implements IFlyweight {
    //内部状态str，作为成员变量，对外共享
    private String str;

    public FlyweightObj(String str) {
        this.str = str;
    }

    //id为外部状态，不共享
    @Override
    public void setId(int id) {
        System.out.println("str: " + str + "id:" + id);
    }
}

/**
 * 享元工厂
 * 享元的核心在于享元工厂，提供一个用于存储享元对象的享元池
 * 用户需要时，从享元池中获取，享元池里不存在，新创建一个加入到享元池中
 */
public class FlyweightFactory {
    private Map<String, Object> strMap = new HashMap<String, Object>();

    public IFlyweight getInstance(String str) {
        IFlyweight fly = (IFlyweight) strMap.get(str);
        if (fly == null) {
            fly = new FlyweightObj(str);
            strMap.put(str, fly);
        }
        return fly;
    }
}

/**
 * 客户端
 */
public class Test {
    public static void main(String[] args) {
        FlyweightFactory factory = new FlyweightFactory();
        //先判断hello是否存在 不存在new 存在直接返回
        FlyweightObj hello = (FlyweightObj) factory.getInstance("hello"); 
        hello.setId(1);//设置外部对象
        
		//上面已经存在了，是一个对象，设置外部对象为2 但还是一个对象，共享元
        FlyweightObj hello2 = (FlyweightObj) factory.getInstance("hello");
        hello.setId(2);//设置外部对象

        FlyweightObj test = (FlyweightObj) factory.getInstance("test");
        hello.setId(3);

        System.out.println(hello);
        System.out.println(hello2);
        //这是个假的，实际作用是保质内部状态相同的对象在享元工厂里只有一个！！！
        System.out.println(hello.equals(hello2));

        System.out.println(test);
        System.out.println(hello.equals(test));
    }
}
```

JDBC的数据库连接池就是享元的一个应用，String 也是享元的一个应用

```java
/**
 * 享元设计模式：大量细粒度对象共享复用
 *
 * 享元角色：
 *   抽象享元：一个接口或抽象类； 
 *   具体享元：内部状态为其成员属性，其实例为享元对象，可以共享；
 *   享元工厂：生产享元对象，将具体享元对象存储在一个享元池中，享元池一般设计为一个存储“键值对”的集合；
 *   客户端：使用享元对象
 *
 * 通过连接池的管理，实现了数据库连接的共享，不需要每一次都重新创建连接
 * 节省了数据库重新创建的开销，提升了系统的性能
 */
public class ConnectionPool {
	private Vector<Connection> pool;
	/* 公有属性 */
	private String url = "jdbc:mysql://localhost:3306/test";
	private String username = "root";
	private String password = "root";
	private String driverClassName = "com.mysql.jdbc.Driver";

	private int poolSize = 100;
	private static ConnectionPool instance = null;
	Connection conn = null;

	// 构造方法，在初始化连接池时向vector中添加N个 连接对象
	private ConnectionPool() {
		pool = new Vector<Connection>(poolSize);
		for (int i = 0; i < poolSize; i++) {
			try {
				Class.forName(driverClassName);
				conn = DriverManager.getConnection(url, username, password);
				pool.add(conn);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/* 释放是将conn重新添加回 连接池 */
	public synchronized void release() {
		pool.add(conn);
	}

	/* 返回连接池中的一个数据库连接 */
	public synchronized Connection getConnection() {
        // 获取连接，从连接池中取出一个连接，如果连接池中没有连接返回 null
		if (pool.size() > 0) {
			Connection conn = pool.get(0);
			pool.remove(conn);
			return conn;
		} else {
			return null;
		}
	}
}
```



## ----------十一种行为型模式----------

   第一类：通过父类与子类的关系进行实现（策略模式、模板方法模式）

   第二类：两个类之间（观察者模式、迭代子模式、责任链模式、命令模式）

   第三类：类的状态（备忘录模式、状态模式）

   第四类：通过中间类（访问者模式、中介者模式、解释器模式）

## 13、策略模式（strategy）

在策略模式（Strategy Pattern）中，一个类的行为或其算法可以在运行时更改。这种类型的设计模式属于行为型模式。

在策略模式中，我们创建表示各种策略的对象和一个行为随着策略对象改变而改变的 context 对象。

策略对象改变 context 对象的执行算法。

如果不使用策略模式，当我们要添加功能时，我们要修改功能，增加很多判断。

策略模式最重要的就是策略接口，在扩展时，不同的实现，体现不同的策略，上下文中传入策略实现不同的操作。

只有策略部分是变的，其他部分是不变的，才可以用策略模式

```java
/**
 * 策略模式，先定义一个统一接口
 */
public interface ICalculator {
	public int calculate(String exp);  
}
/**
 * 策略模式的辅助类
 * 这个类可以没有
 */
public abstract class AbstractCalculator {
	public int[] split(String exp, String opt) {
		String array[] = exp.split(opt);
		int arrayInt[] = new int[2];
		arrayInt[0] = Integer.parseInt(array[0]);
		arrayInt[1] = Integer.parseInt(array[1]);
		return arrayInt;
	}
}
public class Minus extends AbstractCalculator implements ICalculator {
	/**
	 * 传入字符串减法
	 */
	@Override  
    public int calculate(String exp) {  
        int arrayInt[] = split(exp,"-");  
        return arrayInt[0]-arrayInt[1];  
    }
}
public class Multiply extends AbstractCalculator implements ICalculator {
    /**
     * 传入字符串乘法
     */
    @Override
    public int calculate(String exp) {
        int arrayInt[] = split(exp, "\\*");
        return arrayInt[0] * arrayInt[1];
    }
}
public class Plus extends AbstractCalculator implements ICalculator {
    /**
     * 传入字符串加法
     */
    public int calculate(String exp) {
        int arrayInt[] = split(exp, "\\+");
        return arrayInt[0] + arrayInt[1];
    }
}

/**
 * 环境角色类
 * 其中存放一个策略接口
 */
public class PriceContext {
    private ICalculator iCalculator;

    public PriceContext(ICalculator iCalculator) {
        this.iCalculator = iCalculator;
    }

    public int jisuan(String exp) {
        return this.iCalculator.calculate(exp);
    }
}

/**
 * 策略模式 本身提供各种方法封装，外部用户决定调用那个方法
 * 环境角色类里存放了策略接口，创建时决定调用哪个策略
 */
public class StrategyTest {
    public static void main(String[] args) {
        String exp = "3+5";
        ICalculator cal = new Plus();

        PriceContext p = new PriceContext(cal);
        int r = p.jisuan(exp);
        System.out.println(r);
    }
}
```



## 14、模板方法模式（Template Method）

抽象一个模板类，模板类中，非抽象的方法调用抽象的方法，即为模板方法模式。

任何类要继承这个模板，就得重写 abstract 方法，这样方法的实现就延迟到子类中。

```java
/**
 * 抽象类，非抽象方法调用抽象方法
 * 实现会延迟到子类实现
 */
public abstract class AbstractCalculator {

    /* 主方法，实现对本类其它方法的调用 */
    public final int calculate(String exp, String opt) {
        int array[] = split(exp, opt);
        return calculate(array[0], array[1]);
    }

    /* 被子类重写的方法 */
    abstract public int calculate(int num1, int num2);

    public int[] split(String exp, String opt) {
        String array[] = exp.split(opt);
        int arrayInt[] = new int[2];
        arrayInt[0] = Integer.parseInt(array[0]);
        arrayInt[1] = Integer.parseInt(array[1]);
        return arrayInt;
    }
}
// 模板实现类
public class Plus extends AbstractCalculator {
    @Override
    public int calculate(int num1, int num2) {
        return num1 + num2;
    }
}

// 客户端
public class Client {
	public static void main(String[] args) {
		String exp = "8+8";
		//Plus 为子类
		AbstractCalculator cal = new Plus();
		//calculate为模板的主方法。
		int result = cal.calculate(exp, "\\+");
		System.out.println(result);
	}
}
```



## 15、观察者模式（Observer）

   抽象观察者observer，提供-被观察者变化反通知（观察者更新）的方法定义；
   抽象被观察者subject，提供-新增、删除和通知观察者（通知所有观察者）的方法定义；
   实现化观察者，接收来自被观察者发来的通知并处理；
   实现化被观察者，实现增、删观察者、递归循环通知所有被观察者方法。
   客户端：new观察者，new被观察者mysubject，调用被观察者mysubject的add方法添加观察者。
   当所有观察者都设置完毕，调用被观察者的通知观察者的方法通知所有。

   老王，老李，观察小丽，小丽持有老王、老李的手机号；
   当小丽的老公不在家时，通知所有观察者（老王，老李），老公不在家，寂寞无人陪，谁先来我就陪他睡；
   观察者（老王，老李）接到消息，各自处理各自的逻辑，肯定有人是先到小丽家楼下。

```java
/**
 * 抽象被观察者
 * 提供增删和通知观察者
 */
public interface Subject {
	/* 增加观察者 */
	public void add(Observer observer);

	/* 删除观察者 */
	public void del(Observer observer);

	/* 通知所有的观察者 */
	public void notifyObservers();

	/* 自身的操作 */
	public void operation();
}

/**
 * 实例化被观察者实例
 */
public class MySubject extends AbstractSubject {
    @Override
    public void operation() {
        System.out.println("update self!");
        notifyObservers();
    }
}

/**
 * 抽象观察者对象 在得到主题通知时更新自己
 */
public interface Observer {
	public void update();
}

/**
 * 抽象被观察者
 * 持有一个观察者的数组
 * 同时提供新增、删除及通知观察者 主题更新的方法
 */
public abstract class AbstractSubject implements Subject {
    private Vector<Observer> vector = new Vector<Observer>();

    @Override
    public void add(Observer observer) {
        vector.add(observer);
    }

    @Override
    public void del(Observer observer) {
        vector.remove(observer);
    }

    @Override
    public void notifyObservers() {
        Enumeration<Observer> enumo = vector.elements();
        while (enumo.hasMoreElements()) {
            enumo.nextElement().update();
        }
    }
}

/**
 * 实例化观察者1
 */
public class Observer1 implements Observer{
	@Override
	public void update() {
		System.out.println("observer1 has received!");
	}
}

/**
 * 实例化观察者2
 */
public class Observer2 implements Observer{
	@Override
	public void update() {
		System.out.println("observer2 has received!");  
	}
}

/**
 * 被观察者维护观察者抽象对象的实例
 * 当被观察者更新时，通知观察者 主题更新，调用观察者的更新方法
 */
public class ObserverTest {
	public static void main(String[] args) {
		Subject sub = new MySubject();
		sub.add(new Observer1());
		sub.add(new Observer2());
		sub.operation();
	}
}
```



## 16、迭代器模式（Iterator）

顾名思义，迭代器模式就是顺序访问聚集中的对象，一般来说，集合中非常常见

这句话包含两层意思：一是需要遍历的对象，即聚集对象，二是迭代器对象，用于对聚集对象进行遍历访问

抽象容器（接口） 例如list，提供：iterator、get、size、add、remove 等方法

抽象迭代器（接口） Iterator，提供 hasNext、next、first、previous 等遍历方法

```java
/**
 * 抽象容器，需要提供新增、删除、获取、大小等
 */
public interface Collection {
    public Iterator iterator();  
	/*取得集合元素*/  
    public Object get(int i);  
    /*取得集合大小*/  
    public int size();  
}


/**
 * 抽象迭代器
 */
public interface Iterator {
	// 前移
	public Object previous();
	// 后移
	public Object next();
	public boolean hasNext();
	// 取得第一个元素
	public Object first();
}


/**
 * 实现容器
 */
public class MyCollection implements Collection {
    public String string[] = {"A", "B", "C", "D", "E"};
    @Override
    public Iterator iterator() {
        return new MyIterator(this);
    }
    @Override
    public Object get(int i) {
        return string[i];
    }
    @Override
    public int size() {
        return string.length;
    }
}


/**
 * 实现迭代器 提供遍历、方法
 */
public class MyIterator implements Iterator {
    private Collection collection;
    private int pos = -1;

    public MyIterator(Collection collection) {
        this.collection = collection;
    }
    @Override
    public Object previous() {
        if (pos > 0) {
            pos--;
        }
        return collection.get(pos);
    }
    @Override
    public Object next() {
        if (pos < collection.size() - 1) {
            pos++;
        }
        return collection.get(pos);
    }
    @Override
    public boolean hasNext() {
        if (pos < collection.size() - 1) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    public Object first() {
        pos = 0;
        return collection.get(pos);
    }
}


public class Test {
	public static void main(String[] args) {
		Collection collection = new MyCollection();
		Iterator it = collection.iterator();

		while (it.hasNext()) {
			System.out.println(it.next());
		}
	}
}
```



## 17、责任链模式（Chain of Responsibility）

每个对象持有对下一个对象的引用

责任链模式，有多个对象，每个对象持有对下一个对象的引用，这样就会形成一条链

请求在这条链上传递，直到某一对象决定处理该请求，但是发出者并不清楚到底最终那个对象会处理该请求

所以，责任链模式可以实现在隐瞒客户端的情况下，对系统进行动态的调整

- 抽象处理角色（接口），提供处理方法

- 结构抽象（抽象类），持有下一个处理者，并提供 get/set

- 实现化处理角色（继承抽象类，实现接口），判断自己能不能处理，不能处理调用下一个处理者

- 客户端：设置自己和下一个处理者（如果下一个处理者还有其他处理者，继续调用）

  责任链调用后会找到对应的处理方法去处理，如果自己不能处理，会调用他下一个处理者处理

```java
/**
 * 抽象处理角色
 */
public interface Handler {
	public void operator();
}

/**
 * 持有下一个对象的引用
 */
public class AbstractHandler {
    private Handler handler;
    public Handler getHandler() {
        return handler;
    }
    public void setHandler(Handler handler) {
        this.handler = handler;
    }
}

/**
 * 实例化处理角色
 */
public class MyHandler extends AbstractHandler implements Handler {
	private String name;
	public MyHandler(String name) {
		this.name = name;
	}
	@Override
	public void operator() {
		System.out.println(name + "deal!");
		if (getHandler() != null) {
			getHandler().operator();
		}
	}
}

/**
 * 责任链模式
 * 每个对象持有下一个对象的引用，调用时，直到有方法决定处理
 * 适用于比如工作流，起草人员持有部门经理，部门经理持有项目经理，项目经理持有总经理
 * 当调用起草人员提交，他判断自己能不能执行，不能执行调用下一持有对象进行调用，直到有人处理
 */
public class Test {
	public static void main(String[] args) {
		MyHandler h1 = new MyHandler("h1");
		MyHandler h2 = new MyHandler("h2");
		MyHandler h3 = new MyHandler("h3");
		h1.setHandler(h2);
		h2.setHandler(h3);
		h1.operator();
	}
}
```



## 18、命令模式（Command）

命令模式很好理解，举个例子，司令员下令让士兵去干件事情，从整个事情的角度来考虑，司令员的作用是，发出口令，口令经过传递，
传到了士兵耳朵里，士兵去执行。这个过程好在，三者相互解耦，任何一方都不用去依赖其他人，只需要做好自己的事儿就行，司令员要的是结果，
不会去关注到底士兵是怎么实现的。

Invoker是调用者（司令员），Receiver是被调用者（士兵），MyCommand是命令，实现了Command接口，持有接收对象.
命令模式的目的就是达到命令的发出者和执行者之间解耦，实现请求和执行分开，
熟悉Struts的同学应该知道，Struts其实就是一种将请求和呈现分离的技术，其中必然涉及命令模式的思想！
awt就是命令模式

三种角色：调用者、命令、接收者
调用者持有命令；
命令持有接收者；
客户端调用调用者，需要传入命令作为构造，命令需要传入接收者作为构造。

命令模式使得程序执行明确。
责任链模式命令明确。 

```java
/**
 * 抽象命令
 */
public interface Command {
	public void exe();
}

/**
 * 命令实现
 * 持有接收命令者
 * 调用接收者的相应操作
 */
public class MyCommand implements Command {

	private Receiver receiver;

	public MyCommand(Receiver receiver) {
		this.receiver = receiver;
	}

	@Override
	public void exe() {
		receiver.action();
	}
}

/**
 * 调用对象
 * 持有一个命令
 * 调用命令执行
 */
public class Invoker {
	private Command command;
	public Invoker(Command command) {
		this.command = command;
	}
	public void action() {
		command.exe();
	}
}

/**
 * 实际接收者
 * 定义接收者处理方法
 */
public class Receiver {
	public void action() {
		System.out.println("command received!");
	}
}

/**
 * 创建一个接收者
 * 创建一个命令，将接收者传入
 * 创建一个调用，调用命令。
 */
public class Test {
    public static void main(String[] args) {
        Receiver receiver = new Receiver();
        Command cmd = new MyCommand(receiver);
        Invoker invoker = new Invoker(cmd);
        invoker.action();
    }
}
```



## 19、备忘录模式（Memento）

   发起人：记录当前时刻的内部状态，负责定义哪些属于备份范围的状态，负责创建和恢复备忘录数据。
   备忘录：负责存储发起人对象的内部状态，在需要的时候提供发起人需要的内部状态。
   管理角色：对备忘录进行管理，保存和提供备忘录。

   备忘录模式分为单属性备忘录和多属性备忘录，其中多属性备忘录常用。
   //获得一个bean的自省。然后获取所有属性详细，循环。获得名字和getReadMethod() get方法。get.invoke(o,new Object[]{});得到值
   Introspector.getBeanInfo(o.getClass());
   //获取自省对象所有的属性
   PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();

```java
/**
 * 备忘录角色
 * 负责存储发起人角色的内部状态，并在需要的时候提供发起人需要的内部状态（恢复时用）
 */
public class Memento {
	public Memento(String value) {
		this.value = value;
	}
	
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}

/**
 * 发起人角色
 * 内部状态 value
 * 提供创建备忘录，通过备忘录恢复的方法
 */
public class Original {
	private String value;

	public Original(String value) {
		this.value = value;
	}

	/**
	 * 创建备忘录
	 */
	public Memento createMemento() {
		return new Memento(value);
	}

	/**
	 * 恢复
	 */
	public void restoreMemento(Memento memento) {
		this.value = memento.getValue();
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}

/**
 * 管理角色
 * 负责管理、保存和 提供备忘录
 */
public class Storage {
	private Memento memento;

	public Storage(Memento memento) {
		this.memento = memento;
	}

	/**
	 * 备忘录取值
	 */
	public Memento getMemento() {
		return memento;
	}

	/**
	 * 备忘录赋值
	 */
	public void setMemento(Memento memento) {
		this.memento = memento;
	}
}

/**
 * 备忘录模式比较难理解
 * 创建发起人
 * 发起人创建备忘录Memento
 * 创建负责人，负责存储备忘录，将备忘录传入
 * 修改发起人任何节点。
 * 发起人调用负责人 获取备忘录，使用备忘录恢复
 */
public class Test {
	public static void main(String[] args) {
		// 创建原始类
		Original origi = new Original("egg");
		// 创建备忘录
		Storage storage = new Storage(origi.createMemento());
		// 修改原始类的状态
		System.out.println("初始化状态为：" + origi.getValue());
		origi.setValue("niu");
		System.out.println("修改后的状态为：" + origi.getValue());
		// 回复原始类的状态
		origi.restoreMemento(storage.getMemento());
		System.out.println("恢复后的状态为：" + origi.getValue());
	}
}
```

```java
public class BeanUtils {

	/**
	 * 备份
	 * Introspector ： 自省。
	 * Java JDK中提供了一套 API 用来访问某个属性的 getter/setter 方法，这就是内省。
	 * PropertyDescriptor类表示JavaBean类通过存储器导出一个属性。
	 * 1. getPropertyType()，获得属性的Class对象;
	 * 2. getReadMethod()，获得用于读取属性值的方法；getWriteMethod()，获得用于写入属性值的方法;
	 * 3. hashCode()，获取对象的哈希值;
	 * 4. setReadMethod(Method readMethod)，设置用于读取属性值的方法;
	 * 5. setWriteMethod(Method writeMethod)，设置用于写入属性值的方法。
	 * apache用beanutils 替代了 Introspector。
	 */
	public static Map<String, Object> backupProp(Object o) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			//获取自行对象
			BeanInfo beanInfo = Introspector.getBeanInfo(o.getClass());
			//获取自省对象所有的属性
			PropertyDescriptor[] descriptors = beanInfo
					.getPropertyDescriptors();
			//循环所有属性
			for (PropertyDescriptor des : descriptors) {
				//获得属性名称
				String fieldName = des.getName();
				//获得属性的读方法（getter方法）
				Method getter = des.getReadMethod();
				//通过getter方法调用，获得值
				Object fieldValue = getter.invoke(o, new Object[] {});
				if (!fieldName.equalsIgnoreCase("class")) {
					result.put(fieldName, fieldValue);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 恢复
	 */
	public static void restoreProp(Object bean, Map<String, Object> propMap) {
		try {
			//获取自省对象
			BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
			//自省属性
			PropertyDescriptor[] descriptors = beanInfo
					.getPropertyDescriptors();
			//循环自省属性
			for (PropertyDescriptor des : descriptors) {
				//获得自省属性名称
				String fieldName = des.getName();
				if (!fieldName.equalsIgnoreCase("class")) {
					//非class 判断包含，则 获得写方法  setter
					if (propMap.containsKey(fieldName)) {
						Method setter = des.getWriteMethod();
						//调用setter将值反写
						setter.invoke(bean, new Object[] { propMap.get(fieldName) });
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

/**
 * 多属性备忘录角色
 * 负责存储发起人角色的内部状态，并在需要的时候提供发起人需要的内部状态（恢复时用）
 */
public class Memento {
	private Map<String, Object> stateMap;  
    
    public Memento(Map<String, Object> map){  
        this.stateMap = map;  
    }  
  
    public Map<String, Object> getStateMap() {  
        return stateMap;  
    }  
  
    public void setStateMap(Map<String, Object> stateMap) {  
        this.stateMap = stateMap;  
    }  
}

/**
 * 发起人角色
 * 内部状态 value 提供创建备忘录，通过备忘录恢复的方法
 */
public class Original {
	private String state1 = "";
	private String state2 = "";
	private String state3 = "";

	public Original() {
	}

	public Original(String state1, String state2, String state3) {
		super();
		this.state1 = state1;
		this.state2 = state2;
		this.state3 = state3;
	}

	public String getState1() {
		return state1;
	}

	public void setState1(String state1) {
		this.state1 = state1;
	}

	public String getState2() {
		return state2;
	}

	public void setState2(String state2) {
		this.state2 = state2;
	}

	public String getState3() {
		return state3;
	}

	public void setState3(String state3) {
		this.state3 = state3;
	}

	/**
	 * 创建备忘录
	 * 
	 * @return
	 */
	public Memento createMemento() {
		return new Memento(BeanUtils.backupProp(this));
	}

	/**
	 * 恢复
	 * 
	 * @param memento
	 */
	public void restoreMemento(Memento memento) {
		BeanUtils.restoreProp(this, memento.getStateMap());
	}

	public String toString() {
		return "state1=" + state1 + "state2=" + state2 + "state3=" + state3;
	}
}


/**
 * 管理角色
 * 负责管理、保存和 提供备忘录
 */
public class Storage {
	private Map<String, Memento> memMap = new HashMap<String, Memento>();  
    public Memento getMemento(String index){  
        return memMap.get(index);  
    }  
      
    public void setMemento(String index, Memento memento){  
        this.memMap.put(index, memento);  
    }  
}

/**
 * 备忘录模式比较难理解 创建发起人 发起人创建备忘录Memento 创建负责人，
 * 负责存储备忘录，将备忘录传入，修改发起人任何节点。
 * 发起人调用负责人 获取备忘录，使用备忘录恢复
 */
public class Test {
	public static void main(String[] args) {
		Original ori = new Original();
		Storage caretaker = new Storage();
		ori.setState1("中国");
		ori.setState2("强盛");
		ori.setState3("繁荣");
		System.out.println("===初始化状态===\n" + ori);
		caretaker.setMemento("001", ori.createMemento());
		ori.setState1("软件");
		ori.setState2("架构");
		ori.setState3("优秀");
		System.out.println("===修改后状态===\n" + ori);
		ori.restoreMemento(caretaker.getMemento("001"));
		System.out.println("===恢复后状态===\n" + ori);
	}
}
```



## 20、状态模式（State）

   核心思想就是：当对象的状态改变时，同时改变其行为，很好理解！就拿QQ来说，有几种状态，在线、隐身、忙碌等，每个状态对应不同的操作，
   而且你的好友也能看到你的状态，所以，状态模式就两点：1、可以通过改变状态来获得不同的行为。2、你的好友能同时看到你的变化。

   三个角色：上下文角色（环境角色），状态角色，状态实现角色（多个）
   状态角色是个接口，规范方法；
   实现状态：每个状态一个实体类，实现状态角色；
   上下文角色持有一个状态角色，提供一个调用状态角色的方法；
   客户端调用，new一个上下文角色，设置状态，调用上下文方法，上下文方法调用状态实际的方法。
   从而实现，改变状态，改变方法，但是上下文不变还是原上下文。

```java
/**
 * 环境角色
 * 定义客户感兴趣的接口，并保留一个具体状态类的实例
 * 状态切换类
 * 持有状态类
 */
public class Context {
	private State state;  
	  
    public State getState() {  
        return state;  
    }  
  
    public void setState(State state) {  
        this.state = state;  
    }  
  
    public void method() {  
        state.callStateMethd();
    }  
}

public class Rain implements State{
	@Override
	public void callStateMethd() {
		System.out.println("it is rain day!");
	}
}


/**
 * 抽象状态角色
 * 具体状态角色
 * 实际状态类可以加个接口
 * 状态类   有状态 及 各种方法
 */
public interface State{
	public void callStateMethd();
}

public class Sunny implements State{
	@Override
	public void callStateMethd() {
		System.out.println("it is sunny day!");
	}
}

/**
 *	状态模式，创建一个环境上下文，使用状态类。
 *  然后状态类直接设置状态，上下文中直接根据状态改变调用不同方法
 */
public class Test {
	public static void main(String[] args) {
		State state = new Rain();
		Context context = new Context();
		context.setState(state);
		context.method();
		State state1 = new Sunny();
		context.setState(state1);
		context.method();
	}
}
```



## 21、访问者模式（Visitor）

   角色：抽象访问者、访问者、抽象元素类、元素类、结构对象
   举例：顾客去超市买东西。苹果，图书等。买完都扔在购物车里，最终客户要看看性价比、质量；收银员也要访问这些产品计价等。
   此时顾客和收银员  对于购物车来说，都是访问者。
   这种方式，结构是固定的，只是访问者做的事不同，扩展只需扩展访问者即可。

   元素提供一个“接受访问”的方法，入参是访问者，内部方法是调用访问者的访问visit（this）方法。
   访问者提供一个“访问”的方法，入参是元素。
   实际就是元素接受访问后调用他自己的方法，只不过把调用自己的逻辑分离出去，用另外一个访问者剥离调用自己。    

```java
/**
 * 抽象访问者角色
 * 访问者角色(Visitor):
 * 为该对象结构（OjectSrtuts）中具体元素角色（Subject）声明一个访问操作接口
 */
public interface Visitor {
	public void visit(Subject sub);
}

/**
 * 抽象元素角色
 */
public interface Subject {
	/**
	 * 接受访问者访问
	 */
	public void accept(Visitor visitor);  
    /**
     * 元素自己的特有方法。
     */
    public String getSubject();  
}

/**
 * 访问者角色
 */
public class CustVisitor implements Visitor {
	/**
	 * 访问者访问一个产品后，将被访问的东西拿给自己，然后调用自己的访问方法，决策
	 */
	@Override  
    public void visit(Subject sub) {  
        System.out.println("顾客从购物车上访问--》"+sub.getSubject());  
    }
}

/**
 * 访问者角色
 * 收银员
 */
public class FeeVisitor implements Visitor {

	/**
	 * 访问者访问一个产品后，将被访问的东西拿给自己，然后调用自己的访问方法，决策
	 */
	@Override  
    public void visit(Subject sub) {  
        System.out.println("收银员从购物车上访问--》"+sub.getSubject());  
    }  
}

/**
 * 元素角色
 */
public class MySubject implements Subject {
	String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MySubject(String name) {
		super();
		this.name = name;
	}

	/**
	 * 元素提供一个被访问者访问的方法-接受访问者的访问
	 */
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String getSubject() {
		return this.name;
	}
}

/**
 * 对象结构角色(Object Structure): 
 * 这是使用访问者模式必备的角色. 
 * 它要具备以下特征: 能枚举它的元素; 
 * 可以提供一个高层的接口以允许该访问者访问它的元素; 
 * 可以是一个复合(组合模式)或是一个集合, 如一个列表或一个无序集合.
 * 
 * 它的作用类似于购物车，将商品都放入购物车，也可以从购物车删除
 * 同时提供接收访问者访问的方法，类似 顾客查看，收银员收银前拿起来扫码
 *
 */
public class OjectSrtuts {
	private final List<Subject> ls = new ArrayList<Subject>();
	/**
	 * 加入商品
	 */
	public void addElement(Subject s){
		ls.add(s);
	}
	/**
	 * 删除商品
	 */
	public void removeElement(Subject s){
		ls.remove(s);
	}
	/**
	 * 访问者进入，去访问每个商品（元素）
	 */
	public void accept(final Visitor visitor){
		for (final Subject s : ls) {
			s.accept(visitor);
		}
	}
}

public class Test {
	public static void main(String[] args) {
		OjectSrtuts o = new OjectSrtuts();
		o.addElement(new MySubject("橘子")); // 加入产品
		o.addElement(new MySubject("苹果")); // 加入产品
		
		Visitor visitor = new CustVisitor(); // 顾客
		Visitor visitor1 = new FeeVisitor(); // 收银员

		o.accept(visitor); // 顾客访问
		o.accept(visitor1); // 收银员访问
	}
}
```



## 22、中介者模式（Mediator）

   中介者模式也是用来降低类类之间的耦合的，因为如果类类之间有依赖关系的话，不利于功能的拓展和维护，
   因为只要修改一个对象，其它关联的对象都得进行修改。如果使用中介者模式，只需关心和Mediator类的关系，
   具体类类之间的关系及调度交给Mediator就行。

   中介者模式对象：抽象中介（接口）、中介、抽象同事类（抽象类，内置抽象中介对象）、同事类。
   每个同事类都知道中介对象。
   客户端调用，只需要调用要操作的 同事类 的某方法。
   而这个同事类的方法  去  调用中介者，中介者来协调调用关系。
   同事类不需去关注其他的同事类，只专注于同事类即可。

```java
/**
 * 中介者接口
 * 定义各同时之间需要交互的方法
 */
public interface Mediator {
	public void working();
}

/**
 * 具体的中介者实现对象 了解和维护每个同事对象，并协调交互关系
 */
public class MyMediator implements Mediator {
	private User user1;
	private User user2;
	
	public void setUser1(User user1) {
		this.user1 = user1;
	}

	public void setUser2(User user2) {
		this.user2 = user2;
	}

	public User getUser1() {
		return user1;
	}

	public User getUser2() {
		return user2;
	}

	@Override
	public void working() {
		user2.working();
	}
}

/**
 * 同事类抽象
 * 负责约束同事类的类型
 * 实现具体同事类的公共功能
 * 每个同事类必须持有中介类对象的引用
 */
public abstract class User {
	private Mediator mediator;  
    
    public Mediator getMediator(){  
        return mediator;  
    }  
      
    public User(Mediator mediator) {  
        this.mediator = mediator;  
    }  
  
    public abstract void working();  
}

/**
 * 具体同事类
 * 实现自己的 业务，如需与其它同事类交互，就通知中介对象，中介对象负责后续交互
 */
public class User1 extends User{
    public User1(Mediator mediator) {
		super(mediator);
	}
	@Override
    public void working() {  
    	System.out.println("cd开始工作！");
    	getMediator().working();
    }  
}

/**
 * 具体同事类
 * 实现自己的 业务，如需与其它同事类交互，就通知中介对象，中介对象负责后续交互
 */
public class User2 extends User {
	public User2(Mediator mediator){  
        super(mediator);  
    }  
    @Override  
    public void working() {  
        System.out.println("电脑运行起来了!");  
    }  
}

public class Test {
	public static void main(String[] args) {
		MyMediator mediator = new MyMediator();
		User cd = new User1(mediator);
		User computer = new User2(mediator);
		mediator.setUser1(cd);
		mediator.setUser2(computer);
		cd.working();
	}
}
```



## 23、解释器模式（Interpreter）

   四个角色：抽象表达式、终结符表达式、非终结符表达式、上下文。
   扩展只需扩展非终结符表达式即可。
   其实就是给定一个表达式，传入参数，通过解释器 递归自调用，将值传入计算。

```java
/**
 * 环境角色，存放各参数具体值
 * 存放各终结符的具体指，也可以用map直接替代本类
 */
public class Context {
	private int num1;
	private int num2;

	public Context(int num1, int num2) {
		this.num1 = num1;
		this.num2 = num2;
	}

	public int getNum1() {
		return num1;
	}

	public void setNum1(int num1) {
		this.num1 = num1;
	}

	public int getNum2() {
		return num2;
	}

	public void setNum2(int num2) {
		this.num2 = num2;
	}
}


/**
 * 抽象表达式角色
 */
public interface Expression {
	public int interpret(Context context);
}

/**
 * 实现抽象表达式角色
 */
public class Minus implements Expression {
	@Override
	public int interpret(Context context) {
		return context.getNum1() - context.getNum2();
	}
}

/**
 * 实现抽象表达式角色
 */
public class Plus implements Expression {
	@Override  
    public int interpret(Context context) {  
        return context.getNum1()+context.getNum2();  
    }  
}

public class Test {
	public static void main(String[] args) {
		// 计算9+2-8的值
		int result = new Minus().interpret((new Context(new Plus()
				.interpret(new Context(9, 2)), 8)));
		System.out.println(result);
	}
}
```



## -----------------------------------------



## 24、线程池模式

1、线程池管理器（ThreadPool）：用于创建并管理线程池，包括 创建线程池，销毁线程池，添加新任务；

2、工作线程（PoolWorker）：线程池中线程，在没有任务时处于等待状态，可以循环的执行任务；

3、任务接口（Task）：每个任务必须实现的接口，以供工作线程调度任务的执行，它主要规定了任务的入口，

​      任务执行完后的收尾工 作，任务的执行状态等；

4、任务队列（taskQueue）：用于存放没有处理的任务。提供一种缓冲机制。



Executors提供四种线程池，分别为：

1、Executors.newCachedThreadPool创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。

2、Executors.newFixedThreadPool 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。

3、Executors.newScheduledThreadPool 创建一个定长线程池，支持定时及周期性任务执行。

4、Executors.newSingleThreadExecutor 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。



### CachedThreadPoolTest

```java
/**
 * 创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
 * 线程池为无限大，当执行第二个任务时第一个任务已经完成，会复用执行第一个任务的线程，而不用每次新建线程。
 */
public class CachedThreadPoolTest {
	public static void main(String[] args) {
		ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
		for (int i = 0; i < 10; i++) {
			final int index = i;
			try {
				Thread.sleep(index * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			cachedThreadPool.execute(new Runnable() {
				public void run() {
					System.out.println(index);
				}
			});
		}
	}
}
```

### FixedThreadPoolText

```java
/**
 * 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
 * 因为线程池大小为3，每个任务输出index后sleep 2秒，所以每两秒打印3个数字。
 * 定长线程池的大小最好根据系统资源进行设置。
 * 如Runtime.getRuntime().availableProcessors()
 */
public class FixedThreadPoolText {
	public static void main(String[] args) {
		System.out.println(Runtime.getRuntime().availableProcessors());
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
		for (int i = 0; i < 10; i++) {
			final int index = i;
			fixedThreadPool.execute(new Runnable() {
				public void run() {
					try {
						System.out.println(index);
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
}
```

### ScheduledThreadPoolTest

```java
/**
 * 创建一个定长线程池，支持定时及周期性任务执行。
 * 
 */
public class ScheduledThreadPoolTest {
	public static void main(String[] args) {
		schedule();
		scheduleAtFixedRate();
	}

	/**
	 * 延迟执行示例代码 表示延迟3秒执行。
	 */
	public static void schedule() {
		ScheduledExecutorService scheduledThreadPool = Executors
				.newScheduledThreadPool(5);
		scheduledThreadPool.schedule(new Runnable() {
			public void run() {
				System.out.println("delay 3 seconds");
			}
		}, 3, TimeUnit.SECONDS);
	}

	/**
	 * 表示延迟1秒后每3秒执行一次。
	 */
	public static void scheduleAtFixedRate() {
		ScheduledExecutorService scheduledThreadPool = Executors
				.newScheduledThreadPool(5);
		scheduledThreadPool.scheduleAtFixedRate(new Runnable() {
			public void run() {
				System.out
						.println("delay 1 seconds, and excute every 3 seconds");
			}
		}, 1, 3, TimeUnit.SECONDS);
	}
}
```

### SingleThreadExecutorTest

```java
/**
 * 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行
 * 结果依次输出，相当于顺序执行各个任务。
 */
public class SingleThreadExecutorTest {
	public static void main(String[] args) {
		ExecutorService singleThreadExecutor = Executors
				.newSingleThreadExecutor();
		for (int i = 0; i < 10; i++) {
			final int index = i;
			singleThreadExecutor.execute(new Runnable() {
				public void run() {
					try {
						System.out.println(index);
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
}
```



## 25、并发型模式

就是解决并发  使用队列等。

后来扩展的并发型模式有：

- 主动对象模式（Active Object）
- 阻碍模式（Balking）
- 双重检查锁定（Double Checked Locking）
- 守卫模式（Guarded）
- 领导者/追随者模式（Leaders/Followers）
- 监测对象模式（Monitor object）
- 读写锁模式（Read write lock）
- 调度模式（Scheduler）
- FIFO first in first out
- LIFO last in first out

## 26、阻塞队列

BlockingQueue，线程安全的

- offer(obj)，可容纳返回true，否则false，不阻塞

- offer(E o, long timeout, TimeUnit unit)，可以设定等待的时间

- put(anObject)，阻断

- poll(time)，取走BlockingQueue里排在首位的对象,若不能立即取出,则可以等time参数规定的时间,取不到时返回null

- poll(long timeout, TimeUnit unit)

- take()，取走BlockingQueue里排在首位的对象，若BlockingQueue为空,阻断

- drainTo()，一次性从BlockingQueue获取所有可用的数据对象

### BlockingQueue成员详细介绍

- ArrayBlockingQueue 基于数组的阻塞队列实现

- LinkedBlockingQueue 基于链表的阻塞队列

- DelayQueue DelayQueue中的元素只有当其指定的延迟时间到了，才能够从队列中获取到该元素。插不阻，取阻

- PriorityBlockingQueue 基于优先级的阻塞队列，阻塞取数据。插不阻，取阻，所以生产不能大于消费，否则耗尽堆栈

- SynchronousQueue 一种无缓冲的等待队列

|      | 抛出异常  | 特殊值   | 阻塞   | 超时                 |
| ---- | --------- | -------- | ------ | -------------------- |
| 插入 | add(e)    | offer(e) | put(e) | offer(e, time, unit) |
| 移除 | remove()  | poll()   | take() | poll(time, unit)     |
| 检查 | element() | peek()   | 不可用 | 不可用               |

其中只有 put 和 take 是阻塞的。

生产者与消费者就用的这个阻塞。

ConcurrentLinkedQueue 是非阻塞队列，线程安全的，支持高并发。

它使用 add 和 poll 方法，如果队列里没有值，返回null。

### BlockingQueue

```java
/**
 * 只有take和put是阻塞方法
 */
public class BlockingQueueTe {
	public static void main(String[] args) {
		BlockingQueue q = new ArrayBlockingQueue(100);
		Producer p = new Producer(q);
		Consumer c1 = new Consumer(q);
		Consumer c2 = new Consumer(q);
		/*new Thread(p).start();
		new Thread(c1).start();
		new Thread(c2).start();*/
		ExecutorService service = Executors.newCachedThreadPool();
		service.submit(p);
		service.submit(c1);
		service.submit(c2);
	}
}
/**
 * 生产者
 */
class Producer implements Runnable {
	private final BlockingQueue queue;
	Producer(BlockingQueue q) {
		queue = q;
	}
	public void run() {
		try {
			while (true) {
				queue.put(produce());
				System.out.println("生产一个");
			}
		} catch (InterruptedException ex) {
			System.out.println(123);
		}
	}
	Object produce() {
		return "product";
	}
}

/**
 * 消费者
 */
class Consumer implements Runnable {
	private final BlockingQueue queue;
	Consumer(BlockingQueue q) {
		queue = q;
	}
	public void run() {
		try {
			while (true) {
				consume(queue.take());
				System.out.println("消费一个");
			}
		} catch (InterruptedException ex) {
			System.out.println(123);
		}
	}
	void consume(Object x) {
		System.out.println(x);
	}
}
```

### ConcurrentLinkedQueue

```java
/**
 * ConcurrentLinkedQueue
 * 非阻塞队列，先进先出，线程安全，如果队列没有数据返回null
 */
public class ConcurrentLinkedQueueTe {

	public static void main(String[] args) {
		Queue q = new ConcurrentLinkedQueue<Object>();
		Producer1 p = new Producer1(q);
		Consumer1 c1 = new Consumer1(q);
		Consumer1 c2 = new Consumer1(q);
		/*new Thread(p).start();
		new Thread(c1).start();
		new Thread(c2).start();*/
		ExecutorService service = Executors.newCachedThreadPool();
		service.submit(p);
		service.submit(c1);
		service.submit(c2);
	}
}
/**
 * 生产者
 */
class Producer1 implements Runnable {
	private final Queue queue;
	Producer1(Queue q) {
		queue = q;
	}
	public void run() {
		try {
			while (true) {
				queue.add(produce());
				System.out.println("生产一个");
			}
		} catch (Exception ex) {
			System.out.println(123);
		}
	}
	Object produce() {
		return "product";
	}
}

/**
 * 消费者
 */
class Consumer1 implements Runnable {
	private final Queue queue;
	Consumer1(Queue q) {
		queue = q;
	}
	public void run() {
		try {
			while (true) {
				consume(queue.poll());
				System.out.println("消费一个");
			}
		} catch (Exception ex) {
			System.out.println(123);
		}
	}
	void consume(Object x) {
		System.out.println(x);
	}
}
```



# 四、java编程思想

 一、抽象、封装、继承、多态

​      private和final的不能被子类重写，构造方法默认是static的 也不可以重新。

​      static的方法也不可以被子类重新。

​      protect是受保护的，只有他的子类可以访问到他。

​      属性：父类和子类都是由解释器管理，独立分配内存，通过super调用，不是多态的。不能重写。

二、IS-A 继承、 LIKE-A实现接口

三、RTTI （RUN TIME TYPE INFOMATION）

运行时类型信息。就是类的getClass()方法，它能返回类的信息。

- 1.一种是“传统的”RTTI，它假定我们在编译时已经知道了所有的类型，比如Shape s = (Shape)s1；

- 2.另一种是“反射”机制，它运行我们在运行时发现和使用类的信息，即使用Class.forName()。

- 3.其实还有第三种形式，就是关键字instanceof，它返回一个bool值

而如果用==或equals比较实际的Class对象，就没有考虑继承，它或者是这个确切的类型，或者不是

```java
Class c = null;
try {
    c = Class.forName("rtti.FancyToy"); // 必须是全限定名（包名+类名）
} catch(ClassNotFoundException e) {
    System.out.println("Can't find FancyToy");
    System.exit(1);
}
```

Java还提供了  类字段常量。String.class. 简单、安全。创建引用不自动初始化Class，初始化被延迟到了对静态方法（构造器隐式的是静态的）

或者非final静态域（注意final静态域不会触发初始化操作）进行首次引用时才执行，而使用Class.forName时会自动的初始化。



为了使用类而做的准备工作实际包含三个步骤：

   - 加载：由类加载器执行。查找字节码，并从这些字节码中创建一个Class对象
   - 链接：验证类中的字节码，为静态域分配存储空间，并且如果必需的话，将解析这个类创建的对其他类的所有引用。
   - 初始化：如果该类具有超类，则对其初始化，执行静态初始化器和静态初始化块。

```java
Class<?> c = Class.forName(args[0]);
Method[] methods = c.getMethods();
Constructor[] ctors = c.getConstructors();
```

   四、 即时编译器技术：JIT

   五、组合和继承，选择 has-a is-a，如果需要向上转型就是继承，否则用组合has-a

   六、final关键字，禁止方法被覆盖

   七、内部类：操纵外部类的属性

   八、运行javap -c class文件    查看字节码

   九、序列化：实现Externalizable代替实现Serializable接口来对序列化过程进行控制
    

# 五、J2EE中的13种核心技术规范

JDBC，JNDI，EJB，RMI，JSP，Java servlets，XML，JMS，Java IDL，JTS，JTA，JavaMail 和 JAF

| 规范           | 说明                                                         |
| -------------- | ------------------------------------------------------------ |
| JDBC           | Java Database Connectivity，JDCB对数据库的访问，具有平台无关性 |
| JNDI           | Java Name and Directory Interface，JNDI API被用于执行名字和目录服务<br>它提供了一致的模型来存取和操作企业级的资源如DNS和LDAP，本地文件系统，或应用服务器中的对象。 |
| EJB            | Enterprise JavaBean，提供了一个框架来开发和实施分布式商务逻辑 |
| RMI            | Remote Method Invoke，正如其名字所表示的那样，RMI协议调用远程对象上方法 |
| Java IDL/CORBA | 在Java IDL的支持下，开发人员可以将Java和CORBA集成在一起      |
| JSP            | Java Server Pages， JSP页面由HTML代码和嵌入其中的Java代码所组成 |
| Java Servlet   | Servlet是一种小型的Java程序，它扩展了Web服务器的功能         |
| XML            | Extensible Markup Language，XML是一种可以用来定义其它标记语言的语言 |
| JMS            | Java Message Service，JMS是用于和面向消息的中间件相互通信的应用程序接口（API） |
| JTA            | Java Transaction Architecture，JTA定义了一种标准的API，应用系统由此可以访问各种事务监控 |
| JTS            | Java Transaction Service，JTS是CORBA OTS事务监控的基本的实现 |
| JavaMail       | JavaMail是用于存取邮件服务器的API，它提供了一套邮件服务器的抽象类 |
| JAF            | JavaBeans Activation Framework，JavaMail利用JAF来处理MIME编码的邮件附件 |



# 六、泛型

K， 键

V， 值

E， 元素，枚举

T， 类型，Type

## 1.类泛型

```java
// 类名定义后指定泛型<T>
public static class Test <T> {  
    private T ob; // 定义泛型成员变量
    public Test(T ob) {  
        this.ob = ob;  
    }  
}
// new 的时候  需要指定泛型  
// new Test<Integer>(55);
```



## 2.方法泛型

当方法操作的引用数据类型不确定时，可将泛型定义在方法上。
要定义泛型方法，只需将泛型参数列表置于返回值之前。

```java
public <T> void f(T x){  
    System.out.println(x.getClass().getName());  
}
```

​    

### 2.1.可变参数与泛型方法

T... args, ...可以传多个参数

```java
public static <T> List<T> makeList(T... args){  
    List<T> result = new ArrayList<T>();  
    for(T item:args)  
        result.add(item);  
    return result;         
}

public static void main(String[] args) {  
    List ls = makeList("A");  
    System.out.println(ls);  
    ls = makeList("A","B","C");  
    System.out.println(ls);  
    ls = makeList("ABCDEFGHIJKLMNOPQRSTUVWXYZ".split(""));  
    System.out.println(ls);  
}  
```



## 3.通配类型

可以解决当具体类型不确定的时候，这个通配符就是 ?  

当操作类型时，不需要使用类型的具体功能时，只使用Object类中的功能

```java
List<?> list= new ArrayList<>();
```



## 4.上下边界

上限：？extends E：可以接收E类型或者E的子类型对象。

下限：？super E：可以接收E类型或者E的父类型对象。

```java
public static void getUpperNumberData(List<? extends Number> temp){
	System.out.println("class type :" + temp.getClass());
}
```



## 5.复杂泛型

```java
class ThreeTuple2<A,B,C>{
}

public class TupleList<A,B,C> extends ArrayList<ThreeTuple2<A,B,C>>{
}
```

不能创建泛型数组

泛型的类型参数只能是类类型（包括自定义类），不能是简单类型（基本数据类型）。

​      

   

   