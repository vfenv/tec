# Kubernetes Storage (k8s存储)

临时存储:emptyDir

半持久化存储:hostPath

持久化存储:pvc，pv，nfs

分布式存储： glusterfs，rbd，cephfs，云存储(EBS,等)

查看K8s支持多少种存储： kubectl explain pods.spec.volumes

K8S的存储系统从基础到高级又大致分为三个层次：普通Volume，Persistent Volume 和动态供给(Dynamic Provisioning)。

# 1.Volume

K8s中的卷寿命与封装它的pod相同。卷比pod中所有容器的生命周期都要长，当这个容器重启时数据仍然可以保存。Pod不存在，卷也不复存在。

- 容器中的 Volume

  容器的 Volume，其实就是将一个宿主机上的目录，跟一个容器里的目录绑定挂载在了一起。

- 持久化 Volume

  宿主机上的目录，具备“持久性”。即：这个目录里面的内容，既不会因为容器的删除而被清理掉，也不会跟当前的宿主机绑定。这样，当容器被重启或者在其他节点上重建出来之后，它仍然能够通过挂载这个 Volume，访问到这些内容。

卷类型

empityDir、hostPath、local、nfs、iscsi、persistentVolume、PersistentVolumeClain、projectd、rbd、secret

# 2.PV(Persistent Volume)

PV 描述的是持久化存储卷，主要定义的是一个持久化存储在宿主机上的目录，比如一个 NFS 的挂载目录。

PV定义的只是卷，真正使用时需要使用PVC进行声明。

PVC消费PV资源，声明可以请求特定的存储大小和访问模式。

```yaml
apiVersion: v1
kind: PersistentVolume
metadata:
  name: nfs
spec:
  storageClassName: manual
  capacity:
    storage: 1Gi
  accessModes:
    - ReadWriteMany
  nfs:
    server: 192.168.217.149
    path: "/"
```

## 2.1 PV访问模式

PV可以以资源提供者支持的方式挂载到主机上。供应商具有不同的功能，每个PV访问模式都将被设置为该卷支持的特定模式。

例如NFS可以支持多个读写客户端，但特定的NFS PV可能以只读方式导出到服务器上，每个PV都有一套自己的用来描述特定功能的访问模式。

- ReadwriteOnce(RWO) 该卷可以被单个节点以读写模式挂载

- ReadOnlyMany(ROX) 该卷可以被多个节点以只读模式挂载

- ReadWriteMany(ROX) 该卷可以被多个节点以读写模式挂载

## 2.2 PVC(Persistent Volume Claim)

持久化卷声明：PVC是POD对存储的请求的声明。

描述的则是Pod所希望使用的持久化存储的属性。比如Volume存储的大小、可读写权限等等。

```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: nfs
spec:
  accessModes:
    - ReadWriteMany
  storageClassName: manual
  resources:
    requests:
      storage: 1Gi
```

## 2.3 PV和PVC绑定

用户根据所需存储空间大小和访问模式创建（或在动态部署中已创建）一个PVC。

- Kubernetes的Master节点循环监控新产生的PVC，找到与之匹配的PV（如果有的话），并把他们绑定在一起。

动态配置时，循环会一直将PV与这个PVC绑定，直到PV完全匹配PVC。避免PVC请求和得到的PV不一致。

绑定一旦形成，PersistentVolumeClaim绑定就是独有的，不管是使用何种模式绑定的。

- 如果找不到匹配的volume，用户请求会一直保持未绑定状态。在匹配的volume可用之后，用户请求将会被绑定。

Volume Controller：管理多个控制循环，将PV与PVC进行绑定（PV名称写入PVC对象的spec.volumeName）

- 第一个条件，当然是 PV 和 PVC 的 spec 字段。比如，PV 的存储（storage）大小，就必须满足 PVC 的要求。

- 第二个条件，则是 PV 和 PVC 的 storageClassName 字段必须一样。

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: nginx-nfs
  labels:
    role: web-frontend
spec:
  containers:
  - name: web
    image: nginx
    ports:
      - name: web
        containerPort: 80
    volumeMounts:
      - name: nfs
        mountPath: "/usr/share/nginx/html"
  volumes:
    - name: nfs
      persistentVolumeClaim:
        claimName: nfs
