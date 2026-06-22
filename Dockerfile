# =============================================================================
# Stage 1: build
# Compila o projeto e gera o .jar usando Gradle
# =============================================================================
FROM eclipse-temurin:25-jdk-alpine AS build

WORKDIR /app

# Copiar apenas os arquivos de build primeiro (melhor cache de layers)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .

# Garantir que o gradlew é executável
RUN chmod +x gradlew

# Download das dependências (camada cacheável separada do código)
RUN ./gradlew dependencies --no-daemon --quiet || true

# Copiar o código-fonte
COPY src src

# Compilar e gerar o jar executável
RUN ./gradlew bootJar --no-daemon

# =============================================================================
# Stage 2: runtime
# Imagem enxuta apenas com JRE — sem JDK, sem fontes, sem Gradle
# =============================================================================
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Copiar apenas o jar gerado no stage de build
COPY --from=build /app/build/libs/*.jar app.jar

# Porta da aplicação Spring Boot
EXPOSE 8080

# Variáveis de ambiente — injetadas pelo Docker Compose ou docker run -e
# JWT_SECRET: obrigatória — sem ela o container não sobe (fail-fast via ADR-006)
# JWT_SESSION_TIME: opcional — fallback 86400 (24h) definido em application.properties
ENV JWT_SESSION_TIME=604800
ENV SPRING_PROFILES_ACTIVE=dev

ENTRYPOINT ["java", "-jar", "app.jar"]
