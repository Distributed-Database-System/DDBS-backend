# Manual

## 环境配置

需要安装以下环境：

- Docker
- Java 8
- Maven

- [mongosh](https://www.mongodb.com/docs/mongodb-shell/install/)
- [MongoDB Database Tools](https://www.mongodb.com/docs/database-tools/installation/installation/)
- [Mongo Compass](https://www.mongodb.com/docs/compass/current/install/)

数据准备：将 db-generation 解压缩放置到 ${PROJECT}/（指代项目目录）下，重命名为 data 目录

## 部署方法

### 脚本介绍

- /data-generate 目录
  - 该脚本会使用 /data 目录下的数据，并将生成的新数据一并放置在 /data 下

- /script 目录

  - setup.sh、setup.bat：初始化并启动容器，包括数据加载等，⽤于第⼀次启动

  - shutdown.sh、shutdown.bat：停⽌容器的运⾏，并删除容器

  - start.sh、start.bat：开始容器的运⾏（第⼀次启动请使⽤ setup.sh）

  - stop.sh、stop.bat：停⽌容器的运⾏

### 部署流程

#### 数据生成

使用脚本：/data-generate/genTable_mongoDB10G.py

```shell
# 进入到相应目录，然后运行 genTable_mongoDB10G.py
python3 genTable_mongoDB10G.py
```

#### 启动服务

首次启动：使用脚本 /script/setup.sh

```shell
# 进入到 /script 目录，然后运行 setup.sh
./setup.sh
```

setup.sh 脚本说明

```shell
#!/bin/bash

# hdfs
docker-compose -f ../hdfs/docker-compose.yml up -d
sleep 10
# 创建目录，将 /data/articles/ 上传至 hdfs
docker exec -it namenode hadoop fs -mkdir /articles/
docker exec -it namenode hadoop fs -put /data/articles/ /articles/


# redis
docker pull redis:7.0.0
docker run -itd --name redis -p 6379:6379 redis
docker network connect hdfs_hadoopnet redis --ip 172.21.0.6


# mongodb
# config servers
docker-compose -f ../mongodb/docker-compose-configsvr.yml up -d
mongosh --host localhost --port 40011 ../mongodb/setup-configsvr.js
# shard servers
docker-compose -f ../mongodb/docker-compose-shardsvr.yml up -d
mongosh --host localhost --port 40021 ../mongodb/setup-shardsvr-rs1.js
mongosh --host localhost --port 40031 ../mongodb/setup-shardsvr-rs2.js
# mongos
docker-compose -f ../mongodb/docker-compose-mongos.yml up -d
sleep 5
mongosh --host localhost --port 40002 ../mongodb/setup-mongos.js
docker network connect hdfs_hadoopnet mongos --ip 172.21.0.7

# load data
# 使用 mongoimport 直接导入生成的数据
mongoimport --host localhost --port 40002 -d ddbms -c user --file ../data/user.dat
mongoimport --host localhost --port 40002 -d ddbms -c article --file ../data/article.dat
mongoimport --host localhost --port 40002 -d ddbms -c read --file ../data/read.dat
# 使用 js 脚本生成 be-read 和 rank 表
mongosh --host localhost --port 40002 ../mongodb/generate-beread.js
mongosh --host localhost --port 40002 ../mongodb/generate-rank.js
```

#### 环境查看说明

##### Mongo

首先在 Docker Desktop 中 进入 mongos 容器：![截屏2022-11-27 22.22.54](/Users/lly/Desktop/截屏2022-11-27 22.22.54.png)

在 Terminal 中 运行如下命令可以查看导入的数据

```shell
# 进入 mongodb
mongo

# 查看 db
show dbs

# 使用 ddbms 并查询 user 表
use ddbms
db.user.find()
```

![截屏2022-11-27 22.35.42](/Users/lly/Desktop/截屏2022-11-27 22.35.42.png)

查看数据分片![截屏2022-11-27 22.38.06](/Users/lly/Library/Application Support/typora-user-images/截屏2022-11-27 22.38.06.png)



##### Hadoop

访问 localhost:9870 可以看到数据成功导入![截屏2022-11-27 22.21.56](/Users/lly/Desktop/截屏2022-11-27 22.21.56.png)