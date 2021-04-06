### 1.生成公钥和私钥证书

```shell
#如果去掉 --global 参数只对当前仓库有效。
git config ––global user.name 'username'
git config ––global user.email 'username@test.com'

ssh-keygen -t rsa -C 'username@test.com'
```



### 2.基本操作


```shell
Git 的工作就是创建和保存你项目的快照及与之后的快照进行对比。

Git 常用的是以下 6 个命令：
git clone、git push、git add 、git commit、git checkout、git pull

#git的各个区域划分
workspace：工作区
staging area：暂存区/缓存区
local repository：版本库或本地仓库
remote repository：远程仓库

#workspace---staging area---local repository---remote repository
git fetch/clone #从远程仓库拉取到本地仓库
git pull #从远程仓库拉取到工作区里
git push #从本地仓库推送到远程仓库
git checkout #从本地仓库加载到工作区中
git add #从工作区添加到暂存区
git commit #从暂存区提交到本地仓库

#一个简单的操作：
git init	#初始化仓库
git add .	#将文件添加到暂存区
git commit -m [message]	#将暂存区内容添加到仓库中 -a 不用add直接提交 -am [message]

git clone url	#拷贝一个 Git 仓库到本地 -b 指定要克隆的分支
git clone http://www.kernel.org/pub/scm/git/git.git	#直接克隆某个分支
git clone -b master2 ../server . #不指定表示克隆master分支

git status	#比较上次提交后是否有文件再次修改 加-s获得简短输出结果


#git diff 命令比较文件的不同，即比较文件在暂存区和工作区的差异。
#git diff 命令显示已写入暂存区和已经被修改但尚未写入暂存区文件对区别。
#尚未缓存的改动：git diff
#查看已缓存的与未缓存的所有改动：git diff HEAD
#显示摘要而非整个 diff：git diff --stat
git diff #显示暂存区和工作区的差异
git diff --cached [file]  #显示暂存区和上一次提交(commit)的差异
git diff [first-branch]...[second-branch]	#显示两次提交之间的差异

#git reset 命令用于回退版本，可以指定退回某一次提交的版本。
#git reset [--soft | --mixed | --hard] [HEAD]
#--mixed 为默认，可以不用带该参数，用于重置暂存区的文件与上一次的提交(commit)保持一致，工作区文件内容保持不变。
#--soft 参数用于回退到某个版本：
#--hard 参数撤销工作区中所有未提交的修改内容，将暂存区与工作区都回到上一次版本，并删除之前的所有信息提交
$ git reset HEAD^            # 回退所有内容到上一个版本  
$ git reset HEAD^ hello.php  # 回退 hello.php 文件的版本到上一个版本  
$ git  reset  052e           # 回退到指定版本
$ git reset --soft HEAD~3 # 回退上上上一个版本
$ git reset –hard HEAD~3  # 回退上上上一个版本  
$ git reset –hard bae128  # 回退到某个版本回退点之前的所有信息。 
$ git reset --hard origin/master    # 将本地的状态回退到和远程的一样 

注意：谨慎使用 –hard 参数，它会删除回退点之前的所有信息。
HEAD 说明：
HEAD 表示当前版本
HEAD^ 上一个版本
HEAD^^ 上上一个版本
HEAD^^^ 上上上一个版本
以此类推...
可以使用 ～数字表示
HEAD~0 表示当前版本
HEAD~1 上一个版本
HEAD^2 上上一个版本
HEAD^3 上上上一个版本
类推...

#简而言之，执行 git reset HEAD 以取消之前 git add 添加，但不希望包含在下一提交快照中的缓存。


git rm <file>	#从暂存区和工作区中删除文件	-f强制删除
git rm --cache <file>	#只从暂存区中删除，保留在当前工作目录中
git rm -r *  #删除文件下的所有文件和子目录

git mv [file] [newfile]	#移动或重命名工作区文件。-f 强制移动


git log	#查看历史提交记录	 --oneline 选项来查看历史记录的简洁的版本
#我们还可以用 --graph 选项，查看历史中什么时候出现了分支、合并。
#可以用 --reverse 参数来逆向显示所有日志
$ git log --reverse --oneline
#查找指定用户的提交日志可以使用命令：git log --author
$ git log --author=Linus --oneline -5 #要找 Git 源码中 Linus 提交的部分
#如果你要指定日期，可以执行几个选项：--since 和 --before，但是你也可以用 --until 和 --after。
#如果我要看 Git 项目中三周前且在四月十八日之后的所有提交，我可以执行这个（我还用了 --no-merges 选项以隐藏合并提交）：
$ git log --oneline --before={3.weeks.ago} --after={2010-04-18} --no-merges

git blame <file>	#以列表形式查看指定文件的历史修改记录 不会用

git tag		#查看打的标签

git remote	#远程仓库操作
git remote -v #显示所有远程仓库
git remote show https://github.com/tianqixin/runoob-git-test	#显示远程某个仓库的信息
git remote add [shortname] [url]	#添加远程版本库	shortname 为本地的版本库
#$ git remote add origin git@github.com:tianqixin/runoob-git-test.git
#$ git push -u origin master
git remote rm name  # 删除远程仓库
git remote rename old_name new_name  # 修改仓库名


git fetch	#从远程获取代码库
git merge	#从远端仓库提取数据并尝试合并到当前分支	
git fetch [alias]	#假设你配置好了一个远程仓库，并且你想要提取更新的数据，你可以首先执行:当前命令告诉 Git 去获取它有你没有的数据
git merge [alias]/[branch]	#将服务器上的任何更新（假设有人这时候推送到服务器了）合并到你的当前分支
git fetch origin	#我们在本地更新修改




git pull	#下载远程代码并合并	git pull <远程主机名> <远程分支名>:<本地分支名>
#git pull 其实就是 git fetch 和 git merge FETCH_HEAD 的简写
$ git pull
$ git pull origin
git pull origin master:brantest	#将远程主机 origin 的 master 分支拉取过来，与本地的 brantest 分支合并（这个超级好用）
#如果远程分支是与当前分支合并，则冒号后面的部分可以省略
git pull origin master

git push	#上传远程代码并合并	git push <远程主机名> <本地分支名>:<远程分支名>
#本地分支名与远程分支名相同，则可以省略冒号	git push <远程主机名> <本地分支名>

$ git push origin master	#将本地的 master 分支推送到 origin 主机的 master 分支
#相等于：
$ git push origin master:master
#强制推送可以使用 --force 参数：
git push --force origin master

#删除主机但分支可以使用 --delete 参数，以下命令表示删除 origin 主机的 master 分支：
git push origin --delete master




```



