Configuring YARN container execution

YARN supports two different configurations to control how containers are executed. You can define these configurations in the yarn-site.xml file by updating the yarn.nodemanager.container-executor.class property.
About this task

The default value set by BigInsights and IBM Open Platform for Apache Hadoop in non-secure clusters is org.apache.hadoop.yarn.server.nodemanager.DefaultContainerExecutor. This class runs all containers as the Yarn user to avoid accidental operations being executed in the NodeManagers by arbitrary users.

The alternative value for this property is org.apache.hadoop.yarn.server.nodemanager.LinuxContainerExecutor. This class executes containers with the container-executor binary, which performs a privilege escalation to run containers as the users that submitted the application request.

While the default value is recommended in non-secure clusters, it is possible to configure the LinuxContainerExecutor when there is a need to execute containers as the submitting user, or when containers need additional privileges, such as to use cgroups.

Do the following steps to enable LinuxContainerExecutor:
Procedure

    From the Ambari web interface, select the Yarn service, and then select the Config > Advanced tabs. Expand the Advanced yarn-site section.
    Change the yarn.nodemanager.container-executor.class property to org.apache.hadoop.yarn.server.nodemanager.LinuxContainerExecutor in the YARN configuration window.
    Allow arbitrary users to run containers by setting this property in the YARN configuration:

    yarn.nodemanager.linux-container-executor.nonsecure-mode.limit-users = false

    Set the owner and permissions for the container-executor:

    chown root:hadoop /usr/iop/4.1.0.0/hadoop-yarn/bin/container-executor 
    chmod 6050 /usr/iop/4.1.0.0/hadoop-yarn/bin/container-executor

    Modify /etc/hadoop/conf/container-executor.cfg in all NodeManagers, ensuring it contains the following values (or any other appropriate values, if non-defaults are used):

    yarn.nodemanager.local-dirs=/hadoop/yarn/local 
    yarn.nodemanager.linux-container-executor.group=hadoop 
    yarn.nodemanager.log-dirs=/hadoop/yarn/log 
    banned.users=hdfs,yarn,mapred,bin 
    min.user.id=200

    Ensure that the /etc/hadoop/conf/container-executor.cfg file is owned by the user root and group hadoop:

    chown root:hadoop /etc/hadoop/conf/container-executor.cfg

    Restart YARN so that the changes take effect.

