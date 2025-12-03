# Этап сборки
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .

# Этап запуска
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/event-service-*.jar app.jar

# Указываем порт, на котором будет работать приложение
EXPOSE 8086
EXPOSE 5008

# Запуск приложения
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5007", "-jar", "app.jar"]