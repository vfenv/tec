### 查看版本
conda –V


### 通过anaconda安装不同的python环境
conda create –n python27 python=2.7 


### 安装3.6环境
conda create –n python36 python=3.6


### 安装完成后通过命令进入到不同的环境中
source activate python36

conda activate base


### 退出环境
source deactivate python36

conda deactivate

### 编辑conda环境变量
vim ~/.bashrc


### 查看存在哪些虚拟机环境
conda info –e 或者 conda env list

### 删除相应的环境
conda remove –n python27 –all

### 查看安装了哪些包
conda list。

### 查询conda的命令使用
conda update conda，conda --version，conda -h 


### 对虚拟环境中安装额外的包

conda install -n aaaaa [package]

如果已经在环境里 使用下面命令直接安装三方包

conda install 包名  


### 删除环境中的某个包
conda remove --name aaaaa package_name
