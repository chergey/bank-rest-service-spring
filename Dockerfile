FROM openjdk:11-jre-slim
WORKDIR /
ADD ./target/app-1.0-SNAPSHOT.jar app-1.0-SNAPSHOT.jar
EXPOSE 8082
CMD java -jar app-1.0-SNAPSHOT.jar

