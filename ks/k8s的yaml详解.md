# K8S的YAML文件

## 1.必选参数对象

| 参数名                  | 字段类型 | 说明                                                         |
| ----------------------- | -------- | ------------------------------------------------------------ |
| version                 | String   | 这里是指的是K8S API的版本，目前基本上是v1，可以用kubectl api-versions命令查询 |
| kind                    | String   | 这里指的是yaml文件定义的资源类型和角色，比如：Pod            |
| metadata                | Object   | 元数据对象，固定值就写metadata                               |
| metadata.name           | String   | 元数据对象的名字，这里由我们编写，比如命名Pod的名字          |
| metadata.namespace      | String   | 元数据对象的命名空间，由我们自身定义                         |
| Spec                    | Object   | 详细定义对象，固定值就写Spec                                 |
| spec.containers[]       | list     | 这里是Spec对象的容器列表定义，是个列表                       |
| spec.containers[].name  | String   | 这里定义容器的名字                                           |
| spec.containers[].image | String   | 这里定义要用到的镜像名称                                     |

以上这些都是编写一个YAML文件的必写项，一个最基本的YAML文件就包含它们。



**关于apiVersion**

Kubernetes的官方文档中并没有对apiVersion的详细解释，而且因为K8S本身版本也在快速迭代，有些资源在低版本还在beta阶段，到了高版本就变成了stable。
如Deployment:

1.6版本之前 apiVsersion：extensions/v1beta1

1.6版本到1.9版本之间：apps/v1beta1

1.9版本之后:apps/v1

**各种apiVersion的含义:**

| 版本号                      | 含义                                                         |
| --------------------------- | ------------------------------------------------------------ |
| alpha                       | 该软件可能包含错误。<br>启用一个功能可能会导致bug 随时可能会丢弃对该功能的支持，恕不另行通知 |
| beta                        | 经过很好的测试。启用功能被认为是安全的。<br/>默认情况下功能是开启的 <br/>细节可能会改变，但功能在后续版本不会被删除 |
| stable                      | 该版本名称命名方式：vX这里X是一个整数<br/>稳定版本、放心使用 <br/>将出现在后续发布的软件版本中 |
| v1                          | Kubernetes API的稳定版本，包含很多核心对象：pod、service等   |
| apps/v1beta2                | 在kubernetes1.8版本中，新增加了apps/v1beta2的概念，apps/v1beta1同理。DaemonSet，Deployment，ReplicaSet 和 StatefulSet的当时版本迁入apps/v1beta2，兼容原有的extensions/v1beta1 |
| apps/v1                     | 在kubernetes1.9版本中，引入apps/v1，deployment等资源从extensions/v1beta1, apps/v1beta1 和 apps/v1beta2迁入apps/v1，原来的v1beta1等被废弃。 |
| apps/v1                     | 包含一些通用的应用层的api组合，如：Deployments, RollingUpdates, and ReplicaSets |
| batch/v1                    | 代表job相关的api组合<br/>在kubernetes1.8版本中，新增了batch/v1beta1，后CronJob 已经迁移到了 batch/v1beta1，然后再迁入batch/v1 |
| autoscaling/v1              | 代表自动扩缩容的api组合，kubernetes1.8版本中引入。<br/>这个组合中后续的alpha 和 beta版本将支持基于memory使用量、其他监控指标进行扩缩容 |
| extensions/v1beta1          | deployment等资源在1.6版本时放在这个版本中，后迁入到apps/v1beta2,再到apps/v1中统一管理 |
| certificates.k8s.io/v1beta1 | 安全认证相关的api组合                                        |
| authentication.k8s.io/v1    | 资源鉴权相关的api组合                                        |



**查看当前可用的API版本**

```shell
$ kubectl api-versions
```



## 2.可选参数对象

前面都是必选参数，那么还是否有其他参数呢？其他功能的参数，虽然不是必选项，但是为了让YAML定义得更详细、功能更丰富，这里其他参数也需要了解下。接下来的参数都是Spec对象下面的，主要分了两大块：spec.containers 和 spec.volumes。

