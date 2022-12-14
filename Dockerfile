#版本1
FROM java:8
VOLUME /tmp
ADD target/ddbs-backend-1.0.0.jar ddbs-backend.jar
ENV TZ=GMT+8
RUN bash -c 'touch /ddbs-backend.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/ddbs-backend.jar"]
