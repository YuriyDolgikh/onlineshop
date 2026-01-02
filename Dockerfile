# ===== Стадия сборки =====
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src

RUN mvn test \
    -Dspring.profiles.active=test \
    -Dspring.datasource.url=jdbc:postgresql://localhost:5433/test

RUN mvn clean package -DskipTests

# ===== Стадия запуска =====
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]