Git Trouble Shooting List
======================

1, error: server certificate verification failed 解决方案
-----------------

error: server certificate verification failed

在stackoverflow 上有一页专门解答：[server certificate verification failed](https://stackoverflow.com/questions/21181231/server-certificate-verification-failed-cafile-etc-ssl-certs-ca-certificates-c)，针对不同情况，里面有很多的解决方案。 

我的属于系统时间错误，和下面这个问题相同：

```
Another cause of this problem might be that your clock might be off. Certificates are time sensitive.
This was my problem. My university was blocking ntp packets, which was preventing my system from updating time. 
Once I configured the university ntp servers things were working again. Thanks for this tip! –  Kyle Mar 9 at 16:51
```

问题重现

```
****@****:~$ date -R
Sat, 18 Jan 2014 06:11:27 +0800
****@****:~$ git clone https://github.com/appache/hadoop.git
Cloning into 'CPluslab'...
error: server certificate verification failed. CAfile: /etc/ssl/certs/ca-certificates.crt CRLfile: none while accessing https://github.com/bugmeout/CPluslab.git/info/refs
fatal: HTTP request failed
```

由于不能够和时间服务器通信，用命令修改系统时间为当前时间之后，问题得到解决。

```
sudo date -s 'xx:xx:xx xxxx-xx-xx'
```

2，git设置代理
------------

```
git config --global http.proxy http://ip:port
git config --global https.proxy http://ip:port
```
