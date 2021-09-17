# 常用命令

- [kubectl run（创建容器镜像）](http://docs.kubernetes.org.cn/468.html)
- [kubectl expose（将资源暴露为新的 Service）](http://docs.kubernetes.org.cn/475.html)
- [kubectl annotate（更新资源的Annotations信息）](http://docs.kubernetes.org.cn/477.html)
- [kubectl autoscale（Pod水平自动伸缩）](http://docs.kubernetes.org.cn/486.html)
- [kubectl convert（转换配置文件为不同的API版本）](http://docs.kubernetes.org.cn/488.html)

- [kubectl create（创建一个集群资源对象](http://docs.kubernetes.org.cn/490.html)
- [kubectl create clusterrole（创建ClusterRole）](http://docs.kubernetes.org.cn/492.html)
- [kubectl create clusterrolebinding（为特定的ClusterRole创建ClusterRoleBinding）](http://docs.kubernetes.org.cn/494.html)
- [kubectl create configmap（创建configmap）](http://docs.kubernetes.org.cn/533.html)
- [kubectl create deployment（创建deployment）](http://docs.kubernetes.org.cn/535.html)
- [kubectl create namespace（创建namespace）](http://docs.kubernetes.org.cn/537.html)
- [kubectl create poddisruptionbudget（创建poddisruptionbudget）](http://docs.kubernetes.org.cn/539.html)
- [kubectl create quota（创建resourcequota）](http://docs.kubernetes.org.cn/541.html)
- [kubectl create role（创建role）](http://docs.kubernetes.org.cn/543.html)
- [kubectl create rolebinding（为特定Role或ClusterRole创建RoleBinding）](http://docs.kubernetes.org.cn/545.html)

- [kubectl create service（使用指定的子命令创建 Service服务）](http://docs.kubernetes.org.cn/564.html)
- [kubectl create service clusterip](http://docs.kubernetes.org.cn/566.html)
- [kubectl create service externalname](http://docs.kubernetes.org.cn/568.html)
- [kubectl create service loadbalancer](http://docs.kubernetes.org.cn/570.html)
- [kubectl create service nodeport](http://docs.kubernetes.org.cn/572.html)
- [kubectl create serviceaccount](http://docs.kubernetes.org.cn/574.html)

- [kubectl create secret（使用指定的子命令创建 secret）](http://docs.kubernetes.org.cn/548.html)
- [kubectl create secret tls](http://docs.kubernetes.org.cn/558.html)
- [kubectl create secret generic](http://docs.kubernetes.org.cn/556.html)
- [kubectl create secret docker-registry](http://docs.kubernetes.org.cn/554.html)

- [kubectl delete（删除资源对象）](http://docs.kubernetes.org.cn/618.html)
- [kubectl edit（编辑服务器上定义的资源对象）](http://docs.kubernetes.org.cn/623.html)
- [kubectl get（获取资源信息）](http://docs.kubernetes.org.cn/626.html)
- [kubectl label（更新资源对象的label）](http://docs.kubernetes.org.cn/628.html)
- [kubectl patch（使用patch更新资源对象字段）](http://docs.kubernetes.org.cn/632.html)
- [kubectl replace（替换资源对象）](http://docs.kubernetes.org.cn/635.html)
- [kubectl rolling-update（使用RC进行滚动更新）](http://docs.kubernetes.org.cn/638.html)
- [kubectl scale（扩缩Pod数量）](http://docs.kubernetes.org.cn/664.html)

- [kubectl rollout（对资源对象进行管理）](http://docs.kubernetes.org.cn/643.html)
- [kubectl rollout history（查看历史版本）](http://docs.kubernetes.org.cn/645.html)
- [kubectl rollout pause（标记资源对象为暂停状态）](http://docs.kubernetes.org.cn/647.html)
- [kubectl rollout resume（恢复已暂停资源）](http://docs.kubernetes.org.cn/650.html)
- [kubectl rollout status（查看资源状态）](http://docs.kubernetes.org.cn/652.html)
- [kubectl rollout undo（回滚版本）](http://docs.kubernetes.org.cn/654.html)

- [kubectl set（配置应用资源）](http://docs.kubernetes.org.cn/669.html)
- [kubectl set resources（指定Pod的计算资源需求）](http://docs.kubernetes.org.cn/671.html)
- [kubectl set selector（设置资源对象selector）](http://docs.kubernetes.org.cn/672.html)
- [kubectl set image（更新已有资源对象中的容器镜像）](http://docs.kubernetes.org.cn/670.html)
- [kubectl set subject（更新RoleBinding / ClusterRoleBinding中User、Group 或 ServiceAccount）](http://docs.kubernetes.org.cn/681.html)

## 1.快速查找：

```shell
kubectl get pods --show-labels #显示pods 信息 同时显示标签

kubectl delete deployments --all #删除

kubectl get deployments #获取deployments

kubectl run ggl1 --image=registry.cn-qingdao.aliyuncs.com/ggl/ggl:0.0.8 --port=80 --rm #简单方式运行仓库 其中--rm 删除已经存在的镜像

kubectl get pods -o wide #获取pods 更多的简要信息

kubectl get cs #获取健康信息

kubeadm join 172.31.161.220:6443 --token qe5v6b.wt2ik30vrqd4ugc7 --discovery-token-ca-cert-hash sha256:b014fc1f3cb71fb16e4f340c58a5e786e46559aed7074df4c9f8111261c9630c #加入集群

kubectl get pods -n kube-system #显示属于kube-system名称空间里的 pods

Kubectl get ns #获取名称空间，其中有default ，Kube-public, Kube-system

kubectl scale --replicas=3 deployment ggl2-deployment #自动扩容

kubectl set image deployment ggl2-deployment ggl2-image=newimage #滚动更新

kubectl rollout status deployment ggl2-deployment #查看滚动更新状态

kubectl rollout undo deployment ggl2-deployment #回滚到老版本 参数 --to-revision=1 表示回滚到版本1

kubectl rollout history deployment ggl2-deployment #显示版本历史记录

Kubectl edit svc XXX #修改服务

kubectl api-versions #获取yml 中可用的组

kubectl get ep XXXX -o yaml #获取endpoints 明细

kubectl explain pods #查看pods yml 定义说明

kubectl explain pods.XX #查看pods的下一级 yml 定义说明

Kubectl logs podname imagename #查看日志

kubectl exec -it podname -c imagename -- /bin/sh #进入容器 必须带 --

Kubectl delete -f xxx.yaml

nodeselector #标签选择器

deployment #简称deploy

Kubectl apply -f XXX.yaml #新增或者修改 默认是滚动更新 ，只要执行完 Pod 就是自动下载新的镜像
```

## 2.常用命令

```shell
#查看更多-o wide
#参数 --all-namespaces 是去查看所有名称空间的参数，默认查询的是default namespace名称空间的相关对象
1、查看类命令
#查看集群信息
kubectl cluster-info
#查看各组件信息
kubectl -s http://localhost:8080 get componentstatuses
#列出当前所有的pod
kubectl get pods
#查看pods所在的运行节点
kubectl get pods -o wide
#查看pods定义的详细信息
kubectl get pods -o yaml
#查看Replication Controller信息
kubectl get rc
#查看service的信息
kubectl get service
#查看节点信息
kubectl get nodes
#按selector名来查找pod
kubectl get pod --selector name=redis
#查看运行的pod的环境变量
kubectl exec pod名 env

2、操作类命令
#创建
kubectl create -f 文件名
#重建
kubectl replace -f 文件名  [--force]
#删除：
kubectl delete -f 文件名
kubectl delete pod pod名
kubectl delete rc rc名
kubectl delete service service名
kubectl delete pod --all
#启动一个pod
kubectl run mybusybox --image=busybox
#启动多个pod
kubectl run mybusybox --image=busybox --replicas=5
#删除创建的pod
kubectl delete deployments mybusybox
#列出当前所有的pod
kubectl get pods
#查看pod的状态
kubectl describe pod [PODNAME]
#创建带有端口映射的pod
kubectl run mynginx --image=nginx --port=80 --hostport=8000
#创建带有终端的pod
kubectl run -i --tty busybox --image=busybox
```

## 3.维护类：

**master节点：**

```shell
$ for  I  in  etcd  kube-apiserver  kube-controller-manager kube-scheduler;  do
systemctl restart  $I
systemctl enable  $I
done
$ service flanneld start
1.2.3.4.5.
```

**node节点：**

```shell
$for  I  in  kube-proxy  kubelet  docker
do
systemctl  restart  $I
systemctl  enable  $I
done
$ service flanneld start
1.2.3.4.5.6.
```

**获取token：**

```shell
kubectl get secret -n kube-system
kubectl describe secret admin-token-45rzl -n kube-system
#或者
kubectl get secret $(kubectl get secret -n kube-system|grep admin-token|awk '{print $1}') -n kube-system -o jsonpath={.data.token}|base64 -d |xargs echo

#查看kubelet进程启动参数
ps -ef | grep kubelet

#查看日志:
journalctl -u kubelet -f
```

## 4.查看类

```shell
#显示Node的详细信息
kubectl describe nodes <node-name>

#显示Pod的详细信息
kubectl describe pods/<pod-name>

#显示由RC管理的Pod的信息
kubectl describe pods <rc-name>

#查看pods所在的运行节点
 kubectl get pods -o wide

#查看所有namespace的pods运行情况
 kubectl get pods --all-namespaces 

#查看pods具体信息
kubectl get pods -o wide kubernetes-dashboard-xxx --namespace=kube-system

#查看pods定义的详细信息
kubectl get pods -o yaml

#查看集群健康状态
kubectl get cs

#查看rc和servers
kubectl get rc,services

#查看应用的副本数
kubectl get deployments

#查看运行的pod的环境变量信息
kubectl exec pod名称 env

#查看pod日志
kubectl logs pod名称

#获取所有deployment
kubectl get deployment --all-namespaces

#查看具体某个deployment
kubectl get deployment nginx

#查看pods结构信息(特别是通过这个看日志分析错误)对控制器和服务，node同样有效
kubectl describe pods podsname --namespace=某具体namespace名称

#列出该 namespace 中的所有 pod 包括未初始化的
kubectl get pods --include-uninitialized

#按具体selector名来查找pod
kubectl get pod --selector name=nginx

#查看pod/svc/deployment下面的kube-system、namespace等信息(使用-o wide 选项则可以查看存在哪个对应的节点)
kubectl get pod/svc/deployment -n kube-system

#查看pod/svc/deployment下面的所有namespace
kubectl get pod/svc/deployment --all-namcpaces

#查看某个pod变量信息
kubectl exec my-nginx-69okjk -- printenv | grep SERVICE
```

## **5.集群类**

```shell
#集群健康情况
kubectl get cs

#集群核心组件运行情况
kubectl cluster-info

#命名空间
kubectl get namespaces

#获取版本信息
kubectl version

#获取API信息
kubectl api-versions

#查看事件信息
kubectl get events

#获取全部节点信息
kubectl get nodes

#删除某个节点
kubectl delete node node01

#回滚到上一个版本：
kubectl rollout undo deployment/nginx-test

也可以使用 --revision参数指定某个历史版本：
kubectl rollout undo deployment/nginx-test --to-revision=2

#历史记录
kubectl rollout history deployment/nginx-test

#验证发布
kubectl rollout status deploy nginx-test

#回滚发布
kubectl rollout undo deployments/nginx-test

#获取所有deployment
kubectl get deployment --all-namespaces

#获取所有svc
kubectl get svc --all-namespaces
```

## **6.创建**

```shell
#创建资源
kubectl create -f ./nginx.yaml

#创建+更新
kubectl apply -f xxx.yaml

#一键创建当前目录下的所有yaml资源
kubectl create -f .

#一键使用多个yaml文件创建资源
kubectl create -f ./nginx1.yaml -f ./mysql2.yaml -f ./redis3.yaml

#一键使用某目录下的所有yaml清单文件来创建资源
kubectl create -f ./dir

#使用具体的 url 来创建资源
kubectl create -f https://git.io/vPieo

#创建带有终端的pod
kubectl run -i --tty busybox --image=busybox

#启动一个 nginx 实例
kubectl run nginx --image=nginx

#启动多个副本的pod
kubectl run mybusybox --image=busybox --replicas=5

#获取 pod 和 svc 的文档
kubectl explain pods,svc

#使用命令创建yaml
kubectl create deployment my-dep --image=nginx -o yaml --dry-run >web.yaml
```

## **7.更新**

```shell
#滚动更新，且使用私有仓库
docker push xxx-xxx:5000/docker.io/http-server:v2.0
kubectl rolling-update http-server http-server-v2 --image=xxx-xxx:5000/docker.io/http-server:v2.0

#滚动更新升级（注意：当执行rolling-update命令前需要准备好新的RC配置文件以及ConfigMap配置文件，RC配置文件中需要指定升级后需要使用的镜像名称）
kubectl rolling-update redis -f redis-rc.update.yaml

#直接指定镜像名称的方式直接升级
kubeclt rolling-update redis --image=redis-2.0  

#退出已存在的进行中的滚动更新
kubectl rolling-update redis-v1 redis-v2 --rollback 

#部署、升级、降级事例
#运行版本v1
kubectl run kubernetes-bootcamp --image=docker.io/jocatalin/kubernetes-bootcamp:v1 --port=8080
#升级image版本v2
kubectl set image deployments/kubernetes-bootcamp kubernetes-bootcamp=jocatalin/kubernetes-bootcamp:v2
#降级image版本至v1
kubectl rollout undo deployments/kubernetes-bootcamp

#基于 stdin 输入的 JSON 替换 pod
cat pod.json | kubectl replace -f -

#强制替换，删除后重新创建资源。会导致服务中断。
kubectl replace --force -f ./pod.json
```

## **8.重建**

```shell
#为 nginx RC 创建服务，启用本地 80 端口连接到容器上的 8000 端口
kubectl expose rc nginx --port=80 --target-port=8000

#更新单容器 pod 的镜像版本（tag）到 v4
kubectl get pod nginx-pod -o yaml | sed 's/\(image: myimage\):.$/\1:v4/' | kubectl replace -f -

#添加标签
kubectl label pods nginx-pod new-label=awesome

#添加注解
kubectl annotate pods nginx-pod icon-url=http://goo.gl/XXBTWq

#自动扩展 deployment redis
kubectl autoscale deployment redis --min=2 --max=6
```

## **9.编辑资源**

```shell
#编辑名为 docker-registry 的 service
kubectl edit svc/docker-registry

#使用其它编辑器
KUBE_EDITOR="nano" kubectl edit svc/docker-registry

#修改启动参数
vim /etc/systemd/system/kubelet.service.d/10-kubeadm.conf
```

## **10.动态伸缩pod**

```shell
#将副本数增加或减少至3个
kubectl scale deployments/kubernetes-bootcamp --replicas=3

#将foo副本集变成3个
kubectl scale --replicas=3 rs/foo

#缩放“foo”中指定的资源
kubectl scale --replicas=3 -f foo.yaml

#将deployment/mysql从2个变成3个
kubectl scale --current-replicas=2 --replicas=3 deployment/mysql

#变更多个控制器的数量
kubectl scale --replicas=5 rc/foo rc/bar rc/baz

#查看变更进度
kubectl rollout status deploy deployment/mysql
```

## **11.删除**

```shell
#删除基于nginx.yaml定义的名称
kubectl delete -f nginx.yaml

#删除基于Pod.yaml定义的名称
kubectl delete -f pod.yaml

#删除基于rc名定义的名称
kubectl delete rc rc名

#删除基于service定义的名称
kubectl delete service service名

#删除所有Pod
kubectl delete pods --all

#根据label删除
kubectl delete pod -l app=flannel -n kube-system

#删除 pod.json 文件中定义的类型和名称的 pod
kubectl delete -f ./pod.json

#删除名为“baz”的 pod 和名为“foo”的 service
kubectl delete pod,service baz foo

#删除具有 name=myLabel 标签的 pod 和 serivce
kubectl delete pods,services -l name=myLabel

#删除具有 name=myLabel 标签的 pod 和 service，包括尚未初始化的
kubectl delete pods,services -l name=myLabel --include-uninitialized

#删除 my-ns namespace下的所有 pod 和 serivce，包括尚未初始化的
kubectl -n my-ns delete po,svc --all

#强制删除
kubectl delete pods prometheus-7fcfcb9f89-qkkf7 --grace-period=0 --force

#卸载kubernetes-dashboard
kubectl delete -f kubernetes-dashboard.yaml

#方法一：
kubectl delete deployment kubernetes-dashboard --namespace=kube-system 
//
kubectl delete service kubernetes-dashboard  --namespace=kube-system 
#或
kubectl delete svc kubernetes-dashboard --namespace=kube-system
//
kubectl delete role kubernetes-dashboard-minimal --namespace=kube-system 
kubectl delete rolebinding kubernetes-dashboard-minimal --namespace=kube-system
kubectl delete sa kubernetes-dashboard --namespace=kube-system 
kubectl delete secret kubernetes-dashboard-certs --namespace=kube-system
kubectl delete secret kubernetes-dashboard-csrf --namespace=kube-system
kubectl delete secret kubernetes-dashboard-key-holder --namespace=kube-system

方法二：
kubectl -n kube-system delete $(kubectl -n kube-system get pod -o name | grep dashboard)

#强制替换，删除后重新创建资源，该操作会导致服务中断。
kubectl replace --force -f ./pod.json 
```

## **12.交互**

```shell
 #dump 输出 pod 的日志（stdout）
kubectl logs nginx-pod

#dump 输出 pod 中容器的日志（stdout--pod 中有多个容器的情况下使用）
kubectl logs nginx-pod -c my-container

#流式输出 pod 的日志（stdout）
kubectl logs -f nginx-pod

#流式输出 pod 中容器的日志（stdout--pod 中有多个容器的情况下使用）
kubectl logs -f nginx-pod -c my-container

#交互式 shell 的方式运行 pod
kubectl run -i --tty busybox --image=busybox -- sh

#连接到运行中的容器
kubectl attach nginx-pod -i

#转发 pod 中的 6000 端口到本地的 5000 端口
kubectl port-forward nginx-pod 5000:6000

#在已存在的容器中执行命令（注意只有一个容器的情况下操作）
kubectl exec nginx-pod -- ls /

#在已存在的容器中执行命令（pod 中有多个容器的情况下）
kubectl exec nginx-pod -c my-container -- ls /

 #显示指定 pod和容器的指标度量
kubectl top pod POD_NAME --containers

#进入某pod内部
kubectl exec -ti podName /bin/bash
```

## **13.调度配置**

```shell
#标记 my-node 不可调度
kubectl cordon k8s-node

#清空 my-node 以待维护
kubectl drain k8s-node

#标记 my-node 可调度
kubectl uncordon k8s-node

#显示 my-node 的指标度量
kubectl top node k8s-node

#将当前集群状态输出到 stdout
kubectl cluster-info dump

#将当前集群状态输出到 /path/to/cluster-state
kubectl cluster-info dump --output-directory=/path/to/cluster-state

#如果该键和影响的污点（taint）已存在，则使用指定的值替换
kubectl taint nodes foo dedicated=special-user:NoSchedule

#出于安全考虑，默认配置下kubernetes不会将pod调试到master节点，如果希望master也当作节点使用
kubectl taint nodes --all node-role.kubernetes.io/master-
#或是执行
kubectl taint node k8s-master node-role.kubernetes.io/master-

#k8s-master1上禁止在所有master节点上发布应用
kubectl patch node k8s-master -p '{"spec":{"unschedulable":true}}'
#或者执行
kubectl taint node k8s-master node-role.kubernetes.io/master="":NoSchedule
```

## **14.etcdctl 常用操作**

```shell
 #检查网络集群健康状态
etcdctl cluster-health

#带有安全认证检查网络集群健康状态
etcdctl --endpoints=https://192.168.8.130:2379 cluster-health 

#列出集群成员列表
etcdctl member list

#设置etcd健值
etcdctl set /k8s/network/config ‘{ “Network”: “10.1.0.0/16” }’

#新建etcd键值
etcdctl  mk  /k8s/network/config

#获取etcd键值信息
etcdctl get /k8s/network/config

#删除所有etcd键值
etcdctl  rm   /k8s/network/   --recursive

#列出目录（默认为根目录）下的键或者子目录，默认不显示子目录中内容
etcdctl ls /k8s/network/subnets
```

## **15.label 操作**

```shell
#添加label值
kubectl label

#增加节点lable值 spec.nodeSelector: zone: north #指定pod在哪个节点
kubectl label nodes node1 zone=north

#增加lable值 [key]=[value]
kubectl label pod redis-master-1033017107-q47hh role=master 

#删除lable值
kubectl label pod redis-master-1033017107-q47hh role-

 #修改lable值
kubectl label pod redis-master-1033017107-q47hh role=backend --overwrite
```

## **16.导出配置文件**

```shell
#导出proxy
kubectl get ds -n kube-system -l k8s-app=kube-proxy -o yaml>kube-proxy-ds.yaml

#导出kube-dns
kubectl get deployment -n kube-system -l k8s-app=kube-dns -o yaml >kube-dns-dp.yaml

kubectl get services -n kube-system -l k8s-app=kube-dns -o yaml >kube-dns-services.yaml

#导出所有 configmap
kubectl get configmap -n kube-system -o wide -o yaml > configmap.yaml

#将pod容器端口映射到节点的端口
kubectl expose deployment/kubernetes-bootcamp --type="NodePort" --port 8080

#删除kube-system 下Evicted状态的所有pod：
kubectl get pods -n kube-system |grep Evicted| awk ‘{print $1}’|xargs kubectl delete pod -n kube-system
```

