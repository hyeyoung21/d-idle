# Stage 1: 빌드 단계 (Gradle 사용)
FROM gradle:8.4-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build -x test

# Stage 2: 실행 단계 (최종 이미지)
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# 포트 노출 (스프링 부트 기본 포트)
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