```

## 2.4 处理PV流程

### 2.4.1 两阶段

#### 1）第一阶段Attach

当一个 Pod 调度到一个节点上之后，kubelet 就要负责为这个 Pod 创建它的 Volume 目录。

默认情况下，kubelet 为 Volume 创建的目录是如下所示的一个宿主机上的路径：

```shell
/var/lib/kubelet/pods/<Pod的ID>/volumes/kubernetes.io~<Volume类型>/<Volume名字>
```

接下来，kubelet 要做的操作就取决于你的 Volume 类型了
gcloud compute instances attach-disk <虚拟机名字> --disk <远程磁盘名字>

#### 2）第二阶段Mount

Attach 阶段完成后，为了能够使用这个远程磁盘，kubelet 还要进行第二个操作，即：格式化这个磁盘设备，然后将它挂载到宿主机指定的挂载点上。

```shell
#通过lsblk命令获取磁盘设备ID
$ lsblk
#格式化成ext4格式
$ sudo mkfs.ext4 -m 0 -F -E lazy_itable_init=0,lazy_journal_init=0,discard
#挂载到挂载点
$ sudo mkdir -p /var/lib/kubelet/pods/<Pod的ID>/volumes/kubernetes.io~<Volume类型>/<Volume名字>
#将宿主机目录与pod定义VolumeMount目录关联
$ docker run -v  /var/lib/kubelet/pods/<Pod的ID>/volumes/kubernetes.io~<Volume类型>/<Volume名字>
```



## 2.5 PV生命周期

一个PV创建完后状态会变成Available，等待被PVC绑定。

一旦被PVC邦定，PV的状态会变成Bound，就可以被定义了相应PVC的Pod使用。

Pod使用完后会释放PV，PV的状态变成Released。

变成Released的PV会根据定义的回收策略做相应的回收工作。有三种回收策略，Retain、Delete 和 Recycle。

- Retain就是保留现场，K8S什么也不做，等待用户手动去处理PV里的数据，处理完后，再手动删除PV。

- Delete 策略，K8S会自动删除该PV及里面的数据。

- Recycle方式，K8S会将PV里的数据删除，然后把PV的状态变成Available，又可以被新的PVC绑定使用。

```mermaid
graph LR;
Provisioning-->Binding-->Using-->Releasing-->Reclaiming

