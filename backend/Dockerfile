FROM adoptopenjdk/openjdk15:jdk-15.0.2_7-alpine

EXPOSE 8080

ADD build/libs/fat.jar app.jar

ENTRYPOINT exec java ${JAVA_OPTS:-} \
  -Djava.security.egd=file:/dev/urandom \
  -Duser.timezone="Europe/Berlin" \
  -Dhttps.protocols="TLSv1.2" \
  -jar /app.jar
