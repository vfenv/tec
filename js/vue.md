# 一、概述

## 1.前端涉及技术

css预处理器：

SASS基于Ruby 速度快，难度大

LESS：基于nodejs。相对简单

javascript：ecmascript

原生js

## 2.ECMAScript版本

es3

es4

es5（全浏览器支持）

es6（最常用，但是非主流浏览器不支持，需要使用webpack打包成es5的版本，全浏览器就都支持了）

es7

es8

es9（草稿）

## 3.常用JS

typescript：微软开发，js的超集。添加了可选的静态类型和基于类的面向对象编程。

jquery：简化dom操作，缺点频繁操作dom，效率不高

angular（google）：java程序员开发，mvc搬到前台，使用typescript开发的，增加了模块化

react（facebook）：提出了虚拟dom，用于减少真实dom的操作，在内存中模拟dom操作，提高渲染效率，jsx语言开发的

vue：渐进式的js框架，逐步实现新特性，模块，路由，状态管理，模块化和虚拟dom

axios：前端通信框架，ajax。



## 4.常用的ui框架

ant-design 阿里

elementui 饿了么

bootstrap twitter

amazeui 

## 5.js构建工具

Babel：js编译工具

webpack：模块打包工具，打包，压缩，合并以及按序加载



## 6.web2.0

html css js放在cdn上，使用ajax访问web server

MVVM model view viewmodel

templates、controllers、models

缺点：代码不可复用，全异步，性能一半，SPA不能满足所有需求

## 7.基于ajax带来的spa时代

前后端分离，基于ajax

## 8.前后端分离的演进

为了降低前端开发的复杂度，涌现了大量的前端框架，如：angularjs、react、vue、ember js等，这些框架总的原则是先按类型分层，比如：templates、controllers、models，然后层内做切分。

templates：组件就是一个模板

models：数据交互

controllers：后台返回一个数字，前台根据返回内容响应

部署相对独立。

缺点是代码不能复用，后端要做优化。

性能可能不是最佳的。

不能满足所有。



## 9.node.js全栈时代

front-end：既可以用nodejs写前端

back-end：也可以用node写后端

mvc模型：

mvp模型：过度阶段

mvvm模式：model-view-viewmodel，核心是viewmodel层，负责转换model中的数据，它把数据和页面元素进行一个双向的绑定。

view中有一个model（后台业务逻辑和数据库），viewmodel将它俩绑定起来。

## 10.为什么使用MVVM

- 低耦合
- 可复用
- 独立开发
- 可测试

viewmodel封装的数据模型包括视图的状态和行为，而model层的数据模型只包含状态。

- 页面这一块展示什么，哪一块展示什么属于视图的状态

- 页面加载进来时发生什么，点击发生什么，滚动发生什么都属于视图的行为。

## 11.vue简介

一个渐进式框架，按需加载。

视图层view：dom

模型层：javascript objects

viewmodel：dom监听+数据绑定

vue不允许数据和视图直接通信，只能通过viewmodel来通信，而viewmodel就是定义了一个observer观察者



为什么用vue？

压缩版只有30+kb，适合移动端，支持touch事件，吸取了模块化和虚拟dom的长处，比如计算属性；开源，社区活跃。

二大核心要素

- 数据驱动：当观察到有事件，触发相关事件，触发虚拟dom的变更，更改页面显示。
- 组件化：每个独立的可简化的区域视为一个组件，组件可自由搭配

## 12.ES6的基本语法

### 1.变量声明const和let

我们都是知道在ES6以前，var关键字声明变量。无论声明在何处，都会被视为声明在函数的最顶部(不在函数内即在全局作用域的最顶部)。这就是函数**变量提升**例如:

```js
function aa() {
	if(bool) {
		var test = 'hello man'
	} else {
		console.log(test)
	}
}
```

以上的代码实际上是：

```js
function aa() {
    var test // 变量提升
    if(bool) {
        test = 'hello man'
    } else {
        //此处访问test 值为undefined
        console.log(test)
    }
    //此处访问test 值为undefined
}
```

所以不用关心bool是否为true or false。实际上，无论如何test都会被创建声明。

接下来ES6主角登场：
我们通常用let和const来声明，let表示**变量**、const表示**常量**。let和const都是块级作用域。怎么理解这个块级作用域？

- 在一个函数内部
- 在一个代码块内部

**{}大括号内**的代码块即为let 和 const的作用域。

看以下代码：

```js
function aa() {
    if(bool) {
       let test = 'hello man'
    } else {
        //test 在此处访问不到
        console.log(test)
    }
}
```

```js
const name = 'lux'
name = 'joe' //再次赋值此时会报错
```

说一道面试题

```js
var funcs = []
for (var i = 0; i < 10; i++) {
    funcs.push(function() { console.log(i) })
}

funcs.forEach(function(func) {
    func()
})
```

这样的面试题是大家常见，很多同学一看就知道输出 10个10
但是如果我们想依次输出0到9呢？两种解决方法：

```javascript
// ES5可以利用闭包解决这个问题
var funcs = []
for (var i = 0; i < 10; i++) {
    funcs.push(
        function(value) {
            return function() {
                console.log(value)
            }
        }(i)
    )
}

// es6,因为let没有提升作用，所以i的值就是当前循环时i的值。这样就是对的
var funcs = []
for (let i = 0; i < 10; i++) {
    funcs.push(function() {
        console.log(i)
    })
}
```



### 2.模板字符串${}

es6模板字符简直是开发者的福音啊，解决了ES5在字符串功能上的痛点。

第一个用途，基本的字符串格式化。将表达式嵌入字符串中进行拼接。

用==${}==来界定。

```javascript
//es5 
var name = 'lux'
console.log('hello' + name)

//es6
const name = 'lux'
console.log(`hello ${name}`) //hello lux
```

第二个用途，在ES5时我们通过反斜杠(\)来做多行字符串或者字符串一行行拼接。ES6反引号(``)直接搞定。

```php
// es5
var msg = "Hi \
man!"

// es6 使用`` 直接可以包含多行字符串拼接成一个字符
const template = `<div>
<span>hello world</span>
</div>`
```

对于字符串es6当然也提供了很多厉害的方法。说几个常用的。

```javascript
// 1.includes：判断是否包含然后直接返回布尔值
let str = 'hahay'
console.log(str.includes('y'))

// 2.repeat: 获取字符串重复n次
let s = 'he'
console.log(s.repeat(3)) // 'hehehe'

//如果你带入小数, Math.floor(num) 来处理
```



### 3.函数

**函数默认参数**

在ES5我们给函数定义参数默认值

```javascript
function action(num) {
    num = num || 200
    return num
}
```

但细心观察的同学们肯定会发现，num传入为0的时候就是false， 此时num = 200 与我们的实际要的效果明显不一样

ES6为参数提供了默认值。在定义函数时便初始化了这个参数，以便在参数没有被传递进去时使用。

