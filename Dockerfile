# 1. Etapa de Construcción (Usamos Maven para crear el .jar)
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 2. Etapa de Ejecución (Usamos Java para correr la app)
FROM openjdk:17-jdk-slim
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]