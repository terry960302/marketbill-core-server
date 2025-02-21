# docker image pull rate limit 이슈로 docker hub가 아닌 aws ecr 이미지로 대체
FROM public.ecr.aws/docker/library/gradle:7.5.1-jdk-focal AS builder
MAINTAINER Terry Kim <terry960302@gmail.com>

WORKDIR /app
COPY ["build.gradle.kts", "settings.gradle.kts", "./"]
COPY gradlew .
COPY gradle  ./gradle/
COPY src ./src/
RUN chmod +x ./gradlew
RUN ./gradlew build

FROM public.ecr.aws/docker/library/openjdk:latest AS runner

ENV VERSION=0.0.1-SNAPSHOT
ENV PROFILE=prod
ENV TZ=Asia/Seoul

ARG FILENAME=marketbill-core-server-${VERSION}
ARG JAR_FILE=/app/build/libs/${FILENAME}.jar
COPY --from=builder ${JAR_FILE} ./app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar", "-Dspring.profiles.active=${PROFILE}", "-Duser.timezone=${TZ}", "/app.jar"]

## -- (구) Dockerfile --
#FROM gradle:7.5.1-jdk18 AS builder
#MAINTAINER terry960302@gmail.com
#
#WORKDIR /app
#COPY ["build.gradle.kts", "settings.gradle.kts", "./"]
#COPY gradle  ./gradle/
#COPY src ./src/
#RUN gradle clean build --no-daemon
#
#FROM openjdk:18 AS runner
#
#ENV VERSION=0.0.1-SNAPSHOT
#ENV PROFILE=dev
#
#ARG FILENAME=marketbill-core-server-${VERSION}
#ARG JAR_FILE=/app/build/libs/${FILENAME}.jar
#COPY --from=builder ${JAR_FILE} ./app.jar
#ENTRYPOINT ["java","-jar", "-Dspring.profiles.active=${PROFILE}", "/app.jar"]

