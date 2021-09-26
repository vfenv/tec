# 1. Node设置标签

k8s非常常用的一个调度方式：nodeSelector

用户可以灵活的利用label来管理集群中的资源，比如常见的service对象通过label去匹配pod资源。

实际上pod对象也可以通过节点的label 进行调度。

```shell
#查看node的label标签
$kubectl get nodes --show-labels
#给节点node3 添加标签 myapp=cwtestapp
$kubectl label nodes node3 myapp=cwtestapp
#删除节点node3 上myapp的值，就是用key后跟一个减号即可
$kubectl label nodes node3 myapp-
```

pod通过label指定节点：

```yaml
apiVersion: v1
kind: Pod
metadata:
  labels:
    app: busybox-pod
  name: test-busybox
spec:
  containers:
  - command:
    - sleep
    - "3600"
    image: busybox
    imagePullPolicy: Always
    name: test-busybox
  nodeSelector:            #设置nodeSelector指定节点的label
    myapp=cwtestapp
```

nodeSelector 属于强制性的，如果我们的目标节点没有可用的资源，我们的 Pod 就会一直处于 Pending 状态。

nodeSelector 的方式比较直观，但是还够灵活，控制粒度偏大，接下来我们再和大家了解下更加灵活的方式：节点亲和性(nodeAffinity)。

实际的生产环境中，会用到nodeAffinity(节点亲和性)、podAffinity(pod 亲和性) 以及 podAntiAffinity(pod 反亲和性)

# 2. Node Affinity

Affinity 翻译成中文是“亲和性”，它对应的是 Anti-Affinity，我们翻译成“互斥”。这两个词比较形象，可以把 pod 选择 node 的过程类比成磁铁的吸引和互斥，不同的是除了简单的正负极之外，pod 和 node 的吸引和互斥是可以灵活配置的。

Affinity的优点：

- 匹配有更多的逻辑组合，不只是字符串的完全相等
- 调度分成  **软策略(soft)**  和  **硬策略(hard)**，在软策略下，如果没有满足调度条件的节点，pod会忽略这条规则，继续完成调度。

目前主要的node affinity：

- requiredDuringSchedulingIgnoredDuringExecution
  表示pod必须部署到满足条件的节点上，如果没有满足条件的节点，就不停重试。其中IgnoreDuringExecution表示pod部署之后运行的时候，如果节点标签发生了变化，不再满足pod指定的条件，pod也会继续运行。
- requiredDuringSchedulingRequiredDuringExecution
  表示pod必须部署到满足条件的节点上，如果没有满足条件的节点，就不停重试。其中RequiredDuringExecution表示pod部署之后运行的时候，如果节点标签发生了变化，不再满足pod指定的条件，则重新选择符合要求的节点。
- preferredDuringSchedulingIgnoredDuringExecution
  表示优先部署到满足条件的节点上，如果没有满足条件的节点，就忽略这些条件，按照正常逻辑部署。
- preferredDuringSchedulingRequiredDuringExecution
  表示优先部署到满足条件的节点上，如果没有满足条件的节点，就忽略这些条件，按照正常逻辑部署。其中RequiredDuringExecution表示如果后面节点标签发生了变化，满足了条件，则重新调度到满足条件的节点。

> 软策略和硬策略的区分是有用处的，硬策略适用于 pod 必须运行在某种节点，否则会出现问题的情况，比如集群中节点的架构不同，而运行的服务必须依赖某种架构提供的功能；
>
> 软策略不同，它适用于满不满足条件都能工作，但是满足条件更好的情况，比如服务最好运行在某个区域，减少网络传输等。这种区分是用户的具体需求决定的，并没有绝对的技术依赖。

示例：

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: with-node-affinity
spec:
  affinity:
    nodeAffinity:  #节点亲和性 多个亲和规则之前是或者的关系，符合哪一个都可以
      requiredDuringSchedulingIgnoredDuringExecution:  #硬策略
        nodeSelectorTerms:   #节点选择器条款
        - matchExpressions:  #匹配的表达式 如果有多个设置，必须全部符合才能走当前策略
          - key: kubernetes.io/hostname  #kubernetes.io/hostname in('node5','node6')
            operator: In
            values:
            - node5
            - node6
      preferredDuringSchedulingIgnoredDuringExecution:  #软策略
      - weight: 1  #权重1
        preference:  #首选
          matchExpressions:  #匹配的表达式
          - key: myapp  #myapp in('cwtestapp')
            operator: In
            values:
            - cwtestapp
  containers:
  - name: with-node-affinity
    image: gcr.io/google_containers/pause:2.0
