# app
# cd ../library
mvn clean package -DskipTests
docker rmi backend:dev
docker build -t backend:dev .
docker run -d --name backend -p 8088:8088 backend:dev
docker network connect hdfs_hadoopnet backend