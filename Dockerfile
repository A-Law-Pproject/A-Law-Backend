# 1. Build Stage
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# [최적화] 의존성 캐싱을 위해 build.gradle 등을 먼저 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 의존성 미리 다운로드 (소스 코드 변경 시에도 의존성 층은 캐싱됨)
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

# 소스 복사 및 빌드
COPY src src
RUN ./gradlew clean bootJar -x test --no-daemon

# 2. Run Stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 빌드 스테이지에서 생성된 '실행 가능한' JAR만 복사
# bootJar 명령어로 생성된 특정 파일을 지칭하는 것이 안전합니다.
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

# 보안을 위해 root가 아닌 일반 유저로 실행하는 것이 좋지만, 
# 일단 기본 설정으로 진행해도 무방합니다.

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