```

### 2.5.1 Provisioning

这里有两种PV的提供方式:静态或者动态

- Static：集群管理员创建多个PV。 它们携带可供集群用户使用的真实存储的详细信息。 它们存在于Kubernetes API中，可用于消费。
- Dynamic：当管理员创建的静态PV都不匹配用户的PersistentVolumeClaim时，集群可能会尝试为PVC动态配置卷。 此配置基于StorageClasses：PVC必须请求一个类，并且管理员必须已创建并配置该类才能进行动态配置。 要求该类的声明有效地为自己禁用动态配置

### 2.5.2 Binding

在动态配置的情况下，用户创建或已经创建了具有特定数量的存储请求和特定访问模式的PersistentVolumeClaim。 主机中的控制回路监视新的PVC，找到匹配的PV（如果可能），并将它们绑定在一起。 如果为新的PVC动态配置PV，则循环将始终将该PV绑定到PVC。 否则，用户总是至少得到他们要求的内容，但是卷可能超出了要求。 一旦绑定，PersistentVolumeClaim绑定是排他的，不管用于绑定它们的模式。

如果匹配的卷不存在，PVC将保持无限期。 随着匹配卷变得可用，PVC将被绑定。 例如，提供许多50Gi PV的集群将不匹配要求100Gi的PVC。 当集群中添加100Gi PV时，可以绑定PVC。

### 2.5.3 Using

Pod使用PVC作为卷。 集群检查声明以找到绑定的卷并挂载该卷的卷。 对于支持多种访问模式的卷，用户在将其声明用作pod中的卷时指定所需的模式。

一旦用户有声明并且该声明被绑定，绑定的PV属于用户，只要他们需要它。 用户通过在其Pod的卷块中包含persistentVolumeClaim来安排Pods并访问其声明的PV。 

### 2.5.4 Releasing

当用户完成卷时，他们可以从允许资源回收的API中删除PVC对象。 当声明被删除时，卷被认为是“释放的”，但是它还不能用于另一个声明。 以前的索赔人的数据仍然保留在必须根据政策处理的卷上.

### 2.5.5 Reclaiming

PersistentVolume的回收策略告诉集群在释放其声明后，该卷应该如何处理。 目前，卷可以是保留，回收或删除。 保留可以手动回收资源。 对于那些支持它的卷插件，删除将从Kubernetes中删除PersistentVolume对象，以及删除外部基础架构（如AWS EBS，GCE PD，Azure Disk或Cinder卷）中关联的存储资产。 

动态配置的卷始终被删除

### 2.5.6 回收策略

- Retain – 手动重新使用

- Recycle – 基本的删除操作 (“rm -rf /thevolume/*”)

- Delete – 关联的后端存储卷一起删除

后端存储例如AWS EBS, GCE PD或OpenStack Cinder
目前只有NFS和HostPath支持回收，AWS EBS, GCE PD和Cinder volumes只支持删除。

### 2.5.7 卷的状态

- Available –闲置状态，没有被绑定到PVC

- Bound – 绑定到PVC

- Released – PVC被删掉，资源没有被在利用

- Failed – 自动回收失败

## 2.6 StorageClass

人工管理PV的方式叫作Static Provisionning

自动创建PV的机制叫作Dynamic Provisioning：

StorageClass对象就是创建PV的模板。

### 2.6.1 StorageClass

- 作用就是创建PV模板

  PV的属性。比如存储类型，Volume的大小等等

  创建这种PV需要用到的存储插件。如Ceph等等

StorageCalss定义

```yaml
apiVersion: ceph.rook.io/v1beta1
kind: Pool
metadata:
  name: replicapool
  namespace: rook-ceph
spec:
  replicated:
    size: 3
---
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: block-service
provisioner: ceph.rook.io/block
parameters:
  pool: replicapool
  #The value of "clusterNamespace" MUST be the same as the one in which your rook cluster exist
  clusterNamespace: rook-ceph
```

```shell
$ kubectl create -f sc.yaml
```

PVC定义

```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: claim1
spec:
  accessModes:
    - ReadWriteOnce
  storageClassName: block-service
  resources:
    requests:
      storage: 30Gi
```

```shell
$ kubectl create -f pvc1.yaml
$ kubectl describe pvc claim1
```

 

## 2.7 PV类型

PV类型以插件形式实现。

K8s目前支持如下插件类型：
NFS
RBD（Ceph Block Device）
CephFS
Cinder
Glusterfs
HostPath
VMware
GCEPersistentDisk
AWSElasticStore
AzureFile
Fibre Channel
FlexVolume
ISCSI
等

https://kubernetes.io/docs/concepts/storage/persistent-volumes/#types-of-persistent-volumes

 

## 2.8 实战1 基于NFS类型的PV

**1）首先创建NFS服务**

```shell
#nfs-utils所有节点都需要安装
$ yum install nfs-utils rpcbind -y
$ systemctl restart rpcbind nfs
#编辑共享配置文件
/etc/exports写入/mnt/share *(rw)
#生效
$ exportfs -r
#[NFS共享目录] [NFS客户端地址1(参数1,参数2,参数3……)] [客户端地址2(参数1,参数2,参数3……)]
$ mkdir /mnt/share
$ chmod 777 /mnt/share
$ showmount -e
```

**2）创建PV**

```shell
$ vim pv1.yaml
```

```yaml
apiVersion: v1
kind: PersistentVolume
metadata:
  name: nfs
spec:
  storageClassName: manual
  capacity:
    storage: 1Gi
  accessModes:
    - ReadWriteMany
  nfs:
    server: 192.168.217.149
    path: "/mnt/share"
```

```shell
$ kubectl get pv
$ kubectl describe pv nfs
```

**3）创建PVC**

```shell
vim pvc1.yaml
```

```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: nfs
spec:
  accessModes:
    - ReadWriteMany
  storageClassName: manual
  resources:
    requests:
      storage: 1Gi

