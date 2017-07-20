Docker Trouble Shooting
==========================

1, Sending build context to docker daemon
-----------------------------------------

当使用Dockerfile创建镜像时（docker build [-options] image_name Dockerfile_path）：会出现
```
Sending build context to docker daemon XXX.XX MB
```
的提示，有时传输的内容会非常大，严重影响build速度。

搜索发现，build指令默认是将对应路徑下的所有文件都加載到进程中，解决方式有两种：

1.使用.dockerignore文件，设置黑名单，该文件包含的目录不会被发送到Docker daemon中
2.将Dockerfile迁移后其他目录中执行。


2，更换软件源

经常使用docker基础的linux镜像进行部署，然而国内使用官方软件源下载应用简直慢的令人发指。
因此必须将软件源换成高速的源，我一般都换成阿里的源。

Dockerfile如下：

```
FROM ubuntu:latest

ADD sources.list /etc/apt/
```
就是将/etc/apt/下的sources.list文件替换掉

sources.list:阿里源

```
deb http://mirrors.aliyun.com/ubuntu/ trusty main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ trusty-security main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ trusty-updates main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ trusty-proposed main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ trusty-backports main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ trusty main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ trusty-security main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ trusty-updates main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ trusty-proposed main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ trusty-backports main restricted universe multiverse
