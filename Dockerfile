# --- Etapa 1: Compilación ---
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

COPY src ./src
RUN ./mvnw clean package -DskipTests

# --- Etapa 2: Ejecución ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 1. Copiamos ESPECÍFICAMENTE el JAR con dependencias y lo renombramos a app.jar
COPY --from=build /app/target/*-jar-with-dependencies.jar app.jar

# 2. Como el Assembly Plugin ya pone el Main-Class en el manifest,
#    solo necesitas usar java -jar
ENTRYPOINT ["java", "-jar", "app.jar"]