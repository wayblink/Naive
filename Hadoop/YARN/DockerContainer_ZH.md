<!---
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License. See accompanying LICENSE file.
-->

Launching Applications Using Docker Containers
==============================================

使用Docker容器启动应用程序
=============================

<!-- MACRO{toc|fromDepth=0|toDepth=1} -->



概述
--------



[Docker]（https://www.docker.io/）将易于使用的Linux容器和易於面向容器構建的鏡像文件结合在一起。
简而言之，Docker使用户能够将应用程序与其運行所需要的環境變量捆绑在一起。 
更多关于Docker，看他们的[文档]（http://docs.docker.com）。



Linux容器执行器（LCE）允许YARN NodeManager启动YARN容器運行主机容器或者Docker容器。 
请求资源的应用程序可以为每个请求指定容器如何执行。 
LCE还提供增强的安全性,這在部署安全集群时是必需的。 
当YARN要啓用Docker容器执行應用程序時，应用程序可以指定Docker要使用的鏡像。


Docker容器提供了一个自定义應用程序执行环境。
它與NodeManager和其他应用程序的执行环境相互隔离。 
这些容器可以包括特殊的应用程序所需的库，并且它们可以有不同的版本本地工具和库，包括Perl，Python和Java。
容器甚至可以运行不同于NodeManager的Linux環境。



Docker为YARN提供了一致性（所有YARN容器都将具有相同的软件环境）和隔离（不對物理机造成任何幹擾）。


LCE的Docker支持仍在開發過程中。 可以跟蹤
[YARN-3611]（https://issues.apache.org/jira/browse/YARN-3611），
了解改进進度。

Cluster Configuration
---------------------

集群配置
---------------------



LCE要求容器 - 执行器二进制文件由root：hadoop擁有，并具有6050权限。 
为了启动Docker容器，Docker守护进程必须在所有将要启动Docker容器的NodeManager主机上运行。
Docker客户端也必须安装在Docker的所有NodeManager主机上。



为了防止在启动作业时超时，需要使用的任何大型Docker鏡像需要提前加载了应用程序到NodeManager主机。 
加载图像的一种简单方法是通过发布Docker pull请求。 例如：

```
    sudo docker pull images/hadoop-docker:latest
```

以下屬性需要在yarn-site.xml中設定：

```xml
<property>
  <name>yarn.nodemanager.container-executor.class</name>
  <value>org.apache.hadoop.yarn.server.nodemanager.LinuxContainerExecutor</value>
  <description>
    This is the container executor setting that ensures that all applications
    are started with the LinuxContainerExecutor.
  </description>
</property>

<property>
  <name>yarn.nodemanager.linux-container-executor.group</name>
  <value>hadoop</value>
  <description>
    The POSIX group of the NodeManager. It should match the setting in
    "container-executor.cfg". This configuration is required for validating
    the secure access of the container-executor binary.
  </description>
</property>

<property>
  <name>yarn.nodemanager.linux-container-executor.nonsecure-mode.limit-users</name>
  <value>false</value>
  <description>
    Whether all applications should be run as the NodeManager process' owner.
    When false, applications are launched instead as the application owner.
  </description>
</property>

<property>
  <name>yarn.nodemanager.runtime.linux.docker.allowed-container-networks</name>
  <value>host,none,bridge</value>
  <description>
    Optional. A comma-separated set of networks allowed when launching
    containers. Valid values are determined by Docker networks available from
    `docker network ls`
  </description>
</property>

<property>
  <description>The network used when launching Docker containers when no
    network is specified in the request. This network must be one of the
    (configurable) set of allowed container networks.</description>
  <name>yarn.nodemanager.runtime.linux.docker.default-container-network</name>
  <value>host</value>
</property>

<property>
  <name>yarn.nodemanager.runtime.linux.docker.privileged-containers.allowed</name>
  <value>false</value>
  <description>
    Optional. Whether applications are allowed to run in privileged containers.
  </description>
</property>

<property>
  <name>yarn.nodemanager.runtime.linux.docker.privileged-containers.acl</name>
  <value></value>
  <description>
    Optional. A comma-separated list of users who are allowed to request
    privileged contains if privileged containers are allowed.
  </description>
</property>
```


