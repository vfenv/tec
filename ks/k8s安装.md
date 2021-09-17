手工搭建 Kubernetes 集群是一件很繁琐的事情，为了简化这些操作，就产生了很多安装配置工具，如 Kubeadm ，Kubespray，RKE 等组件，我最终选择了官方的 Kubeadm 主要是不同的 Kubernetes 版本都有一些差异，Kubeadm 更新与支持的会好一些。Kubeadm 是 Kubernetes 官方提供的快速安装和初始化 Kubernetes 集群的工具

| 系统       | 内核                  | docker  | ip             | 主机名 | 配置  |
| ---------- | --------------------- | ------- | -------------- | ------ | ----- |
| centos 7.6 | 3.10.0-957.el7.x86_64 | 19.03.5 | 192.168.31.150 | master | 2核4G |
| centos 7.6 | 3.10.0-957.el7.x86_64 | 19.03.5 | 192.168.31.183 | node01 | 2核4G |

# 1.安装docker

略...



# 2.kubelet、kubctl、kubeadm区别

kubelet：运行在集群cluster所有节点上，负责启动POD和容器

kubeadm：用于初始化集群cluster，集群安装工具

kubectl：kubectl是kubenetes命令行工具，通过kubectl可以部署和管理应用，查看各种资源，创建，删除和更新组件



# 3.准备工作

## 3.1关闭防火墙

如果各个主机启用了防火墙，需要开放Kubernetes各个组件所需要的端口，可以查看Installing kubeadm中的”Check required ports”一节。 这里简单起见在各节点禁用防火墙：

```shell
systemctl stop firewalld
systemctl disable firewalld
```

## 3.2禁用selinux

```shell
# 临时禁用
setenforce 0
# 永久禁用 
vim /etc/selinux/config    # 或者修改/etc/sysconfig/selinux
SELINUX=disabled
```

## 3.3修改k8s.conf文件

```shell
cat <<EOF >  /etc/sysctl.d/k8s.conf
net.bridge.bridge-nf-call-ip6tables = 1
net.bridge.bridge-nf-call-iptables = 1
EOF
sysctl --system

#开启ipv4转发
vi /etc/sysctl.conf
#添加
net.ipv4.ip_forward = 1

#刷新参数
sysctl -p
```

## 3.4关闭swap

```shell
# 临时关闭
swapoff -a

#永久关闭
#修改 /etc/fstab 文件，注释掉 SWAP 的自动挂载（永久关闭swap，重启后生效）
# 注释掉以下字段  前面加上#
/dev/mapper/cl-swap     swap                    swap    defaults        0 0
```

## 3.5修改主机名

```shell
#注意：主机名不能带下划线，只能带中划线,否则安装k8s会报错
$ hostnamectl set-hostname master

$ hostname
#master
```



# 4.安装kubelet、kubctl、kubeadm

修改yum安装源

```shell
cat <<EOF > /etc/yum.repos.d/kubernetes.repo
[kubernetes]
name=Kubernetes
baseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64/
enabled=1
gpgcheck=0
repo_gpgcheck=0
gpgkey=http://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg http://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
EOF
```

安装软件（master和node都需要操作）

```shell
#查看所需的镜像列表
$ yum list --showduplicates kubelet|grep 1.18
$ yum list --showduplicates kubeadm|grep 1.18
$ yum list --showduplicates kubectl|grep 1.18
#本地下载rpm包
$ yum install -y kubelet-1.18.6-0 kubeadm-1.18.6-0 kubectl-1.18.6-0
#kubelet设置开启启动并开启
$ systemctl enable kubelet && systemctl start kubelet
```



# 5.初始化Master节点

## 5.1运行初始化命令

```shell
kubeadm init --kubernetes-version=1.18.6 \
--apiserver-advertise-address=192.168.31.150 \
--image-repository registry.aliyuncs.com/google_containers \
--service-cidr=10.1.0.0/16 \
--pod-network-cidr=10.244.0.0/16
#–kubernetes-version: 用于指定k8s版本；
#–apiserver-advertise-address：用于指定kube-apiserver监听的ip地址,就是 master本机IP地址。
#–pod-network-cidr：用于指定Pod的网络范围； 10.244.0.0/16
#–service-cidr：用于指定SVC的网络范围；
#–image-repository: 指定阿里云镜像仓库地址

#注意：修改–apiserver-advertise-address为你自己的环境的master ip
#这一步很关键，由于kubeadm 默认从官网k8s.grc.io下载所需镜像，国内无法访问，因此需要通过–image-repository指定阿里云镜像仓库地址

#生成的token的其它节点加入集群时需要添加
```

## 5.2配置kubectl工具

```shell
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
```

## 5.3安装calico

```shell
mkdir k8s
cd k8s
wget https://docs.projectcalico.org/v3.10/getting-started/kubernetes/installation/hosted/kubernetes-datastore/calico-networking/1.7/calico.yaml

## 将192.168.0.0/16修改ip地址为10.244.0.0/16
sed -i 's/192.168.0.0/10.244.0.0/g' calico.yaml

#加载Calico
kubectl apply -f calico.yaml
```

## 5.5查看Pod状态

```shell
kubectl get pod --all-namespaces -o wide
#等待几分钟，确保所有的Pod都处于Running状态

#注意：calico-kube-controllers容器的网段不是10.244.0.0/16
kube-system   calico-kube-controllers-6b64bcd855-tdv2h   1/1     Running   0          2m37s   192.168.235.195   k8s-master   <none>           <none>

#删除Calico，重新加载
kubectl delete -f calico.yaml
kubectl apply -f calico.yaml

#再次查看ip，发现，ip地址已经是10.244.0.0/16 网段了。
kubectl get pod --all-namespaces -o wide
```

