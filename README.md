### 一 数据库主从配置

#### （一）主库配置

**1、修改配置文件vim /etc/my.cnf，添加配置：**

~~~
[mysqld]
log-bin=mysql-bin #开启二进制日志
server-id=100  #id随意，不要和从库相同就行
~~~

**2、重启mysql服务：**

~~~
systemctl restart mysqld
~~~

**3、进入mysql，执行命令：**

~~~
grant replication slave on *.* to 'xiaoming'@'%' identified by 'Root@123456';
~~~

赋予'xiaoming'用户从库的权利，账号为xiaoming，密码为Root@123456

 **4、执行下面sql，记录结果中的File和Position的值（从库会用到）**

~~~
show master status;
~~~


#### （二）从库配置

**1、修改配置文件vim /etc/my.cnf，添加配置：**

~~~
[mysqld]
server-id=101  #id随意，不要和主库相同就行
~~~

**2、重启mysql服务：**

~~~
systemctl restart mysqld
~~~

**3、进入mysql，执行两条命令：**

~~~
change master to master_host='192.168.179.134',master_user='xiaoming',master_password='Root@123456',master_log_file='mysql-bin.000001',master_log_pos=441;

start slave;（开启一个线程跑 slave）
~~~


上面的命令中，master_host为主库电脑的IP，master_user和master_password为刚才主库设置的用户名和密码，master_log_file和master_log_pos为刚才主库查看的master status。



**注意：如果使用本机的两台虚拟机做主从库，可能导致两个mysql服务的uuid相同而主从库不生效*。需要找到任意一台虚拟机的auto.cnf配置文件(一般在"/var/lib/mysql/auto.cnf"），删除该文件，或者更改server-uuid与主库不同即可。若下面两个值均为Yes，说明主从库配置已经生效，对主库的操作也会在从库自动复制一份**

因图片无法显示，若主从库配置讲解不清楚，可以自行去掉该配置，更改项目配置文件，换成单库即可。或者自行查阅资料，完成主从配置。