### 2.1 spec.containers

spec.containers 是个list数组，很明显，它代表的是描述container容器方面的参数。所以它下面的参数是非常多的，具体参数看如下表格：

| 参数名                                      | 字段类型 | 说明                                                         |
| ------------------------------------------- | -------- | ------------------------------------------------------------ |
| spec.containers[].name                      | String   | 定义容器的名字                                               |
| spec.containers[].image                     | String   | 定义要用到的镜像名称                                         |
| spec.containers[].imagePullPolicy           | String   | 定义镜像拉取策略，有Always、Never、IfNotPresent三个值可选（1）Always：意思是每次都尝试重新拉取镜像 （2）Never：表示仅使用本地镜像 （3）IfNotPresent：如果本地有镜像就使用本地镜像，没有就拉取在线镜像。 上面三个值都没设置的话，默认是Always。 |
| spec.containers[].command[]                 | List     | 指定容器启动命令，因为是数组可以指定多个，不指定则使用镜像打包时使用的启动命令。 |
| spec.containers[].args[]                    | List     | 指定容器启动命令参数，因为是数组可以指定多个。               |
| spec.containers[].workingDir                | String   | 指定容器的工作目录                                           |
| spec.containers[].volumeMounts[]            | List     | 指定容器内部的存储卷配置                                     |
| spec.containers[].volumeMounts[].name       | String   | 指定可以被容器挂载的存储卷的名称                             |
| spec.containers[].volumeMounts[].mountPath  | String   | 指定可以被容器挂载的存储卷的路径                             |
| spec.containers[].volumeMounts[].readOnly   | String   | 设置存储卷路径的读写模式，ture 或者false，默认为读写模式     |
| spec.containers[].ports[]                   | List     | 指定容器需要用到的端口列表                                   |
| spec.containers[].ports[].name              | String   | 指定端口名称                                                 |
| spec.containers[].ports[].containerPort     | String   | 指定容器需要监听的端口号                                     |
| spec.containers[].ports[].hostPort          | String   | 指定容器所在主机需要监听的端口号，默认跟上面containerPort相同，注意设置了hostPort 同一台主机无法启动该容器的相同副本（因为主机的端口号不能相同，这样会冲突） |
| spec.containers[].ports[].protocol          | String   | 指定端口协议，支持TCP和UDP，默认值为TCP                      |
| spec.containers[].env[]                     | List     | 指定容器运行前需设置的环境变量列表                           |
| spec.containers[].env[].name                | String   | 指定环境变量名称                                             |
| spec.containers[].env[].value               | String   | 指定环境变量值                                               |
| spec.containers[].resources                 | Object   | 指定资源限制和资源请求的值（这里开始就是设置容器的资源上限） |
| spec.containers[].resources.limits          | Object   | 指定设置容器运行时资源的运行上限                             |
| spec.containers[].resources.limits.cpu      | String   | 指定CPU的限制，单位为core数，将用于 docker run --cpu-shares参数，CPU的单位是milicpu，500mcpu=0.5cpu；内存的单位E, P, T, G, M, K, Ei, Pi, Ti, Gi, Mi, Ki等。<br />cpu: "300m" |
| spec.containers[].resources.limits.memory   | String   | 指定MEM内存的限制，单位为MIB、GiB                            |
| spec.containers[].resources.requests        | Object   | 指定容器启动和调度时的限制设置                               |
| spec.containers[].resources.requests.cpu    | String   | CPU请求，单位为core数，容器启动时初始化可用数量              |
| spec.containers[].resources.requests.memory | String   | 内存请求，单位为MIB、GiB，容器启动的初始化可用数量           |

### 2.2 spec.volumes

spec.volumes是个list数组，很明显，看名字就知道它是定义同步存储方面的参数。它下面的参数是非常多，具体参数看如下表格：

