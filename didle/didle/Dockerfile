# didle/didle/Dockerfile

# Build Stage
FROM gradle:8.7-jdk17 AS build
WORKDIR /app
# 상위 디렉토리에서 파일 복사 (didle/ → /app)
COPY ../build.gradle ../settings.gradle ./
COPY ../src ./src
RUN gradle clean build -x test

# Runtime Stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
