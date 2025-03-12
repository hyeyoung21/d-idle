# Build Stage
FROM gradle:8.7-jdk17 AS build
WORKDIR /app

# 프로젝트 루트(didle/didle)를 빌드 컨텍스트로 사용
COPY build.gradle settings.gradle ./
COPY src ./src
RUN gradle clean build -x test

# Runtime Stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
