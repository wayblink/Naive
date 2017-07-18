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


2，
