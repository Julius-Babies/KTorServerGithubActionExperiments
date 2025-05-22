FROM eclipse-temurin:21.0.1_12-jre-alpine
EXPOSE 8080:8080

RUN mkdir /app
RUN mkdir /config
RUN mkdir /temp
COPY ./build/libs/KTorServerGithubActionExperiments-all.jar /app/app.jar
CMD ["java","-jar","/app/app.jar"]
