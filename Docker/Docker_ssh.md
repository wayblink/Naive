
```
docker pull ubuntu
```

```
mkdir -p /home/docker/home
```

```
docker run -v /home/docker/home/:/home -it ubuntu

```

进入docker ubuntu容器

更新程序库
```
apt-get update
```

安装ssh

```
apt-get install ssh
```

为了方便，我安装了vim，也可以不装

```
apt-get install -y vim
```

