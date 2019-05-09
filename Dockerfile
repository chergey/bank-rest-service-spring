FROM openjdk:11-jre-slim
WORKDIR /
ADD ./app/target/app-1.0-SNAPSHOT.jar app-1.0-SNAPSHOT.jar
EXPOSE 8082
CMD java -jar --add-exports=java.base/jdk.internal.misc=ALL-UNNAMED --add-exports=java.base/sun.nio.ch=ALL-UNNAMED app-1.0-SNAPSHOT.jar