# Этап сборки
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Этап запуска
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/resource-server-*.jar app.jar

# Указываем порт, на котором будет работать приложение
EXPOSE 8090

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]