```

```shell
$ kubectl get pvc
$ kubectl describe pvc nfs
```

**4）创建pod调用pvc**

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: nginx-nfs
  labels:
    role: web-frontend
spec:
  containers:
  - name: web
    image: nginx
    ports:
      - name: web
        containerPort: 80
    volumeMounts:
        - name: nfs
          mountPath: "/usr/share/nginx/html"
  volumes:
  - name: nfs
    persistentVolumeClaim:
      claimName: nfs
```

```shell
#查看
$ kubectl get pod
$ echo hello >/mnt/share/1
$ cat /mnt/share/1
$ kubectl exec -it nginx-nfs /bin/bash
$ ls /usr/share/nginx/html/
$ echo nginx > /usr/share/nginx/html/2
$ ls /usr/share/nginx/html/
$ cat /usr/share/nginx/html/1
$ cat /usr/share/nginx/html/2
#退出查看
$ exit
#删除/mnt/share/1
$ cd /mnt/share
$ rm -rf 1
$ kubectl exec -it nginx-nfs /bin/bash
```



## 2.9 实战2 基于NFS的动态PV

NFS StorageClass    https://github.com/kubernetes-incubator/external-storage/blob/master/nfs/deploy/kubernetes

接着实战1的环境，先做点准备工作

```shell
$ mkdir /nfs3
$ chmod 777 /nfs3
#在/etc/exports写入/nfs3 *(rw)
$ exportfs -r  #生效
```

**1）创建RBAC授权rbac.yaml**

```yaml
kind: ClusterRole
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: nfs-provisioner-runner
rules:
  - apiGroups: [""]
    resources: ["persistentvolumes"]
    verbs: ["get", "list", "watch", "create", "delete"]
  - apiGroups: [""]
    resources: ["persistentvolumeclaims"]
    verbs: ["get", "list", "watch", "update"]
  - apiGroups: ["storage.k8s.io"]
    resources: ["storageclasses"]
    verbs: ["get", "list", "watch"]
  - apiGroups: [""]
    resources: ["events"]
    verbs: ["create", "update", "patch"]
  - apiGroups: [""]
    resources: ["services", "endpoints"]
    verbs: ["get"]
  - apiGroups: ["extensions"]
    resources: ["podsecuritypolicies"]
    resourceNames: ["nfs-provisioner"]
    verbs: ["use"]
---
kind: ClusterRoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: run-nfs-provisioner
subjects:
  - kind: ServiceAccount
    name: nfs-provisioner
    namespace: default
roleRef:
    kind: ClusterRole
    name: nfs-provisioner-runner
    apiGroup: rbac.authorization.k8s.io
---
kind: Role
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: leader-locking-nfs-provisioner
rules:
  - apiGroups: [""]
    resources: ["endpoints"]
    verbs: ["get", "list", "watch", "create", "update", "patch"]
---
kind: RoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: leader-locking-nfs-provisioner
subjects:
  - kind: ServiceAccount
    name: nfs-provisioner
    namespace: default
roleRef:
    kind: Role
    name: leader-locking-nfs-provisioner
    apiGroup: rbac.authorization.k8s.io
```

**2）serviceaccount.yaml文件**

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: nfs-provisioner
```

**3）创建Storageclass类**

storageclass-nfs.yaml

```yaml
kind: StorageClass
apiVersion: storage.k8s.io/v1
metadata:
  name: nfs
provisioner: example.com/nfs

```

**4）创建nfs的deployment**

修改相应的nfs服务器ip及挂载路径即可。

deployment-nfs.yaml

拉取镜像registry.cn-hangzhou.aliyuncs.com/open-ali/nfs-client-provisioner

```shell
$ docker pull registry.cn-hangzhou.aliyuncs.com/open-ali/nfs-client-provisioner
```

deployment-nfs.yaml文件内容

```yaml
kind: Deployment
apiVersion: extensions/v1beta1
metadata:
  name: nfs-provisioner
