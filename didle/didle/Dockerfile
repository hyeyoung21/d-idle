# 베이스 이미지 설정 (OpenJDK 17 사용 예시)
FROM openjdk:17-jdk-slim

# 애플리케이션 JAR 파일 복사
ARG JAR_FILE=didle.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

# 컨테이너에서 실행될 명령어
ENTRYPOINT ["java", "-jar", "/app.jar"]
