# ============================================
# STAGE 1: BUILD
# ============================================
FROM eclipse-temurin:17-jdk-alpine AS builder

# Instalar Maven
RUN apk add --no-cache maven

# Establecer directorio de trabajo
WORKDIR /build

# Copiar archivos de configuración Maven
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# Descargar dependencias (capa cacheada)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar aplicación (omitir compilación de tests)
RUN mvn clean package -Dmaven.test.skip=true -B

# ============================================
# STAGE 2: RUNTIME
# ============================================
FROM eclipse-temurin:17-jre-alpine

# Metadata
LABEL maintainer="TechShare Team"
LABEL version="0.5.0"
LABEL description="TechShare Backend - Spring Boot 3.4.1"

# Instalar utilidades necesarias
RUN apk add --no-cache \
  postgresql-client \
  netcat-openbsd \
  curl \
  && rm -rf /var/cache/apk/*

# Crear usuario no-root para seguridad
RUN addgroup -g 1000 spring && \
  adduser -u 1000 -G spring -s /bin/sh -D spring

# Establecer directorio de trabajo
WORKDIR /app

# Copiar el JAR desde el stage de build
COPY --from=builder /build/target/*.jar app.jar

# Crear el script de espera directamente en Linux con printf (evita CRLF)
RUN printf '#!/bin/sh\n\
  # wait-for-db.sh\n\
  HOST=${SPRING_DATASOURCE_HOST:-postgres}\n\
  PORT=${SPRING_DATASOURCE_PORT:-5432}\n\
  echo "Waiting for PostgreSQL at $HOST:$PORT..."\n\
  for i in $(seq 1 60); do\n\
  if nc -z -w2 "$HOST" "$PORT" 2>/dev/null; then\n\
  echo "PostgreSQL is ready!"\n\
  exec java $JAVA_OPTS -jar /app/app.jar\n\
  fi\n\
  echo "Attempt $i/60..."\n\
  sleep 2\n\
  done\n\
  echo "Timeout waiting for PostgreSQL" >&2\n\
  exit 1\n' > /usr/local/bin/wait-for-db.sh && chmod +x /usr/local/bin/wait-for-db.sh

# Crear directorios necesarios
RUN mkdir -p /app/uploaded-images /app/logs && \
  chmod -R 755 /app/uploaded-images && \
  chmod -R 755 /app/logs && \
  chown -R spring:spring /app && \
  chmod -R u+rwx,g+rx,o+rx /app/logs /app/uploaded-images

# Cambiar a usuario no-root
USER spring:spring

# Exponer puerto
EXPOSE 8080

# Health check - Temporalmente deshabilitado para debug
# HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
#     CMD curl -f http://localhost:8080/actuator/health || exit 1

# Variables de entorno por defecto
# Agregado: -XX:+PrintFlagsFinal para ver flags reales
# Agregado: -XX:+PrintGCDetails para ver garbage collection
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+PrintFlagsFinal"
ENV SPRING_PROFILES_ACTIVE=prod

# Entrypoint con configuración JVM
ENTRYPOINT ["/usr/local/bin/wait-for-db.sh"]
