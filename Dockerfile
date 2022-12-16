# 版本1
FROM openjdk:8u262-jre-slim-buster
ADD target/ddbs-backend-1.0.0.jar /app/ddbs-backend.jar
ADD runboot.sh /app/
WORKDIR /app
RUN chmod a+x runboot.sh
CMD /app/runboot.sh