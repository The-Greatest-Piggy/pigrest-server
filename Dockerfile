FROM amazoncorretto:21 AS builder
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

FROM amazoncorretto:21
WORKDIR /app

ENV SPRING_PROFILES_ACTIVE=prod

COPY --from=builder /app/src/main/resources/application.yaml .
COPY --from=builder /app/src/main/resources/application-prod.yaml .

COPY --from=builder /app/build/libs/pigrest.jar /app/pigrest-server.jar

CMD ["java", "-jar", "/app/pigrest-server.jar"]
