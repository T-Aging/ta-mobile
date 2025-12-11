# 1단계: Build Stage
FROM gradle:8.14-jdk17 AS builder
WORKDIR /app

COPY build.gradle settings.gradle ./
COPY src ./src

RUN gradle clean bootJar --no-daemon

# 2단계: Run Stage
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

# 프로필
# ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]
