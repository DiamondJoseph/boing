FROM maven:3-eclipse-temurin-25-alpine AS build

WORKDIR /server
ADD ./server /server
RUN mvn clean package -DskipTests -Dspotless.check.skip=true --batch-mode

FROM eclipse-temurin:25-jre

USER 1000:1000
COPY --from=build --chown=1000:1000 --chmod=+rwX /server/target/*.jar /app.jar

ENTRYPOINT ["java","-jar","/app.jar","--version"]
