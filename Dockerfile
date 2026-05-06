# ==================== Build stage ====================
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

COPY gradlew* ./
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew

RUN ./gradlew dependencies --no-daemon

COPY . .

RUN ./gradlew clean bootJar -x test --no-daemon

# ==================== Runtime stage ====================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
