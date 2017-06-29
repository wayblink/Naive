This is leaning notes for Hadoop Yarn

The source code structure:

Modules
-------
YARN consists of multiple modules. The modules are listed below as per the directory structure:

hadoop-yarn-api - Yarn's cross platform external interface

hadoop-yarn-common - Utilities which can be used by yarn clients and server

hadoop-yarn-server - Implementation of the hadoop-yarn-api  
	hadoop-yarn-server-common - APIs shared between resourcemanager and nodemanager  
	hadoop-yarn-server-nodemanager (TaskTracker replacement)  
	hadoop-yarn-server-resourcemanager (JobTracker replacement)  

