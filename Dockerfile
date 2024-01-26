FROM openjdk:17-oracle
EXPOSE 9298
COPY target/*.jar customerBatchApp.jar
ENTRYPOINT [ "sh", "-c", "java -jar customerBatchApp.jar" ]