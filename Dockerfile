FROM amazoncorretto:21 AS builder
WORKDIR /pigrest-server
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

FROM amazoncorretto:21
WORKDIR /pigrest-server
COPY --from=builder /app/build/libs/pigrest-server.jar .
ENV SPRING_PROFILES_ACTIVE=prod
CMD ["java", "-jar", "pigrest-server.jar"]