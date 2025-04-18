FROM amazoncorretto:21 AS builder
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

FROM amazoncorretto:21
WORKDIR /app

ARG DB_URL
ARG DB_USERNAME
ARG DB_PASSWORD
ARG JWT_SECRET_KEY

ENV DB_URL=${DB_URL}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV JWT_SECRET_KEY=${JWT_SECRET_KEY}
ENV REDIS_HOST=${REDIS_HOST}
ENV REDIS_PORT=${REDIS_PORT}
ENV REDIS_PASSWORD=${REDIS_PASSWORD}
ENV SPRING_PROFILES_ACTIVE=prod

COPY --from=builder /app/src/main/resources/application.yaml .
COPY --from=builder /app/src/main/resources/application-prod.yaml .

COPY --from=builder /app/build/libs/pigrest.jar /app/pigrest-server.jar

CMD ["java", "-jar", "/app/pigrest-server.jar"]
