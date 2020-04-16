FROM adoptopenjdk
EXPOSE 8080
WORKDIR /app
COPY target/caed-java*.jar ./app.jar
ENTRYPOINT [ "java", "-jar", "./app.jar" ]