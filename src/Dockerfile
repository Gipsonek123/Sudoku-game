FROM openjdk:11-jdk

RUN apt-get update && apt-get install -y \
	x11-apps \
	libxtst6 \
	libxrender1 \
	libxi6

COPY Main.java .

ENV DISPLAY host.docker.internal:0.0

CMD ["java", "-jar", "Main.java"]