另外，一个container-executer.cfg文件必须存在并包含container executor的設置。 
文件必须由拥有权限为0400的root拥有。
文件的格式是标准的Java属性文件格式，例如：

    `key=value`

需要以下属性来启用Docker支持：

|Configuration Name | Description |
|:---- |:---- |
| `yarn.nodemanager.linux-container-executor.group` | The Unix group of the NodeManager. It should match the yarn.nodemanager.linux-container-executor.group in the yarn-site.xml file. |
| `feature.docker.enabled` | Must be 0 or 1. 0 means launching Docker containers is disabled. 1 means launching Docker containers is allowed. |


以下属性是可选的：

|Configuration Name | Description |
|:---- |:---- |
| `min.user.id` | The minimum UID that is allowed to launch applications. The default is no minimum |
| `banned.users` | A comma-separated list of usernames who should not be allowed to launch applications. The default setting is: yarn, mapred, hdfs, and bin. |
| `allowed.system.users` | A comma-separated list of usernames who should be allowed to launch applications even if their UIDs are below the configured minimum. If a user appears in allowed.system.users and banned.users, the user will be considered banned. |
| `docker.binary` | The path to the Docker binary. The default is "docker". |
| `feature.tc.enabled` | Must be 0 or 1. 0 means traffic control commands are disabled. 1 means traffic control commands are allowed. |


Docker鏡像要求
-------------------------


为了与YARN合作，Docker鏡像需要滿足两个要求。

首先，Docker容器将与应用程序一起显式启动，應用所有者將作为容器用户。 
如果应用程序所有者不是有效的用户，在Docker鏡像中，应用程序将失败。 
容器用户由用户的UID來確定。 如果NodeManager主机和Docker容器上的UID不同，
容器可能会以错误的用户身份启动或可能无法启动，因为UID不存在。



其次，Docker映像必须具有应用程序所期望的任何内容。 
例如，在Hadoop（MapReduce或Spark）的情况下，Docker鏡像必须包含JRE和Hadoop库并具有必要
环境变量集：JAVA_HOME，HADOOP_COMMON_PATH，HADOOP_HDFS_HOME，
HADOOP_MAPRED_HOME，HADOOP_YARN_HOME和HADOOP_CONF_DIR。 
请注意Docker镜像中提供Java和Hadoop组件版本必須与集群環境以及同一任務的其他Docker映像中的版本兼容。
否则在Docker容器中启动的Hadoop组件可能无法与外部的Hadoop组件通信。



如果Docker的图像有
[命令]（https://docs.docker.com/engine/reference/builder/#cmd）
设置，行为将取决于是否將
`YARN_CONTAINER_RUNTIME_DOCKER_RUN_OVERRIDE_DISABLE`设置为true。 如果是这样，
当LCE使用YARN启动鏡像时，命令将被覆盖容器启动脚本。



如果Docker的鏡像有[入口点]（https://docs.docker.com/engine/reference/builder/#entrypoint）
设置，入口点将被兑现，但默认命令可能是被覆盖，如上所述。 除非入口点是
类似于`sh -c`或者`YARN_CONTAINER_RUNTIME_DOCKER_RUN_OVERRIDE_DISABLE`设为true。
不建議使用入口點。



如果应用程序请求尚未加载的Docker映像， Docker守护程序将在主机上执行Docker pull命令。 
而MapReduce和Spark都有10分钟以上不做工作报告进度則認定爲停滯的機制。
因此，使用大的Docker鏡像可能会导致应用程序失败。


申请提交
----------------------


在尝试启动Docker容器之前，请确保LCE配置正確，可以爲应用程序请求普通的YARN容器。
如果在启用LCE后，一个或多个NodeManager无法启动，原因最有可能的是容器执行者的所有权和/或权限不正确。 
检查日志以确认。

为了在Docker容器中运行应用程序，请设置以下环境变量：

