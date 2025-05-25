# Используем базовый образ с OpenJDK 22
FROM eclipse-temurin:22-jdk-jammy

# Автор
LABEL maintainer="VivoBook-15"

# Рабочая директория внутри контейнера
WORKDIR /app

# Копируем JAR-файл из target в контейнер
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]