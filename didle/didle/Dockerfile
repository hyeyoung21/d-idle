# didle/didle/Dockerfile

# Build Stage
FROM gradle:8.7-jdk17 AS build
WORKDIR /app

# 1. 전체 프로젝트 복사 (상대 경로 조정)
COPY . .

# 2. 빌드 실행
RUN gradle clean build -x test

# Runtime Stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# 3. JAR 파일 복사 (명확한 경로 지정)
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
