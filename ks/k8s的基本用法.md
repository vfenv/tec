# 1.K8S基本用法

## 1.1.信息获取

查看K8S集群信息:

```shell
$ kubectl get nodes -o wide
NAME                          STATUS   ROLES         AGE   VERSION
k3711v.abc.net                Ready    <none>        97d   v1.12.2
```

查看集群中存在的Pod，--all-namespaces表示所有命名空间下的，-n abc表示查看abc名称空间下对象

```shell
kubectl get pods --all-namespaces
```

查看集群中的Deployment信息。通过增加一个 -o wide参数，可以输出更多的信息。

包括一次部署期望的RS数量，当前的数量，可以数量。对应的容器，容器使用的Docker镜像等等信息。

```shell
kubectl get deployment --all-namespaces -o wide
```

查看集群中的Service信息。其中TYPE列表示了服务的类型。

```shell
kubectl get service --all-namespaces -o wide
```

导出yaml

```
kubectl get pod podname -n mynamespace -o yaml >> pod.yaml
```



## 1.2.对象创建

上面这几个是最常见的信息获取方式。那如何创建一个新的对象。前面我们构建了一个http_server:v1的镜像，我们希望在K8S集群里，使用Pod的方式把这个容器运行起来。在这之前，我们需要把这个镜像push到一个镜像仓库，使得可以在多个K8S节点间同步。去[阿里云](https://link.zhihu.com/?target=https%3A//cr.console.aliyun.com/cn-hangzhou/repositories)申请一个免费的仓库。

首先把原来的镜像重新命名成带阿里云仓库url的方式。然后使用docker push命令就可以把这个镜像推送到阿里云上了。

全世界任何一台有Docker的机器上，都可以把这个镜像原封不动的下载下来。

```shell
#把本地的镜像http_server:v1，重命名成阿里云的镜像名称registry.cn-hangzhou.aliyuncs.com/chenzhen-docker-repo/http_server:v1
$ docker tag http_server:v1 registry.cn-hangzhou.aliyuncs.com/chenzhen-docker-repo/http_server:v1
#使用docker push把这个镜像提交到阿里云上了
$ docker push registry.cn-hangzhou.aliyuncs.com/chenzhen-docker-repo/http_server:v1
```

接下来，使用YAML语法格式，定义一个Pod。按照Pod的Schema来定义即可。主要包括这个Pod的名字，内部包含的容器信息，比如启动容器使用的镜像，运行的命令等。

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: cz-demo
  labels:
    app: myapp
spec:
  containers:
    - name: http-server-container
      image: registry.cn-hangzhou.aliyuncs.com/chenzhen-docker-repo/http_server:v1
      command: ['/root/run.sh']
```

把这个文件保存为pod.yaml。然后指定kubectl create命令就可创建出一个Pod了。

```shell
$ kubectl create/apply -f pod.yaml

$ kubectl get pods -o wide | grep cz-demo
```

这就是最简单的对象创建过程。

### 1.2.1创建Deployment

首先定义Deployment。一个Deployment主要包括对Pod的定义和RS的定义。

每个Pod里有两个容器，分别是http_server和calc_server。二者由于在一个pod内，所以可以通过127.0.0.1互相通信。

此外通过replicas: 3这个配置项，定义了一个3副本的RS。K8S会在集群里保证有3个相同的Pod副本。

这里要注意的是最后一样，我们把http-server这个容器的端口4567显示暴露出来。

```yaml
apiVersion: apps/v1beta1  
kind: Deployment
metadata:
  name: my-deployment
spec:
  replicas: 3
  template:
    metadata:
      labels:
        app: back-end  #定义pod的标签
    spec:
      containers:
        - name: calc-server
          image: registry.cn-hangzhou.aliyuncs.com/chenzhen-docker-repo/calc_server:v1
          command: ['/root/run.sh']

        - name: http-server
          image: registry.cn-hangzhou.aliyuncs.com/chenzhen-docker-repo/http_server:v2
          command: ['/root/run.sh']
          ports:
            - containerPort: 4567
              name: myport
              protocol: HTTP
```

保存上述文件内容为deployment.yaml。

```shell
#执行kubectl create命令创建Deployment
$ kubectl create -f deployment.yaml

#查看Deployment信息，发现3个副本的Pod均被调度执行起来
$ kubectl get deployment -o wide| grep my-deployment  

#通过get pod看具体的Pod信息，其中两个Pod在一台物理机，另外一个Pod在另外一台机器上。这三个Pod有各自独立的IP地址。
$ kubectl get pod -o wide| grep my-deployment

#curl测试一下新接口，发现一切都正常
$ curl 10.244.1.155:4567
```



### 1.2.2 Service提供服务

现在我们有了3个Pod，有3个IP。但是正常情况下我们需要对外暴露一个统一且不变的IP和端口。这就用到了Service机制。创建一个Service也很简单。同样编写一个YAML文件。这里面有三个地方。

第一是定义Service的类型，这里我们使用**LoadBalance**模式。

第二是定义Selector，使用**app:back-end**来**选择Pod**。这个和上面Deployment中的定义是一致的。

第三，我们选择8888作为LoadBalance**对外的接口**，对内当然用的就是http-server容器的4567端口。

```yaml
apiVersion: v1
kind: Service
metadata:
  name: my-service
  namespace: myspace
spec:
  type: LoadBalancer  #service类型
  selector:
    app: back-end
  ports:
    - name: http
      port: 8888  #对外端口
      targetPort: 4567  #内部端口
```

保存这个文件为service_lb.yaml。

```shell
#使用kubectl create创建
$ kubectl create -f service_lb.yaml

#使用get service查看服务状态  
$ kubectl get service -o wide | grep my-service

#describle查看Service的配置详情，发现Endpoints部分已经自动配置为上面的3个Pod的IP。
#更厉害的地方在于这个配置是动态的，当Pod的IP发生变化时这里会自动变化  
#在集群内，可以通过ClusterIP:8888调用服务，负载均衡服务默认会轮训向后面的3个Pod转发请求。
$ kubectl describe service my-service -n myspace
 
#curl通过测试一下新接口
$ curl 10.105.242.158:8888
```

在集群外，我们可以直接使用物理机的IP或机器名来访问服务。这里需要注意，负载均衡对外又做了一次端口映射，我们需要使用的不是8888，而是31657。

### 1.2.3横向扩容

我们尝试做一些服务的更新操作，首先是横向扩容。在K8S中，横向扩容非常简单，最简单的做法是修改deployment.yaml文件中的replicas配置项，然后执行kubectl apply。还可以使用kubectl edit命令直接修改。kubectl edit类似vi方式，保存后就直接被更新应用了。下面展示了把replicas从3改成5之后。无论是deployment，RS，Pod还是Service，全部自动被更新了，非常方便。

```shell
#kubectl edit类似vi方式,保存就直接更新了
$ kubectl edit deployment my-deployment -n myspace

#修改之后查看deployment
$ kubectl get deployment -n myspace -o wide | grep my-deployment
 
#查看副本情况
$ kubectl get rs -n myspace -o wide | grep my-deployment
 
#查看pod情况
$ kubectl get pod -n myspace -o wide | grep my-deployment

#查看service详细信息
$ kubectl describe service my-service -n myspace
```



## 1.3.kube-proxy监控和管理k8s

使用token的方式登录

```shell
#创建一个dashboard的管理用户
$ kubectl create serviceaccount dashboard-admin -n kube-system

#将创建的dashboard用户绑定为管理用户
$ kubectl create clusterrolebinding dashboard-cluster-admin --clusterrole=cluster-admin --serviceaccount=kube-system:dashboard-admin

#获取刚刚创建的用户对应的token名称
#$ kubectl get secrets -n kube-system | grep dashboard
$ kubectl get secrets -n kubernetes-dashboard

#查看管理员的token的详细信息
$ kubectl describe secrets -n kubernetes-dashboard admin-user-token-fvg2f
```







