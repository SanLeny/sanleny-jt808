FROM openjdk:8-jdk-alpine
MAINTAINER 308644773@qq.com
RUN echo "Asia/Shanghai" > /etc/timezone
ENV APP_NAME jt808-server.jar
ADD target/$APP_NAME /usr/local/$APP_NAME
CMD java -jar /usr/local/$APP_NAME