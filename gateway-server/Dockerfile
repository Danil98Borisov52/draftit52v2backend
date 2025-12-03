# Этап сборки
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
#RUN mvn clean package -DskipTests

# Этап запуска
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/gateway-server-*.jar app.jar

# Указываем порт, на котором будет работать приложение
EXPOSE 8081
EXPOSE 5006

# Запуск приложения с включенным удалённым дебагом на порту 5005
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5006", "-jar", "app.jar"]