# 1. 빌드 단계
FROM gradle:8.4.0-jdk21 AS build
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN gradle build -x test

# 2. 실행 단계
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]