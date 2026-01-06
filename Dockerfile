# 1. Etapa de Construcción (Usamos Maven con Java 21)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 2. Etapa de Ejecución (Usamos Java 21 para correr la app)
FROM eclipse-temurin:21-jdk-alpine
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]