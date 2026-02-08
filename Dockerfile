# ---- Build stage ----
FROM maven:3.9.8-eclipse-temurin-11 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

# ---- Run stage ----
FROM eclipse-temurin:11-jre
WORKDIR /app
COPY --from=build /app/target/employee-management-rabbitmq-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
