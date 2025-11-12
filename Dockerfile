# https://spring.io/guides/gs/spring-boot-docker

FROM gradle:jdk21-alpine AS builder
# RUN addgroup -S spring && adduser -S spring -G spring
# USER spring:spring
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080
ENTRYPOINT ["java","-cp","app:app/lib/*","com.matchalab.travel_todo_api"]