| Environment Variable Name | Description |
| :------------------------ | :---------- |
| `YARN_CONTAINER_RUNTIME_TYPE` | Determines whether an application will be launched in a Docker container. If the value is "docker", the application will be launched in a Docker container. Otherwise a regular process tree container will be used. |
| `YARN_CONTAINER_RUNTIME_DOCKER_IMAGE` | Names which image will be used to launch the Docker container. Any image name that could be passed to the Docker client's run command may be used. The image name may include a repo prefix. |
| `YARN_CONTAINER_RUNTIME_DOCKER_RUN_OVERRIDE_DISABLE` | Controls whether the Docker container's default command is overridden.  When set to true, the Docker container's command will be "bash _path\_to\_launch\_script_". When unset or set to false, the Docker container's default command is used. |
| `YARN_CONTAINER_RUNTIME_DOCKER_CONTAINER_NETWORK` | Sets the network type to be used by the Docker container. It must be a valid value as determined by the yarn.nodemanager.runtime.linux.docker.allowed-container-networks property. |
| `YARN_CONTAINER_RUNTIME_DOCKER_RUN_PRIVILEGED_CONTAINER` | Controls whether the Docker container is a privileged container. In order to use privileged containers, the yarn.nodemanager.runtime.linux.docker.privileged-containers.allowed property must be set to true, and the application owner must appear in the value of the yarn.nodemanager.runtime.linux.docker.privileged-containers.acl property. If this environment variable is set to true, a privileged Docker container will be used if allowed. No other value is allowed, so the environment variable should be left unset rather than setting it to false. |
| `YARN_CONTAINER_RUNTIME_DOCKER_LOCAL_RESOURCE_MOUNTS` | Adds additional volume mounts to the Docker container. The value of the environment variable should be a comma-separated list of mounts. All such mounts must be given as "source:dest", where the source is an absolute path that is not a symlink and that points to a localized resource. Note that as of YARN-5298, localized directories are automatically mounted into the container as volumes. |


前两个是必需的。 剩余部分可以根据需要进行设置。 而
通过环境变量来控制容器类型並不非常理想，它允许不關心YARN Docker支持的应用程序通过支持配置应用程序环境来利用它。


一旦应用程序提交到Docker容器中，应用程序的行为与任何其他YARN应用程序的行为完全相同。 
日志将会聚合并存储在相关历史服务器中。 应用生命周期将与非Docker应用程序相同。


连接到安全的Docker存储库
----------------------------------------

直到YARN-5428完成，Docker客户端命令都是从默认位置獲取配置，即NodeManager上的$ HOME / .docker / config.json。 
Docker配置是存储安全repository库凭据的位置，所以直到YARN-5428完成，都不推薦使用带有安全Docker repos的LCE。

作为一种解决方法，您可以在每个NodeManager上手动登录Docker守护程序
使用Docker登录命令主机进入安全备份：

```
  docker login [OPTIONS] [SERVER]

  Register or log in to a Docker registry server, if no server is specified
  "https://index.docker.io/v1/" is the default.

  -e, --email=""       Email
  -p, --password=""    Password
  -u, --username=""    Username
```

请注意，这种方法意味着所有用户都可以访问安全repo。

Example: MapReduce
------------------

要将pi作业提交到Docker容器中运行，请运行以下命令：

```
    vars="YARN_CONTAINER_RUNTIME_TYPE=docker,YARN_CONTAINER_RUNTIME_DOCKER_IMAGE=hadoop-docker"
    hadoop jar hadoop-examples.jar pi -Dyarn.app.mapreduce.am.env=$vars \
        -Dmapreduce.map.env=$vars -Dmapreduce.reduce.env=$vars 10 100
```

请注意，這個應用的master，map，reduce任務被分別配置。 在这个例子中，三個任務我们都使用hadoop-docker鏡像。

Example: Spark
--------------

要在Docker容器中运行Spark shell，请运行以下命令：

```
    spark-shell --master yarn --conf spark.executorEnv.YARN_CONTAINER_RUNTIME_TYPE=docker \
        --conf spark.executorEnv.YARN_CONTAINER_RUNTIME_DOCKER_IMAGE=hadoop-docker \
        --conf spark.yarn.AppMasterEnv.YARN_CONTAINER_RUNTIME_DOCKER_IMAGE=hadoop-docker \
        --conf spark.yarn.AppMasterEnv.YARN_CONTAINER_RUNTIME_TYPE=docker
```

请注意，独立配置了应用程序主控和执行程序。 在这个例子中，我们使用的hadoop-docker鏡像。