### 3.Commit and Push 冲突处理

```
commit and push时，系统会检查出文件有冲突，提示需要合并，出现冲突窗口

点击文件，即可进入详情页面

详情页面分为三栏，左边为本地版本，中间为修改前版本，右边为服务器最新版本，左右两边代码不可以修改

当前冲突页面1所指按钮可以切换冲突位置，2处按钮可以让冲突部分高亮显示，3处可以选择文件是否整体滑动。

基于两个版本对中间版本修改后点击apply，即生成最终版本的文件，选择提交

这时会提示上传失败，最好将文件重新编译一下，确认无误后重新push即可
```



### 4.取消提交

```
有的时候我们不想push已经commit，可以在idea中进行如下操作取消

项目右键选择git->Repository->Reset HEAD

在To Commit中填入回退的版本信息， HEAD^ 代表回退到上次提交前，HEAD~n 数字代表往回退的版本数，回退的代码不会丢失，想要提交的话再次commit即可。
```



### 5.查看已经备份的历史版本

```shell
右键文件->git->showHistory

git config --list  #查看当前的配置
```



### 6.给项目添加仓库

```shell
git init 

#当前路径下所有添加到git里面（把项目加入到本地仓库的stage区暂存）
git add .

#还需要提交一下  
git commit -m "提交内容描述信息"

#首先要建好一个远端仓库(URL),如https://xxx/SpringBootInAction.git
git remote add origin https://xxx/SpringBootInAction.git
git push -u -f origin master
```



### 7.常规流程

```shell
git status #查看本地分支文件信息，确保更新时不产生冲突

git checkout – [file name] #若文件有修改，可以还原到最初状态; 若文件需要更新到服务器上，应该先merge到服务器，再更新到本地

git branch #查看当前分支情况

git checkout remote branch #若分支为本地分支，则需切换到服务器的远程分支
	
git pull	#拉取
```

### 8.快速流程