```

这个 pod 同时定义了 requiredDuringSchedulingIgnoredDuringExecution 和 preferredDuringSchedulingIgnoredDuringExecution 两种亲和性。

第一个要求 pod 必须运行在node5或者node6上，第二个希望节点最好有 myapp:cwtestapp 标签。

这里的匹配逻辑是label在某个列表中，可选的操作符有：

| 操作符       | 含义                              |
| ------------ | --------------------------------- |
| In           | label的值在某个列表中             |
| NotIn        | label的值不在某个列表中           |
| Exists       | 某个label存在                     |
| DoesNotExist | 某个label不存在                   |
| Gt           | label的值大于某个值（字符串比较） |
| Lt           | abel的值小于某个值（字符串比较）  |

- 如果nodeAffinity中nodeSelector有多个选项，节点满足任何一个条件即可

- 如果matchExpressions有多个选项，则节点必须同时满足这些选项才能执行这个策略 

> 需要说明的是，node并没有anti-affinity这种东西，因为NotIn和DoesNotExist能提供类似的功能。

# 3. Pod Affinity

通过《K8S调度之节点亲和性》，我们知道怎么在调度的时候让pod灵活的选择node，但有些时候我们希望调度能够考虑pod之间的关系，而不只是pod与node的关系。于是在kubernetes 1.4的时候引入了pod affinity。

为什么有这样的需求呢？

- 服务A和服务B网络通信比较多，需要部署在同个主机、机房、城市

- 数据服务C和数据服务D尽量分开，如果分配到一起，主机或者机房出问题，会导致应用完全不可用

pod亲和性调度需要各个相关的pod对象运行于"同一位置"， 而反亲和性调度则要求他们不能运行于"同一位置"。

这里指定“同一位置” 是通过 **topologyKey** 来定义，topologyKey 对应的值是 node 上的一个标签名称。

**topology实际控制的就是pod的调度范围**

比如一些节点的标签zone=A，一些节点的标签zone=B，当topologyKey定义为zone，那么调度pod的时候就会围绕着A拓扑，B拓扑来调度，而相同拓扑下的node就为“同一位置”。

如果基于各个节点kubernetes.io/hostname标签作为评判标准，那么很明显  **“同一位置”意味着同一节点**，不同节点既为不同位置。

当前有两个机房（ beijing，shanghai），需要部署一个nginx产品，副本为两个，为了保证机房容灾高可用场景，需要在两个机房分别部署一个副本

每个node都需要设置所在机房的label

```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: nginx-affinity-test
spec:
  serviceName: nginx-service
  replicas: 2
  selector:
    matchLabels:
      service: nginx  #statefulset需要匹配的pod标签
  template:
    metadata:
      name: nginx
      labels:
        service: nginx  #pod标签
    spec:
      affinity:
        podAntiAffinity:  #pod的   反亲和性   设置
          requiredDuringSchedulingIgnoredDuringExecution:  #硬策略
          - labelSelector:
              matchExpressions:  #pod的标签匹配规则service in("nginx")，同zone同pod标签的不部署在同node上
              - key: service
                operator: In
                values:
                - nginx
            topologyKey: zone  #调度范围标签zone
      containers:
      - name: nginx
        image: contos7:latest
        command:
        - sleep
        - "360000000"
```

与node affinity 相似，pod affinity 也有 requiredDuringSchedulingIgnoredDuringExecution 和 preferredDuringSchedulingIgnoredDuringExecution等，意义也和Node Affinity一样。

如果要使用亲和性，在 affinity 下面要添加 podAffinity 字段，如果要使用非亲和性，在 affinity 下面要添加 podAntiAffinity 字段。

先定义一个参照目标pod（如果是多副本，多个副本之间就是相互参照的，就不用单独有一个参照目标了）：

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: pod-flag
  labels:
    security: "S1"
    app: "nginx"
spec:
  containers:
  - name: nginx
    image: nginx
```

## 3.1 Pod亲和性调度

下面是一个亲和性调度的示例

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: pod-affinity
spec:
  affinity:
    podAffinity:  #设置pod的亲和性
      requiredDuringSchedulingIgnoredDuringExecution:  #硬策略
      - labelSelector:  #标签选择
          matchExpressions:  #匹配表达式
          - key: security
            operator: In
            values:
            - S1
        topologyKey: kubernetes.io/hostname  #这个是pod默认的标签，主机名
  containers:
  - name: with-pod-affinity
    image: gcr.io/google_containers/pause:2.0
```

创建后可以看到这个pod与上面那个参照的pod位于同一个node上，另外，如果将这个node上的kubernetes.io/hostname标签干掉，将会发现pod会一直处于pending状态，这是因为找不到满足条件的node了。

## 3.2 pod互斥性调度

下面是一个互斥性调度的示例：

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: with-pod-affinity
spec:
  affinity:
    podAffinity:  #pod的亲和设置
      requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
          - key: security
            operator: In
            values:
            - S1
        topologyKey: "failure-domain.beta.kubernetes.io/zone"
    podAntiAffinity:  #pod的非亲和设置
      preferredDuringSchedulingIgnoredDuringExecution:  #软配置
      - weight: 100
        podAffinityTerm:  #pod互斥的条约
          labelSelector:
            matchExpressions:
            - key: security
              operator: In
              values:
              - S2
          topologyKey: kubernetes.io/hostname  #这个是pod默认的标签，主机名
  containers:
  - name: with-pod-affinity
    image: gcr.io/google_containers/pause:2.0
```

