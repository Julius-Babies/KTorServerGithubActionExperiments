FROM gradle:8.4-jdk21-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

ARG MAVEN_USERNAME
ARG MAVEN_PASSWORD

ENV MAVEN_USERNAME=$MAVEN_USERNAME
ENV MAVEN_PASSWORD=$MAVEN_PASSWORD

RUN gradle buildFatJar --no-daemon

FROM eclipse-temurin:21.0.1_12-jre-alpine
EXPOSE 8080:8080

RUN mkdir /app
RUN mkdir /config
RUN mkdir /temp
COPY --from=build /home/gradle/src/build/libs/KTorServerGithubActionExperiments-all.jar /app/app.jar
CMD ["java","-jar","/app/app.jar"]