spec:
  replicas: 1
  strategy:
    type: Recreate
  template:
    metadata:
      labels:
        app: nfs-provisioner
    spec:
      serviceAccount: nfs-provisioner
      containers:
        - name: nfs-provisioner
          image: registry.cn-hangzhou.aliyuncs.com/open-ali/nfs-client-provisioner
          volumeMounts:
            - name: nfs-client-root
              mountPath: /persistentvolumes
          env:
            - name: PROVISIONER_NAME
              value: example.com/nfs
            - name: NFS_SERVER
              value: 192.168.217.149
            - name: NFS_PATH
              value: /nfs3
      volumes:
        - name: nfs-client-root
          nfs:
            server: 192.168.217.149
            path: /nfs3
```

```shell
$ kubectl get pod
```

**5）创建测试claim**

```yaml
kind: PersistentVolumeClaim
apiVersion: v1
metadata:
  name: test-claim1
spec:
  accessModes:
    - ReadWriteMany
  resources:
    requests:
      storage: 1Mi
  storageClassName: nfs
```

```shell
$ kubectl get pvc
```

**6）创建一个测试的Pod使用这个PVC**

编写test-pod.yaml文件如下

```yaml
kind: Pod
apiVersion: v1
metadata:
  name: test-pod
spec:
  containers:
  - name: test-pod
    image: busybox
    command:
      - "/bin/sh"
    args:
      - "-c"
      - "touch /mnt/SUCCESS && exit 0 || exit 1"
    volumeMounts:
      - name: nfs-pvc
        mountPath: "/mnt"
  restartPolicy: "Never"
  volumes:
    - name: nfs-pvc
      persistentVolumeClaim:
        claimName: test-claim1
```

查看Pod状态是否变为Completed。如果是，则应该能在NFS系统的共享路径中看到一个SUCCESS文件。

这样，StorageClass动态创建PV的功能就成功实现了。

```shell
$ ls /nfs3/default-test-claim1-pvc-bab1c62f-8080-11ea-811d-000c29bec19e/
SUCCESS
```

**7）创建StatefulSet案例**

```yaml
apiVersion: apps/v1beta1
kind: StatefulSet
metadata:
  name: web
spec:
  serviceName: "nginx1"
  replicas: 2
  volumeClaimTemplates:
  - metadata:
      name: test
      annotations:
        volume.beta.kubernetes.io/storage-class: "nfs"
    spec:
      accessModes: [ "ReadWriteOnce" ]
      resources:
        requests:
          storage: 1Gi
  template:
    metadata:
      labels:
        app: nginx1
    spec:
      serviceAccount: nfs-provisioner
      containers:
      - name: nginx1
        image: nginx
        imagePullPolicy: IfNotPresent
        volumeMounts:
        - mountPath: "/persistentvolumes"
          name: test
```

测试：

```shell
#进入web-0测试
$ ls /nfs3/default-test-web-0-pvc-306e26d2-8081-11ea-811d-000c29bec19e/
$ kubectl exec -it web-0 /bin/bash
root@web-0:/$ ls /persistentvolumes/
root@web-0:/$ echo hello > /persistentvolumes/1
root@web-0:/$ exit

#回到宿主机kubernetes01
$ ls /nfs3/default-test-web-0-pvc-306e26d2-8081-11ea-811d-000c29bec19e/
$ cat  /nfs3/default-test-web-0-pvc-306e26d2-8081-11ea-811d-000c29bec19e/1

#在回到web-0
$ kubectl exec -it web-0 /bin/bash
$ ls /persistentvolumes/
$ cat /persistentvolumes/2
$ rm -rf /persistentvolumes/2
$ rm -rf /persistentvolumes/1
$ exit
```

这时宿主机也没有了，而web-1和web-0是隔离的，并不互相影响。



# 3.Local Persistent Volume

“本地”持久化存储，直接使用宿主机上的本机磁盘目录，而不依赖于远程存储服务，来提供“持久化”的容器Volume。

## 3.1 应用场景

- 分布式数据存储：MongoDB

- 分布式文件系统：GlusterFS，Ceph
  进行数据缓存的分布式应用

## 3.2 实现原理

将本地硬盘抽象成PV：一个PV一块盘？

Pod正常调度到指定Node

在开始使用 Local Persistent Volume 之前，你首先需要在集群里配置好磁盘或者块设备。

实现方法：

- 第一种，你的宿主机挂载并格式化一个可用的本地磁盘，这也是最常规的操作；

- 第二种，对于实验环境，你其实可以在宿主机上挂载几个 RAM Disk（内存盘）来模拟本地磁盘。

## 3.3 实战

采用第二种

**1）在kubernetes02宿主机上创建挂载点，用RAM Disk模拟本地磁盘**

```shell
# 在kubernetes02上执行
$ mkdir -p /mnt/disks/vol1
$ mount -t tmpfs vol1 /mnt/disks/vol1

