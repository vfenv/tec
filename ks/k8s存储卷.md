# 1.存储卷

## 1、存储卷概述

由于容器本身是非持久化的，当容器崩溃时，kubelet将重新启动容器，这时写入容器的文件将会丢失，容器将会以镜像的初始状态重新开始；第二，在通过一个Pod中一起运行的容器，通常需要共享容器之间一些文件。Kubernetes通过存储卷解决上述的两个问题。

在Docker有存储卷的概念卷，但Docker中存储卷只是磁盘的或另一个容器中的目录，并没有对其生命周期进行管理。Kubernetes的存储卷有自己的生命周期，它的生命周期与使用的它Pod生命周期一致。因此，相比于在Pod中运行的容器来说，存储卷的存在时间会比的其中的任何容器都长，并且在容器重新启动时会保留数据。当然，当Pod停止存在时，存储卷也将不再存在。在Kubernetes支持多种类型的卷，而Pod可以同时使用各种类型和任意数量的存储卷。在Pod中通过指定下面的字段来使用存储卷：

- *spec.volumes*：通过此字段提供指定的存储卷
- *spec.containers.volumeMounts*：通过此字段将存储卷挂接到容器中



## 2、存储卷类型和示例

当前Kubernetes支持如下所列这些存储卷类型，并以hostPath、nfs和persistentVolumeClaim类型的存储卷为例，介绍如何定义存储卷，以及如何在Pod中被使用。

**awsElasticBlockStore、azureDisk、azureFile、cephfs、configMap、csi、downwardAPI、emptyDir、fc (fibre channel)、flocker、gcePersistentDisk、gitRepo、glusterfs、hostPath、iscsi、local、nfs、persistentVolumeClaim、projected、portworxVolume、quobyte、rbd、scaleIO、secret、storageos、vsphereVolume**

### 2.1、hostPath

hostPath类型的存储卷用于将宿主机的文件系统的文件或目录挂接到Pod中，除了需要指定path字段之外，在使用hostPath类型的存储卷时，也可以设置type，type支持的枚举值由下表。另外在使用hostPath时，需要注意下面的事项：

- 具有相同配置的Pod（例如：从同一个podTemplate创建的），可能会由于Node的文件不同，而行为不同。
- 在宿主机上创建的文件或目录，只有root用户具写入的权限。您要么在容器中以root身份运行进程，要么在主机上修改的文件或目录的权限，以便具备写入内容到hostPath的存储卷中。

|         值          |                             行为                             |
| :-----------------: | :----------------------------------------------------------: |
|                     | 空字符串（默认）是用于向后兼容，这意味着在挂接主机路径存储卷之前不执行任何检查。 |
| `DirectoryOrCreate` | 如果path指定目录不存在，则会在宿主机上创建一个新的目录，并设置目录权限为0755，此目录与kubelet拥有一样的组和拥有者。 |
|     `Directory`     |                    path指定的目标必需存在                    |
|   `FileOrCreate`    | 如果path指定的文件不存在，则会在宿主机上创建一个空的文件，设置权限为0644，此文件与kubelet拥有一样的组和拥有者。 |
|       `File`        |                    path指定的文件必需存在                    |
|      `Socket`       |                path指定的UNIX socket必需存在                 |
|    `CharDevice`     |                  path指定的字符设备必需存在                  |
|    `BlockDevice`    |               在path给定路径上必须存在块设备。               |

下面是使用hostPath作为存储卷的YAML文件，此YAML文件定义了一个名称为test-pd的Pod资源。它通过hostPath类型的存储卷，将Pod宿主机上的/data挂接到容器中的/teset-pd目录。

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: test-pd
spec:
  containers:
    - image: k8s.gcr.io/test-webserver
      name: test-container
      # 指定在容器中挂接路径
      volumeMounts:
        - mountPath: /test-pd
          name: test-volume
  # 指定所提供的存储卷
  volumes:
    - name: test-volume
      hostPath:
        path: /data  # 宿主机上的目录
        type: Directory 
```

### 2.2、NFS

在Kubernetes中，可以通过nfs类型的存储卷将现有的NFS（网络文件系统）到的挂接到Pod中。在移除Pod时，NFS存储卷中的内容被不会被删除，只是将存储卷卸载而已。这意味着在NFS存储卷总可以预先填充数据，并且可以在Pod之间共享数据。NFS可以被同时挂接到多个Pod中，并能同时进行写入。需要注意的是：在使用nfs存储卷之前，必须已正确部署和运行NFS服务器，并已经设置了共享目录。

下面是一个redis部署的YAML配置文件，redis在容器中的持久化数据保存在/data目录下；存储卷使用nfs，nfs的服务地址为：192.168.8.150，存储路径为：/k8s-nfs/redis/data。容器通过*volumeMounts.name*的值确定所使用的存储卷。

```yaml
apiVersion: apps/v1 # for versions before 1.9.0 use apps/v1beta2
kind: Deployment
metadata:
  name: redis
spec:
  selector:
    matchLabels:
      app: redis
  revisionHistoryLimit: 2
  template:
    metadata:
      labels:
        app: redis
    spec:
      containers:
        - image: redis
          name: redis
          imagePullPolicy: IfNotPresent
          # 应用的内部端口
          ports:
            - containerPort: 6379
              name: redis6379
          env:
            - name: ALLOW_EMPTY_PASSWORD
              value: "yes"
            - name: REDIS_PASSWORD
              value: "redis"   
        	# 持久化挂接位置，在docker中 
          volumeMounts:
            - name: redis-persistent-storage
              mountPath: /data
      volumes:
        - name: redis-persistent-storage
          nfs:
            path: /k8s-nfs/redis/data
            server: 192.168.8.150

```

### 2.3、persistentVolumeClaim

persistentVolumeClaim类型存储卷将PersistentVolume挂接到Pod中作为存储卷。使用此类型的存储卷，用户并不知道存储卷的详细信息。

此处定义名为busybox-deployment的部署YAML配置文件，使用的镜像为busybox。基于busybox镜像的容器需要对**/mnt**目录下的数据进行持久化，在YAML文件指定使用名称为nfs的PersistenVolumeClaim对容器的数据进行持久化。

```yaml
apiVersion: v1
kind: Deployment
metadata:
  name: busybox-deployment
spec:
  replicas: 2
  selector:
    name: busybox-deployment
  template:
    metadata:
      labels:
        name: busybox-deployment
    spec:
      containers:
        - image: busybox
          imagePullPolicy: IfNotPresent
          name: busybox
          command:
            - sh
            - -c
            - 'echo 123'
          volumeMounts:
            - name: nfs  # name must match the volume name below  
              mountPath: "/mnt"
      volumes:
        - name: nfs
          persistentVolumeClaim:
            claimName: nfs-pvc
```



