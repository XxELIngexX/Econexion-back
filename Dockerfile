# Etapa simple sin layertools
FROM eclipse-temurin:21-jre-alpine

# Usuario no-root
RUN addgroup -g 1000 -S appuser && \
    adduser -u 1000 -S appuser -G appuser

# Configurar zona horaria
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/America/Bogota /etc/localtime && \
    echo "America/Bogota" > /etc/timezone && \
    apk del tzdata

# Directorio de trabajo
WORKDIR /app

# Crear directorios para logs
RUN mkdir -p /app/logs && \
    chown -R appuser:appuser /app

# Copiar el JAR (usando el que SÍ funciona)
# NOTA: Estoy usando el JAR ejecutable grande
COPY --chown=appuser:appuser target/econexion-1.0-SNAPSHOT.jar app.jar

# Cambiar a usuario no-root
USER appuser

# Puerto
EXPOSE 35001

# Comando SIMPLE que debe funcionar
ENTRYPOINT ["java", "-jar", "app.jar"]