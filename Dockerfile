## Build stage#
FROM maven:3.9.6-eclipse-temurin-21 AS builder
RUN mkdir /build
COPY . /build
WORKDIR /build
RUN mvn clean package -DskipTests

## Package stage#
FROM eclipse-temurin:21-jdk
RUN mkdir /app
COPY --from=builder /build/target/service-1.0-SNAPSHOT.jar /app/checker.jar
WORKDIR /app
CMD ["java","-jar","checker.jar"]