| 参数名                                           | 类型   | 说明                                                         |
| ------------------------------------------------ | ------ | ------------------------------------------------------------ |
| spec.volumes[].name                              | String | 定义Pod的共享存储卷的名称，容器定义部分的spec.containers[].volumeMounts[].name的值跟这里是一样的。 |
| spec.volumes[].emptyDir                          | Object | 指定Pod的临时目录，值为一个空对象：emptyDir:{}               |
| spec.volumes[].hostPath                          | Object | 指定挂载Pod所在宿主机的目录                                  |
| spec.volumes[].hostPath.path                     | String | 指定Pod所在主机的目录，将被用于容器中mount的目录             |
| spec.volumes[].secret                            | Object | 指定类型为secret的存储卷，secret意为私密、秘密的意思，很容易理解，它存储一些密码，token或者秘钥等敏感安全文件。挂载集群预定义的secret对象到容器内部。 |
| spec.volumes[].configMap                         | Object | 指定类型为configMap的存储卷，表示挂载集群预定义的configMap对象到容器内部。 |
| spec.volumes[].livenessProbe                     | Object | 指定Pod内容器健康检查的设置，当探测无响应几次后，系统将自动重启该容器。这个在前面的文章中有说，具体可以设置：exec、httpGet、tcpSocket。 |
| spec.volumes[].livenessProbe.exec                | Object | 指定Pod内容器健康检查的设置，确定是exec方式                  |
| spec.volumes[].livenessProbe.exec.command[]      | String | 指定exec方式后需要指定命令或者脚本，用这个参数设置           |
| spec.volumes[].livenessProbe.httpGet             | Object | 指定Pod内容器健康检查的设置，确定是httpGet方式               |
| spec.volumes[].livenessProbe.tcpSocket           | Object | 指定Pod内容器健康检查的设置，确定是tcpSocket方式             |
| spec.volumes[].livenessProbe.initialDelaySeconds | Number | 容器启动完成后手册探测的时间设置，单位为s                    |
| spec.volumes[].livenessProbe.timeoutSeconds      | Number | 对容器健康检查的探测等待响应的超时时间设置，单位为S，默认为1s。若超过该超时时间设置，则认为该容器不健康，会重启该容器。 |
| spec.volumes[].livenessProbe.periodSeconds       | Number | 对容器健康检查的定期探测时间设置，单位为S，默认10s探测一次。 |

## 3.其它参数对象

除了上面containers和volumes两个主要参数，剩下有几个参数：

| 参数名                | 字段类型 | 说明                                                         |
| --------------------- | -------- | ------------------------------------------------------------ |
| spec.restartPolicy    | String   | 定义Pod的重启策略，可选值为Always、OnFailure，默认值为Always。 1.Always：Pod一旦终止运行，则无论容器是如何终止的，kubelet服务都将重启它。 2.OnFailure：只有Pod以非零退出码终止时，kubelet才会重启该容器。如果容器正常结束（退出码为0），则kubelet将不会重启它。 3. Never：Pod终止后，kubelet将退出码报告给Master，不会重启该Pod。 |
| spec.nodeSelector     | Object   | 定义Node的Label过滤标签，以key:value格式指定                 |
| spec.imagePullSecrets | Object   | 定义pull镜像时使用secret名称，以name:secretkey格式指定       |
| spec.hostNetwork      | Boolean  | 定义是否使用主机网络模式，默认值为false。设置true表示使用宿主机网络，不使用docker网桥，同时设置了true将无法在同一台宿主机上启动第二个副本。 |

## 4.yaml举例

