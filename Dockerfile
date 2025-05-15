FROM debian:12.10

ENV CONTAINER docker

RUN apt update && apt install -y openjdk-17-jdk

RUN mkdir /tmp/oltom

COPY src/ /tmp/oltom/src
COPY script/ /tmp/oltom/script
COPY *.gradle /tmp/oltom

#RUN /tmp/oltom/script/install-gradle.sh

EXPOSE 80
EXPOSE 9000

# CMD bin/dev/start.sh
