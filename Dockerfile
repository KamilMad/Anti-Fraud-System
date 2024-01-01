FROM openjdk:latest

COPY target/Anti-Fraud-System-0.0.1-SNAPSHOT.jar antifraud.jar

ENTRYPOINT ["java", "-jar", "antifraud.jar"]