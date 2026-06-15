FROM amazoncorretto:17-alpine AS builder
WORKDIR /opt/app
COPY --chmod=755 .mvn/ .mvn
COPY --chmod=755 ./mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY ./src ./src
RUN ./mvnw clean package -DskipTests

FROM amazoncorretto:17-alpine AS final
RUN apk update && apk upgrade && apk --no-cache add curl
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
WORKDIR /opt/app
COPY --from=builder /opt/app/target/*.war /opt/app/app.war
ENTRYPOINT ["java", "-jar", "/opt/app/app.war"]
HEALTHCHECK --start-interval=30s --interval=10s --timeout=3s --retries=5 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1