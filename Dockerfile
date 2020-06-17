FROM adoptopenjdk
EXPOSE 8080
WORKDIR /app
COPY target/avaliacao*.jar ./app.jar
ENTRYPOINT [ "java", "-jar", "./app.jar" ]