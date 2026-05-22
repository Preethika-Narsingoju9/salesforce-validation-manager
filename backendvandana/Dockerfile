FROM eclipse-temurin:17
WORKDIR /app
COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests
CMD ["java","-jar","target/backendvandana-0.0.1-SNAPSHOT.jar"]