## 5.6Node加入集群

```shell
#修改主机名部分，改为k8s-node01
hostnamectl set-hostname node01

#加入节点
#登录到node节点，确保已经安装了docker和kubeadm，kubelet，kubectl
#添加任意数量的工作节点到集群中
kubeadm join 192.168.31.150:6443 --token ute1qr.ylhan3tn3eohip20 \
--discovery-token-ca-cert-hash sha256:f7b37ecd602deb59e0ddc2a0cfa842f8c3950690f43a5d552a7cefef37d1fa31

#设置开机启动
systemctl enable kubelet

#查看节点
kubectl get nodes -o wide

#创建nginx
kubectl create deployment nginx --image=nginx
kubectl expose deployment nginx --port=80 --type=NodePort

kubectl get pod,svc -o wide

#允许外网访问nodePort
iptables -P FORWARD ACCEPT
```







# 6.CentOS使用kubeadm手动安装集群

基于4.安装kubelet、kubctl、kubeadm基础上，如果不使用yum install -y kubelet-1.18.6-0 kubeadm-1.18.6-0 kubectl-1.18.6-0安装，则就要手动下载镜像到本地服务器，然后逐个去安装。

## 6.1.master镜像

```shell
kubeadm config images list
#会得到一个要列表，使用docker pull 逐个拉取
img_list='
k8s.gcr.io/kube-catapiserver:v1.18.6
k8s.gcr.io/kube-controller-manager:v1.18.6
k8s.gcr.io/kube-scheduler:v1.18.6
k8s.gcr.io/kube-proxy:v1.18.6
k8s.gcr.io/pause:3.2
k8s.gcr.io/etcd:3.4.3-0
k8s.gcr.io/coredns:1.6.7
```

```shell
#使用脚本下载镜像并导出
#/bin/bash
url=k8s.gcr.io
version=v1.18.5
images=(`kubeadm config images list --kubernetes-version=$version|awk -F '/' '{print $2}'`)
for imagename in ${images[@]} ; do
  #echo $imagename
  echo "docker pull $url/$imagename"
  docker pull $url/$imagename
done
 
images=(`kubeadm config images list --kubernetes-version=$version`)
echo "docker save ${images[@]} -o kubeDockerImage$version.tar"
docker save ${images[@]} -o kubeDockerImage$version.tar
```



## 6.2.worker镜像

```shell
#只需要 proxy 和 pause
#在主机中
docker save -o kube-proxy.tar k8s.gcr.io/kube-proxy:v1.18.6
docker save -o pause.tar k8s.gcr.io/pause:3.2
scp kube-proxy.tar pause.tar worker1:/root/
scp kube-proxy.tar pause.tar worker2:/root/
#在客户机
docker load -i kube-proxy.tar
docker load -i pause.tar
```

## 6.3安装前系统设置

```yaml
/etc/sysctl``.conf 添加参数
net.bridge.bridge-nf-call-ip6tables = 1
net.bridge.bridge-nf-call-iptables = 1
net.ipv4.ip_forward = 1
生效：sysctl -p
关闭防火墙和Selinux
关闭swap　
```

## 6.4升级内核

```shell
#1、查看当前内核
[root@dev-k8s-master-1-105 ~]# uname -a
Linux dev-k8s-master-1-105 3.10.0-957.1.3.el7.x86_64 #1 SMP Thu Nov 29 14:49:43 UTC 2018 x86_64 x86_64 x86_64 GNU/Linux
[root@dev-k8s-master-1-105 ~]#
#2、导入ELRepo仓库的公共密钥
rpm --import https://www.elrepo.org/RPM-GPG-KEY-elrepo.org
#3、安装ELRepo仓库的yum源
rpm -Uvh http://www.elrepo.org/elrepo-release-7.0-3.el7.elrepo.noarch.rpm
#4、查看可用的系统内核包
yum --disablerepo="*" --enablerepo="elrepo-kernel" list available

#lt稳定版 安装这个
#5、安装内核
yum --enablerepo=elrepo-kernel install kernel-lt.x86_64 -y
--enablerepo 选项开启 CentOS 系统上的指定仓库。默认开启的是 elrepo，这里用 elrepo-kernel 替换。

#6、
 awk -F \' '$1=="menuentry " {print i++ " : " $2}' /etc/grub2.cfg
#7、
#8、
#9. 查看当前实际启动顺序
grub2-editenv list
#10. 设置默认启动
grub2-set-default 0　
grub2-editenv list
#11、重启
reboot
#12、卸载老版本
```

## 6.5安装rpm并导入镜像

```shell
yum install -y ./rpmKubeadm/*.rpm
docker load -i kubeDockerImagev1.18.5.tar
```

## 6.6集群初始化

```shell
#主机
kubeadm init --kubernetes-version=v1.18.6 --pod-network-cidr=172.16.0.0/16 --apiServer-adverties-address=192.168.216.100
```

k8s一共有三个网路：节点网络、集群网络、POD网络，后二个通过插件进行配置

上面的初始化是采取的cidr网络，如果使用fliner或者是lelyfull网络，默认是10.244.0.0/16，可以后期修改网络的时候再写

初始化执行完毕生成的信息要存储在文档里，后面的操作还要用到里面的内容。

```shell
#配置文件处理
mkdir -p $HOME/.kube
sudo cp -i /etc/kubenetes/admin.conf ~/.kube/config
#sudo chown $(id -u):$(id -g) ~/.kube/config   #root登录安装的不用改这个

#添加任意数量的工作节点到集群中
kubeadm join 192.168.216.100:6443 ...token...
```

## 6.7安装网络

```shell
wget https://docs.projectcalico.org/v3.14/manifests/calico.yaml
kuberctl apply -f calico.yaml
```