$ mkdir -p /mnt/disks/vol2
$ mount -t tmpfs vol2 /mnt/disks/vol2

$ mkdir -p /mnt/disks/vol3
$ mount -t tmpfs vol3 /mnt/disks/vol3
```

**2）创建本地磁盘对应PV：local-pv.yaml**

```yaml
apiVersion: v1
kind: PersistentVolume  #pv
metadata:
  name: example-pv
spec:
  capacity:
    storage: 5Gi  #容量5g
  volumeMode: Filesystem  #卷模式 文件系统
  accessModes:
  - ReadWriteOnce  #访问模式 一次读写
  persistentVolumeReclaimPolicy: Delete  #持久化卷的重新声明策略
  storageClassName: local-storage
  local:
    path: /mnt/disks/vol1  #本地路径
  nodeAffinity:  #节点亲和性
    required:
      nodeSelectorTerms:
      - matchExpressions:  #匹配表达式
        - key: kubernetes.io/hostname  #key
          operator: In  #操作  是 in
          values:
          - kubernetes02  #值是kubernetes02-->也就是hostname 是 值是kubernetes02的主机。
```

```shell
$ kubectl create -f local-pv.yaml
$ kubectl get pv
```

**3）创建StorageClass描述上面创建的PV：local-sc.yaml**

```yaml
kind: StorageClass
apiVersion: storage.k8s.io/v1
metadata:
  name: local-storage
provisioner: kubernetes.io/no-provisioner  #供应者：无供应者
volumeBindingMode: WaitForFirstConsumer  #卷绑定模式：等待第一个消费者绑定
```

- provisioner: kubernetes.io/no-provisioner：因为 Local Persistent Volume 目前尚不支持 Dynamic Provisioning，所以它没办法在用户创建 PVC 的时候，就自动创建出对应的 PV。也就是说，我们前面创建 PV 的操作，是不可以省略的。

- volumeBindingMode=WaitForFirstConsumer：延迟绑定，当你提交了 PV 和 PVC 的 YAML 文件之后，Kubernetes 就会根据它们俩的属性，以及它们指定的 StorageClass 来进行绑定。只有绑定成功后，Pod 才能通过声明这个 PVC 来使用对应的 PV。

Pod 声明 PVC pvc1,node Affinity: node2

local PV: pv1,pv2

pv1：磁盘所在的节点node1。。。。pvc1和pv1绑定了

pv2：磁盘所在的节点node2

#kubectl create Pod

所以pvc的绑定不能够提前

**4）创建PVC：local-pvc.yaml**

```yaml
kind: PersistentVolumeClaim
apiVersion: v1
metadata:
  name: example-local-claim  #pvc name
spec:
  accessModes:
  - ReadWriteOnce
  resources:
    requests:
      storage: 5Gi
  storageClassName: local-storage  #使用存储类名
```

```shell
$kubectl create -f local-pvc.yaml
$kubectl get pvc
```

 **5）编写pod使用这个PVC：pod.yaml**

```yaml
kind: Pod
apiVersion: v1
metadata:
  name: example-pv-pod
spec:
  volumes:
    - name: example-pv-storage  #定义卷，卷名称要与挂载时volumeMounts.name一样
      persistentVolumeClaim:
       claimName: example-local-claim  #pvc name
  containers:
    - name: example-pv-container
      image: nginx
      ports:
        - containerPort: 80
          name: "http-server"
      volumeMounts:
        - mountPath: "/usr/share/nginx/html"
          name: example-pv-storage
