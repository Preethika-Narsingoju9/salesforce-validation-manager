# FROM eclipse-temurin:17
# WORKDIR /app
# COPY . .
# RUN chmod +x mvnw
# RUN ./mvnw clean package -DskipTests
# CMD ["java","-jar","target/backend-0.0.1-SNAPSHOT.jar"]

# ---------- BUILD STAGE ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# ---------- RUN STAGE ----------
FROM eclipse-temurin:17
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

CMD ["java","-jar","app.jar"]