FROM maven:3.9.6-eclipse-temurin-21-alpine as builder

WORKDIR /usr/local/app
COPY pom.xml ./

COPY src ./src

RUN mvn clean package -DskipTests 

FROM eclipse-temurin:21-jre as runtime

WORKDIR /usr/local/app

COPY --from=builder /usr/local/app/target/*SNAPSHOT.jar  app.jar

EXPOSE 8080

RUN useradd app
USER app

CMD ["java","-jar","app.jar"]