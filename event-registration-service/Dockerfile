# Этап сборки
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .

# Этап запуска
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/event-registration-service-*.jar app.jar

# Указываем порт, на котором будет работать приложение
EXPOSE 8088

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]