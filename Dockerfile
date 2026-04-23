FROM eclipse-temurin:17-jre-alpine
LABEL maintainer="senyehui"

WORKDIR /app
COPY syh-api/target/syh-api-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar", \
    "--server.port=8081", \
    "--spring.datasource.url=${SYH_DB_URL:jdbc:mysql://mysql:3306/db_syh_saas?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true}", \
    "--spring.datasource.username=${SYH_DB_USERNAME:root}", \
    "--spring.datasource.password=${SYH_DB_PASSWORD:syh2026}", \
    "--spring.data.redis.host=${SYH_REDIS_HOST:redis}", \
    "--spring.data.redis.port=${SYH_REDIS_PORT:6379}", \
    "--logging.level.com.senyehui=info"]