```shell
#上面是比较安全的做法，如果你可以确定什么都没有改过只是更新本地代码 

git pull #拉取

git branch #看看分支 

git branch -a  #查看远程分支

git branck aaa  #创建aaa分支 

git chechout aaa  #切换分支aaa 

#删除本地分支： 
git branch -d 分支名称

#强制删除本地： 
git branch -D 分支名称

#删除远程分支: 
git push origin --delete 远程分支名称

#从公用仓库fetch代码： 
git fetch origin branchname1:branchname1

#然后才可以切换分支 
git checkout branchname1

#本地创建 aaa分支，同时切换到aaa分支。只有提交的时候才会在服务端上创建一个分支
git chechout -b aaa 

#移除大文件的正确姿势
$ git rm --cached giant_file（文件名） 

```



### 9.大文件上传报错的处理

Stage our giant file for removal, but leave it on disk  

```shell
$ git commit --amend -CHEAD  #--no-edit修改提交而不更改提交消息		--amend对上一次的提交进行修改

$ git push	#提交代码到远程仓库

git fetch -f -p	#从本地拿到远程最新分支，覆盖本地存放的远程分支

git checkout dev	#切换到dev分支

git reset origin/dev --hard		#把自己的本地dev分支覆盖，使用远程的分支

```

```shell
git status 		#查看未被传送到远程代码库的提交次数

git cherry -v 	#查看未被传送到远程代码库的提交描述和说明

git reset commit_id 	#撤销未被传送到远程代码库的提交

#做到这里就已经可以重新添加提交了（注意一定要撤销有大文件的提交）

#"git restore --staged <file>..." to unstage)    
#这个不行  git checkout
```



### 10.统计代码量

```shell
git log --since="2020-02-01" --before="2021-02-28" --author="author_name" --pretty=tformat: --numstat | awk '{ add += $1; subs += $2; loc += $1 - $2 } END { printf "added lines: %s, removed lines: %s, total lines: %s\n", add, subs, loc }'
```



### 11.添加的.gitignore可能不起作用

```shell
1)
	要先进入项目包所在的文件夹
2)	
	git rm -r –cached . #后面有个点
3)
	git add . #后面有个点
4)
	git commit -m  "update .gitignore"	#注意中英文符号
5)
	git push #提交
```



### 12.查看日志

```shell
git log -p 
#-p 或 --patch 显示每次提交所引入的差异
#--stat 选项在每次提交的下面列出所有被修改过的文件、有多少文件被修改了以及被修改过的文件的哪些行被移除或是添加了。 
# 在每次提交的最后还有一个总结。

$ git log --since=2.weeks
#列出最近两周的所有提交:该命令可用的格式十分丰富——可以是类似 "2008-01-15" 的具体的某一天，也可以是类似 "2 years 1 day 3 minutes ago" 的相对日期。

--author 			选项显示指定作者的提交
--grep 				选项搜索提交说明中的关键字。
-<n>				仅显示最近的 n 条提交。
--since, --after	仅显示指定时间之后的提交。
--until, --before	仅显示指定时间之前的提交。
--author			仅显示作者匹配指定字符串的提交。
--committer			仅显示提交者匹配指定字符串的提交。
--grep				仅显示提交说明中包含指定字符串的提交。
-S					仅显示添加或删除内容匹配指定字符串的提交。
```



### 13.git pull时冲突的几种解决方式

```shell
#1. 忽略本地修改，强制拉取远程到本地
   git fetch --all
   git reset --hard origin/eureka
   git pull
#2. 未commit先pull，视本地修改量选择revert或stash

#如果本地修改量小，例如只修改了一行，可以按照以下流程
-> revert(把自己的代码取消) -> 重新pull -> 在最新代码上修改 -> [pull确认最新] -> commit&push

#本地修改量大，冲突较多，有两种方式处理
-> stash save(把自己的代码隐藏存起来) -> 重新pull -> stash pop(把存起来的隐藏的代码取回来 ) -> 代码文件会显示冲突 -> 右键选择edit conficts，解决后点击编辑页面的 mark as resolved->  commit&push

-> stash save(把自己的代码隐藏存起来) -> 重新pull -> stash pop(把存起来的隐藏的代码取回来 ) -> 代码文件会显示冲突 -> 右键选择resolve conflict -> 打开文件解决冲突 ->commit&push
```



#### 13.1.idea图形界面解决冲突

```shell
git->Repository->Stash Changes

git->pull

UnStash Changes

git->commit

git->push
```



#### 13.2.命令行解决冲突

```shell
#本地修改存储起来：
git stash

#查看保存信息：
git stash list

#还原暂存的内容：
git stash pop stash@{0}
```

```shell
git branch -a 

git diff --stat --color remotes/main/master..origin/master

git diff remotes/main/master/..origin/master
```
