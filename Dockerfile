FROM openjdk:15-jdk-slim
MAINTAINER miguelpadillaespino@gmail.com
COPY "target/hackaton-backend-*.jar" "app.jar"
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]