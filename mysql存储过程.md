# Hello mysql Procedure

```mysql
drop procedure if exists cwtest;

DELIMITER $$
CREATE PROCEDURE cwtest (IN pname VARCHAR (50)) 
	BEGIN	#这个是定义临时表  用一个begin
	drop TEMPORARY table  if exists my_tmp;
	create TEMPORARY table my_tmp
	(id varchar(200),
	name varchar(200)
	) engine = memory;
	
	
	begin	#这是定义参数  用了一个begin
	DECLARE  v_alg_id VARCHAR ( 200 );
	DECLARE  v_alg_name VARCHAR ( 200 );
	
	#定义一个结束循环的标识，初始为0
	declare done int default 0;
	
	declare v_num int default 0;
	
	#定义一个游标，可以在这传入参数
	DECLARE my_cursor CURSOR FOR SELECT alg_id, alg_name FROM	t_algorithm;
	
	#定义一个语句，如果继续处理句柄没有找到，说明游标已经循环结束，这是done=1
	declare continue HANDLER for not found set done =1;
	
	
	
	#打开游标   游标循环之前一定要打开
	OPEN my_cursor;
	#定义一个标识位，loop会一直循环，所以要手动退出
	end_loop1:LOOP
	#取出游标内的内容存入临时变量
	FETCH my_cursor INTO v_alg_id,v_alg_name;
		
	  if done = 1 #如果游标已经循环结束
		  then leave end_loop1;  #退出loop1
		end if;
		
		set v_num = v_num+1;
		#将内容插入临时表
		insert into my_tmp(id,name) values (v_alg_id,v_alg_name);
		
	END LOOP;
	
	#select '123' 相当于java的system.out.print 只不过是用表表示出来而已
	select * from my_tmp;
	CLOSE my_cursor;
	select v_num;
	
	END; 	#这是结束上面参数定义的
END$$		#这是结束整个函数的

DELIMITER;	#还原分隔符为分号



#调用
call cwtest('123'); 
```



# mysql 存储过程



## 一、创建、删除、调用

- 创建

  ```mysql
  DELIMITER $$ #修改分隔符
  CREATE PROCEDURE test(IN pname VARCHAR(50)) #括号里是入参、IN代表传入的、OUT代表传出的、INOUT代表既可以传入也可以传出。
    BEGIN
      select pname; #存储过程中的语句用;结尾
    END$$ #存储过程本身用修改后的分隔符$$结尾
  DELIMITER ; ##还原分隔符
  ```

  begin、end之间是存储的执行的代码块。

　　　tips：mysql不支持匿名块（直接写begin、end执行一段sql），因此代码块只能放在存储过程，自定义函数，触发器中。

- 删除

  ```mysql
  DROP PROCEDURE IF EXISTS test; #存储过程
  ```

  和表一样建立前确保数据库中没有同名存储过程。

- 调用

  ```mysql
  CALL test('xx');
  ```

  调用用call命令、这里参数传一个字符串进去、调用后显示xx。

 

## 二、声明变量、变量赋值

- declare声明变量

- set是直接赋值

- select是将查询的结果进行赋值

  ```mysql
  DROP PROCEDURE IF EXISTS test;
  DELIMITER $$
  CREATE PROCEDURE test(IN pname VARCHAR(50))
    BEGIN
      DECLARE prefix VARCHAR(20); #声明变量
      set prefix = 'nihao:'; #select 'nihenhao:' into prefix; 赋值
      select concat(prefix,pname); #concat函数：拼接2个字符串
    END$$
  DELIMITER ;
  CALL test('xx');：赋值方式有2种、根据需求选择。
  ```

 

## 三、声明游标

- 准备一个测试表

  ```mysql
  TRUNCATE names;
  create table names
  (
    name VARCHAR(20),
    age int
  );
  INSERT into names VALUES ('lby',45);
  INSERT into names VALUES ('lala',23);
  ```

- 游标可以理解为是一个带指针的结果集。

  ```mysql
  DROP PROCEDURE IF EXISTS test;
  DELIMITER $$
  CREATE PROCEDURE test()
    BEGIN
      DECLARE name VARCHAR(20);
      DECLARE age int;
      DECLARE temp VARCHAR(50);
      DECLARE c1 CURSOR FOR select * from names; #声明游标
      OPEN c1; #使用游标前打开游标
      FETCH c1 INTO name,age; #将游标中的第一行依次付给name，age（name=lby,age=45）。tips：游标列数和变量数要相同。
      set temp = CONCAT(name,age);
      FETCH c1 INTO name,age; #第一次fetch后指针下移动，赋值第二行给name，age（name=lala,age=23）。
      CLOSE c1; #使用游标后关闭游标
      set temp = CONCAT(temp,CONCAT(name,age));
      SELECT temp from dual; #lby45lala23
    END$$
  DELIMITER ;
  
  CALL test();
  ```

 

