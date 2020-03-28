FROM openjdk:8-jre-alpine

COPY ./build/libs/example-product-service-0.0.1-SNAPSHOT.jar /app.jar
RUN apk add --no-cache ttf-dejavu
RUN echo "Asia/Shanghai" > /etc/timezone

CMD ["/usr/bin/java", "-jar", "/app.jar"]