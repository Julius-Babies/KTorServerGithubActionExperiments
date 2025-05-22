FROM eclipse-temurin:21.0.1_12-jre-alpine
EXPOSE 8080:8080

COPY ./app.jar /app/app.jar
CMD ["java","-jar","/app/app.jar"]