```js
function action(num = 200) {
    console.log(num)
}
```

**箭头函数**

ES6很有意思的一部分就是函数的快捷写法，也就是箭头函数。

箭头函数最直观的三个特点。

- 不需要function关键字来创建函数
- 省略return关键字
- 继承当前上下文的 this 关键字

```javascript
//例如：
[1,2,3].map( x => x + 1 )

//等同于：
[1,2,3].map((function(x){
    return x + 1
}).bind(this))
```

当你的函数**有且仅有**一个参数的时候，是可以省略掉括号的。

```js
var people = name => 'hello' + name
```

多个参数的函数：参数用()包起来，函数内部如果有操作，用 {} 包起来

```javascript
var people = (name, age) => {
    const fullName = 'h' + name
    return fullName
}
```



### 4.拓展的对象功能

对象初始化简写

ES5我们对于对象都是以键值对的形式书写，是有可能出现价值对重名的。例如：

```javascript
function people(name, age) {
    return {
        name: name,
        age: age
    };
}
```

键值对重名，ES6可以简写如下：

```javascript
function people(name, age) {
    return {
        name,
        age
    };
}
```

ES6 同样改进了为对象字面量方法赋值的语法。ES5为对象添加方法：

```javascript
const people = {
    name: 'lux',
    getName: function() {
        console.log(this.name)
    }
}
```

ES6省略==冒号==与 ==function== 关键字：

```js
const people = {
    name: 'lux',
    getName () {
        console.log(this.name)
    }
}
```

ES6 对象提供了Object.assign()这个方法来实现浅复制。Object.assign()可以把任意多个源对象自身可枚举的属性拷贝给目标对象，然后返回目标对象。第一参数即为目标对象。在实际项目中，我们为了不改变源对象。一般会把目标对象传为{}

```javascript
const obj = Object.assign({}, objA, objB)
```



### 5.更方便的数据访问--解构

数组和对象是JS中最常用也是最重要表示形式。为了简化提取信息，ES6新增了**解构**

这是将一个数据结构分解为更小的部分的过程

ES5我们提取对象中的信息形式如下：

```js
const people = {
    name: 'lux',
    age: 20
}
const name = people.name
const age = people.age
console.log(name + ' --- ' + age)
```

是不是觉得很熟悉，没错，在ES6之前我们就是这样获取对象信息的，一个一个获取。现在，解构能让我们从对象或者数组里取出数据存为变量，例如

```js
//对象
const people = {
    name: 'lux',
    age: 20
}
const { name, age } = people
console.log(`${name} --- ${age}`)

//数组
const color = ['red', 'blue']
const [first, second] = color
console.log(first) //'red'
console.log(second) //'blue'
```

### 6.Spread Operator 展开运算符 ...

ES6中另外一个好玩的特性就是Spread Operator 也是三个点儿 ...

接下来就展示一下它的用途。

组装对象或者数组

```js
//数组
const color = ['red', 'yellow']
const colorful = [...color, 'green', 'pink']
console.log(colorful) //[red, yellow, green, pink]
//对象
const alp = { fist: 'a', second: 'b'}
const alphabets = { ...alp, third: 'c' }
console.log(alphabets) //{ "fist": "a", "second": "b", "third": "c"}
```

有时候我们想获取数组或者对象除了前几项或者除了某几项的其他项

```js
//数组
const number = [1,2,3,4,5]
const [first, ...rest] = number
console.log(rest) //2,3,4,5
//对象
const user = {
    username: 'lux',
    gender: 'female',
    age: 19,
    address: 'peking'
}
const { username, ...rest } = user
console.log(rest) //{"address": "peking", "age": 19, "gender": "female"}
```

对于 Object 而言，还可以用于组合成新的 Object 。当然如果有重复的属性名，右边覆盖左边

```javascript
const first = {
    a: 1,
    b: 2,
    c: 6,
}
const second = {
    c: 3,
    d: 4
}
const total = { ...first, ...second }
console.log(total) // { a: 1, b: 2, c: 3, d: 4 }
```

### 7.import 和 export

import导入模块、export导出模块

```js
//全部导入
import people from './example'
//有一种特殊情况，即允许你将整个模块当作单一对象进行导入
//该模块的所有导出都会作为对象的属性存在
import * as example from "./example.js"
console.log(example.name)
console.log(example.age)
console.log(example.getName())
//导入部分
import {name, age} from './example'

// 导出默认, 有且只有一个默认
export default App
// 部分导出
export class App extend Component {};
```

导入的时候有没有大括号的区别：

```coffeescript
1.当用export default people导出时，就用 import people 导入（不带大括号）
2.一个文件里，有且只能有一个export default。但可以有多个export。
3.当用export name 时，就用import { name }导入（记得带上大括号）
4.当一个文件里，既有一个export default people, 又有多个export name 或者 export age时，导入就用 import people, { name, age } 
5.当一个文件里出现n多个 export 导出很多模块，导入时除了一个一个导入，也可以用import * as example
```

### 8. Promise

在promise之前代码过多的回调或者嵌套，可读性差、耦合度高、扩展性低。通过Promise机制，扁平化的代码机构，大大提高了代码可读性；用同步编程的方式来编写异步代码，保存线性的代码逻辑，极大的降低了代码耦合性而提高了程序的可扩展性。

==说白了就是用同步的方式去写异步代码。==

发起异步请求

```js
fetch('/api/todos')
    .then(res => res.json())
    .then(data => ({ data }))
    .catch(err => ({ err }));
```

今天看到一篇关于面试题的很有意思。

```js
setTimeout(function() {
    console.log(1)
}, 0);

new Promise(function executor(resolve) {
    console.log(2);
    for( var i=0 ; i<10000 ; i++ ) {
        i == 9999 && resolve();
    }
    console.log(3);
}).then(function() {
    console.log(4);
});
console.log(5);
```

