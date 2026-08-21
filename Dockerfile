# Use latest LTS, keep in sync with devcontainer
FROM node:24-alpine AS client-build

WORKDIR /client
# Cache dependencies
COPY client/package*.json .
RUN npm install
# Build project
COPY ./client .
RUN npm run build
# Check built product
RUN node dist/version.js

# Use latest Java LTS, keep in sync with devcontainer
FROM maven:3-eclipse-temurin-25-alpine AS server-build

WORKDIR /server
COPY ./server .
RUN mvn clean package -DskipTests -Dspotless.check.skip=true --batch-mode
# Check built product
RUN java -jar target/*.jar --version

# Use latest LTS, keep in sync with devcontainer
FROM eclipse-temurin:25-jre

USER 1000:1000
COPY --from=server-build --chown=1000:1000 --chmod=+rwX /server/target/*.jar /app.jar
COPY --from=client-build --chown=1000:1000 --chmod=+rwX /client/dist /client

ENV SPRING_WEB_RESOURCES_STATIC_LOCATIONS="classpath:/static/,file:/client"

ENTRYPOINT ["java","-jar","/app.jar"]
