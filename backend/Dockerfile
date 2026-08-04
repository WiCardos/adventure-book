# Build stage — has the full JDK + Maven toolchain
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

COPY src src
RUN ./mvnw package -DskipTests -B

# Runtime stage — only the JRE and the compiled jar, nothing else
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV APP_SAVES_DIRECTORY=/app/saves
ENV APP_BOOKS_DIRECTORY=/app/books
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]