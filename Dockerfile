# syntax=docker/dockerfile:1

FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY gradle/ gradle/
COPY gradlew ./
COPY build.gradle.kts settings.gradle.kts ./

RUN chmod +x gradlew \
    && ./gradlew dependencies --no-daemon

COPY src/ src/

RUN ./gradlew bootJar --no-daemon -x test \
    && JAR="$(ls build/libs/*.jar | grep -v -- '-plain\.jar$' | head -n 1)" \
    && test -n "$JAR" \
    && cp "$JAR" /app/application.jar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

USER spring:spring
COPY --from=builder /app/application.jar /app/application.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/application.jar"]
