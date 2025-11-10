# ---Stage 1: 빌더 스테이지 (Build Stage) ---
FROM gradle:8.8-jdk17 AS builder

# 1-1. 작업 디렉토리 설정
WORKDIR /app

# 1-2. 캐시 레이어 최적화를 위해 빌드 파일만 먼저 복사
# 캐시가 깨지지 않도록 변경 빈도가 낮은 파일부터 복사.
COPY build.gradle settings.gradle /app/
COPY gradlew /app/
COPY gradle /app/gradle

# 1-3. 의존성만 다운로드 (소스 코드 변경 시에도 재사용 가능하도록)
# CodeBuild에서는 데몬 사용 안 함(--no-daemon)을 권장.
RUN ./gradlew dependencies --no-daemon

# 1-4. 소스 코드 복사 및 최종 빌드
# buildspec.yml에서 이미 build를 실행했다면, 이 단계는 JAR 파일 생성만 확인.
COPY . /app
RUN ./gradlew build --no-daemon -x test

# --- Stage 2: 런타임 스테이지 (Runtime Stage) ---
# 애플리케이션 실행에 필요한 JRE만 포함된 경량 이미지를 사용.
FROM eclipse-temurin:17-jre-alpine

# 2-1. 작업 디렉토리 설정 및 사용자/그룹 생성
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# 2-2. 빌더 스테이지에서 생성된 JAR 파일을 복사
COPY --from=builder /app/build/libs/*.jar /app/app.jar

# 2-3. 포트 노출 (ECS 설정과는 별개로 정보 제공 목적)
EXPOSE 8080

# 2-4. 컨테이너가 시작될 때 실행할 명령어 (JAR 파일 실행)
ENTRYPOINT ["java", "-jar", "app.jar"]