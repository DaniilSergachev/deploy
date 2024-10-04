FROM maven:3.8.5-openjdk-17 AS builder
COPY pom.xml /tmp/
COPY src /tmp/src
WORKDIR /tmp
RUN mvn clean package

FROM openjdk:17-jdk-slim
COPY --from=builder /tmp/target/exchange-1.0-SNAPSHOT.jar /tmp/app.jar
WORKDIR /tmp
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
