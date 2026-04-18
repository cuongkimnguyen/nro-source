FROM maven:3.9.11-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw
RUN ./mvnw --no-transfer-progress -q -DskipTests dependency:go-offline || true
COPY src src
COPY Config Config
COPY sql sql
RUN ./mvnw --no-transfer-progress clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN mkdir -p /app/logs /app/log /app/backup /app/runtime-data /app/Config /app/sql
COPY --from=builder /build/target/HunrProvision-0.0.1-SNAPSHOT.jar /app/server.jar
COPY --from=builder /build/Config /app/Config
COPY --from=builder /build/sql /app/sql
COPY docker/entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh
EXPOSE 1707 14445
ENV TZ=Asia/Bangkok \
    XMS=1G \
    XMX=4G
ENTRYPOINT ["/app/entrypoint.sh"]
