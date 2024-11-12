FROM openjdk:21-jdk-slim

FROM openjdk:21-jdk-slim

# Метаданные
LABEL maintainer="your-email@example.com"
LABEL version="1.0"
LABEL description="Spring Boot application Sweet Dreams"

# Создание рабочей директории
WORKDIR /app

# Копирование JAR файла
COPY target/Sweet_Dreams-0.0.1-SNAPSHOT.jar app.jar

# Переменные окружения
ENV PORT=1001
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Открытие порта
EXPOSE ${PORT}

# Запуск приложения
CMD ["sh", "-c", "java ${JAVA_OPTS} -Dserver.port=${PORT} \
    -Dspring.datasource.url=jdbc:postgresql://autorack.proxy.rlwy.net:20225/railway \
    -Dspring.datasource.username=postgres \
    -Dspring.datasource.password=ucboaXsYVcupKADZrmbfiwpnHOslPwjf \
    -jar app.jar"]

#FROM openjdk:21-jdk-slim
#
## Метаданные об образе
#LABEL maintainer="your-email@example.com"
#LABEL version="1.0"
#LABEL description="Spring Boot application Sweet Dreams"
#
## Создание рабочей директории
#WORKDIR /app
#
## Копирование JAR файла
#COPY target/Sweet_Dreams-0.0.1-SNAPSHOT.jar app.jar
#
## Переменные окружения
#ENV PORT=1001
#ENV JAVA_OPTS=""
#
## Открытие порта
#EXPOSE ${PORT}
#
## Запуск приложения
#ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -Dserver.port=${PORT} -jar app.jar"]