这个例子要求这个新pod与security=S1的pod为同一个zone，但是不与security=S2的pod为同一个node。



原则上，topologyKey可以使用任何合法的标签Key赋值，但是出于性能和安全方面的考虑，对topologyKey有如下限制：

- 在pod亲和性和RequiredDuringScheduling的pod互斥性的定义中，不允许使用空的topologyKey
- 如果admission controller包含了LimitPodHardAntiAffinityTopology，那么针对RequiredDuringScheduling的pod互斥性定义就被限制为kubernetes.io/hostname，要使用自定义的topologyKey，就要改写或禁用该控制器
- 在PerferredDuringScheduling类型的Pod互斥性定义中，空的topologyKey会被解释为kubernetes.io/hostname、failure-domain.beta.kubernetes.io/zone及failure-domain.beta.kubernetes.io/region的组合



podAffinity规则设置的注意事项：

- 在labelSelector和topologyKey同级，还可以定义namespaces列表，表示匹配哪些namespace里面的pod，默认情况下，会匹配定义的pod所在的namespace，如果定义了这个字段，但是它的值为空，则匹配所有的namespaces。
- 所有关联requiredDuringSchedulingIgnoredDuringExecution的matchExpressions全都满足之后，系统才能将pod调度到某个node上。



# 4. 污点和容忍

## 4.1 污点Taints

如果一个节点标记为 Taints ，除非 Pod 也被标识为可以容忍污点节点，否则该 Taints 节点不会被调度 Pod。 

比如用户希望把 Master 节点保留给 Kubernetes 系统组件使用，或者把一组具有特殊资源预留给某些 Pod，则污点就很有用了，Pod 不会再被调度到 taint 标记过的节点。

我们使用 kubeadm 搭建的集群默认就给 master 节点添加了一个污点标记，所以我们看到我们平时的 Pod 都没有被调度到 master 上去： 

```shell
$ kubectl get nodes
$ kubectl describe nodes master
Name:               master
......
Taints:             node-role.kubernetes.io/master:NoSchedule    #看这。。。。。被标记为污点，pod就不往master节点调度了
Unschedulable:      false
......
#我们可以使用上面的命令查看 master 节点的信息，其中有一条关于 Taints 的信息：node-role.kubernetes.io/master:NoSchedule，就表示master 节点打了一个污点的标记，其中影响的参数是 NoSchedule，表示 Pod 不会被调度到标记为 taints 的节点
```

Taints的值代表内容：

- NoSchedule：表示 Pod 不会被调度到标记为 taints 的节点，老的不驱逐

- PreferNoSchedule：NoSchedule 的软策略版本，表示尽量不调度到污点节点上去

- NoExecute：该选项意味着一旦 Taint 生效，如该节点内正在运行的 Pod 没有对应容忍（Tolerate）设置，则会直接被逐出



### 4.1.1 给节点设置污点

```shell
#将 node2 节点标记为了污点，影响策略是 NoSchedule，只会影响新的 Pod 调度
$ kubectl taint nodes node2 test=node2:NoSchedule
node "node2" tainted
```



### 4.1.2 取消节点的污点标记

```shell
$ kubectl taint nodes node2 test-
node "node2" untainted
```





## 4.2 容忍Toleration

如果仍然希望某个 Pod 调度到 taint 节点上，则必须在 Spec 中做出 Toleration 定义，才能调度到该节点，

比如现在我们想要将一个 Pod 调度到 master 节点：taint-demo.yaml

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: taint
  labels:
    app: taint
spec:
  replicas: 3
  selector:
    matchLabels:
      app: taint
  template:
    metadata:
      labels:
        app: taint
    spec:
      containers:
      - name: nginx
        image: nginx
        ports:
        - name: http
          containerPort: 80
      #由于 master 节点被标记为了污点，所以我们这里要想 Pod 能够调度到master节点去，就需要增加容忍的声明
      tolerations:  #设置容忍
      - key: "node-role.kubernetes.io/master"
        operator: "Exists"  #存在
        effect: "NoSchedule"  #效果 不调度或者说有污点
```

然后创建上面的资源，查看结果：

```shell
$ kubectl apply -f taint-demo.yaml
deployment.apps "taint" created
$ kubectl get pods -o wide
NAME                                      READY     STATUS             RESTARTS   AGE       IP             NODE
......
taint-845d8bb4fb-bbvmp                    1/1       Running            0          1m        10.244.0.33    master
......
#我们可以看到有一个 Pod 副本被调度到了 master 节点，这就是容忍的使用方法。
```

对于 tolerations 属性的写法，其中的 key、value、effect 与 Node 的 Taint 设置需保持一致， 还有以下几点说明：

1. 如果 operator 的值是 Exists，则 value 属性可省略

2. 如果 operator 的值是 Equal，则表示其 key 与 value 之间的关系是 equal(等于)

3. 如果不指定 operator 属性，则默认值为 Equal

另外，还有两个特殊值：

- 空的 key 如果再配合 Exists 就能匹配所有的 key 与 value，也就是是能容忍所有节点的所有 Taints

- 空的 effect 匹配所有的 effect
