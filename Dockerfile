# ============================================================
# Dockerfile template — Java 21 / Spring Boot service
# Copy this into each Java service repo as ./Dockerfile
#
# Build args:
#   SERVICE_NAME — used for the JAR filename (default: app)
#   JVM_OPTS    — JVM tuning flags (default: optimised for containers)
# ============================================================

# ── Stage 1: Build ────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /workspace

# Copy dependency descriptors first for Docker layer caching
COPY pom.xml .
# COPY .mvn/ .mvn/ (Directory missing in source)
RUN mvn dependency:go-offline -B -q

# Copy source and build
COPY src/ src/
RUN mvn package -DskipTests -B -q

# Extract Spring Boot layered JAR for optimised image layers
RUN java -Djarmode=layertools     -jar target/*.jar     extract --destination target/extracted

# ── Stage 2: Runtime ──────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

# Non-root user — required for AKS pod security
RUN addgroup -S gtbs && adduser -S gtbs -G gtbs
USER gtbs

WORKDIR /app

# Copy Spring Boot layers in dependency-change order
# (least-likely-to-change first = better layer cache hits)
COPY --from=build /workspace/target/extracted/dependencies/          ./
COPY --from=build /workspace/target/extracted/spring-boot-loader/    ./
COPY --from=build /workspace/target/extracted/snapshot-dependencies/ ./
COPY --from=build /workspace/target/extracted/application/           ./

# Health check — Spring Actuator endpoint
HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=3 \
  CMD wget -qO- http://localhost:8082/actuator/health || exit 1

EXPOSE 8082

# Container-aware JVM flags:
#   UseContainerSupport — honour cgroup memory limits
#   MaxRAMPercentage    — use 75% of container memory for heap
#   ExitOnOutOfMemoryError — crash fast so K8s can restart cleanly

ENV JVM_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"

ENTRYPOINT ["sh", "-c", "java $JVM_OPTS org.springframework.boot.loader.launch.JarLauncher"]