```yaml
# yaml格式的pod定义文件完整内容：
apiVersion: v1       #必选，版本号，例如v1
kind: Pod       #必选，Pod
metadata:       #必选，元数据
  name: string       #必选，Pod名称
  namespace: string    #必选，Pod所属的命名空间
  labels:      #自定义标签
    - name: string     #自定义标签名字
  annotations:       #自定义注释列表
    - name: string
spec:         #必选，Pod中容器的详细定义
  containers:      #必选，Pod中容器列表
  - name: string     #必选，容器名称
    image: string    #必选，容器的镜像名称
    imagePullPolicy: [Always | Never | IfNotPresent] #获取镜像的策略 Alawys表示下载镜像 IfnotPresent表示优先使用本地镜像，否则下载镜像，Nerver表示仅使用本地镜像
    command: [string]    #容器的启动命令列表，如不指定，使用打包时使用的启动命令
    args: [string]     #容器的启动命令参数列表
    workingDir: string     #容器的工作目录
    volumeMounts:    #挂载到容器内部的存储卷配置
    - name: string     #引用pod定义的共享存储卷的名称，需用volumes[]部分定义的的卷名
      mountPath: string    #存储卷在容器内mount的绝对路径，应少于512字符
      readOnly: boolean    #是否为只读模式
    ports:       #需要暴露的端口库号列表
    - name: string     #端口号名称
      containerPort: int   #容器需要监听的端口号
      hostPort: int    #容器所在主机需要监听的端口号，默认与Container相同
      protocol: string     #端口协议，支持TCP和UDP，默认TCP
    env:       #容器运行前需设置的环境变量列表
    - name: string     #环境变量名称
      value: string    #环境变量的值
    resources:       #资源限制和请求的设置
      limits:      #资源限制的设置
        cpu: string    #Cpu的限制，单位为core数，将用于docker run --cpu-shares参数
        memory: string     #内存限制，单位可以为Mib/Gib，将用于docker run --memory参数
      requests:      #资源请求的设置
        cpu: string    #Cpu请求，容器启动的初始可用数量
        memory: string     #内存清楚，容器启动的初始可用数量
    livenessProbe:     #对Pod内个容器健康检查的设置，当探测无响应几次后将自动重启该容器，检查方法有exec、httpGet和tcpSocket，对一个容器只需设置其中一种方法即可
      exec:      #对Pod容器内检查方式设置为exec方式
        command: [string]  #exec方式需要制定的命令或脚本
      httpGet:       #对Pod内个容器健康检查方法设置为HttpGet，需要制定Path、port
        path: string
        port: number
        host: string
        scheme: string
        HttpHeaders:
        - name: string
          value: string
      tcpSocket:     #对Pod内个容器健康检查方式设置为tcpSocket方式
         port: number
       initialDelaySeconds: 0  #容器启动完成后首次探测的时间，单位为秒
       timeoutSeconds: 0   #对容器健康检查探测等待响应的超时时间，单位秒，默认1秒
       periodSeconds: 0    #对容器监控检查的定期探测时间设置，单位秒，默认10秒一次
       successThreshold: 0
       failureThreshold: 0
       securityContext:
         privileged:false
    restartPolicy: [Always | Never | OnFailure] #Pod的重启策略，Always表示一旦不管以何种方式终止运行，kubelet都将重启，OnFailure表示只有Pod以非0退出码退出才重启，Nerver表示不再重启该Pod
    nodeSelector: obeject  #设置NodeSelector表示将该Pod调度到包含这个label的node上，以key：value的格式指定
    imagePullSecrets:    #Pull镜像时使用的secret名称，以key：secretkey格式指定
    - name: string
    hostNetwork:false      #是否使用主机网络模式，默认为false，如果设置为true，表示使用宿主机网络
    volumes:       #在该pod上定义共享存储卷列表
    - name: string     #共享存储卷名称 （volumes类型有很多种）
      emptyDir: {}     #类型为emtyDir的存储卷，与Pod同生命周期的一个临时目录。为空值
      hostPath: string     #类型为hostPath的存储卷，表示挂载Pod所在宿主机的目录
        path: string     #Pod所在宿主机的目录，将被用于同期中mount的目录
      secret:      #类型为secret的存储卷，挂载集群与定义的secre对象到容器内部
        scretname: string  
        items:     
        - key: string
          path: string
      configMap:     #类型为configMap的存储卷，挂载预定义的configMap对象到容器内部
        name: string
        items:
        - key: string
          path: string

```