```

**6）测试文件写入**

```shell
# 在kubernetes01上
[root@kubernetes01 ~]$ kubectl exec -it example-pv-pod /bin/bash
root@example-pv-pod:/usr/share/nginx/html$ ls
root@example-pv-pod:/usr/share/nginx/html$ echo hello > test.txt     
root@example-pv-pod:/usr/share/nginx/html$ ls
test.txt

# 在kubernetes02上
[root@kubernetes02 ~]$ ls /mnt/disks/vol1
test.txt
[root@kubernetes02 ~]$ cat /mnt/disks/vol1/test.txt 
hello
```

**7）删除pod测试**

```shell
[root@kubernetes01 ~]$ kubectl delete -f pod.yaml
# 回到kubernetes02
[root@kubernetes02 ~]$ ls /mnt/disks/vol1
hello  test.txt
```

这时会发现原来数据还在，再次创建pod.yaml

```shell
[root@kubernetes01 ~]$ kubectl apply -f pod.yaml 
pod/example-pv-pod created
[root@kubernetes01 ~]$ kubectl get pod
NAME             READY   STATUS    RESTARTS   AGE
example-pv-pod   1/1     Running   0          25s
[root@kubernetes01 ~]$ kubectl exec -it example-pv-pod /bin/bash
root@example-pv-pod:/$ ls /usr/share/nginx/html/
hello  test.txt
```

这时会发现上次pod创建的数据还在，神奇。。。。

```shell
root@example-pv-pod:/$ echo shenqi > /usr/share/nginx/html/zhenshenqi
# 回到kubernetes02
[root@kubernetes02 ~]# ls /mnt/disks/vol1
hello  test.txt  zhenshenqi
```

 

# 4.总结

## 4.1 Volume

单节点Volume是最简单的普通Volume，它和Docker的存储卷类似，使用的是Pod所在K8S节点的本地目录。

具体有两种：

- 一种是 emptyDir，是一个匿名的空目录，由Kubernetes在创建Pod时创建，删除Pod时删除。

- 另外一种是 hostPath，与emptyDir的区别是，它在Pod之外独立存在，由用户指定路径名。这类和节点绑定的存储卷在Pod迁移到其它节点后数据就会丢失，所以只能用于存储临时数据或用于在同一个Pod里的容器之间共享数据。

## 4.2 PV

普通Volume和使用它的Pod之间是一种静态绑定关系，在定义Pod的文件里，同时定义了它使用的Volume。

Volume 是Pod的附属品，我们无法单独创建一个Volume，因为它不是一个独立的K8S资源对象。

而Persistent Volume 简称PV是一个K8S资源对象，所以我们可以单独创建一个PV。

它不和Pod直接发生关系，而是通过Persistent Volume Claim，简称PVC来实现动态绑定。

Pod定义里指定的是PVC，然后PVC会根据Pod的要求去自动绑定合适的PV给Pod使用。

## 4.3 PVC

## 4.4 LPV

## 4.5 PV和普通Volume的区别

普通Volume和使用它的Pod之间是一种静态绑定关系，在定义Pod的文件里，同时定义了它使用的Volume。

Volume 是Pod的附属品，我们无法单独创建一个Volume，因为它不是一个独立的K8S资源对象。

而Persistent Volume 简称PV是一个K8S资源对象，所以我们可以单独创建一个PV。

它不和Pod直接发生关系，而是通过Persistent Volume Claim，简称PVC来实现动态绑定。

Pod定义里指定的是PVC，然后PVC会根据Pod的要求去自动绑定合适的PV给Pod使用。

PV的访问模式有三种：

- 第一种，ReadWriteOnce：是最基本的方式，可读可写，但只支持被单个Pod挂载。

- 第二种，ReadOnlyMany：可以以只读的方式被多个Pod挂载。

- 第三种，ReadWriteMany：这种存储可以以读写的方式被多个Pod共享。

不是每一种存储都支持这三种方式，像共享方式，目前支持的还比较少，比较**常用的是NFS**。

在PVC绑定PV时通常根据两个条件来绑定，一个是**存储的大小**，另一个就是**访问模式**。
