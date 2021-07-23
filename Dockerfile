FROM openjdk:8u212-jre
EXPOSE 30007
MAINTAINER ZDS
# 时区问题
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone

VOLUME /tmp
ADD datasender-zh-1.0-SNAPSHOT.jar /app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-jar","/app.jar"]