## 四、声明异常处理器

- 当sql执行时报错时会报出相应的sqlstate，根据不同的sqlstate我们可以给出不同的处理。--附一个sqlstate的详解：[blog.csdn.net/u014653854/article/details/78986780](https://blog.csdn.net/u014653854/article/details/78986780)

- 处理器有2种：exit、continue。

  ```mysql
  TRUNCATE names; #清空names表使fetch报错SQLSTATE'02000'
  DROP PROCEDURE IF EXISTS test;
  DELIMITER $$
  CREATE PROCEDURE test()
    BEGIN
      declare age int default 0 ;
      declare name VARCHAR(20) default 0 ;
      declare flag int default 0;
      DECLARE c1 CURSOR FOR select * from names;
      declare CONTINUE HANDLER FOR SQLSTATE '02000' SET flag = 111;  # continue handler 检测报错为02000的时候 将flag设为111，存储过程继续执行，如果换为exit handler在行为完处理语句set flag=111后直接退出存储过程。
      OPEN c1;
      FETCH c1 into name,age; #当执行这句时会报错sqlstate:02000 ，continue handler触发set flag= 111,继续执行后面的代码。
      CLOSE c1;
      select flag; #111
    END$$
  DELIMITER ;
  CALL ifpay_ccpay.test();
  ```

 

## 五、判断

- if判断

  ```mysql
  DROP PROCEDURE IF EXISTS test;
  DELIMITER $$
  CREATE PROCEDURE test(in age int)
    BEGIN
      declare flag VARCHAR(20);
      IF age = 1 THEN SET flag = '一';
      ELSEIF age = 2 THEN SET flag = '二';
      ELSE SET flag = 'I DONT KNOW';
      END IF; #结束标志
      select flag; #二
    END$$
  DELIMITER ;
  CALL ifpay_ccpay.test(2);
  ```

 

## 六、循环

循环有三种

- while循环

  ```mysql
  DROP PROCEDURE IF EXISTS test;
  DELIMITER $$
  CREATE PROCEDURE test()
    BEGIN
      DECLARE age int DEFAULT 0;
      mywhile:
      WHILE age < 5 #条件，
      DO
        set age = age+1; #do与endwhile之间是循环内容
      END WHILE ;
      SELECT age; #5
    END$$
  DELIMITER ;
  CALL test();
  ```

   

- repeat循环

  ```mysql
  DROP PROCEDURE IF EXISTS test;
  DELIMITER $$
  CREATE PROCEDURE test()
    BEGIN
      DECLARE age int DEFAULT 0;
      mywhile:
      repeat
       set age = age+1; #循环体
      UNTIL age>3 END REPEAT ; #条件
      SELECT age; #4
    END$$
  DELIMITER ;
  CALL test();
  ```

   

- loop循环

  ```mysql
  DROP PROCEDURE IF EXISTS test;
  DELIMITER $$
  CREATE PROCEDURE test()
    myproc: #存储过程的label
    BEGIN
      DECLARE age int DEFAULT 0;
      DECLARE count int DEFAULT 10;
      myloop: #loop的label
      LOOP
        set age = age +1;
        IF age < 5 then
          ITERATE myloop; #忽略后面代码继续执行。(continue)
        END IF;
        set count = count - 1; 
        SELECT 'haha'; 
        LEAVE myloop; #离开循环。(break) leave也可直接用于存储过程本身myproc,如果这里换成myproc,则显示haha
      END LOOP;
      SELECT age,count; #5    9
    END$$
  DELIMITER ;
  CALL test();
  ```

　　　

```
代码中有3个概念：label、iterate、leave

　　　label：while、repeat、loop循环或者 存过过程本身begin关键字前面都可以加一个名称标签。　　

　　　iterate：用于循环、继续当前循环，相当于java循环中的continue。

　　　leave：1.用于循环、离开当前循环，相当于java循环中的break。2.也可用于存过过程本身，直接离开存过过程。

 
```

```
循环小结：1.while repeat区别在于:while 先判断循环条件再循环,repeat 先循环后判断。

　　　　　2.loop没有结束条件，需要手动退出。

　　　　　3.iterate、leave也可以用于while、repeat。
```

 

## 七、使用心得

1. 存储过程中慎用delete语句。
2. 存储过程适合一些有规律的数据操作，尽量不要用它在生产中跑业务。


## 八、常用方法

```sql
--使用,连接
SELECT GROUP_CONCAT(concat('''',id,'''')) FROM `my_table` where name LIKE '%abc%'

--查询变量值
show variables like 'group_concat_max_len';

--设置全局变量和会话变量值
SET GLOBAL group_concat_max_len = 4294967295;
SET SESSION group_concat_max_len = 4294967295;

--显示所有变量
show variables;
```

