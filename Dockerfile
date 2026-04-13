FROM maven:3.9.6-eclipse-temurin-21 as build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

RUN mkdir -p /app/upload-dir && chown -R spring:spring /app/upload-dir && chmod 755 /app/upload-dir

COPY --from=build /app/target/*.jar app.jar

USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-Dstorage.upload-dir=/app/upload-dir", "-jar", "app.jar"]