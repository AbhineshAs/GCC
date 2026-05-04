# ===== BUILD STAGE =====
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .

# Clean build
RUN mvn clean package -DskipTests

# ===== RUN STAGE =====
FROM eclipse-temurin:17-jdk
WORKDIR /app

# ✅ Copy exact jar (avoid mismatch issue)
COPY --from=build /app/target/crm-0.0.1-SNAPSHOT.jar app.jar

# Render uses dynamic port
EXPOSE 8080

# Run app
ENTRYPOINT ["java","-jar","/app/app.jar"]