[Excuse me？这个前端面试在搞事！](https://zhuanlan.zhihu.com/p/25407758)

当然以上promise的知识点，这个只是冰山一角。需要更多地去学习应用。

### 9.Generators

生成器（ generator）是能返回一个**迭代器**的函数。生成器函数也是一种函数，最直观的表现就是比普通的function多了个星号*，在其函数体内可以使用yield关键字,有意思的是函数会在每个yield后暂停。

这里生活中有一个比较形象的例子。咱们到银行办理业务时候都得向大厅的机器取一张排队号。你拿到你的排队号，机器并不会自动为你再出下一张票。也就是说取票机“暂停”住了，直到下一个人再次唤起才会继续吐票。

OK。说说迭代器。当你调用一个generator时，它将返回一个迭代器对象。这个迭代器对象拥有一个叫做next的方法来帮助你重启generator函数并得到下一个值。next方法不仅返回值，它返回的对象具有两个属性：done和value。value是你获得的值，done用来表明你的generator是否已经停止提供值。继续用刚刚取票的例子，每张排队号就是这里的value，打印票的纸是否用完就这是这里的done。

```javascript
// 生成器
function *createIterator() {
    yield 1;
    yield 2;
    yield 3;
}
// 生成器能像正规函数那样被调用，但会返回一个迭代器
let iterator = createIterator();
console.log(iterator.next().value); // 1
console.log(iterator.next().value); // 2
console.log(iterator.next().value); // 3
```

那生成器和迭代器又有什么用处呢？

围绕着生成器的许多兴奋点都与异步编程直接相关。异步调用对于我们来说是很困难的事，我们的函数并不会等待异步调用完再执行，你可能会想到用回调函数，（当然还有其他方案比如Promise比如Async/await）。

生成器可以让我们的代码进行等待。就不用嵌套的回调函数。使用generator可以确保当异步调用在我们的generator函数运行一下行代码之前完成时暂停函数的执行。

那么问题来了，咱们也不能手动一直调用next()方法，你需要一个能够调用生成器并启动迭代器的方法。就像这样子的

```javascript
function run(taskDef) { //taskDef即一个生成器函数
    // 创建迭代器，让它在别处可用
    let task = taskDef();
    // 启动任务
    let result = task.next();
    // 递归使用函数来保持对 next() 的调用
    function step() {
        // 如果还有更多要做的
        if (!result.done) {
            result = task.next();
            step();
        }
    }
    // 开始处理过程
    step();
}
```

> 生成器与迭代器最有趣、最令人激动的方面，或许就是可创建外观清晰的异步操作代码。你不必到处使用回调函数，而是可以建立貌似同步的代码，但实际上却使用 yield 来等待异步操作结束。

### 总结

ES6的特性远不止于此，但对于我们日常的开发开说。这已经是够够的了。还有很多有意思的方法。比如findIndex...等等。包括用set来完成面试题常客数组去重问题。我和我的小伙伴们都惊呆了!



# 二、vue简单使用

## 1. 通过外部引用直接使用vue

```html
<!DOCTYPE html>
<html>
	<head>
		<script src="https://cdn.jsdelivr.net/npm/vue/dist/vue.js" ></script>
	</head>
	<body>
		<div id="app">
			{{message}}
		</div>

		<script>
			var app = new Vue({
				el: "#app",
				data: {
					message:"hello"
				}
			})
		</script>
	</body>
</html>
```

## 2.el: 挂载点

作用范围：el标签命中的标签内部生效，外部不行

是否可以使用其他选择器：可以 例如   el:".app" 是可以生效的，el: "div" 也是可以的，但是常规开发还是建议使用ID选择器

是否使用其他的标签，目前是div：可以，但是不能挂载在html和body上，建议挂载在普通标签上

## 3.data: 数据对象

vue使用的数据都被定义在data中

{{ schoole.name }} 、{{ arr[0] }}、{{ arr[1] }}

## 4.vue获取和操纵元素(Vue指令)

内容绑定：

```js
v-text  设置标签的文本值，缺点全部替换，如果部分替换直接使用{{ message + '!' }}就可以了，不用v-text
v-html  设置html内容，设置普通文本与v-text一样，如果html会解析。
v-on  为元素绑定事件 v-on:click="doit" @click="doit" @dblclick="doit"

vue不建议直接修改dom元素。直接修改数据就更改了dom元素中的内容。通过这种方式大道修改dom的目的。
通过this可以直接拿到data中的元素从而达到修改数据的目的。
```

显示切换，属性绑定：

```
v-show	让元素显示和隐藏 v-show="age>18" true显示，false隐藏，操作的是样式，频繁切换用v-show
v-if	条件判断 这个操作的dom，如果不复核条件从dom中删除
v-bind	未元素绑定属性，简写 <img :src="123" />
		:class="isActive?'active':''"
		:class="{active:isActive}" 使用对象方式来写，active的值是否生效取决于isActive它是不是true，这种写法更加优雅
```

列表循环和表单元素绑定：

```
v-for   <li v-for="item in arr">{{ item }}</li> <li v-for="(item,index) in arr">{{ item.name }}</li>
v-on    补充，调用事件的时候传递参数，传递自定义参数和事件修饰符
		<button value="button" @click='doit(p1,p2)'> 可以直接通过方法调用传参数
		<button value="button" @keyup.enter='sayHi'> .enter就是事件修饰符
		
v-model	获取和设置表单元素的值（双向数据绑定） <input type="text" v-model="message">  v-model将表单和data关联起来
		data:{
			message:'hello'
		}
```



## 2. VUE对象app的生命周期

new Vue()新建vue实例

-> 初始化事件和生命周期

-> beforeCreate( )

-> 初始化注入和校验

-> created( )

判断--------->是否有el选项：

因为要将视图和模型层双向绑定

如果是：和对应的templdate绑定，将template编辑到render( )函数中

如果否：调用vm.$mount( el )函数时，将el外部的html作为template编译。

也就是把data和模板里的{{message}}关联起来

-> beforeMount( )  此时页面还没有任何内容

-> 创建vm.$el并用它替换el

-> mounted( )

-> 挂载完毕

当data被修改时，执行beforeUpdate( )，虚拟dom重新渲染并应用更新，执行updated( )

------>当调用vm.$destroy( )函数时

-> beforeDestroy( )

-> 解除绑定，销毁子组件以及事件监听器

-> 销毁完毕

-> destroyed( )

## 3. 条件渲染

v-if ：只有当指令表达式返回true的时候被渲染。

v-else

v-else-if

```html
<!DOCTYPE html>
<html>
	<head>
		<script src="https://cdn.jsdelivr.net/npm/vue/dist/vue.js" ></script>
	</head>
	<body>
		<div id="app">
			<div v-if="flag==='a'">
			{{message}} a
			</div>
			<div v-else-if="flag==='b'">
			{{message}} b
			</div>
			<div v-else>
			not a/b
			</div>
			
		</div>

		<script>
			var app = new Vue({
				el: "#app",
				data: {
					message:"hello",
					flag:'c'
				}
			})
		</script>
	</body>
</html>
```

## 4.列表渲染

v-for 基于数组来渲染一个列表

```html
<!DOCTYPE html>
<html>
	<head>
		<script src="https://cdn.jsdelivr.net/npm/vue/dist/vue.js" ></script>
	</head>
	<body>
		<div id="app">
			<ul v-for="item in mylist">
				<li>{{item.message}}</li>
			</ul>
		</div>
		<script>
			var app = new Vue({
				el: "#app",
				data: {
					mylist:[
						{message:"first"},
						{message:"second"}
					]
				}
			})
		</script>
	</body>
</html>
```

## 5. 事件处理

v-on：监听DOM事件，并触发一些javascript代码。

其中，方法定义在methods中，如果在methods中想获取data中值使用this，代表的是data。

```html
<!DOCTYPE html>
<html>
	<head>
		<script src="https://cdn.jsdelivr.net/npm/vue/dist/vue.js" ></script>
	</head>
	<body>
		<div id="app">
			<button v-on:click="show">click</button>
		</div>
		<script>
			var app = new Vue({
				el: "#app",
				data: {
					message: "hello"
				},
				methods: {
					show: function(){
						//this代表的就是data
						alert(this.message);
					}
				}
			})
		</script>
	</body>
</html>
```

## 6. Axios实现异步通信

一个开源的可用在浏览器端和NodeJS的异步通信框架，主要功能就是实现Ajax异步通信。特点如下：

- 浏览器中创建XMLHttpRequests
- 从nodejs创建http请求
- 支持promise API，也就是链式编程
- 拦截请求和响应
- 转换请求数据和响应数据
- 取消请求
- 自动转换JSON数据
- 客户端支持防御XSRF（跨站请求伪造）

因为本身VUE是一个视图层框架，不包括ajax通信功能，作者开发了一个vue-resource的插件，不过2.0之后停止更新并推荐axios

==VUE有一个mounted方法，编译好的HTML挂载到页面完成后执行的事件钩子，这个钩子函数中一半会做一些AJAX请求获取数据进行数据初始化，mounted在整个实例中只执行一次==

其中还有一个==v-bind==将元素的属性和元素绑定。

```json
{
	"name": 'baidu',
	"url": 'www.baidu.com'
}
```

```html
<!DOCTYPE html>
<html>
	<head>
		<script src="https://cdn.jsdelivr.net/npm/vue/dist/vue.js" ></script>
		<script src="https://unpkg.com/axios/dist/axios.min.js" ></script>
	</head>
	<body>
		<div id="app">
			<div>姓名：{{info.name}}</div>
			<div>地址：<a v-bind:href="info.url" target="_blank">{{info.url}}</a></div>
		</div>
		<script>
			var app = new Vue({
				el: "#app",				
				data(){
					return{
						info:{
							name: '', //这只是声明，并没有真实数据
							url: ''
						}
					}
				},
				mounted(){
					//会有跨域的问题
					axios.get('http://wthrcdn.etouch.cn/weather_mini?city='+'北京')
					.then(response => this.info = response.data)
				}
			})
		</script>
	</body>
</html>
```



```js
//axios基础语法，其中then内部二个function，第一个会在请求成功之后返回，第二个在请求失败之后执行。
axios.get(地址).then(function(response){},function(err){});
//post与get类似，只不过参数会以对象的形式在第二个参数请求
axios.post(地址, {}).then(function(response){},function(err){});

//axios的then方法获取不到外部的this，可以预先设置一个值
methods:{
    getjoke: function(){
    	var that = this;
        axios.get(地址).then(
            function(response){
                that.jock = response.data;
            },function(err){
                console.log(err);
            }
        );    
    }
}
```

天气查询

```html
<!DOCTYPE html>
<html>
	<head>
		<script src="https://cdn.jsdelivr.net/npm/vue/dist/vue.js" ></script>
		<script src="https://unpkg.com/axios/dist/axios.min.js" ></script>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	</head>
	<body>
		<div id="app">
			<input type="text" v-model:city="city" @keyup.enter="search">
			<button @click="search">查询</button>
			<ul>
				<li v-for="item in arr">{{ item.date }} {{item.low}}~{{item.high}}  风向：{{item.fengxiang}}  阳光{{item.type}}</li>
			</ul>
		</div>
		<script>
			var app = new Vue({
				el: "#app",				
				data:{
					city: '',
					arr: []
				},
				methods:{
					search: function(){
						axios.get('http://wthrcdn.etouch.cn/weather_mini?city=' + this.city).then(response => {
							this.arr=response.data.data.forecast;
						})
					}
				}
			})
		</script>
	</body>
</html>
```

播放器

```html
<!DOCTYPE html>
<html>
	<head>
		<script src="https://cdn.jsdelivr.net/npm/vue/dist/vue.js" ></script>
		<script src="https://unpkg.com/axios/dist/axios.min.js" ></script>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<style>
		html,head,body{    padding: 0; margin: 0;}
		.videoDiv {width: 100%; height: 100%; position: fixed; float: left;}
		.video{position: fixed; left: 20%;width:800px; z-index: 100;}
		#shadow {width: 100%; height: 100%; background: #000; opacity: 65%; z-index: -100;}
		</style>
	</head>
	<body>
		<div id="app">
			<input type="text" v-model:keywords="keywords" @keyup.enter="search">
			<button @click="search">查询</button>
			<div>
				<audio ref="audio" :src="musicUrl" @play="play" @pause="pause" controls autoplay loop></audio>
			</div>
			<div style="display:none;" v-show="isShow" class="videoDiv">
				<video :src="mvUrl" controls="controls" class="video"></video>
				<div id="shadow" @click="close"></div>
			</div>
			<ul>
				<li v-for="item in arr">{{ item }}
				<a href="javascript:void(0);" @click="playMusic(item.id)">播放音频</a>
				<a v-if="item.mvid!=0" href="javascript:void(0);" @click="playMv(item.mvid)">播放MV</a>
				</li>
			</ul>
			
			
		</div>
		<script>
			var app = new Vue({
				el: "#app",				
				data:{
					keywords: '',
					arr: [],
					musicUrl: '',
					mvUrl: '',
					isShow: false
				},
				methods:{
					search: function(){
						axios.get('https://autumnfish.cn/search?keywords=' + this.keywords).then(response => {
							this.arr=response.data.result.songs;
						})
					},
					playMusic: function(musicId){
						axios.get('https://autumnfish.cn/song/url?id=' + musicId).then(response => {
							this.musicUrl=response.data.data[0].url
						})
					},
					playMv: function(mvid){
						axios.get('https://autumnfish.cn/mv/url?id=' + mvid).then(response => {
							this.isShow = true;
							this.mvUrl=response.data.data.url
							
						})
					},
					play: function(){
						console.info('playing...');
					},
					pause: function(){
						console.info('pauseing...');
					},
					close: function(){
						this.mvUrl="";
						this.isShow=false;
					}
				}
			})
		</script>
	</body>
</html>
```



## 7.表单输入绑定

双向数据绑定：

v-model 指令在表单<input> <textarea> <select>元素上创建双向数据绑定。根据控件类型自动选取正确的方法更新元素，是个语法糖。负责监听用户输入事件以更新数据，只是个监听者。

忽略所有表单元素的value、checked、selected、attribute的初始值而将VUE实例的数据作为数据来源。应该在vue的data中初始值。

v-model在内部为不同元素使用不同属性，不同事件：

- text和textarea使用==value==属性和==input==事件
- checkbox和radio使用==checked==和==change==事件
- select使用==value==和==change==事件

==各个控件的使用参考官网==，下面只给了一个text的demo

```html
<!DOCTYPE html>
<html>
	<head>
		<script src="https://cdn.jsdelivr.net/npm/vue/dist/vue.js" ></script>
	</head>
	<body>
		<div id="app">
			<input type="text" v-model="message"/>
			input value is {{message}}
		</div>
		<script>
			var app = new Vue({
				el: "#app",	
				data: {
					message:"abc"
				}
			})
		</script>
	</body>
</html>
```

## 8. VUE组件

组件是可复用的模板

```html
<!DOCTYPE html>
<html>
	<head>
		<script src="https://cdn.jsdelivr.net/npm/vue/dist/vue.js" ></script>
	</head>
	<body>
		<div id="app">
			<ul>
				<!-- 使用组件，通过v:bind将属性和data内的元素进行绑定 -->
				<my-component-li v-for="item in items" v-bind:item="item">{{item}}</my-component-li>
			</ul>
		</div>
		<script>
			//定义一个组件
			Vue.component("my-component-li",{
				props:['item'],
				template: '<li>{{item}}</li>'
			})
			var app = new Vue({
				el: "#app",
				data:{
					items:['a','b','c']
				}
			})
		</script>
	</body>
</html>
```

## 9. 计算属性

将计算结果缓存起来变成一个静态的属性。

这样使用取的时候值不会变，而方法每次取都会重新计算。

直接在{{abc.split(','),join(',')}}中计算会导致页面计算内容太多

```html
<!DOCTYPE html>
<html>
	<head>
		<script src="https://cdn.jsdelivr.net/npm/vue/dist/vue.js" ></script>
	</head>
	<body>
		<div id="app">
			<p>当前时间-使用方法获取：{{getCurrTime()}}</p>
			
			<p>当前时间-使用计算属性获取：{{getCurrTime1}}</p>
		</div>
		<script>
			
			var app = new Vue({
				el: "#app",
				methods:{
					getCurrTime: function(){
						return Date.now();
					}
				},
				computed:{
					getCurrTime1: function(){
						return Date.now();
					}
				}
			})
		</script>
	</body>
</html>
```

## 10. 插槽与自定义事件

vue实现了一套内容分发的API，将<slot>元素作为承载分发内容的出口。

通俗的讲插槽是用来确定子组件位置的。

```html
<!DOCTYPE html>
<html>
	<head>
		<script src="https://cdn.jsdelivr.net/npm/vue/dist/vue.js" ></script>
	</head>
	<body>
		<div id="app">
			<todo>
				<!-- slot属性设置的值，对应的是组件中插槽slot的name属性对应的值 -->
				<todo-title slot="todo-title1" v-bind:title="title"></todo-title>
				<!-- v-bind:title="title" 中v-bind:后面的title对应的是组件的props中的title，后面的title对应的是data中的title -->
				<!-- v-bind:title 直接可以用 :title 来代替 -->
				<todo-items slot="todo-items1" v-for="(it,index) in items" v-bind:item="it" :index="index" @rm="removeIndex"></todo-items>
			</todo>
		</div>
		<script>
			Vue.component("todo",{
				template: '<div><slot name="todo-title1"></slot><ul><slot name="todo-items1"></slot></ul></div>'
			})
			Vue.component("todo-title",{
				props:['title'],
				template: '<div>{{title}}</div>'
			})
			//v-on:click可以用@click替代，效果一样，节省代码
			//组件中的@绑定的控件只能调用组件内定义的methods，不能调用外面的
			//但是可以使用this.$emit('绑定的方法名') 这种方式，在内部方法和外部方法通信
			Vue.component("todo-items",{
				props:['item','index'],
				template: '<li>{{index}}----{{item}}<button @click="remove">删除</button></li>',
				methods:{
					remove:function(index){
						this.$emit('rm',this.index)
					}
				}
			})
			var app = new Vue({
				el: "#app",
				data:{
					title: 'slot槽传递data和参数',
					items:['刘德华', '张学友', '黎明']
				},
				methods:{
					removeIndex: function(index){
						console.info(index)
						this.items.splice(index,1)
					}
				}
			})
		</script>
	</body>
</html>
```



# 三、vue-cli

vue-cli是vue官方提供的脚手架，用于快速的生成一个VUE项目

功能：统一的结构，本地调试，热部署，单元测试，集成打包上线

准备：

- node.js >6.x版本，首选8.x版本

- git

## 1. vue-cli下载地址

http://nodejs.cn/download/

下载16.15版本运行安装,win7只能用12.x，高版本会检查window系统环境

在运行中输入cmd 右键 以管理员身份运行。

```shell
node -v 
npm -v
```

## 2. 安装nodejs加速器cnpm

```shell
#不推荐
npm install cnpm -g  

#推荐
npm install --registry=https://registry.npm.taobao.org

#如果遇到错误，提示没有package.json，需要初始化npm
npm init -y

npm config get userconfig  ## 查看配置文件路径

npm config ls -l  ##　查看所有配置项

npm config get cache  ## 查看缓存配置，get后面可以跟任意配置项

npm config edit  ## 直接编辑config文件，这个会打开文本
```

## 3. 安装vue-cli

```shell
#-g是 global给所有用户都安装
npm install vue-cli -g
#查看有哪些已经安装
vue list
#主要看webpack
```

## 4. 使用webpack创建vue

```shell
cmd D:\vuetest\npm
vue init webpack myproject
```

## 5. 启动项目

```shell
cd myproject

#安装项目所需的依赖
npm install

#执行启动命令，也可使用自定义命令启动 如npm start
npm run dev
```

## 6.vue项目的目录结构

```shell
build和config  放webpack的配置文件
node_modules  放依赖包
src  放源码
	components #存放组件，组件中<style scoped>样式指定当前样式只在当前组件中生效，不对外污染
	main.js 入口函数
		import Vue from 'vue'
		import App from './App'  #这里App.vue后缀可省略
	App.vue	
		<template></template> #这是模板
		export default{ #导出一个组件，别的地方才能引入
			name:'App'			
		}
	assets #存放静态资源图片
static  静态资源
.babelrc 让es6编译成es5的一个babel的配置文件
index.html 首页
package.json  项目的配置文件
	其中package中可以定义项目相关的配置
	也可以自定义一些启动命令
	如start:"npm run dev",启动的时候执行 npm start
	
	dependencies 正式环境的依赖，会比较少
	devDependencies 测试环境的依赖
	
package-lock.json  
```



# 四、webpack

## 1. 模块化

类似maven，js应用程序静态模块打包器（module bundle）。会递归的构建一个一个依赖关系图（dependency graph）。可以将项目打包成一个或多个bundle

例如把jquery.js和bootstrap.js打包成一个bundle.js

万物皆模块，js是，css是，图片等都可以是模块。

## 2. COMMONJS

nodejs遵循commonsjs的规范，该规范的核心思想是通过require方法来同步加载所需依赖的模块，然后exports 或 module.exports来导入需要暴露的接口。

```js
require("module")
require("../module.js")
export doStuff = function(){}
module.exports = someValue
```

优点：模块重用、简单、npm已有超过45万可用模块

缺点：同步阻塞，不能并行加载多个模块

## 3. AMD

asynchronous module definition，一个异步非阻塞，主要接口defin(id?, dependencies?, factory),它在声明的时候指定所有的依赖dependencies，并且还要当做形参传入到factory中，对于依赖的模块提前执行。

```js
define("module", ["dep1","dep2"], function(d1,d2){
    return someExportedValue;
});
require(["module", "../file.js"], function(module, file) {});
```

优点是可以异步加载模块

缺点，不优雅

## 4. CMD

和AMD很相似

```js
define(function(require, exports, module){
    var $ = require("jQuery");
    var Spinning = require("./spinning");
	exports.doSometing = ...;
    module.exports = ...;
});
```

依赖就近，延迟执行，缺点：依赖SPM打包，模块加载逻辑偏重，需要Sea.js 和 coolie

## 5. ES6模块

es6增加了js语言层面的模块体系定义，es6的设计思想尽量静态化，编译时就能确定模块的依赖冠以以及输入输出的变量。

```js
import "jQuery";
export function doStuff(){}
module "localModel" {}
```

优点：容易静态分析，面向未来的 ECMAScript 标准

缺点：原生浏览器端没有实现该标准，全新的命令，新版node支持

实现：babel

期望人模块系统：

可以兼容多种模块风格，尽量利用已有的代码，不仅仅是js模块化，css、图片、字体资源也需要模块化。

## 6. 安装webpack

```shell
npm install webpack webpack-cli -g
```

创建一个项目mywebpack

创建一个modules，里面创建一个入口函数main.js

在modules创建一个hello.js，并把它当成模块来用

```js
//hello.js
exports.sayhi = function(){
	document.write("<div>hello</div>");
}
```

```js
//main.js
var hello = require(".hello");
hello.sayhi();
```

在mywebpack中创建一个webpack的配置文件webpack.config.js

```js
module.exports = {
    entry: "./modules/main.js", //入口文件，指定项目用哪个文件作为入口文件
    output: { //输出，指定webpack把处理完成的文件放置到指定路径
        //path: "", //path可以省略，直接和filename放在一起写就可以了
        filename: "./js/bundle.js"
    }
    /**
    ,module:{ //模块，用于处理各种类型的文件
        loaders:[
            {test: /\.js$/, loader:""}
        ]
    },
    plugins: {}, //插件，如热更新，代码重用等
    resovle: {}, //设置路径指向
    watch: true //监听,页面内容，也不用
    **/	
}
```

去打包

```shell
#进到项目路径内
#执行webpack就可以了

#一般这样用
webpack --watch 
```

打包之后多了一个dist文件夹

使用：

在项目下创建一个index.html

```html
<!DOCTYPE html>
<html>
    <head>
    	<meta charset="utf-8">
        <title></title>
    </head>
    <body>
        <script src="dist/js/bundle.js"></script>
    </body>
</html>
```

# 五、vue-router

vue项目只有组件，不能实现页面跳转，vue-router就是实现页面跳转功能

官方路由管理器，主要功能：

- 嵌套的路由/视图表

- 模块化、基于组件的路由配置

- 路由参数、查询、通配符

- 基于vue过去系统的视图过滤效果

- 细粒度的导航控制

- 带有自动激活的css class的链接

- html5历史模式或hash模式，ie9中自动降级

- 自定义滚动条行为

## 1. vue-router安装

```shell
#如果没有安装，使用下命令安装
#--save-dev就是把我们的环境安装在package.json的devDepencies里
npm install vue-router --save-dev
```

## 2. vue-router使用

在src下面新建一个router文件夹，在新建一个index.js

```js
import Vue from 'vue'
import VueRouter from 'vue-router'
import content from '../components/content'

Vue.use(VueRouter);

export default new Router({
    routers:[{
        path: '/content', // 路由的跳转路径,浏览器里输入这个路径就会跳转到对应的组件里
        name: 'content',  // name作用是类似组件，在渲染指定要渲染的路由<router-vue name="content"></router-vue>
        component: content // 路由跳转的组件
    }]
});

```

routers中name的作用：

```sh
#1.name作用是类似组件，在渲染指定要渲染的路由<router-vue name="content"></router-vue>，这样只加载需要的路由
#2.可以用routers的name传值，$route.name获取组件的name值
#3.用于params传参
var router = new VueRouter({
	routers: [
		{
			path: '/regist',
			name: 'regist',
			component: regist
		}
	]
})
<router-link :to="{name:'regist', params:{id:10, name:'lili'}}">注册</router-link>
```

在components下新建一个content.vue的文件：

```vue
<template>
	<div>我是内容页</div>
	<div>{{ $route.name }}</div>
</template>

<script>
	export default{
        name:'content'
    }
</script>

<style>
</style>
```

在主函数main.js，修改

```js
import Vue from 'vue'
import App from './App'
import vue VueRouter from 'vue-router'
import router from './router'

Vue.use(VueRouter);

Vue.config.productionTip = false

new Vue({
    el: '#app',
    router,
    conponents: { App },
    template: '<App/>'
})
```

app.vue

```html
<!-- 在 <div id='app'>里增加<router-view/> 即可 -->
<div id='app'>
    <router-view/>
</div>
<!-- a标签使用router-link替代 to是a的href-->
<router-link to="/">首页</router-link>
<router-link to="/content">内容页</router-link>
```

# 六、整合ElementUI

vue只关注视图层，没有自己的UI界面，需要整合其他的UI框架，这里整合EUI

```shell
#创建工程
vue init webpack vue-elementui
#进入到项目路径，自己安装一些东西
npm install vue-router --save-dev
#安装ElementUI
npm i element-ui -S
#安装SASS加载器，指定了版本号
#这里会报错，要全局安装一个windows工具
#npm install --global --production windows-build-tools
npm install sass-loader@7.3.1 node-sass --save-dev
#安装所有项目的依赖
npm install
```

**文件夹结构:**

==vue-element==

* src
  * router
    * index.js

* views

  * login.vue

  * main.vue

* .eslintrc.js

* App.vue
* main.js

**main.js:**

```js
import Vue from 'vue'
import App from './App'
import VueRouter from 'vue-router'
import router from './router'
//需要引入elui的样式和控件
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

// 显示的加载router
Vue.use(VueRouter)
// 显示的加载elui
Vue.use(ElementUI)

Vue.config.productionTip = false

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  render: h => h(App) //这个是elui使用方式
})
```

**App.vue:**

```vue
<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

```

**/src/router/index.js:**

```js
import Vue from 'vue'
import Router from 'vue-router'
import Login from '../views/login'
import Main from '../views/main'

Vue.use(Router)

// 把路由暴露出去
export default new Router({
  routes: [
    {
      path: '/login', // 访问路径通过path设置
      name: 'login',
      component: Login
    },
    {
      path: '/main',
      name: 'main',
      component: Main
    }
  ]
})

```

**/src/views/login.vue:**

```vue
<template>
    <div>
    <el-form ref="form" :model="form" :rules="rules" class="login-box">
    <h3>欢迎登录</h3>
      <el-form-item label="账号" prop="name">
        <el-input type="text" placeholder="请输入用户名" v-model="form.name"/>
      </el-form-item>
      <el-form-item label="密码">
        <el-input type="password" placeholder="请输入密码" v-model="form.passwd"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit('form')">提交</el-button>
      </el-form-item>
    </el-form>
    </div>
</template>

<script>
export default {
  name: 'mylogin', // 这个属性也没起什么作用
  data () {
    return {
      form: {
        name: '',
        password: ''
      },
      rules: {
        name: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    onSubmit (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.$router.push('/main')
        } else {
          this.$message({message: 'error', type: 'warning'})
        }
      })
    }
  }
}
</script>

<style scoped>
  .login-box{
    width: 350px;
    margin: 150px auto;
    border: 1px solid #DCDFE6;
    padding: 20px;
    border-radius: 5px;
    box-shadow: 0 0 30px #DCDFE6;
  }
  h3{
    text-align: center;
  }
</style>

```

**/scr/views/main.vue:**

```vue
<template>
    <div>
		我是谁？
    </div>
</template>

<script>
export default {
  name: 'main'
}
</script>

```

此时启动，直接访问localhost:8080/login即可，有表单，有校验。更多更登录ElementUI的官网查看ui的使用。

# 七、嵌套路由

又称子路由，通常由多层嵌套的组件组合成。url中各段动态路径也按照结构对应嵌套的各个组件。

菜单，导航在切换时内容不变，只有目标区域的内容变了，这种就是子路由的应用

修改上面的例子

**/src/views/member/memeberList.vue：**

```vue
<template>
    <div>
		会员列表
    </div>
</template>

<script>
export default {
  name: 'memberList'
}
</script>
```

**/src/views/member/memeberLevel.vue：**

```vue
<template>
    <div>
		会员等级
    </div>
</template>

<script>
export default {
  name: 'memberList'
}
</script>
```

**/src/views/main.vue：**

只是在el-main里增加了一个<router-view/>

```vue
<template>
    <div>
		<el-container>
			<el-aside width="200px">
				<el-menu :default-openeds="['1']">
					<el-submenu index="1">
						<template slot="title"><i class="el-icon-message"></i>会员管理</template>
						<el-menu-item-group>
							<el-menu-item index="1-1">
								<router-link to="/member/list">会员列表</router-link>
							</el-menu-item>
							<el-menu-item index="1-2">
								<router-link to="/member/level">会员等级</router-link>
							</el-menu-item>
						</el-menu-item-group>
					</el-submenu>
					<el-submenu index="2">
						<template slot="title"><i class="el-icon-message"></i>商品管理</template>
						<el-menu-item-group>
							<el-menu-item index="2-1">商品分类</el-menu-item>
							<el-menu-item index="2-2">商品列表</el-menu-item>
						</el-menu-item-group>
					</el-submenu>
				</el-menu>
			</el-aside>
			
			<el-container>
				<el-header style="text-align: right; font-size: 12px">
					<el-dropdown>
						<i class="el-icon-setting" style="margin-right: 15px"></i>
						<el-dropdown-menu slot="dropdown">
							<el-dropdown-item>用户中心</el-dropdown-item>
							<el-dropdown-item>退出</el-dropdown-item>
						</el-dropdown-menu>
						<span>Admin</span>
					</el-dropdown>
				</el-header>
				<el-main>
					<router-view/> <!-------------------11111111111--------------->
				</el-main>
			</el-container>
		</el-container>
    </div>
</template>

<script>
export default {
  name: 'main'
}
</script>

<style scoped>
.el-header{
	background-color: #B3C0D1;
	color: #333;
	line-height: 60px;
}
.el-aside{
	color: #333;
}
</style>

```

**/src/router/index.js：**

增加子路由配置

```js
import Vue from 'vue'
import Router from 'vue-router'
import Login from '../views/login'
import Main from '../views/main'
import MemberList from '../views/member/memberList'
import MemberLevel from '../views/member/memberLevel'

Vue.use(Router)

// 把路由暴露出去
export default new Router({
  routes: [
    {
      path: '/login', // 访问路径通过path设置，name好像是没用
      name: 'login',
      component: Login
    },
    {
      path: '/main', // 访问路径通过path设置，name好像是没用
      name: 'main',
      component: Main,
	  children: [//配置子路由
		{
			path: '/member/list',
   		    name: 'MemberList',
		    component: MemberList
		},
		{
			path: '/member/level',
   		    name: 'MemberLevel',
		    component: MemberLevel
		}
	  ]
    }
  ]
})

```

# 八、路由的参数传递

## 1.通过路径传参

参数放在URL里直接调用：

```js
//设置router路由配置
{
	path: '/member/level/:id',
	name: 'MemberLevel',
	component: MemberLevel
}

//在vue中使用链接地址时通过url传参
<router-link to="/member/level/2">会员等级</router-link>

//使用时
{{$route.params.id}}
```

请求参数放在to属性上 调用URL：

```js
//设置router路由配置 多加了一个参数 :type
{
    path: '/member/list/:type',
    name: 'MemberList',
    component: MemberList
}

//参数不放在url，而是通过绑定到 to属性上，传递参数
<router-link :to="{name: 'MemberList', params:{type: 3}}">会员列表</router-link>

//使用时
{{$route.params.type}}
```



## 2.通过props传参

相对于不在url里传递的，区别在于使用时是不是在 $route.params里面。

```js
//启动prop传参
{
	path: '/member/level/:id',
	name: 'MemberLevel',
	component: MemberLevel,
	props: true // 启动prop传参
}
//调用方式并没有改变，二种都可以
<router-link to="/member/level/2">会员等级</router-link>
<router-link :to="{name: 'MemberLevel', params:{id: 2}}">会员等级</router-link>

//在使用的时候发生了变变化，定义模板时，直接使用id，并且要在export中指定props
<template>
    <div>
        会员等级{{id}}
    </div>
</template>
<script>
export default {
  name: 'memberList',
  props: ['id']
}
</script>
```

## 3. 页面跳转时的变化

```js
//可以跳页面
this.$router.push('/main')
//跳页面时传递参数
this.$router.push({name: 'main', params:{name: this.form.name}})
//接收方
{{$route.params.name}}
```

# 九、重定向

```js
{
    path: '/goMain',
	redirect: '/main'
}

//使用
<router-link to="/goMain">返回首页</router-link>

//想要传递参数
{
    path: '/goMain/:name',
	redirect: '/main/:name'
}
<router-link to="/goMain/chenwei">返回首页</router-link>
```

# 十、路由模式+处理404

默认的路由模式是hash，可以显示的修改成history

```js
export default new Router({
  mode: 'history',
})
```

```vue
{
	path: '*',
	component: NotFound
}

<template>
    <div>
		File not found
    </div>
</template>

<script>
  export default {
    name: 'NotFound'
  }
</script>

```



# 十一、路由钩子和异步请求

路由中的钩子函数：

beforeRouteEnter : 进入路由之前执行

beforeRouteLeave : 离开路由之前执行

安装axios：

```shell
npm install axios -s
```

钩子函数为了异步获取数据，在调用之前，先做一个JSON，钩子请求JSON

/static/my.json

```json
{"name":"chenwei","password":"123456"}
```

/src/main.js

```js
import Vue from 'vue'
import App from './App'
import VueRouter from 'vue-router'
import router from './router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import axios from 'axios'

// 显示的加载router
Vue.use(VueRouter)
// 显示的加载elui
Vue.use(ElementUI)

//使用axios
Vue.prototype.$axios = axios

Vue.config.productionTip = false

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  render: h => h(App)
})

```

memberLevel.vue

```vue
<template>
    <div>
        会员等级{{id}}
    </div>
</template>

<script>
export default {
  name: 'memberList',
  props: ['id'],
  //进入路由之前
  beforeRouteEnter: (to, from, next) => { //to目标组件，from从哪个组件来，next继续执行的函数
	console.info('beforeRouteEnter');
	// 跟doFilter()一样，进入下面逻辑
	next(vm => {
		vm.getData();
	}); 
  },
  //离开路由之前
  beforeRouteLeave: (to, from, next) => {
	console.info('beforeRouteLeave');
	next(); // 跟doFilter()一样，进入下面逻辑
  },
  methods:{
	getData: function(){
		this.$axios({
			method: 'get',
			url: 'http://localhost:8080/static/my.json',
		}).then(function(repos){
			console.log(repos);
			debugger
		}).catch(function(error){
			console.log(error);
		});
	}
  }
}
</script>

```

# 十二、VUEX

## 1.sessionStorage管理登录状态

是一个vue.js状态管理模式。集中管理所有组件状态，并以响应的规则保证状态以一种可预测的方式发生变化。

```sh
#项目根路径安装
npm install vuex@3.6.2 --save
```

main.js

```js
import Vuex from 'vuex'
Vue.use(Vuex);
```

我们利用钩子beforeEach判断是否登录，期间会用到sessionStorage存储功能

beforeEach 是每次跳转之前

login.vue

```js
onSubmit (formName) {
    this.$refs[formName].validate((valid) => {
        if (valid) {
            sessionStorage.setItem('isLogin', true);
            this.$router.push({name: 'main', params:{name: this.form.name}})
        } else {
            this.$message({message: 'error', type: 'warning'})
        }
    })
}
```

main.js

```js
router.beforeEach((to, from, next) => {
	debugger
	let isLogin = sessionStorage.getItem('isLogin');
    if(to.path == '/logout'){
        sessionStorage.clear();
        next({path: '/login'});
    }else if(to.path == '/login'){
        if(isLogin){
            next({path: '/main'});
        }
    }else if(!isLogin){
        next({path: '/login'});
    }
	next()
})
```



## 2.VUEX管理登录状态（操作对象）

类似java操作数据库

/src/store/index.js

```js
import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex);
//全局state对象，用于保存所有组件的公共数据
const state = sessionStorage.getItem('state') && JSON.parse(sessionStorage.getItem('state')).user.name ? JSON.parse(sessionStorage.getItem('state')) : {
    user:{
        name:''
    }
};
//监听state对象值的最新状态（计算属性）
const getters={
    getUser(state){
        return state.user;
    }
};
//唯一一个可以修改state对象值的方法(注意，这个方法是一个同步的方法)
const mutations={
    updateUser(state, user){
        state.user = user;
		if(user){
			sessionStorage.setItem('state',JSON.stringify(state));
		}
    }
};
//异步执行mutations
const actions={
    asyncUpdateUser(context, user){
        context.commit("updateUser",user);
    }
};
export default new Vuex.Store({
    state,
    getters,
    mutations,
    actions
})
```

/src/main.js 主函数引入store

```js
//注意这，虽然只引用了store，实际上在前面已经引用了vuex。
import store from './store'

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})

```

/src/views/login.vue

```js
//this.$store.dispatch("asyncUpdateUser",{name:this.form.name});
methods: {
    onSubmit (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
		  sessionStorage.setItem('isLogin', true);
		  this.$store.dispatch("asyncUpdateUser",{name:this.form.name});
          this.$router.push({name: 'main', params:{name: this.form.name}})
        } else {
          this.$message({message: 'error', type: 'warning'})
        }
      })
    }
  }
```

/src/views/main.vue

```vue
<span>{{$store.getters.getUser.name}}</span>
```



## 3.VUEX使用模块管理登录状态

状态太多会导致管理混乱，将相同的业务抽取出来，单独存放，里面的调用方式换一下

新增一个文件

/src/store/modules/user.js

```js
const user = {
	//全局state对象，用于保存所有组件的公共数据
	state : sessionStorage.getItem('state') && JSON.parse(sessionStorage.getItem('state')).user.name ? JSON.parse(sessionStorage.getItem('state')) : {
		user:{
			name:''
		}
	},
	//监听state对象值的最新状态（计算属性）
	getters : {
		getUser(state){
			return state.user;
		}
	},
	//唯一一个可以修改state对象值的方法(注意，这个方法是一个同步的方法)
	mutations : {
		updateUser(state, user){
			state.user = user;
			if(user){
				sessionStorage.setItem('state',JSON.stringify(state));
			}
		}
	},
	//异步执行mutations 
	actions : {
		asyncUpdateUser(context, user){
			context.commit("updateUser",user);
		}
	}
}

export default user


```

/src/store/index.js

```js
import Vue from 'vue'
import Vuex from 'vuex'
import user from './modules/user'

Vue.use(Vuex);

export default new Vuex.Store({
    modules:{user}
})
```

/src/app.vue

```vue
<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
export default {
  name: 'App',
  mounted(){
    //监听页面刷新事件
	window.addEventListener('unload',this.saveState());
  },
  methods:{
    saveState(){
		sessionStorage.setItem('state',JSON.stringify(this.$store.state.user));
	}
  }
}
</script>

```







# 十三、备注：小问题解决

## 1. 关闭eslint校验和解决格式化冲突

```sh
1.config 中 index.js修改以下代码
useEslint false,
2.在报错行上加上
/* eslint-disable */

3.根目录下有个文件 .eslintignore 文件，把你不需要校验的文件添加进去即可

4.在校验规则改成0，（0是不校验，1是警告，2是报错）

5.直接修改配置文件vue.config.js
module.exports = {
  lintOnSave: false
}
```



## 2.常用工具

vscode + live server（监测vscode保存，自动刷新）
