FROM amazoncorretto:17-alpine-jdk
WORKDIR application
COPY ./target/ttbay-*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
EXPOSE 8080