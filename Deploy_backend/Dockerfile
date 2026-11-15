# Build the jar first (in forumParent/forum):  mvn clean package -DskipTests
# then copy forumParent/forum/target/forum-0.0.1-SNAPSHOT.jar next to this Dockerfile and run:
#   docker build -t livdays-backend .
#   docker run -d -p 8080:8080 \
#     -e DB_URL="jdbc:mysql://<host>:3306/LivDayDB?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false" \
#     -e DB_USERNAME=root -e DB_PASSWORD=... \
#     -e MAIL_USERNAME=... -e MAIL_PASSWORD=... \
#     -e JWT_SECRET=<at least 32 random characters> \
#     -e OSS_ACCESS_KEY_ID=... -e OSS_ACCESS_KEY_SECRET=... \
#     livdays-backend
#
# (The old version was based on centos:7, which is end-of-life, and extracted a JDK
#  tarball whose name did not match the copied file, so the image could not be built.)
FROM eclipse-temurin:17-jre

ENV LANG=C.UTF-8 \
    TZ=UTC

WORKDIR /forum
COPY forum-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/forum/app.jar"]
